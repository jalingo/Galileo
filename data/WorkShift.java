package data;

import java.io.*;
import java.time.*;
import java.util.*;

public class WorkShift {
	//instance variables
	private static File f = new File("./com.lingotechsolutions.data/shifts.xml");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<WorkShift> shiftQueue = new ArrayList<WorkShift>();

	public static int OPERATIONS = 0;
	public static int SERVICES = 1;

	public static int UNKNOWN = -1;
	public static int POTENTIAL = 0;
	public static int PRIVATE = 1;
	public static int PUBLIC = 2;

	public static int EARLYSHIFT = POTENTIAL;
	public static int MIDSHIFT = PRIVATE;
	public static int LATESHIFT = PUBLIC;
	
	private int type;
	private int privacy;
	private int area;
	private Technician tch;
	private Operator opr;
	private Branch br;
	private LocalDate date;

	public WorkShift(Operator o, LocalDate d, int a, int shift) {
		type = OPERATIONS;
		date = d;
		opr = o;
		area = a;
		privacy = shift; }

	public WorkShift(Technician t, LocalDate d, int a, int p) {
		type = SERVICES;
		date = d;
		tch = t;
		area = a;
		privacy = p; }

	public WorkShift() {
		this(new Technician(), LocalDate.MIN, Site.SF, WorkShift.POTENTIAL); }
	
	//methods
	
	//getter/setter methods
	public Operator 	getOperator() 	{ return opr; }
	public Technician	getTechnician() { return tch; }
	public LocalDate 	getDate() 		{ return date; }
	public Branch 		getBranch() 	{ return br; }
	public int 			getType() 		{ return type; }
	public int 			getPrivacy() 	{ return privacy; }
	public int			getShiftType()	{ return getPrivacy(); }
	public int 			getArea() 		{ return area; }

	public void setArea(int a) 				{ area = a; }
	public void setPrivacy(int p) 			{ privacy = p; }
	public void setShiftType(int s)			{ setPrivacy(s); }
	public void setType(int t) 				{ type = t; }
	public void setBranch(Branch b) 		{ br = b; }
	public void setDate(LocalDate d) 		{ date = d; }
	public void setTechnician(Technician t) { tch = t; }
	public void setOperator(Operator o) 	{ opr = o; }
	
	public static ArrayList<WorkShift> getShifts() {
		readData();
		return shiftQueue; }

	public static ArrayList<WorkShift> getShifts(int t) {
		ArrayList<WorkShift> currentSchedule = new ArrayList<WorkShift>();
		readData();
		for (WorkShift w : shiftQueue) {
			if (w.getType() == t) { currentSchedule.add(w); }
		}
		return currentSchedule; }
	
	public static ArrayList<WorkShift> getShifts(LocalDate d, int t) {
		ArrayList<WorkShift> currentSchedule = new ArrayList<WorkShift>();
		readData();
		for (WorkShift w : getShifts(t)) {
			if (w.getDate().equals(d)) { currentSchedule.add(w); }
		}
		return currentSchedule; }
	
	//IO methods	
	public static void initData() {
		shiftQueue.clear();
		for (int i = 0; i < 3; i++) { shiftQueue.add(new WorkShift()); }
		writeData(); }
	
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			shiftQueue.clear();
			while(complete != true) {	
				WorkShift test = (WorkShift)in.readObject();
				if(test != null && test.getDate().isAfter(LocalDate.now().minusDays(180))) { 
					shiftQueue.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
		
		//pair WorkShifts with Availabilities (availabilities 3 weeks back and 3 months forward only only)
/*		for (long counter = LocalDate.now().minusDays(21).toEpochDay(); counter < LocalDate.now().plusDays(90).toEpochDay(); counter++) {
			for (Availability a : Availability.getAvailabilities()) {
				WorkShift rePlacer = new WorkShift(a.getTech(), a.getDate(), WorkShift.SERVICES, WorkShift.UNKNOWN); 
				WorkShift.addData(rePlacer); }
		}*/
	}
	public static void writeData() {
		if(shiftQueue.isEmpty()) { shiftQueue.add(new WorkShift()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(WorkShift w : shiftQueue) { out.writeObject(w); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
		
		//pair WorkShifts with Availabilites
/*		for (WorkShift w : WorkShift.getShifts()) {
			if (w.getType() == WorkShift.SERVICES && w.getPrivacy() != WorkShift.POTENTIAL) {
				Availability rePlacer = new Availability(w.getTechnician(), w.getDate(), new Site(w.getArea())); 
				Availability.addData(rePlacer); }
		}*/
	}
	public static void removeData(WorkShift sched) {
		readData();
		ArrayList<WorkShift> targets = new ArrayList<WorkShift>();
		
		for (WorkShift w : shiftQueue ) {
			if (w.getType() == WorkShift.OPERATIONS) {
				if (w.getDate().equals(sched.getDate()) && w.getOperator().equals(sched.getOperator())) { 
					targets.add(w); }	
			}
			else if (w.getType() == WorkShift.SERVICES) {
				if (w.getDate().equals(sched.getDate()) && w.getTechnician().equals(sched.getTechnician())) { 
					targets.add(w); }
			}
		}
		shiftQueue.removeAll(targets);	

		//need to remove matching availabilities for targets of type SERVICES here
		
		writeData(); }
	
	public static void addData(WorkShift sched) {
		readData();
		
		//When pairing with availabilities, privacy needs to be recovered
		if (sched.getType() == WorkShift.SERVICES && sched.getPrivacy() == WorkShift.UNKNOWN) {
			for (WorkShift w : WorkShift.getShifts(WorkShift.SERVICES)) {
				if (w.getDate().equals(sched.getDate()) && w.getTechnician().equals(sched.getTechnician()) && w.getArea() == sched.getArea()) {
					sched.setPrivacy(w.getPrivacy()); }
			}
		}
		
		removeData(sched); 
		shiftQueue.add(sched); 
		writeData(); }
}
