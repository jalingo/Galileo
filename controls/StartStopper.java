package controls;

import java.awt.event.*;
import java.time.*;

import javax.swing.*;

import data.*;

public class StartStopper extends AbstractAction {
	private static final long serialVersionUID = -7478414612510199740L;
	private LocalTime[] times = new LocalTime[7];
	private LocalTime target0;
	private LocalTime target1;
	private Technician t;
	private int e;
	
	public StartStopper(Technician tech, int element, LocalTime t0, LocalTime t1) {
		t = tech;
		e = element; 
		target0 = t0;
		target1 = t1; }
	
	@Override
	public void actionPerformed(ActionEvent event) {
		times = t.getStarts();
			times[e] = target0;
		t.setStarts(times);

		times = t.getStops();
			times[e] = target1;
		t.setStops(times); 
		
		Technician.addData(t); }
}
