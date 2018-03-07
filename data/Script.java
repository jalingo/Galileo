package data;

import java.io.*;
import java.util.*;

public class Script {
	private static File f = new File("./com.lingotechsolutions.data/scriptedLanguage.xml");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<Script> scriptedLanguage = new ArrayList<Script>();

	private String title = "Not Set";
	private String language = "empty";
	private ArrayList<String> triggers;
	
	public Script(String t, String l) {
		title = t;
		language = l; }
	public Script() {}
	
	public void setTitle(String t) { title = t; }
	public void setLanguage(String l) { language = l; }
	public void addTrigger(String s) { 
		for (String x : triggers) {
			if (triggers.contains(x)) {} 
				else { triggers.add(s); }
		}
	}
	public void removeTrigger(String s) {
		for (String x : triggers) { 
			if (triggers.contains(x)) { triggers.remove(x); }
		}
	}
	public ArrayList<String> getTriggers() { return triggers; }
	public String getTitle() { return title; }
	public String getLanguage() { return language; }
	
	public static void initData() {
		scriptedLanguage.clear();
		scriptedLanguage.add(new Script());
		writeData(); }
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			scriptedLanguage.clear();
			while(complete != true) {	
				Script test = (Script)in.readObject(); 		
				if(test != null){ scriptedLanguage.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(scriptedLanguage.isEmpty()) { scriptedLanguage.add(new Script()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(Script c : scriptedLanguage) { out.writeObject(c); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public static void removeData(Script scr) {
		readData();
		ArrayList<Script> targets = new ArrayList<Script>();
		for (Script o : scriptedLanguage ) {
			if (o.getTitle().equals(scr.getTitle())) { targets.add(o); }
			scriptedLanguage.removeAll(targets); }		
		writeData(); }
	public static void addData(Script scr) {
		readData();
		removeData(scr);
		scriptedLanguage.add(scr); 
		writeData(); }
}
