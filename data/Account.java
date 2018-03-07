package data;

import java.io.*;
import java.util.*;

public class Account {
	//instance variables
	private static File f = new File("./com.lingotechsolutions.data/accounts.xml");
	private static XStreamer xstream = new XStreamer();
	private static ArrayList<Account> accounts = new ArrayList<Account>();

	private int identifier;
	private String site = "empty";
	private String account = "empty";
	private String passphrase = "empty";
	
	//constructors
	public Account(String s, String a, String p) {
		site = s;
		account = a;
		passphrase = p; }
	public Account() {}
	
	//methods
	public void setSite(String s) { site = s; }
	public void setAccount(String a) { account = a; }
	public void setPass(String p) { passphrase = p; }
	public void setId(int x) { identifier = x; }
	public int getId() { return identifier; }
	public String getSite() { return site; }
	public String getAccount() { return account; }
	public String getPass() { return passphrase; }

	public static ArrayList<Account> getAccounts() {
		readData();
		return accounts; }
	public static void initData() {
		accounts.clear();
		accounts.add(new Account());
		writeData(); }
	public static void readData() {
		try {
			Boolean complete = false;
			FileReader reader = new FileReader(f);
			ObjectInputStream in = xstream.createObjectInputStream(reader);
			accounts.clear();
			while(complete != true) {	
				Account test = (Account)in.readObject(); 		
				if(test != null){ accounts.add(test); }
					else { complete = true; }
			}
			in.close(); } 
		catch (IOException | ClassNotFoundException e) { e.printStackTrace(); } 
	}
	public static void writeData() {
		if(accounts.isEmpty()) { accounts.add(new Account()); }
		try {
			FileWriter writer = new FileWriter(f);
			ObjectOutputStream out = xstream.createObjectOutputStream(writer);
			for(Account b : accounts) { out.writeObject(b); }
			out.writeObject(null);
			out.close(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public static void removeData(Account acct) {
		readData();
		ArrayList<Account> targets = new ArrayList<Account>();
		for (Account b : accounts ) {
			if (b.getId() == acct.getId()) { targets.add(b); }
			//if (b.getSite().equals(acct.getSite())) { targets.add(b); }
		}
		accounts.removeAll(targets); 
		writeData(); }
	public static void addData(Account acct) {
		removeData(acct);
		readData();
		accounts.add(acct); 
		writeData(); }
}
