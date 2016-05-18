package def.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import def.taskmodel.Task;
import def.taskmodel.TasksModel;

/**
 * Basic interface for communication with the remote task scheduler server implementation.
 * @author ovoievodin
 */
public interface RemoteTaskSchedulerServer extends Remote {
	
	/**
	 * Register the remote client on the server and assign an id to him.
	 * @param client remote client to register on server.
	 * @throws RemoteException
	 * @author ovoievodin
	 */
	void registerClient(RemoteTaskSchedulerClient client) throws RemoteException;
	
	/**
	 * Un-Register(log-out) remote client from the server.
	 * @param client remote client to log out.
	 * @throws RemoteException
	 * @author ovoievodin
	 */
	void unRegisterClient(RemoteTaskSchedulerClient client) throws RemoteException;
	
	/**
	 * Add new task to the schedule.
	 * <li>If the schedule has any tasks scheduled for new task's time,
	 * then new task will be appended to the list(no new hash-map entry created.)</li>
	 * <li>Else there will be created a new entry in the schedule with time and single-element list with new task, 
	 * which will be potentially grow in the future.</li>
	 * Also there is occurring an appending to a task schedule non-duplicate priority queue, 
	 * containing only tasks execution time, to easily sort them and poll/reference, when needed. 
	 * @author ovoievodin
	 */
	void addTask(Task newTask) throws RemoteException;
	
	/**
	 * Just remove task form the server schedule.
	 * @param task task to remove
	 * @throws RemoteException
	 * @author ovoievodin
	 */
	void removeTask(Task task) throws RemoteException;
	
	/**
	 * Get all of the available tasks, scheduled on the server.
	 * @return Tasks Model from the server.
	 * @throws RemoteException
	 * @author ovoievodin
	 */
	TasksModel getTasks() throws RemoteException;
}