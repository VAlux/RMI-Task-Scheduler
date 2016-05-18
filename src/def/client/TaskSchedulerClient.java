package def.client;

import static def.utils.Preferences.BOUNDING_PORT;
import static def.utils.Preferences.DEFAULT_CLIENT_IDENTIFIER;
import static def.utils.Preferences.GUI_LOOKANDFEEL_CLASSNAME;
import static def.utils.Preferences.REMOTE_LOOKUP_SERVER_TARGET;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

import def.client.gui.RMIClientFrame;
import def.remote.RemoteTaskSchedulerClient;
import def.remote.RemoteTaskSchedulerServer;
import def.taskmodel.Task;
import def.taskmodel.TasksModel;

/**
 * Scheduler client represents mostly just an interface
 * for communicating the scheduler server with a Client GUI.
 * @author ovoievodin
 */
public class TaskSchedulerClient implements RemoteTaskSchedulerClient {

	private RemoteTaskSchedulerServer taskSchedulingServer;
	private RMIClientFrame clientGUI;
	private long id;
	private boolean isConnectionLost;
	
	public TaskSchedulerClient() {
		id = DEFAULT_CLIENT_IDENTIFIER;
		isConnectionLost = false;
	}
	
	/**
	 * Attempt to schedule new task on server.
	 * Will raise error dialog in the GUI in case of error.
	 * @param time task execution time.
	 * @param target execution target filename.
	 * @author ovoievodin
	 */
	public void addTask(Date time, String target) {
		final Task task = new Task(time, target);
		task.setOwnerId(this.id);
		try {
			taskSchedulingServer.addTask(task);
		} catch (RemoteException e) {
			displayServerCommunicationError("Error creating remote task on server: " + task + "\n" + e.getMessage());
		}
	}
	
	/**
	 * Load the tasks model from the remote task scheduling server.
	 * Will raise error dialog in the GUI in case of error.
	 * @return Tasks model or null in case of exception.
	 * @author ovoievodin
	 */
	public TasksModel loadTasks() {
		try {
			if(isConnectionLost){
				connectToServer();
			}
			return taskSchedulingServer.getTasks();
		} catch (RemoteException | NotBoundException e) {
			displayServerCommunicationError("Error loading tasks model from server. " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Remove task from the server's schedule.
	 * @param task target task to remove from server.
	 * @author ovoievodin
	 */
	public void removeTask(Task task) {
		try {
			taskSchedulingServer.removeTask(task);
		} catch(RemoteException e) {
			displayServerCommunicationError("Error removing task from schedule. " + e.getMessage());
		}
	}
	
	/**
	 * Register client on server.
	 * @author ovoievodin
	 */
	public void registerOnServer() {
		try {
			taskSchedulingServer.registerClient(this);
		} catch (RemoteException e) {
			displayServerCommunicationError("Error registering on scheduler server. " + e.getMessage());
		}
	}
	
	/**
	 * Un-Register(log-out) client from server.
	 * @author ovoievodin 
	 */
	public void unRegisterOnServer() {
		try {
			taskSchedulingServer.unRegisterClient(this);
		} catch (RemoteException e) {
			displayServerCommunicationError("Error un-registering on scheduler server. " + e.getMessage());
		}
	}

	/**
	 * Client tasks model initialization.
	 * @author ovoievodin
	 */
	public void initTasksModel() {
		if(this.getClientGUI() != null) {
			try {
				this.updateTasksModel(this.loadTasks());
			} catch (RemoteException e) {
				displayServerCommunicationError("Error initializing the tasks model. " + e.getMessage());
			}
		}
	}
	
	/**
	 * Just print the error message to the log and raise the error dialogue in the client GUI.
	 * @param message actually the error message to display.
	 * @author ovoievodin
	 */
	private void displayServerCommunicationError(final String message) {
		isConnectionLost = true;
		System.err.println(message);
		clientGUI.showErrorDialog(message + "\nYou can try to reconnect by pressing 'Load'");
	}
		
	public void setScheduler(RemoteTaskSchedulerServer scheduler) {
		this.taskSchedulingServer = scheduler;
	}

	public RMIClientFrame getClientGUI() {
		return clientGUI;
	}
	
	public void setClientGUI(RMIClientFrame clientGUI) {
		this.clientGUI = clientGUI;
	}
	
		
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public void assignId(long newId) throws RemoteException {
		this.setId(newId);
	}
	
	@Override
	public void updateTasksModel(TasksModel model) throws RemoteException {
		this.clientGUI.refreshTasksTableModel(model);
	}
	
	public void connectToServer() throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(BOUNDING_PORT);
		this.setScheduler((RemoteTaskSchedulerServer) registry.lookup(REMOTE_LOOKUP_SERVER_TARGET));
		if(isConnectionLost) {
			this.registerOnServer(); // if we lose the connection -> should re-register on server. 
		}
		isConnectionLost = false;
	}
	
	/**
	 * Basic client initialization.
	 * <li>create RMI registry.
	 * <li>lookup for the remote lookup server target in the RMI registry,
	 * <li>if the connection was lost, try to re-register on newly located server.
	 * <li>create and export client stub object.
	 * <li>bind the created stub to the registry.
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void initClient() throws RemoteException, NotBoundException {
		connectToServer();
		RemoteTaskSchedulerClient stub = (RemoteTaskSchedulerClient) UnicastRemoteObject.exportObject(this, 0);
		this.taskSchedulingServer.registerClient(stub);
		System.out.println("Client initialization complete!");
	}

	public static void main(String[] args) {
		
		TaskSchedulerClient client = new TaskSchedulerClient();
	
		try {
			client.initClient();
		} catch (RemoteException | NotBoundException ex) {
			System.err.println("Client initialization error: [" + ex.getMessage() + "]");
			return;
		}	

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				client.unRegisterOnServer();
				System.exit(0);
			}
		}));
		
		client.setClientGUI(new RMIClientFrame(client));
		client.getClientGUI().switchLookAndFeel(GUI_LOOKANDFEEL_CLASSNAME);
		client.getClientGUI().setVisible(true);
		client.initTasksModel();
	}
}
