package data;

import java.time.*;

import javax.swing.JOptionPane;

public class Person {
	
	//Instance Variables
	private String name = "empty";
	private String email = "unknown@unknown.com";
	private long sms_number = -1;
	private long telephone_number = -1;
	private boolean contact_email = true;
	private boolean contact_sms = false;
	private boolean contact_telephone = false;
	private LocalDate inductionDate = LocalDate.now();

	//Constructor Classes
	public Person(String n, String e, long s) { name = n; email = e; sms_number = s; telephone_number = s; }
	public Person(String n, String e) { this(n, e, -1); }	
	public Person(String n, long s) { this(n, "unknown@email.com", s); }
	public Person(String n) { this(n, -1); }
	public Person(long sT) { this("empty", sT); }
	public Person() { this("empty"); }

	//various getField methods
	public String getName() { return name; }
	public String getEmail() { return email; }
	public long getSms() { return sms_number; }
	public long getTelephone() { return telephone_number; }
	public boolean getContactEmail() { return contact_email; }
	public boolean getContactTelephone() { return contact_telephone; }
	public boolean getContactSms() { return contact_sms; }	
	public LocalDate getInductionDate() { return inductionDate; }
	
	//various setField methods
	public void setName(String n) { name = n;}
	public void setEmail(String e) { email = e; }
	public void setSms(long sms) { sms_number = coreOf(sms); }
	public void setSms(String sms) {
		if (isLong(sms)) {
			sms_number = coreOf(Long.valueOf(sms)); }
		else {
			if (isLong(sms.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("-", "").replaceAll("+", "").replaceAll(" ", ""))) {
				sms_number = coreOf(Long.valueOf(sms.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("-", "").replaceAll("+", "").replaceAll(" ", ""))); }
			else {
				JOptionPane.showMessageDialog(null, "Not a 10 digit number! Will remain unchanged.", "SMS Entry Issue", JOptionPane.PLAIN_MESSAGE); }
		}
	}
	public void setTelephone(long t) { telephone_number = t; }
	public void setContactEmail(boolean c) { contact_email = c; }
	public void setContactTelephone(boolean c) { contact_telephone = c; }
	public void setContactSms(boolean c) { contact_sms = c; }
	public void setInductionDate(LocalDate d) { inductionDate = d; }

	//strips off leading 1 from google voice
	private long coreOf(long l) {
		String value = String.valueOf(l);
		long number = l;
		
		if (value.startsWith("1")) { number = Integer.valueOf(value.substring(1)); }
		
		return number; }
	
	//utility to quickly determine if long
	public static boolean isLong(String str) {
		if (str.endsWith("L")) { str = str.substring(0, str.length() - 2); }
		
		if (str == null) { return false; }
		
		int length = str.length();
		if (length == 0) { return false; }
		
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) { return false; }
			i = 1; }
		
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') { return false; }
		}
		return true; }
}
