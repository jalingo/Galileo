package controls;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;

import data.*;

import javax.swing.*;

public class TimeSelector extends AbstractAction {
	private static final long serialVersionUID = -4366316839767513653L;
	public static final int START = 0;
	public static final int STOP = 1;
	
	private JFrame frame = new JFrame();
	private ServiceRequest sRtarget;
	private Operator oprTarget;
	private JComboBox<Integer> start_hours = new JComboBox<Integer>();
	private JComboBox<Integer> start_mins = new JComboBox<Integer>();
	private JComboBox<String> start_meridian = new JComboBox<String>();
	private JComboBox<Integer> stop_hours = new JComboBox<Integer>();
	private JComboBox<Integer> stop_mins = new JComboBox<Integer>();
	private JComboBox<String> stop_meridian = new JComboBox<String>();
	private JButton parent;
	private int type;
	private String dayOfWeek;

	public TimeSelector(ServiceRequest r, JButton p) {
		parent = p;
		sRtarget = r;
		type = 0; }

	public TimeSelector(Operator o, String day, JButton p) {
		parent = p;
		oprTarget = o; 
		type = 1; 
		dayOfWeek = day; }

/*	public TimeSelector(Technician t, int i, int x) {
		option = i;
		techTarget = t; 
		type = 2;
		day = x; }*/

	@Override
	public void actionPerformed(ActionEvent e) {
		JPanel header = new JPanel();
			header.add(new JLabel("Change from:", JLabel.LEFT));
		frame.add(header, BorderLayout.NORTH);
		
		start_mins.removeAllItems();
		stop_mins.removeAllItems();
		start_hours.removeAllItems();
		stop_hours.removeAllItems();
		start_meridian.removeAllItems();
		stop_meridian.removeAllItems();
		
		JPanel panel = new JPanel();
			start_mins.addItem(0);
			stop_mins.addItem(0);
			for (int i = 1; i < 13; i++) {
				stop_hours.addItem(i);
				stop_mins.addItem(i);
				start_hours.addItem(i);
				start_mins.addItem(i); }
			for (int i = 13; i < 60; i++) {
				start_mins.addItem(i); 		
				stop_mins.addItem(i); }		
			start_meridian.addItem("am");
			start_meridian.addItem("pm");
			stop_meridian.addItem("am");
			stop_meridian.addItem("pm");
			
			if (type == 0) {
				bUIlder(sRtarget.getStart().getHour(), sRtarget.getStart().getMinute(), sRtarget.getStop().getHour(), sRtarget.getStop().getMinute()); }
			else if (type == 1) {
				LocalDateTime[] begins = oprTarget.getStarts();
				LocalDateTime[] ends = oprTarget.getStops();
				
				int i = 0; //if Sunday, index remains 0
				if 		(dayOfWeek.equals(DayOfWeek.MONDAY))	{ i = 1; }
				else if (dayOfWeek.equals(DayOfWeek.TUESDAY)) 	{ i = 2; }
				else if (dayOfWeek.equals(DayOfWeek.WEDNESDAY)) { i = 3; }
				else if (dayOfWeek.equals(DayOfWeek.THURSDAY)) 	{ i = 4; }
				else if (dayOfWeek.equals(DayOfWeek.FRIDAY)) 	{ i = 5; }
				else if (dayOfWeek.equals(DayOfWeek.SATURDAY)) 	{ i = 6; }

				bUIlder(begins[i].getHour(), begins[i].getMinute(), ends[i].getHour(), ends[i].getMinute()); }
			
			panel.add(start_hours);
			panel.add(start_mins);
			panel.add(start_meridian);
			panel.add(new JLabel(" - "));
			panel.add(stop_hours);
			panel.add(stop_mins);
			panel.add(stop_meridian);
		frame.add(panel, BorderLayout.CENTER);		
		
		JPanel footer = new JPanel();
			JButton cancel = new JButton("Cancel");
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						frame.dispose(); }
				});
			footer.add(cancel);
			JButton save = new JButton("Save");
				save.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						int start_hrs = start_hours.getSelectedIndex() + 1;
						if (start_meridian.getSelectedIndex() == 1 && start_hrs != 12) { start_hrs = start_hrs + 12; }
						if (start_meridian.getSelectedIndex() != 1 && start_hrs == 12) { start_hrs = 0; }

						int stop_hrs = stop_hours.getSelectedIndex() + 1;
						if (stop_meridian.getSelectedIndex() == 1 && stop_hrs != 12) { stop_hrs = stop_hrs + 12; }
						if (stop_meridian.getSelectedIndex() != 1 && stop_hrs == 12) { stop_hrs = 0; }

						int start_m = start_mins.getSelectedIndex();
						int stop_m = stop_mins.getSelectedIndex();

						LocalDateTime newStart = LocalDateTime.of(sRtarget.getDate(), LocalTime.of(start_hrs, start_m));
						LocalDateTime newStop = LocalDateTime.of(sRtarget.getDate(), LocalTime.of(stop_hrs, stop_m));
						if (type == 0) {
							sRtarget.setStart(newStart);
							sRtarget.setStop(newStop);
							parent.setText(DateTimeFormatter.ofPattern("h:mma").format(sRtarget.getStart()) + " - " + DateTimeFormatter.ofPattern("h:mma").format(sRtarget.getStop())); 
							ServiceRequest.addData(sRtarget); }
						else if (type == 1) {
							oprTarget.setStart(newStart, dayOfWeek);
							oprTarget.setStop(newStop, dayOfWeek);
							parent.setText(dayOfWeek + ": " + newStart.toLocalTime().toString() + " - " + newStop.toLocalTime().toString());
							Operator.addData(oprTarget); }
						frame.dispose(); }
				});
			footer.add(save);
		frame.add(footer, BorderLayout.SOUTH);
		frame.setTitle("Select Times:");
		frame.pack();
		frame.setVisible(true); }	

	public void bUIlder(int hr0, int mn0, int hr1, int mn1) {
		if (hr0 != 0 && hr0 != 12) {
			if (hr0 < 12) {
				start_meridian.setSelectedItem("am");
				start_hours.setSelectedItem(hr0); }
			else {
				start_meridian.setSelectedItem("pm");
				start_hours.setSelectedItem(hr0 - 12); }
		}
		else {
			start_hours.setSelectedIndex(11);
			if (hr0 == 12) {
				start_meridian.setSelectedItem("pm"); }
			else {
				start_meridian.setSelectedItem("am"); }
		}
		start_mins.setSelectedItem(mn0);
		
		if (hr1 != 0 && hr1 != 12) {
			if (hr1 < 12) {
				stop_meridian.setSelectedItem("am");
				stop_hours.setSelectedItem(hr1); }
			else {
				stop_meridian.setSelectedItem("pm");
				stop_hours.setSelectedItem(hr1 - 12); }
		}
		else {
			stop_hours.setSelectedIndex(11);
			if (hr1 == 12) {
				stop_meridian.setSelectedItem("pm"); }
			else {
				stop_meridian.setSelectedItem("am"); }
		}
		stop_mins.setSelectedItem(mn1); }
}
