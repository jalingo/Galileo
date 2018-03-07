package controls;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import data.*;
import interfaceComponents.*;

public class WorkShifter extends AbstractAction {
	private static final long serialVersionUID = -7567506294116639492L;
	private WorkShift shift;
	
	public WorkShifter(WorkShift w) {
		shift = w;
	}
	
	//methods
	public JPanel messageBuilder() {
		JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			
			//builds message based on type
			if (shift.getType() == WorkShift.SERVICES) {
				JPanel header = new JPanel();
					final TechCombo tech = new TechCombo();
						tech.setSelectedItem(shift.getTechnician()); 
						tech.setEditable(true);
						tech.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								shift.setTechnician(Technician.getTech(tech.getSelectedItem().toString())); }
						});
					header.add(tech); 
				panel.add(header, BorderLayout.NORTH); 	
				
				JPanel body = new JPanel();
					final JComboBox<String> combo = new JComboBox<String>();
						combo.addItem("Potential");
						combo.addItem("Private");
						combo.addItem("Public");
						combo.setSelectedIndex(shift.getPrivacy());
						combo.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								shift.setPrivacy(combo.getSelectedIndex()); }
						});
					body.add(combo);
					final AreaCombo area = new AreaCombo();
						area.setSelectedIndex(shift.getArea());
						area.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								shift.setArea(area.getSelectedIndex()); }
						});
					body.add(area); 		
				panel.add(body, BorderLayout.CENTER); }
			else {
				JPanel header = new JPanel();
					final JComboBox<String> combo = new JComboBox<String>();
						for (Operator opr : Operator.getOperators()) {
							combo.addItem(opr.getName()); }
						combo.setSelectedItem(shift.getOperator()); 
						combo.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								for (Operator opr : Operator.getOperators()) {
									if (opr.getName().equals(combo.getSelectedItem().toString())) {
										shift.setOperator(opr); }
								}
							}
						});						
					header.add(combo); 
				panel.add(header, BorderLayout.NORTH);
				
				JPanel body = new JPanel();
					final JComboBox<String> shifts = new JComboBox<String>();
						shifts.addItem("Early Shift");
						shifts.addItem("Middle Shift");
						shifts.addItem("Late Shift");
						shifts.setSelectedIndex(shift.getShiftType());
						shifts.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent event) {
								shift.setShiftType(shifts.getSelectedIndex()); }
						});
					body.add(shifts);
				panel.add(body, BorderLayout.CENTER); }
			
		return panel; }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int response = JOptionPane.showConfirmDialog(null, messageBuilder(), "Schedule Entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (response == JOptionPane.OK_OPTION) {
			WorkShift.addData(shift); }
	}
}
