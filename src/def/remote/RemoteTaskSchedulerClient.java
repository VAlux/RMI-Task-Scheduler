package def.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import def.taskmodel.TasksModel;

/**
 * Basic interface for communication with remote task scheduler client.
 * @author ovoievodin
 */
public interface RemoteTaskSchedulerClient extends Remote {
	
	/**
	 * Remote server pushes it's tasks model to the client 
	 * and updates the client's tasks table UI.
	 * @author ovoievodin
	 */
	void updateTasksModel(TasksModel model) throws RemoteException; 
	
	/**
	 * Method for assigning the identifier for the remote client.
	 * Server is responsible for assigning new id for the client.
	 * @param newId new id to assign to remote client.
	 * @throws RemoteException
	 * @author ovoievodin
	 */
	void assignId(final long newId) throws RemoteException; 
	
	/**
	 * Get the client Id.
	 * @return client id number.
	 * @throws RemoteException
	 * @author ovoievodin
	 */
	long getId() throws RemoteException;
}
