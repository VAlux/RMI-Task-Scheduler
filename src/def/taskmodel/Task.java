package def.taskmodel;

import java.io.Serializable;
import java.util.Date;

/**
 * Basic Task class.
 * Represents the executable task entity, 
 * which contains the date and time of the task execution, 
 * and, actually, the target executable file name.
 * @author ovoievodin
 */
public final class Task implements Serializable, Comparable<Task> {
	private static final long serialVersionUID = 8620820732821381177L;
	private Date executionDate;
	private String target;
	private long ownerId;
	
	public Task(Date execDate, String execTarget) {
		this.executionDate = execDate;
		this.target = execTarget;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * Returns true only if 2 tasks have equal execution time and target.
	 * @author ovoievodin
	 */
	@Override
	public boolean equals(Object other) {
		if(other instanceof Task) {
			return this.getExecutionDate().equals(((Task) other).getExecutionDate()) && 
				   this.getTarget().equals(((Task) other).getTarget());
		}
		return false;
	}

	/**
	 * Tasks are compared only by execution date.
	 * @author ovoievodin
	 */
	@Override
	public int compareTo(Task otherTask) {
		return executionDate.compareTo(otherTask.executionDate);
	}
	
	@Override
	public String toString() {
		return "[ " + executionDate + " :: " + target + " ]";
	}
}
