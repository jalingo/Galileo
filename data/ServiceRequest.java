package data;

import java.io.*;
import java.text.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

import javax.swing.*;

public class ServiceRequest extends ScheduleEvent implements Comparable<ServiceRequest> {
	//instance variables
	private static final File f = new File("./com.lingotechsolutions.data/events.xml");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<ServiceRequest> serviceQueue = new ArrayList<ServiceRequest>();

	public static final int PENDNG = 0;
	public static final int CONFRM = 1; 
	public static final int OPENED = 2;
	public static final int CLOSED = 3;
	public static final int CANCEL = 4;
	
	private int statusCycle = PENDNG;
	private String comments = "No Comments";
	private String paymentType = "CASH";
	private Operator initiator = new Operator();
	private Operator confirmer = new Operator();
	private Operator closer = new Operator();
	private Client customer = new Client();
	private Technician provider = new Technician();
	private Boolean testOpenHoursPass;
	private Boolean testDurationPass;
	private Boolean testAreaConflictPass = true;
	private Boolean testTechConflictPass = true;

	//constructors
	public ServiceRequest(Client c, Technician t, Site s0, LocalDateTime s1, LocalDateTime s2) { super(s0, s1, s2); customer = c; provider = t; }
	public ServiceRequest(Client c, Technician t, Site s0) { this(c, t, s0, LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusMinutes(30).plusHours(1)); }
	public ServiceRequest(Client c, LocalDateTime s1, LocalDateTime s2) { this(c, new Technician(), new Site(), s1, s2 ); }
	public ServiceRequest(Client c, LocalDateTime s) { this(c, s, s); }
	public ServiceRequest(Client c, Site s0) { this(c, new Technician(), s0); }
	public ServiceRequest(Client c, Technician t) { this(c, t, new Site()); }
	public ServiceRequest(Client c) { this (c, new Technician()); }
	public ServiceRequest() { this(new Client()); }
	
	//get & set methods
	public int getStatusCycle() { return statusCycle; }
	public String getComments() { return comments; }
	public String getPaymentType() { return paymentType; }
	public Operator getInitiator() { return initiator; }
	public Operator getConfirmer() { return confirmer; }
	public Operator getCloser() { return closer; }
	public Client getClient() { return customer; }
	public Technician getTech() { return provider; }
	public String getPeriod() {
		SimpleDateFormat startStop = new SimpleDateFormat("h:mma");
		String period = startStop.format(this.getStart()) + " - " + startStop.format(this.getStop());
		return period; }
	public void setStatusCycle(int s) { statusCycle = s; }
	public void setComments(String s) { comments = s; }
	public void setPaymentType(String s) { paymentType = s; }
	public void setInitiator(Operator o) { initiator = o; }
	public void setConfirmer(Operator o) { confirmer = o; }
	public void setCloser(Operator o) { closer = o; }
	public void setClient(Client c) { customer = c; }
	public void setTech(Technician t) { provider = t; }
	public void setStart(LocalDateTime s) { 
		if (s.toLocalTime().isAfter(LocalTime.of(9, 15)) && s.toLocalTime().isBefore(LocalTime.of(23, 0))) { testOpenHoursPass = true; }
			else { testOpenHoursPass = false; }

		if (s.isBefore(this.getStop().minusMinutes(59))) { testDurationPass = true; }
			else { testDurationPass = false; }
		
		ArrayList<ServiceRequest> queue = ServiceRequest.getServiceRequests(s.toLocalDate());
		queue.remove(this);
		
		for (ServiceRequest r : queue) {
			if (r.getStatusCycle() != ServiceRequest.CANCEL && s.isAfter(r.getStart()) && s.isBefore(r.getStop())) {
				if (this.getLocation().getArea() == r.getLocation().getArea()) {
					testAreaConflictPass = false; }
				if (this.getTech().getName().equals(r.getTech().getName())) {
					testTechConflictPass = false; }
			}
		}
		
		if (testOpenHoursPass.equals(true) && testDurationPass.equals(true) && testAreaConflictPass.equals(true) && testTechConflictPass.equals(true)) {
				start = s; }
		else {
			StringBuilder message = new StringBuilder("The following conflicts discovered:\n");
			if (testOpenHoursPass.equals(false)) { message.append("\nOff Hours!\n\tStart: " + s.toLocalTime().toString()); }
			if (testDurationPass.equals(false)) { message.append("\nDuration Inadequate!\n\t" + s.toLocalTime().toString() + " - " + this.getStop().toLocalTime().toString() + " [newSTART]"); }
			if (testAreaConflictPass.equals(false)) { message.append("\nSite Occupied!"); }
			if (testTechConflictPass.equals(false)) { message.append("\nTech Occupied!"); }
			message.append("\n\nPlease ensure this request is possible before clicking OK...");
			
			int response = JOptionPane.showConfirmDialog(null, message, "Manually Overide?", JOptionPane.OK_CANCEL_OPTION);
			if (response == JOptionPane.OK_OPTION) { start = s; }
		}
	}
	public void setStop(LocalDateTime s) { 
		if (s.toLocalTime().isAfter(LocalTime.of(10, 0)) && s.toLocalTime().isBefore(LocalTime.of(23, 59))) { testOpenHoursPass = true; }
			else { testOpenHoursPass = false; }

		if (s.isAfter(this.getStart().plusMinutes(59))) { testDurationPass = true; }
			else { testDurationPass = false; }
		
		ArrayList<ServiceRequest> queue = ServiceRequest.getServiceRequests(s.toLocalDate());
		queue.remove(this);
		
		for (ServiceRequest r : queue) {
			if (s.isAfter(r.getStart()) && s.isBefore(r.getStop())) {
				if (this.getLocation().getArea() == r.getLocation().getArea()) {
					testAreaConflictPass = false; }
				if (this.getTech().getName().equals(r.getTech().getName())) {
					testTechConflictPass = false; }
			}
		}
		
		if (testOpenHoursPass.equals(true) && testDurationPass.equals(true) && testAreaConflictPass.equals(true) && testTechConflictPass.equals(true)) {
				stop = s; }
		else {
			StringBuilder message = new StringBuilder("The following conflicts discovered:\n");
			if (testOpenHoursPass.equals(false)) { message.append("\nOff Hours!\n\tStop: " + s.toLocalTime().toString()); }
			if (testDurationPass.equals(false)) { message.append("\nDuration Inadequate!\n" + this.getStart().toLocalTime().toString() + " - " + s.toLocalTime().toString() + " [newSTOP]"); }
			if (testAreaConflictPass.equals(false)) { message.append("\nSite Occupied!"); }
			if (testTechConflictPass.equals(false)) { message.append("\nTech Occupied!"); }
			message.append("\n\nPlease ensure this request is possible before clicking OK...");
			
			int response = JOptionPane.showConfirmDialog(null, message, "Manually Overide?", JOptionPane.OK_CANCEL_OPTION);
			if (response == JOptionPane.OK_OPTION) { stop = s; }
		}
	}
	
