package interfaceComponents;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.time.*;

import data.*;
import controls.*;

public class PayEntry extends JPanel {
	private static final long serialVersionUID = 7175381477891127345L;
	private static final int ADMIN = 0;
	@SuppressWarnings("unused")
	private static final int OPRTR = 1;
	private Operator targetEmployee;
	private JTextField payRate = new JTextField(5);
	private LocalDate start;
	private LocalDate stop;
	private int status;
	
	public PayEntry(Operator o, LocalDate d0, LocalDate d1, int priv) {
		targetEmployee = o;
		start = d0;
		stop = d1;
		status= priv;

		targetEmployee.setHoursAndBonuses(start, stop);
		setBackground(Color.MAGENTA);
		
		add(new JLabel(targetEmployee.getName() + ":\t(", JLabel.LEFT));
		
		JButton hours = new JButton(String.valueOf(targetEmployee.getHours()));
			hours.setToolTipText("Unverified Hours");
		add(hours);
		
		add(new JLabel(" x $"));

		payRate.setText(String.valueOf(targetEmployee.getPayRate()));
			payRate.setToolTipText("Pay Rate");
		add(payRate);

		add(new JLabel(") + "));

		JButton bonus = new JButton(String.valueOf(targetEmployee.getBonuses()));
			bonus.setToolTipText("Commission");
		add(bonus);
	
		//calculate totalPay
		add(new JLabel(" = " + targetEmployee.getTotalPay())); 

		//allow editing for admins
		if (status == ADMIN) {
			hours.addActionListener(new ScheduleSetter(targetEmployee, start, stop));
			payRate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					targetEmployee.setPayRate(Double.valueOf(payRate.getText().trim())); 
					targetEmployee.setHoursAndBonuses(start, stop); }
			});
		}
	} 
	
	public PayEntry(Operator o, LocalDate d0, LocalDate d1) {
		this(o, d0, d1, ADMIN);	}
}
