package data;

public class Employee extends Person {

	//instance variables
	private static final double MINIMUM_PAY = 0.00;
	private double payRate = MINIMUM_PAY;
	private boolean hourly = true;
	
	//constructor classes
	public Employee(String n, Double p, Boolean h) {
		super(n);
		payRate = p;
		hourly = h;	}
	public Employee(String n, Double p) { this (n, p, true); }
	public Employee(String n) { this(n, MINIMUM_PAY, true); }
	public Employee() { this("empty"); }
	
	//various getField methods
	public double getPayRate() { return payRate; }
	public boolean askHourly() { return hourly; }
	
	//various setField methods
	public void setPayRate(double p) { payRate = p; }
	public void setHourly(boolean h) { hourly = h; }
}
