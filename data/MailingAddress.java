package data;

public class MailingAddress {

	//instance variables
	private String streetAddress = "ADDR NOT SET";
	private String unit = "";
	private String city = "????";
	private String state = "CA";
	private int zipCode = 90210;
	private int area = 7;
	
	public static final int SF = 0;
	public static final int SB = 1;
	public static final int EB = 2;
	public static final int OC = 3;
	public static final int SD = 4;
	public static final int LA = 5;
	public static final int NY = 6;
	public static final int XX = 7;
	public static final int OK0 = 8;
	public static final int OK1 = 9;
	
	//constructors
	public MailingAddress(String sA, String u, String c, String s, int z, int a) {
		streetAddress = sA; unit = u; city = c; state = s; zipCode = z; area = a; }
	public MailingAddress(String sA, String c, String s, int z, int a) { this(sA, "", c, s, z, a); }
	public MailingAddress(String sA, String c, String s, int z) { this(sA, "", c, s, z, XX); }
	public MailingAddress(String sA, String u, String c, String s, int a) { this(sA, u, c, s, -1, a); }
	public MailingAddress(String sA, String u, String c, String s) { this(sA, u, c, s, -1, XX); }
	public MailingAddress(String sA, String c, String s) { this(sA, "", c, s, -1, XX); }
	public MailingAddress(String c, String s) { this("Site", c, s);	}
	public MailingAddress(int a) {
		area = a;
		if 		(a == SF) { city = "Union Square"; }
		else if	(a == SB) { city = "South Bay"; }
		else if (a == EB) { city = "East Bay"; }
		else if (a == SD) { city = "San Diego"; }
		else if (a == LA) { city = "LA"; }
		else 			  { city = "unknown"; }
		streetAddress = "STREET NOT SET";
		state = "CA";
		zipCode = 90210; }
	public MailingAddress() {}
	
	//set & get methods
	public String getStreetAddress() { return streetAddress; }
	public String getUnit() { return unit; }
	public String getCity() { return city; }
	public String getState() { return state; }
	public int getZipCode() { return zipCode; }
	public int getArea() { return area; }
	public void setStreetAddress(String sA) { streetAddress = sA; }
	public void setUnit(String u) { unit = u; }
	public void setCity(String c) { city = c; }
	public void setState(String s) { state = s; }
	public void setZipCode(int z) { zipCode = z; }
	public void setArea(int a) { area = a; }

	//methods
	public void setAreaByString (String s) {
		if (s.equals("SF")) { area = MailingAddress.SF; }
		if (s.equals("SJ")) { area = MailingAddress.SB; }
		if (s.equals("SB")) { area = MailingAddress.SB; }
		if (s.equals("EV")) { area = MailingAddress.EB; }
		if (s.equals("EB")) { area = MailingAddress.EB; }
		if (s.equals("SD")) { area = MailingAddress.SD; }
		if (s.equals("OC")) { area = MailingAddress.OC; }
	 }
}
