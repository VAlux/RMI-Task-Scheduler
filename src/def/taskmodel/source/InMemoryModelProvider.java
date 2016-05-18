package def.taskmodel.source;

import def.taskmodel.TasksModel;

public class InMemoryModelProvider extends TasksModelProvider {

	@Override
	public void load() {
		model = new TasksModel();
	}

	@Override
	public void save() { /*nothing to do here*/ }
}