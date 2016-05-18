package def.taskmodel;

import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Just a simple table model for displaying currently 
 * scheduled tasks on the server in the client's GUI.
 * @author ovoievodin
 *
 */
public class TasksTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -4702828061603057203L;
	
	private final String[] columnHeaders = {
			"Execution Time",
			"Eexcution Target"
	};
	
	private TasksModel data;
	private List<Task> cache;
	
	public TasksTableModel(TasksModel model) {
		this.setTasksModel(model);
	}
	
	public void setTasksModel(TasksModel model) {
		this.data = model;
		if(model != null) {
			this.cache = model.asList(); // cache the data in useful for table format.
		}
	}
	
	public List<Task> getCache() {
		return cache;
	}
	
	public TasksModel getData() {
		return data;
	}

	@Override
	public String getColumnName(int column) {
		return this.columnHeaders[column];
	}

	@Override
	public int getColumnCount() {
		return columnHeaders.length;
	}

	@Override
	public int getRowCount() {
		return data != null ? this.data.getTasksAmount() : 0;
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
			case 0: return cache.get(row).getExecutionDate();
			case 1: return cache.get(row).getTarget();
			default: return null;
		}
	}
}
