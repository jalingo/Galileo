package data;

import java.io.*;
import java.util.*;

public class gAccount {
	private static File f = new File("./com.lingotechsolutions.data/gAccounts.xml");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<gAccount> accounts = new ArrayList<gAccount>();
	
	private String accountName;
	private String accountPass;
	
	public gAccount(String a, String p) {
		accountName = a;
		accountPass = p; }
	public gAccount() {
		this("empty", "empty"); }
	
	public void setName(String a) { accountName = a; }
	public void setPass(String p) { accountPass = p; }
	public String getName() { return accountName; }
	public String getPass() { return accountPass; }
	
	public static ArrayList<gAccount> getAccounts() {
		readData();
		return accounts; }
	public static void initData() {
		accounts.clear();
		accounts.add(new gAccount());
		writeData(); }
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			accounts.clear();
			while(complete != true) {	
				gAccount test = (gAccount)in.readObject(); 		
				if(test != null){ accounts.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(accounts.isEmpty()) { accounts.add(new gAccount()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(gAccount b : accounts) { out.writeObject(b); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public static void removeData(gAccount acct) {
		readData();
		ArrayList<gAccount> targets = new ArrayList<gAccount>();
		for (gAccount b : accounts ) {
			if (b.getName() == acct.getName()) { targets.add(b); }
		}
		accounts.removeAll(targets); 
		writeData(); }
	public static void addData(gAccount acct) {
		readData();
		removeData(acct);
		accounts.add(acct); 
		writeData(); }
}
