package data;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Client extends Person {
	//instance variables
	private static File f = new File("./com.lingotechsolutions.data/clients.xml");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<Client> clients = new ArrayList<Client>();

	private String status = "UNKNOWN";
	private boolean webAccess = false;
	private String comments = new String();
	private ServiceRequest lastSuccessfulService;
	private ServiceRequest lastUnsuccessfulService;
	
	//constructors
	public Client(String n, String e, long s, String st) { 
		super(n, e, s); 
		status = st; }	
	public Client(String n, String e, long sT) { this(n, e, sT, "unknown"); }
	public Client(String e, long sT, String st) { this("empty", e, sT, st); }
	public Client(String e, long sT) { this(e, sT, "unknown"); }
	public Client(String e) { this(e, -1); }
	public Client(long sT) { this("unknown@unknown.com", sT); }
	public Client() { this(-1); }
	
	//get & set methods
	public String getStatus() { return status; }
	public int getIndex() {
		if 		(status.equalsIgnoreCase("APPROVED")) { return 1; }
		else if (status.equalsIgnoreCase("MEMBER")) { return 2; }
		else if (status.equalsIgnoreCase("ALISTER")) { return 3; }
		else if (status.equalsIgnoreCase("DEFUNCT")) { return 4; }
		else if (status.equalsIgnoreCase("BANNED")) { return 5; }
		else { return 0; }		
	}
	public boolean getWebAccess() { return webAccess; }
	public String getComments() { return comments; }
	public String getStats() { 
		ArrayList<ServiceRequest> history = new ArrayList<ServiceRequest>();
		for (ServiceRequest s : ServiceRequest.getServiceRequests()) {
			if (s.getClient().getSms() == this.getSms()) {
				history.add(s); }
		}
		
		double hours = 0;
		int requests = 0;
		double closures = 0;
		for (ServiceRequest r : history) {
			requests++;
			if (r.getStatusCycle() == ServiceRequest.CLOSED) {
				closures++;
				hours = hours + r.getDuration(); }
		}
		double average = hours / closures;
		double success = ( closures * 100 ) / requests;
		
		DecimalFormat df1 = new DecimalFormat("#0.0");
		DecimalFormat df2 = new DecimalFormat("#0.00");

		return df1.format(average) + "hrs / " + df2.format(success) + "% / " + requests + "x"; }
	
	public ServiceRequest getLastSuccess() { 
		if (lastSuccessfulService == null) {
			lastSuccessfulService = new ServiceRequest();
			lastSuccessfulService.setDate(LocalDate.now().minusDays(180)); }
		for (ServiceRequest s : ServiceRequest.getServiceRequests()) {
			if (s.getClient().getSms() == this.getSms() && s.getDate().isAfter(lastSuccessfulService.getDate()) && s.getStatusCycle() == ServiceRequest.CLOSED) {
				lastSuccessfulService = s; }
		}
		return lastSuccessfulService; }
	
	public ServiceRequest getLastUnsuccess() { 
		if (lastUnsuccessfulService == null) {
			lastUnsuccessfulService = new ServiceRequest();
			lastUnsuccessfulService.setDate(LocalDate.now().minusDays(180)); }
		for (ServiceRequest s : ServiceRequest.getServiceRequests()) {
			if (s.getClient().getSms() == this.getSms() && s.getDate().isAfter(lastUnsuccessfulService.getDate()) && s.getStatusCycle() == ServiceRequest.CANCEL) {
				lastUnsuccessfulService = s; }
		}
		return lastUnsuccessfulService; }
	
	public void setLastSuccess(ServiceRequest request) { lastSuccessfulService = request; }
	public void setLastUnsuccess(ServiceRequest request) { lastUnsuccessfulService = request; }
	public void setComments(String s) { comments = s; }
	public void setStatus(String s) { status = s; }
	public void setWebAccess(boolean w) { webAccess = w; }

	//methods
	public static void updateClientsStatus() {
		readData();
		ArrayList<ServiceRequest> queue = ServiceRequest.getServiceRequests();
		ArrayList<Client> targets = new ArrayList<Client>();
		
		for (Client c : clients) {
			System.out.print(".");
			Boolean current = false;		
			if (c.getStatus().equals("ALISTER") || c.getStatus().equals("MEMBER")) {
				for (ServiceRequest r : queue) { 
					if (r.getClient().getSms() == c.getSms() || r.getClient().getEmail().equals(c.getEmail())) { current = true; } 
				}
				if (current == false) { 
					c.setStatus("DEFUNCT");
					targets.add(c); }
//				else { targets.add(c); }
			}
			else if (c.getStatus().equals("APPROVED") || c.getStatus().equals("DEFUNCT")) {
				for (ServiceRequest r : queue) {
					if (r.getClient().getSms() == (c.getSms()) || r.getClient().getEmail().equals(c.getEmail())) { current = true; }
				}
				if (current == true) { 
					c.setStatus("MEMBER");
					targets.add(c); }
//				else if (c.getStatus().equals("APPROVED") && c.getInductionDate().isAfter(LocalDate.now().minusDays(90))) {
//					c.setStatus("DEFUNCT");
//					Client.removeData(c);
//					targets.add(c); }
			}
		}

		for (Client c : targets) {
			System.out.print("_");
			addData(c); }
		System.out.println("...COMPLETE"); }

	public static ArrayList<Client> getClients() {
		readData();
		return clients;	}
	
	public static String checkStatusOf(Long sms) {
		String status = "UNKNOWN";
		readData();
		
		for (Client c : clients) {
			if (c.getSms() == sms) {
				status = c.getStatus(); }
		}
		return status; }
	
	//IO methods
	public static void initData() {
		clients.clear();
		writeData(); }
	
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			clients.clear();
			while(complete != true) {	
				Client test = (Client)in.readObject(); 		
				if(test != null){ clients.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(clients.isEmpty()) { clients.add(new Client()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(Client c : clients) { out.writeObject(c); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public static void removeData(Client cust) {
		readData();
		ArrayList<Client> targets = new ArrayList<Client>();
		for (Client c : clients ) {
			if (c.getSms() == cust.getSms()) { targets.add(c); }
		}
		clients.removeAll(targets); 
		writeData(); }
	
	public static void addData(Client cust) {
		removeData(cust);
		readData();
		
		//checks for expired approvals
		ArrayList<Client> removeThese = new ArrayList<Client>();
		for (Client client : Client.getClients()) {
			if (client.getStatus().equals("APPROVED") && client.getInductionDate().isBefore(LocalDate.now().minusDays(90))) {
				removeThese.add(client);
				JOptionPane.showMessageDialog(null, client.getSms() + "'s approval has expired!"); }
		}
		clients.removeAll(removeThese);
		
		clients.add(cust);
		writeData(); }
}
