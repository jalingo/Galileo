package data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Link {
	private static File f = new File("./com.lingotechsolutions.data/links.xml");
	private static ArrayList<Link> links = new ArrayList<Link>();
	private static XStreamer xstream = new XStreamer();
	
	private String url = "empty";
	private String account = null;
	private String pass = null;
	private String comments = "empty";
	
	public Link(String u, String a, String p, String c) {
		url = u;
		account = a;
		pass = p;
		comments = c; }
	public Link(String u, String c) { this(u, null, null, c); }
	public Link(String u) { this(u, "empty"); }
	public Link() {}
	
	public void setUrl(String u) { url = u; }
	public void setAccount(String a) { account = a; }
	public void setPass(String p) { pass = p; }
	public void setComments(String c) { comments = c; }
	
	public String getUrl() { return url; }
	public String getAccount() { return account; }
	public String getPass() { return pass; }
	public String getComments() { return comments; }
	
	public static ArrayList<Link> getLinks() {
		readData();
		return links; }
	public static void initData() {
		links.clear();
		links.add(new Link("NULL"));
		writeData(); }
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			links.clear();
			while(complete != true) {	
				Link test = (Link)in.readObject(); 		
				if(test != null){ links.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(links.isEmpty()) { links.add(new Link()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(Link l : links) { out.writeObject(l); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public static void removeData(Link link) {
		readData();
		ArrayList<Link> targets = new ArrayList<Link>();
		for (Link l : links ) {
			if (l.getUrl().equals(link.getUrl())) { targets.add(l); }
		}	
		links.removeAll(targets); 		
		writeData(); }
	public static void addData(Link link) {
		readData();
		removeData(link);
		links.add(link); 
		writeData(); }
}
