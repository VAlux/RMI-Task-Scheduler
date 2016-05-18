package def.server;

import static def.utils.Preferences.BOUNDING_PORT;
import static def.utils.Preferences.REMOTE_LOOKUP_SERVER_TARGET;
import static def.utils.Preferences.TASK_LOOKUP_FREQUENCY;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import def.remote.RemoteTaskSchedulerClient;
import def.remote.RemoteTaskSchedulerServer;
import def.taskmodel.Task;
import def.taskmodel.TasksModel;
import def.taskmodel.source.FileModelProvider;
import def.taskmodel.source.TasksModelProvider;

/**
 * Basic RMI task scheduling server.
 * Allows remote clients to add tasks to the schedule,
 * which will be executed at the specified time.
 * @author ovoievodin
 */
public class TaskSchedulerServer implements RemoteTaskSchedulerServer {
	
	/**
	 * The map of tasks, which are needed to be scheduled.
	 * Key is the time of the execution and the value is actually 
	 * a list of tasks to execute in this moment of time.
	 * So here we have tasks, grouped by execution time.
	 */
	private TasksModel tasks; 
	
	private TasksModelProvider modelProvider;
	
	/**
	 * Logged-in clients set.
	 */
	private Set<RemoteTaskSchedulerClient> clients;
	
	private boolean isSchedulingJobEnabled;
	
	public TaskSchedulerServer() {
		modelProvider = new FileModelProvider();
		modelProvider.load();
		tasks = modelProvider.getModel();
		clients = new HashSet<>();
	}
	
	@Override
	public synchronized void addTask(Task newTask) throws RemoteException {
		final ConcurrentHashMap<Date, List<Task>> tasksData = tasks.getData(); // less boilerplate.
		if(tasksData.containsKey(newTask.getExecutionDate())) {
			tasksData.get((newTask.getExecutionDate())).add(newTask);
		} else {
			final List<Task> newTasksList = new ArrayList<>();
			newTasksList.add(newTask);
			tasksData.put(newTask.getExecutionDate(), newTasksList);
		}
		tasks.getTasksSchedule().offer(newTask.getExecutionDate());
		notifyClients();
		saveModel();
		System.out.println("New task: " + newTask + " added.");
	}
	
	@Override
	public TasksModel getTasks() throws RemoteException {
		return this.tasks;
	}
	
	@Override
	public synchronized void removeTask(Task task) throws RemoteException {
		tasks.remove(task);
		notifyClients();
		saveModel();
		System.out.println("Task: " + task + " removed from schedule.");
	}
	
	@Override
	public synchronized void registerClient(RemoteTaskSchedulerClient client) throws RemoteException {
		if(!this.clients.contains(client)){
			client.assignId(clients.size()); // new client will have maximal id.
			this.clients.add(client);
			System.out.println("Client logged in: " + client);	
		} else {
			System.out.println("Client logging failed: " + client + " already logged in.");
		}
	}
	
	@Override
	public synchronized void unRegisterClient(RemoteTaskSchedulerClient client) throws RemoteException {
		this.clients.remove(client);
		System.out.println("Client logged out: " + client);
	}
	
	private void saveModel() {
		modelProvider.save();
	}
	
	/**
	 * Notify all clients about the tasks model update.
	 * @author ovoievodin
	 */
	private void notifyClients() {
		for (RemoteTaskSchedulerClient client : clients) {
			try {
				client.updateTasksModel(tasks);	
			} catch (RemoteException e) {
				System.err.println("Error updating clients task model " + client);
			}
		}
	}
	
	/**
	 * Method for starting the task scheduling process.
	 * @author ovoievodin
	 */
	private void startScheduling() {
		System.out.println("Task scheduling started...");
		final ConcurrentHashMap<Date, List<Task>> tasksData = tasks.getData(); // less boilerplate.
		final Queue<Date> schedule = tasks.getTasksSchedule();
		this.isSchedulingJobEnabled = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(isSchedulingJobEnabled) {
					if(isNeededToCheckTask()) {
						if(tasksData.containsKey(schedule.peek()) && isNeededToExecuteNow(schedule.peek())) {
							final Date executionDate = schedule.poll();
							execute(tasksData.get(executionDate));
							tasksData.remove(executionDate);
							notifyClients();
						}
					}
				}
				System.out.println("Scheduling job stopped.");
			}
		}).start();
	}
	
	/**
	 * Kill the task scheduling job.
	 * @author ovoievodin
	 */
	public void stopScheduling() {
		System.out.println("Scheduling job shutdown requested.");
		this.isSchedulingJobEnabled = false;
	}
	
	/**
	 * Iterate over the tasks list and execute each of them.
	 * @param tasksList source tasks list to execute.
	 * @author ovoievodin
	 */
	private void execute(List<Task> tasksList) {
		for (Task task : tasksList) {
			System.out.println("Executing task " + task);	
			try {
				java.lang.Runtime.getRuntime().exec(task.getTarget());
			} catch (IOException e) {
				System.err.println("Error executing the task: " + task + " :: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Determine, if the task is needed to be executed in the current moment in time, or it is out-dated.
	 * @param taskExecutionDate the source task execution date.
	 * @return true -> need to be executed now or in the past<br> 
	 *         false -> execution is not needed currently(will be in the future).
	 * @author ovoievodin
	 */
	private boolean isNeededToExecuteNow(Date taskExecutionDate) {
		final Date now = Calendar.getInstance().getTime();
		return taskExecutionDate.before(now) || taskExecutionDate.equals(now);
	}
	
	/**
	 * Determine if it is needed to perform task execution time checking.
	 * @return true -> if the tasks queue is <b>NOT</b> empty, 
	 * and current system time is eligible to check.<br>
	 * false -> otherwise.
	 * @author ovoievodin
	 */
	private boolean isNeededToCheckTask() {
		return !tasks.isEmpty() && ((System.currentTimeMillis() / 1000L) % TASK_LOOKUP_FREQUENCY == 0);
	}
	
	/**
	 * Basic server initialization:
	 * <li>create and export scheduler stub object
	 * <li>create RMI registry.
	 * <li>bind the created stub to the registry.
	 * @throws RemoteException
	 * @throws AlreadyBoundException
	 * @author ovoievodin
	 */
	private void initServer() throws RemoteException, AlreadyBoundException {
		RemoteTaskSchedulerServer stub = (RemoteTaskSchedulerServer) UnicastRemoteObject.exportObject(this, 0);
		Registry registry = LocateRegistry.createRegistry(BOUNDING_PORT);
		registry.bind(REMOTE_LOOKUP_SERVER_TARGET, stub);
		System.out.println("Server initialization complete!");
	}
	
	public static void main(String[] args) {
		try {
			final TaskSchedulerServer server = new TaskSchedulerServer();
			server.initServer();
			server.startScheduling();
		} catch(RemoteException | AlreadyBoundException ex) {
			System.err.println("Server initialization|scheduling error: [" + ex.getMessage() + "]");
		}
	}
}