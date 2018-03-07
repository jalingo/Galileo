package data;

import java.io.*;
import java.util.*;

public class Branch {
	//instance variables
	private static File f = new File("./com.lingotechsolutions.data/branches.xml");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<Branch> branches = new ArrayList<Branch>();

	private String name = "unknown";
	private String[][] staff = {{"executives", "empty"},{"managerial", "empty"},{"operations", "empty"},{"services", "empty"}};
	private String[][] regions = {{"eastbay", "EV", "EB", "BE", "OK", "OC"},{"san francisco", "SF", "OC"},{"southbay", "SB", "MP", "SC", "SJ", "OC"},{"socal", "OC", "SD", "LA", "IE"}};
	
	//constructors
	public Branch(String identifier, String[][] people, String[][] areas) {
		name = identifier;
		staff = people;
		regions = areas; }
	public Branch(String identifier) {
		name = identifier; }
	public Branch() {}
	
	//methods(get/set)
	public void setName(String n) { name = n; }
	public void setStaff(String[][] s) { staff = s; }
	public void setRegions(String[][] r) { regions = r; }
	public String getName() { return name; }
	public String[][] getStaff() { return staff; }
	public String[][] getRegions() { return regions; }
	
	//IO methods
	public static void initData() {
		branches.clear();
		branches.add(new Branch());
		writeData(); }
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			branches.clear();
			while(complete != true) {	
				Branch test = (Branch)in.readObject(); 		
				if(test != null){ branches.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(branches.isEmpty()) { branches.add(new Branch()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(Branch b : branches) { out.writeObject(b); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public static void removeData(Branch div) {
		readData();
		ArrayList<Branch> targets = new ArrayList<Branch>();
		for (Branch b : branches ) {
			if (b.getName().equals(div.getName())) { targets.add(b); }
		}
		branches.removeAll(targets); 		
		writeData(); }
	public static void addData(Branch div) {
		readData();
		removeData(div);
		branches.add(div); 
		writeData(); }
}
