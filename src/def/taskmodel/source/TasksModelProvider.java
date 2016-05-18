package def.taskmodel.source;

import def.taskmodel.TasksModel;

public abstract class TasksModelProvider {
	
	protected TasksModel model;
	
	public abstract void load();
	public abstract void save();
	
	public TasksModel getModel() {
		return model;
	}
}
