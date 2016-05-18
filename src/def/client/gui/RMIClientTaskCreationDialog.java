package def.client.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import def.utils.DateUtils;

public final class RMIClientTaskCreationDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 7058906470258941358L;
	
	private JButton btnOk;
	private JButton btnCancel;
	private JTextField tfExecTime;
	private JTextField tfExecTarget;
	private JPanel contentPane;
	private JLabel lblExecTime;
	private JLabel lblExecTarget;
	
	private Date executionTime;
	private String executionTarget;
	private boolean isConfirmed;
	
	private GridBagConstraints constraints;
	
	private static final String title = "Remote Task Creation";
	
	public RMIClientTaskCreationDialog(JFrame parent) {
		super(parent, true); // modal by default.
		
		this.loadDefaultValues();
		
		this.constraints = new GridBagConstraints();
		this.contentPane = new JPanel(new GridBagLayout());
		this.btnOk = new JButton("OK");
		this.btnCancel = new JButton("Cancel");
		this.tfExecTime = new JTextField();
		this.tfExecTarget = new JTextField();
		this.lblExecTime = new JLabel("Execution time(hh:MM):");
		this.lblExecTarget = new JLabel("Execution target:");
		
		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.5;
		constraints.weighty = 0.0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		this.contentPane.add(lblExecTime, constraints);
		
		constraints.gridx = 1;
		this.contentPane.add(tfExecTime, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		this.contentPane.add(lblExecTarget, constraints);
		
		constraints.gridx = 1;
		this.contentPane.add(tfExecTarget, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		this.contentPane.add(btnOk, constraints);
		
		constraints.gridx = 1;
		this.contentPane.add(btnCancel, constraints);
		
		this.add(contentPane);
		this.setTitle(title);
		this.pack();
		this.setSize(400, getHeight());
		this.setResizable(false);
		this.setLocationRelativeTo(parent);
	}
	
	private void loadDefaultValues() {
		this.executionTime = DateUtils.parseTime("00:00");
		this.executionTarget = "NoTarget";
	}
	
	public Date getExecutionTime() {
		return executionTime;
	}

	public String getExecutionTarget() {
		return executionTarget;
	}

	public boolean isConfirmed() {
		return isConfirmed;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(btnOk)) {
			final Date parsedTime = DateUtils.parseTime(this.tfExecTime.getText());
			if (parsedTime == null) {
				JOptionPane.showMessageDialog(
						contentPane, 
						"Incorrect time entered. Please enter time in format 'HH:mm'", 
						"Error", 
						JOptionPane.ERROR_MESSAGE
				);
				loadDefaultValues();
				this.isConfirmed = false;
				return;
			} else {
				this.executionTime = parsedTime;
				this.executionTarget = this.tfExecTarget.getText();
				this.isConfirmed = true;
			}
		} else {
			loadDefaultValues();
		}
		setVisible(false);
	}
}
