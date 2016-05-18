package def.taskmodel.source;

import static def.utils.Preferences.SOURCE_MODEL_FILNAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import def.taskmodel.TasksModel;

public class FileModelProvider extends TasksModelProvider {

	@Override
	public void load() {
		File modelFile = new File(SOURCE_MODEL_FILNAME);
		if(!modelFile.exists()) {
			model = new TasksModel(); // we have nothing to load :(
		} else {
			try {
				FileInputStream inFileStream;
				inFileStream = new FileInputStream(modelFile);
				ObjectInputStream inObjStream = new ObjectInputStream(inFileStream);
				model = (TasksModel) inObjStream.readObject();
				inObjStream.close();
				inFileStream.close();
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("Tasks Model loading error: " + e.getMessage());
			}	
		}
	}

	@Override
	public void save() {
		File modelFile = new File(SOURCE_MODEL_FILNAME);
		try {
			if(!modelFile.exists()) {
				modelFile.createNewFile();
			}
			FileOutputStream outFileStream = new FileOutputStream(modelFile);
			ObjectOutputStream outObjStream = new ObjectOutputStream(outFileStream);
			outObjStream.writeObject(model);
			outObjStream.close();
			outFileStream.close();
			System.out.println("Tasks Model successfully saved!");
		} catch (IOException e) {
			System.err.println("Tasks Model saving error: " + e.getMessage());
		}
	}
}
