package data;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.swing.JOptionPane;

public class Operator extends Employee {
	//instance variables
	private static File f = new File("./com.lingotechsolutions.data/operators.xml");
	private static File l = new File("./com.lingotechsolutions.data/log");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<Operator> operators = new ArrayList<Operator>();

	private int shifts = 0;
	private double hours = 0.0;
	private double totalPay = 0.00;
	private double bonuses = 0.00;
	private String userName = "null";
	private String passPhrase;
	private String accessPriv = "unauthorized";
	private LocalDateTime[] start = new LocalDateTime[7];
	private LocalDateTime[] stop = new LocalDateTime[7];
	private ArrayList<Branch> branches = new ArrayList<Branch>();
	
	//constructors
	public Operator(String n, String e, long s, double p) { 
		super(n, p); 
		this.setEmail(e);
		this.setSms(s); }
	public Operator(String n, String e, long s) { this(n, e, s, 0.00); }
	public Operator(String n) { this(n, "unknown@email.com", -1); }
	public Operator() { this("empty"); }
	
	//get & set methods
	public String getUserName() { return userName; }
	public String getPassPhrase() { return passPhrase; }
	public String getAccessPrivs() { return accessPriv; }
	public double getTotalPay() { return totalPay; }
	public double getHours() { return hours; }
	public double getBonuses() { return bonuses; }
	public double getHoursPerShift(LocalDate d0, LocalDate d1) {
		setHoursAndBonuses(d0, d1);
		double response = getHours() / shifts;
		return response; }
	
	public double getConfirmRate(LocalDate d0, LocalDate d1) {
		double requests = 0;
		double confirms = 0;
		double response = 0;
		
		if (d0.isBefore(d1)) {
			for (int i = 0; i < ChronoUnit.DAYS.between(d0, d1); i++) {
				ArrayList<ServiceRequest> queue = ServiceRequest.getServiceRequests(d0.plusDays(i));
				for (ServiceRequest s : queue) {
					if (s.getInitiator().getName().equals(this.getName())) { 
						requests++; }
					if (s.getConfirmer().getName().equals(this.getName())) { 
						confirms++; }
				}
			}
		}
		else {
			for (int i = 0; i < ChronoUnit.DAYS.between(d1, d0); i++) {
				ArrayList<ServiceRequest> queue = ServiceRequest.getServiceRequests(d1.plusDays(i));
				for (ServiceRequest s : queue) {
					if (s.getInitiator().getName().equals(this.getName())) { 
						requests++; }
					if (s.getConfirmer().getName().equals(this.getName())) { 
						confirms++; }
				}
			}
		}
		
		if (requests != 0) {
			response = confirms / requests; }
		return response * 100; }
	
	public double getCancelRate(LocalDate d0, LocalDate d1) {
		double confirms = 0;
		double cancels = 0;
		double response = 0;
		
		if (d0.isBefore(d1)) {
			for (int i = 0; i < ChronoUnit.DAYS.between(d0, d1); i++) {
				ArrayList<ServiceRequest> queue = ServiceRequest.getServiceRequests(d0.plusDays(i));
				for (ServiceRequest s : queue) {
					if (s.getConfirmer().getName().equals(this.getName())) { 
						confirms++; }
					if (s.getConfirmer().getName().equals(this.getName()) && s.getStatusCycle() == ServiceRequest.CANCEL) { 
						cancels++; }
				}
			}
		}
		else {
			for (int i = 0; i < ChronoUnit.DAYS.between(d1, d0); i++) {
				ArrayList<ServiceRequest> queue = ServiceRequest.getServiceRequests(d1.plusDays(i));
				for (ServiceRequest s : queue) {
					if (s.getConfirmer().getName().equals(this.getName())) { 
						confirms++; }
					if (s.getConfirmer().getName().equals(this.getName()) && s.getStatusCycle() == ServiceRequest.CANCEL) { 
						cancels++; }
				}
			}
		}

		if (confirms != 0) { response = cancels / confirms; }
		return response * 100; }
	public LocalDateTime[] getStarts() { return start; }
	public LocalDateTime[] getStops() { return stop; }
	public ArrayList<Branch> getBranches() { return branches; }
	
	public void setBranches(ArrayList<Branch> b) { branches  = b; }
	public void setStarts(LocalDateTime[] l) { start = l; }
	public void setStart(LocalDateTime t, String dayOfWeek) { 
		int i = 0; //if Sunday, index remains 0
		if 		(dayOfWeek.equals(DayOfWeek.MONDAY))	{ i = 1; }
		else if (dayOfWeek.equals(DayOfWeek.TUESDAY)) 	{ i = 2; }
		else if (dayOfWeek.equals(DayOfWeek.WEDNESDAY)) { i = 3; }
		else if (dayOfWeek.equals(DayOfWeek.THURSDAY)) 	{ i = 4; }
		else if (dayOfWeek.equals(DayOfWeek.FRIDAY)) 	{ i = 5; }
		else if (dayOfWeek.equals(DayOfWeek.SATURDAY)) 	{ i = 6; }		
		start[i] = t; }
	
	public void setStops(LocalDateTime[] l) { stop = l; }
	public void setStop(LocalDateTime t, String dayOfWeek) { 
		int i = 0; //if Sunday, index remains 0
		if 		(dayOfWeek.equals(DayOfWeek.MONDAY))	{ i = 1; }
		else if (dayOfWeek.equals(DayOfWeek.TUESDAY)) 	{ i = 2; }
		else if (dayOfWeek.equals(DayOfWeek.WEDNESDAY)) { i = 3; }
		else if (dayOfWeek.equals(DayOfWeek.THURSDAY)) 	{ i = 4; }
		else if (dayOfWeek.equals(DayOfWeek.FRIDAY)) 	{ i = 5; }
		else if (dayOfWeek.equals(DayOfWeek.SATURDAY)) 	{ i = 6; }		
		stop[i] = t; }
	
