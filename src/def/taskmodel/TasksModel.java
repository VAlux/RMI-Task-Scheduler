package def.taskmodel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple holder/wrapper for the tasks collection 
 * with some helpful auxiliary handling methods. 
 * @author ovoievodin
 */
public final class TasksModel implements Serializable {
	
	private static final long serialVersionUID = 775750715626568841L;

	/**
	 * The map of tasks, which are needed to be scheduled.
	 * Key is the time of the execution and the value is actually 
	 * a list of tasks to execute in this moment of time.
	 * So we have tasks, grouped by execution time.
	 */
	private ConcurrentHashMap<Date, List<Task>> data;
	
	/**
	 * The queue of tasks execution dates, sorted and without duplicates. 
	 */
	private Queue<Date> tasksSchedule;
	
	public TasksModel() {
		this.data = new ConcurrentHashMap<>();
		this.tasksSchedule = new PriorityQueueSet<>();
	}
	
	/**
	 * Get the total amount of tasks in the data collection.
	 * @return total amount of tasks.
	 * @author ovoievodin
	 */
	public int getTasksAmount() {
		int amount = 0;
		for(List<Task> tasksSublist : data.values()) {
			amount += tasksSublist.size();
		}
		return amount;
	}
	
	/**
	 * Represent all tasks from map as a single list.
	 * TODO: maybe rewrite without streams.
	 * @return flat tasks list.
	 * @author ovoievodin
	 */
	public List<Task> asList() {
		System.out.println("tasks cache refreshed.");
		return data.values().stream().flatMap(task -> task.stream()).collect(Collectors.toList());
	}
	
	/**
	 * Find particular task with specified execution time and index.
	 * @param date task execution time.
	 * @param index position in the schedule for the specified time.
	 * @author ovoievodin
	 */
	public Task getAt(Date date, int index) {
		return data.get(date).get(index);
	}
	
	public ConcurrentHashMap<Date, List<Task>> getData() {
		return data;
	}

	public void setData(ConcurrentHashMap<Date, List<Task>> data) {
		this.data = data;
	}

	public Queue<Date> getTasksSchedule() {
		return tasksSchedule;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public void remove(Task task) {
		data.get(task.getExecutionDate()).remove(task);		
		tasksSchedule.remove(task);
	}
}
