package data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class Checklist {
	private static File f = new File("./com.lingotechsolutions.data/checks.xml");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<Checklist> checkLists = new ArrayList<Checklist>();
	
	private String itemDescription = "empty";
	private Boolean itemStarted = false;
	private Boolean itemClosed = false;
	private String itemTips = null;
	
	public Checklist(String d, String t) {
		itemDescription = d;
		itemTips = t; }
	public Checklist(String d) {
		this(d, null); }
	public Checklist() {}
	
	public void setItemDescription(String d) { itemDescription = d; }
	public void setItemStarted(Boolean b) { itemStarted = b; }
	public void setItemClosed(Boolean b) { itemClosed = b; }
	public void setItemTips(String t) { itemTips = t; }

	public String getItemDescription() { return itemDescription; }
	public Boolean isItemStarted() { return itemStarted; }
	public Boolean isItemClosed() { return itemClosed; }
	public String getItemTips() { return itemTips; }
	
	public static ArrayList<Checklist> getChecklists() {
		readData();
		return checkLists; }
	
	public static void reset() {
		readData();
		for (Checklist c : checkLists) {
			c.setItemClosed(false);
			c.setItemStarted(false); }
		writeData(); }
	
	public static void initData() {
		checkLists.clear();
		checkLists.add(new Checklist("NULL"));
		writeData(); }
	
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			checkLists.clear();
			while(complete != true) {	
				Checklist test = (Checklist)in.readObject(); 		
				if(test != null){ checkLists.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(checkLists.isEmpty()) { checkLists.add(new Checklist()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(Checklist c : checkLists) { out.writeObject(c); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public static void removeData(Checklist list) {
		readData();
		ArrayList<Checklist> targets = new ArrayList<Checklist>();
		for (Checklist c : checkLists ) {
			if (c.getItemDescription().equals(list.getItemDescription())) { targets.add(c); }
		}
		checkLists.removeAll(targets);
		writeData(); }
	public static void addData(Checklist list) {
		readData();
		removeData(list);
		checkLists.add(list); 
		writeData(); }
 }