	public void setHours(double h) { hours = h; }
	public void setHoursAndBonuses(LocalDate d0, LocalDate d1) {
//		ArrayList<ServiceRequest> queue;
		hours = 0;
		bonuses = 0;
		shifts = 0;

		final String DELIM = "__";
		FileReader reader;
		String[] field = new String[3];
		List<String> lines;

		try {
//System.out.print(".");
			reader = new FileReader(l);
			lines = Files.readAllLines(Paths.get(l.getAbsolutePath()), StandardCharsets.US_ASCII);
			reader.close(); 
//System.out.print(".");
			int difference = Math.abs(d0.compareTo(d1));
			for (int i = 0; i < difference; i++) {
				LocalTime start = LocalTime.of(23, 59, 59, 0);
				LocalTime stop = LocalTime.of(0, 0, 0, 0);
//System.out.print("*.");
				if (d0.isBefore(d1)) {
					for (String line : lines) {
						for (int x = 0; x < 3; x++) { field[x] = Arrays.asList(line.split(DELIM, 3)).get(x); }
						if (field[0].equalsIgnoreCase(getName()) && field[1].substring(0, 10).equals(d0.plusDays(i).toString())) {
							LocalTime target = LocalTime.parse(field[1].subSequence(11, 16));
							if (field[2].equalsIgnoreCase("IN")) {
								if (start.isAfter(target)) {
									start = target; }
							}
							else if (field[2].equalsIgnoreCase("OUT")) {
								if (stop.isBefore(target)) {
									stop = target; }
							}
//System.out.print(".");
						}
					}
//System.out.print(".*");
//					ArrayList<ServiceRequest> queue = ServiceRequest.getServiceRequests(d0.plusDays(i));
//					queue = ServiceRequest.getServiceRequests(d0.plusDays(i));
//System.out.print(".!");
//					for (ServiceRequest s : queue) {
					for (ServiceRequest s : ServiceRequest.getServiceRequests(d0.plusDays(i))) {
//System.out.print("x");
						if (s.getConfirmer().getName().equals(getName())) {
//System.out.print("X");
							bonuses = bonuses + (s.getDuration() * 5); }
//System.out.print("x");
						if (s.getCloser().getName().equals(getName())) {
							bonuses = bonuses + (s.getDuration() * 5); }						
					}
				}
				else {
					for (String line : lines) {
						for (int x = 0; x < 3; x++) { field[x] = Arrays.asList(line.split(DELIM, 3)).get(x); }
						if (field[0].equalsIgnoreCase(getName()) && field[1].substring(0, 10).equals(d1.plusDays(i).toString())) {
							LocalTime target = LocalTime.parse(field[1].subSequence(11, 16));
							if (field[2].equalsIgnoreCase("IN")) {
								if (start.isAfter(target)) {
									start = target; }
							}
							else if (field[2].equalsIgnoreCase("OUT")) {
								if (stop.isBefore(target)) {
									stop = target; }
							}
						}
					}
//System.out.print(".*");
//					ArrayList<ServiceRequest> queue = ServiceRequest.getServiceRequests(d1.plusDays(i));
//					queue = ServiceRequest.getServiceRequests(d1.plusDays(i));
//System.out.print(".!");
//					for (ServiceRequest s : queue) {
					for (ServiceRequest s : ServiceRequest.getServiceRequests(d1.plusDays(i))) {
//System.out.print("x");
						if (s.getConfirmer().getName().equals(getName())) {
							bonuses = bonuses + (s.getDuration() * 5); }
//System.out.print("X");
						if (s.getCloser().getName().equals(getName())) {
							bonuses = bonuses + (s.getDuration() * 5); }						
//System.out.print("o");
					}
//System.out.print("0");
				}
//System.out.println(".");
				if (((stop.toSecondOfDay() - start.plusSeconds(1).toSecondOfDay()) / 3600) != 0) {
					shifts++; }
				hours = hours + ((stop.toSecondOfDay() - start.plusSeconds(1).toSecondOfDay()) / 3600); }
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to read LOG!");
			e.printStackTrace(); }
		setTotalPay(); }
	
	public void setTotalPay(double t) { totalPay = t; }
	public void setTotalPay() {
		totalPay = bonuses + (hours * getPayRate()); }
	public void setBonuses(double b) { bonuses = b; }
	public void setUserName(String u) { userName = u; }
	public void setPassPhrase(String cs) { passPhrase = cs; }
	public void setPassPhrase(char[] cs) { passPhrase = String.valueOf(cs); }
	public void setAccessPriv(String a) { accessPriv = a; }
	
	public static ArrayList<Operator> getOperators() { 
		readData();
		return operators; }
	
	//IO methods
	public static void initData() {
		operators.clear();
		operators.add(new Operator("NULL"));
		writeData(); }
	
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			operators.clear();
			while(complete != true) {	
				Operator test = (Operator)in.readObject(); 		
				if(test != null){ operators.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(operators.isEmpty()) { operators.add(new Operator()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(Operator c : operators) { out.writeObject(c); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public static void removeData(Operator opr) {
		readData();
		ArrayList<Operator> targets = new ArrayList<Operator>();
		for (Operator o : operators) { 
			if (o.getUserName().equals(opr.getUserName())) { targets.add(o); }
		}
		operators.removeAll(targets);
		writeData(); }
	
	public static void addData(Operator opr) {
		readData();
		removeData(opr);
		operators.add(opr); 
		writeData(); }
}
