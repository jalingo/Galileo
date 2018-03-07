package data;

import java.text.*;
import java.time.*;
import java.util.*;
import java.io.*;

public class Technician extends Employee {
	//instance fields
	private static XStreamer xstream = new XStreamer();
	private static File f = new File("./com.lingotechsolutions.data/technicians.xml");
	private static ArrayList<Technician> techs = new ArrayList<Technician>();
	
	private double hours = 0.0;
	private double expenses = 0.00;
	private double totalPay = 0.00;
	private double publicRate;
	private String skillSets = "unknown";
	private String branch = "OK0";
	public LocalTime[] start = new LocalTime[7];
	public LocalTime[] stop = new LocalTime[7];

	//constructors
	public Technician(String n, String e, long s, double p, String sk, String br) { 
		super(n, p);
		skillSets = sk;
		branch = br;
		setEmail(e);
		setSms(s); }
	public Technician(String n, String e, long sT, String sk, String br) { this(n, e, sT, -1L, sk, br); }
	public Technician(String n, long s, String br) { this(n, "unknown@email.com", s, "unknown", br); }
	public Technician(String n) { this(n, -1L, "null"); }
	public Technician() { this("empty"); }

	//get & set methods
	public void setHours(double h) { hours = h; }
	public void setTotalPay(double t) { totalPay = t; }
	public void setExpenses(double e) { expenses = e; }
	public void setPublicRate(double p) { publicRate = p; }
	public void setSkillSets(String s) { skillSets = s; }
	public void setBranch(String b) { branch = b; }
	public void setStarts(LocalTime[] start) { this.start = start; }
	public void setStart(LocalTime t, int e) { start[e] = t; }
	public void setStops(LocalTime[] stop) { this.stop = stop; }	
	public void setStop(LocalTime t, int e) { stop[e] = t; }
	
	public String getTotals() {
		DecimalFormat df = new DecimalFormat("#0.0");
		double totalHours = 0;
		int totalRequests = 0;
		int completedRequests = 0;

		for (ServiceRequest req : ServiceRequest.getServiceRequests()) {
			if (req.getTech().getName().equals(this.getName())) {
				totalRequests++;
				if (req.getStatusCycle() == ServiceRequest.CLOSED) {
					totalHours += req.getDuration();
					completedRequests++; }
			}
		}
		
		//prevents division by zero
		if (completedRequests == 0) { 
			completedRequests++; 
			totalRequests++; }

		return df.format(totalHours / completedRequests) + "hrs / " + df.format(completedRequests * 100 / totalRequests) + "% / " + totalRequests + "x"; }

	public String getStats(int index, boolean percentage, int element) {
		DecimalFormat df1 = new DecimalFormat("#0.0");
		DecimalFormat df2 = new DecimalFormat("#0.00");
		String aString = new String();
		int total = 0;
		int comp = 0;
		double hours = 0;
		
		if (index == 0) {		//if index == (stats by) AREA
			for (ServiceRequest req : ServiceRequest.getServiceRequests()) {
				if (req.getTech().getName().equals(getName()) && req.getStatusCycle() == ServiceRequest.CLOSED) {
					total++;
					if (req.getLocation().getArea() == element) {
						comp++;
						hours += req.getDuration(); }
				}
			}
		}
		else { 					//if index == (stats by) DAYS
			int pointer;
			if (element != 0) {
				pointer = element; }
			else {
				pointer = 7; }
			
			for (ServiceRequest req : ServiceRequest.getServiceRequests()) {
				if (req.getTech().getName().equals(getName()) && req.getStatusCycle() == ServiceRequest.CLOSED) {
					total++;
										
					if (req.getDate().getDayOfWeek().getValue() == pointer) {
						comp++;
						hours += req.getDuration(); }
				}
			}
		}
		
		if (percentage == true) {						//returns value as % or $
			if (total == 0) { total++; }
			aString = df1.format(comp * 100 / total) + "%"; }
		else {
			if (total == 0) { total++; }
			aString = "$" + df2.format(hours * 100 / comp); }
		
		return aString; }
		
	public LocalTime[] getStops() { return stop; }
	public LocalTime getStop(int e) { return stop[e]; }
	public LocalTime[] getStarts() { return start; }
	public LocalTime getStart(int e) { return start[e]; }
	public double getPublicRate() { return publicRate; }
	public String getSkillSets() { return skillSets; }
	public String getBranch() { return branch; }
	
	public String getSite(LocalDate date) {
		String site = "Information Unavailable/Not Set";
			for (Availability shift : Availability.getAvailabilities(date)) {
				if (shift.getTech().getName().equals(getName())) {
					site = shift.getComments() + " (" + shift.getLocation().getStreetAddress() + ", " + shift.getLocation().getCity() + ")."; }
			}
		return site; }
	
	//static methods
	public static double getFees(LocalDate d, Technician t) {
		double fees = 0;
		
		ArrayList<ServiceRequest> queue = ServiceRequest.getServiceRequests(d);
		for (ServiceRequest r : queue) {
			if (r.getTech().getName().equals(t.getName()) && r.getStatusCycle() == ServiceRequest.CLOSED) {
				fees = fees + (r.getDuration() * 100); }
		}
		return fees; }
	
	public static ArrayList<Technician> getTechs() { 
		readData();
		return techs; }
	
	//IO methods
	public static void initData() {
		techs.clear();
		techs.add(new Technician("NULL")); 
		writeData(); }
	
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			techs.clear();
			while(complete != true) {	
				Technician test = (Technician)in.readObject(); 		
				if(test != null) { techs.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(techs.isEmpty()) { initData(); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(Technician t : techs) { out.writeObject(t); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public static void removeData(Technician tech) {
		readData();
		ArrayList<Technician> targets = new ArrayList<Technician>();
		for (Technician t : techs ) {
			if (t.getName().equals(tech.getName())) { targets.add(t); }
		}
		techs.removeAll(targets); 
		writeData(); }

	public static void addData(Technician tech) {
		removeData(tech);	//removes any pre-existing versions of tech
		readData();			//downloads techs<> from xml
		techs.add(tech); 	//adds new version of tech
		writeData(); }		//uploads techs<> to xml

	public static boolean checkTech(String s) { 
		boolean answer = false;
		readData();
		for (Technician t : techs) {
			if (s.length() > 5) {
				if (t.getName().equals(s) || t.getName().startsWith(s.substring(0, 6))) { answer = true; }
			} else {
				if (t.getName().equals(s) || t.getName().startsWith(s.substring(0, s.length()))) { answer = true; }
			}
		}
		return answer; }

	public static Technician getTech(String s) {
		Technician oldTech = new Technician(s);
		readData();
		for (Technician t : techs) { 
			if (t.getName().equals(s) || t.getName().startsWith(s.substring(0, 3))) { oldTech = t; }
		}
		return oldTech; }
	
	//calculate an operator's wages
	public double wagePerPeriod(LocalDate a, LocalDate b) { 
		hours = a.compareTo(b);
		totalPay = hours - expenses;
		return totalPay; }

}
