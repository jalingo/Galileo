package controls;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.*;
import java.time.*;
import java.util.*;

import javax.swing.*;

import data.*;

public class ScheduleSetter extends AbstractAction {
	private static final long serialVersionUID = 1377008182282932546L;
	private static final List<String> DAYSOFWEEK = Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
	private JCheckBox[] box = new JCheckBox[7];
	private LocalTime[] start = new LocalTime[7];
	private LocalTime[] stop = new LocalTime[7];

	
	public ScheduleSetter(Operator o, LocalDate d0, LocalDate d1) {		//for specific operator week?
		
	}
	public ScheduleSetter(Technician t) {								//for tech availability 
		
	}
	public ScheduleSetter(Operator o) {									//for opr availability
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		BUIlder(); }

	private void BUIlder() {
		JFrame frame = new JFrame();
			frame.setLayout(new GridLayout(0, 1));
			frame.setBackground(Color.GRAY);
		
			int i = 0;
			for (String day : DAYSOFWEEK) {
				box[i] = new JCheckBox(day + ":");
				start[i] = LocalTime.of(10, 0);
				stop[i] = LocalTime.of(22, 0);

				JPanel panel = new JPanel();
					panel.add(box[i]);
					
					JButton startButton = new JButton(start[i].toString());
						startButton.setName("START:" + day);
						startButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
							
							}
						});
					panel.add(startButton);		
				
					JButton stopButton = new JButton(stop[i].toString());			
						stopButton.setName("STOP:" + day);
					panel.add(stopButton);
				frame.add(panel);
				i++; }
		
		frame.setVisible(true);
		frame.pack(); }
}
