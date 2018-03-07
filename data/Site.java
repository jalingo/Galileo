package data;

import java.io.*;
import java.util.ArrayList;

public class Site extends MailingAddress {
	//instance variables
	private static File f = new File("./com.lingotechsolutions.data/sites.xml");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<Site> sites = new ArrayList<Site>();

	private String comments = new String("No comments.");
	private double siteCosts = 0.00;
	private String roomCode = "XXX";
	private boolean onSite = false;
	
	//constructors
	public Site(MailingAddress l, double s, boolean o) { super(l.getStreetAddress(), l.getUnit(), l.getCity(), l.getState(), l.getZipCode(), l.getArea()); siteCosts = s; onSite = o; }
	public Site(MailingAddress l, boolean o) { this(l, 0.00, o); }
	public Site(MailingAddress l) { this(l, false); }
	public Site(int a) { this(new MailingAddress(a)); }
	public Site() { this(new MailingAddress()); }
	
	//get & set methods
	public String getComments() { return comments; }
	public double getSiteCosts() { return siteCosts; }
	public String getRoomCode() { return roomCode; }
	public boolean getOnSite() { return onSite; }
	public void setComments(String c) { comments = c; }
	public void setSiteCosts(double s) { siteCosts = s; }
	public void setRoomCode(String r) { roomCode = r; }
	public void setOnSite(boolean o) { onSite = o; }

	//IO methods
	public static ArrayList<Site> getSites() { //eventually pass branch
		readData();
		return sites; }
	
	public static void initData() {
		sites.clear();
		sites.add(new Site(Site.XX)); 
		writeData(); }
	
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			sites.clear();
			while(complete != true) {	
				Site test = (Site)in.readObject(); 		
				if(test != null){ sites.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(sites.isEmpty()) { sites.add(new Site()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(Site c : sites) { out.writeObject(c); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void removeData(Site loc) {
		readData();
		ArrayList<Site> targets = new ArrayList<Site>();
		for (Site o : sites ) {
			if (o.getStreetAddress().equals(loc.getStreetAddress()) && o.getArea() == loc.getArea()) { targets.add(o); }
		}
		sites.removeAll(targets); 
		writeData(); }
		
	public static void addData(Site loc) {
		readData();
		removeData(loc);
		sites.add(loc); 
		writeData(); }
}