	//static methods
	public static ArrayList<ServiceRequest> getServiceRequests(LocalDate d) {
		ArrayList<ServiceRequest> querryByDate = new ArrayList<ServiceRequest>();
		readData(); 
		Collections.sort(serviceQueue);
		for (ServiceRequest s : serviceQueue ) {
			if (s.getDate().equals(d)) { 
				querryByDate.add(s); }
		}
		return querryByDate; }

	public static ArrayList<ServiceRequest> getServiceRequests() {
		readData();
		return serviceQueue; }

	//methods
	public void setStatusCycleByString(String s) {
		if (s.equals("PENDNG")) { setStatusCycle(PENDNG); }
		if (s.equals("CONFRM")) { setStatusCycle(CONFRM); }
		if (s.equals("OPENED")) { setStatusCycle(OPENED); }
		if (s.equals("CLOSED")) { setStatusCycle(CLOSED); }
		if (s.equals("CANCEL")) { setStatusCycle(CANCEL); }
	}
	public void setTechByString(String s) { provider = Technician.getTech(s); } 

	public String getConfirmationStatement() {
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mma");
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd LLL yy");

		String date;
		if 		(getDate().isEqual(LocalDate.now())) 				{ date = "Today"; }
		else if (getDate().isEqual(LocalDate.now().plusDays(1))) 	{ date = "Tomorrow"; }
		else 														{ date = dateFormat.format(getDate()); }

		//creates confirmation statement
		return "Can you confirm a " + getDuration() + "hr(s) appointment with " + getTech().getName() + " at " + timeFormat.format(getStart()) + ", " + date + " in " + getLocation().getCity() + "?"; }
	
	//IO methods
	public static void initData() {
		serviceQueue.clear();
		serviceQueue.add(new ServiceRequest(new Client(-1), LocalDateTime.MIN));
		writeData(); }
	
	public static void readData() {
		serviceQueue.clear();
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			while(complete != true) {	
				ServiceRequest test = (ServiceRequest)in.readObject(); 		
				if(test != null) {
					if (test.getDate().isAfter(LocalDate.now().minusDays(180))) { 
						serviceQueue.add(test); }
				}
				else { complete = true; } 
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	
	public static void writeData() {
		if(serviceQueue.isEmpty()) { serviceQueue.add(new ServiceRequest()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(ServiceRequest r : serviceQueue) { out.writeObject(r); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void removeData(ServiceRequest r) {
		readData();  		
		Iterator<ServiceRequest> it = serviceQueue.iterator();
		try {
			while(it.hasNext()) {
				ServiceRequest s = it.next();
				if (s.getClient().getSms() == r.getClient().getSms() && s.getTech().getName().equals(r.getTech().getName()) && s.getDate().equals(r.getDate())) {
					it.remove(); }
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "r: [" + r.getClient().getSms() + "s/" + r.getClient().getTelephone() + "t, " + r.getClient().getEmail() + ", " + r.getTech().getName() + ", " + r.getComments() + ", " + r.getStatusCycle() + ", " + r.getDate() + ", " + r.getLocation().getArea() + "] failed to write."); }
		writeData(); }

	public static void addData(ServiceRequest r) {
		if (r.getClient().getStatus().equals("MEMBER") || r.getClient().getStatus().equals("ALISTER")) {
			removeData(r);
			serviceQueue.add(r); } 
		else if (r.getClient().getStatus().equals("BANNED") || r.getClient().getStatus().equals("UNKNOWN")) {
			JOptionPane.showMessageDialog(null, "New Request failed: " + r.getClient().getSms() + " is " + r.getClient().getStatus() + "!", "ERROR: " + r.getClient().getSms(), JOptionPane.WARNING_MESSAGE);
		}
		else if (r.getClient().getSms() == -1) { /*make no changes to potential/new request until a client is set (via sms) */ }
		else {
			int response = JOptionPane.showConfirmDialog(null, r.getClient().getSms() + " is " + r.getClient().getStatus() + "...", "Manually Overide?", JOptionPane.OK_CANCEL_OPTION);
			if (response == JOptionPane.OK_OPTION) {
				removeData(r);
				serviceQueue.add(r); }
		}
		writeData(); }
	
//	@Overide
	public int compareTo(ServiceRequest s) {
		int area = s.getLocation().getArea();
		return this.location.getArea() - area; }
}