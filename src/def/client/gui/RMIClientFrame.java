package def.client.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import def.client.TaskSchedulerClient;
import def.taskmodel.Task;
import def.taskmodel.TasksModel;
import def.taskmodel.TasksTableModel;
import def.utils.Preferences;

public class RMIClientFrame extends JFrame {

	private static final long serialVersionUID = -8708377586485930367L;
	private JButton btnAdd;
	private JButton btnRemove;
	private JButton btnLoad;
	private JLabel lblAppName;
	private JTable tblTasksTable;
	private JScrollPane scpTasksTableScrollPane;
	private JPanel pnlContentPane;
	private GridBagConstraints constraints;
	
	private TaskSchedulerClient schedulerClient;
	private TasksTableModel tasksTableModel;
	
	public RMIClientFrame(TaskSchedulerClient client) {
		schedulerClient = client;
		tasksTableModel = new TasksTableModel(null);
		pnlContentPane = new JPanel(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 5, 5, 5);
		
		//Label
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		lblAppName = new JLabel(Preferences.GUI_CLIENT_FRAME_TITLE);
		pnlContentPane.add(lblAppName, constraints);
		
		//Table
		constraints.ipady = 80;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weighty = 1;
		tblTasksTable = new JTable(tasksTableModel);
		tblTasksTable.setFillsViewportHeight(true);
		scpTasksTableScrollPane = new JScrollPane(tblTasksTable);
		pnlContentPane.add(scpTasksTableScrollPane, constraints);
		
		//Buttons		
		constraints.gridwidth = 1;
		constraints.ipady = 0;
		constraints.weighty = 0.0;
		constraints.gridx = 0;
		constraints.gridy = 2;
		btnAdd = new JButton("Add");
		pnlContentPane.add(btnAdd, constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 2;	
		btnRemove = new JButton("Remove");
		pnlContentPane.add(btnRemove, constraints);
		
		constraints.gridx = 2;
		constraints.gridy = 2;
		btnLoad = new JButton("Load");
		pnlContentPane.add(btnLoad, constraints);
		
		//Frame essentials
		setContentPane(pnlContentPane);
		setTitle(Preferences.GUI_CLIENT_FRAME_TITLE);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		addActionListeners();
	}
	
	/**
	 * Update tasks table model using the specified new model and update table UI.
	 * @param newModel model, which is going to update old one.
	 * @author ovoievodin
	 */
	public void refreshTasksTableModel(TasksModel newModel) {
		tasksTableModel.setTasksModel(newModel);
		tasksTableModel.fireTableDataChanged();
	}
	
	/**
	 * Handling all of the user input.
	 * @author ovoievodin
	 */
	private void addActionListeners() {
		final RMIClientTaskCreationDialog taskCreationDialog = new RMIClientTaskCreationDialog(this);
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				taskCreationDialog.setVisible(true);
				if(taskCreationDialog.isConfirmed()) {
					schedulerClient.addTask(taskCreationDialog.getExecutionTime(), taskCreationDialog.getExecutionTarget());
					refreshTasksTableModel(schedulerClient.loadTasks());	
				}
			}
		});
		
		btnLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTasksTableModel(schedulerClient.loadTasks());
			}
		});
		
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int index = tblTasksTable.getSelectedRow();
				if(index >= 0 && index < tasksTableModel.getCache().size()){
					final Task task = tasksTableModel.getCache().get(index);
					schedulerClient.removeTask(task);
				}
			}
		});
	}

	/**
	 * Just show an error dialogue for the user with the specified message.
	 * @param message error message to display.
	 * @author ovoievodin
	 */
	public void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(this, message, "Erorr occurred", JOptionPane.ERROR_MESSAGE);		
	}

	/**
	 * Switch look and feel of the client frame to the specified LAF class name, if it is possible.
	 * @param lafClassName target look and feel class name.
	 */
	public void switchLookAndFeel(final String lafClassName) {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if (lafClassName.equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            return;
		        }
		    }
		    System.err.println("Supplied UI look and feel class name is incorrect or not installed.");
		} catch (Exception e) {
		    System.err.println("Supplied UI look and feel is not supported on your system.");
		}
	}
}