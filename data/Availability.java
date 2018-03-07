package data;

import java.io.*;
import java.time.*;
import java.util.*;

public class Availability extends ScheduleEvent implements Comparable<Availability>{
	//instance variables
	private static File f = new File("./com.lingotechsolutions.data/schedule.xml");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<Availability> schedule = new ArrayList<Availability>();
	
	private Technician tech = new Technician();
	private String comments = new String("No Comments");
	
	//constructors
	public Availability(Technician t, LocalDate d, Site s) { 
		super(s, LocalDateTime.of(d, LocalTime.now()), LocalDateTime.of(d, LocalTime.now())); 
		tech = t; }
	public Availability(Technician t) { this(t, LocalDate.now(), new Site()); }
	public Availability() { this(new Technician());	}
	
	//get/set methods
	public Technician getTech() { return tech; }
	public String getComments() { return comments; }
	public void setTech(Technician t) { tech = t; }
	public void setComments(String s) { comments = s; }
		
	//IO methods
	public static ArrayList<Availability> getAvailabilities() {
		readData();
		return schedule; }
	
	public static ArrayList<Availability> getAvailabilities(LocalDate d) {
		ArrayList<Availability> currentSchedule = new ArrayList<Availability>();
		readData();
		Collections.sort(schedule);
		for (Availability a : schedule) {
			if (a.getDate().equals(d)) { currentSchedule.add(a); }
		}
		return currentSchedule; }
	
	public static void initData() {
		schedule.clear();
		for (int i = 0; i < 5; i++) { schedule.add(new Availability(new Technician(), LocalDate.MIN, new Site())); }
		writeData(); }
	
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			schedule.clear();
			while(complete != true) {	
				Availability test = (Availability)in.readObject(); 		
				if(test != null && test.getDate().isAfter(LocalDate.now().minusDays(180))) { 
					schedule.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(schedule.isEmpty()) { schedule.add(new Availability()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(Availability b : schedule) { out.writeObject(b); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public static void removeData(Availability sched) {
		readData();
		ArrayList<Availability> targets = new ArrayList<Availability>();
		
		for (Availability b : schedule ) {
			//b.getTech() + " " + sched.getTech());
			if (b.getTech().getName().equals(sched.getTech().getName()) && b.getDate().equals(sched.getDate())) { 
				//System.out.println(b + " selected for deletion.");
				targets.add(b); }
		}
		if (targets.isEmpty()) { System.out.println("targets is empty."); }
		schedule.removeAll(targets);
		
		writeData(); }
	
	public static void addData(Availability sched) {
		readData();
		removeData(sched);
		schedule.add(sched); 
		writeData(); 
		
		//pair new Availability with WorkShift (if no more than one month old)
		if(sched.getDate().isAfter(LocalDate.now().minusMonths(1))) {
				WorkShift.addData(new WorkShift(sched.getTech(), sched.getDate(), sched.getLocation().getArea(), WorkShift.UNKNOWN)); }
	}

	@Override
	public int compareTo(Availability a) {
		int area = a.getLocation().getArea();
		return a.getLocation().getArea() - area; }
}


