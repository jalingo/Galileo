package controls;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import data.*;

public class InitData extends AbstractAction {
	private static final long serialVersionUID = -3462720814986084045L;
	private JCheckBox clientBox = new JCheckBox("DELETE all Clients!");
	private JCheckBox entryBox = new JCheckBox("DELETE all Service Entries!");
	private JCheckBox shiftBox = new JCheckBox("DELETE all Shifts");
	private JCheckBox techBox = new JCheckBox("DELETE all Technicians!");
	private JCheckBox operBox = new JCheckBox("DELETE all Operators!");
	private JCheckBox schedBox = new JCheckBox("DELETE Availability Schedule!");
	private JCheckBox sideBox = new JCheckBox("DELETE all SideBar data!");
	private JCheckBox gBox = new JCheckBox("DELETE all Google data");
	private JCheckBox allBox = new JCheckBox("DELETE ALL DATA!");
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JPanel message = new JPanel();
		message.setLayout(new GridLayout(0, 1));
		
		allBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (allBox.isSelected()) {
					clientBox.setSelected(true);
					entryBox.setSelected(true);
					shiftBox.setSelected(true);
					techBox.setSelected(true);
					operBox.setSelected(true);
					schedBox.setSelected(true);
					sideBox.setSelected(true);
					gBox.setSelected(true);}
				else {
					clientBox.setSelected(false);
					entryBox.setSelected(false);
					techBox.setSelected(false);
					shiftBox.setSelected(false);
					operBox.setSelected(false);
					schedBox.setSelected(false);
					sideBox.setSelected(false); 
					gBox.setSelected(false); }
			}
		});
		
		message.add(clientBox);
		message.add(entryBox);
		message.add(shiftBox);
		message.add(techBox);
		message.add(operBox);
		message.add(schedBox);
		message.add(sideBox);
		message.add(gBox);
		message.add(new JLabel(" "));
		message.add(allBox);

		int response = JOptionPane.showConfirmDialog(null, message, "Initialize Data", JOptionPane.OK_CANCEL_OPTION);
		if (response == JOptionPane.OK_OPTION) {
			String confirmation = JOptionPane.showInputDialog("This action CANNOT BE UNDONE and will result in the LOSS OF DATA (including but not limited to scheduled events, fiscal transactions, and personnel dossiers)!\n\nPlease type \"CONFIRM\" to continue...");
			if (confirmation.equalsIgnoreCase("CONFIRM")) {
				if (clientBox.isSelected()) { Client.initData(); }
				if (entryBox.isSelected()) { ServiceRequest.initData(); }
				if (shiftBox.isSelected()) { WorkShift.initData(); }
				if (techBox.isSelected()) { Technician.initData(); }
				if (operBox.isSelected()) { Operator.initData(); }
				if (schedBox.isSelected()) { Availability.initData(); }
				if (sideBox.isSelected()) { 
					Account.initData(); 
					Checklist.initData();
					Link.initData(); }
				if (gBox.isSelected()) {
					gAccount.initData(); }
			}
		}
	}
}
