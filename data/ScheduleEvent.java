package data;

import java.text.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

public class ScheduleEvent extends Observable {
	//instance variables
	public Site location = new Site();
	protected LocalDateTime start = LocalDateTime.now().plusMinutes(20);
	protected LocalDateTime stop = LocalDateTime.now().plusMinutes(20).plusHours(1);
	private double duration; 
	
	//constructors
	public ScheduleEvent(Site s0, LocalDateTime s1, LocalDateTime s2) { 
		location = s0; start = s1; stop = s2;
		duration = ((stop.getHour() - start.getHour()) * 60) + (stop.getMinute() - start.getMinute()); }
	public ScheduleEvent(LocalDateTime s1, LocalDateTime s2) { this(new Site(), s1, s2 ); }
	public ScheduleEvent(Site s0) { this(s0, LocalDateTime.now(), LocalDateTime.now().plusHours(1)); }
	public ScheduleEvent() { this(new Site()); }
	
	//set & get methods
	public Site getLocation() { return location; }
	public LocalDate getDate() { return start.toLocalDate(); }
	public LocalDateTime getStart() { return start; }
	public LocalDateTime getStop() { return stop; }
	public double getDuration() { 
		if (stop.isBefore(start)) {
			duration = ((24 - start.getHour() + stop.getHour()) + ((stop.getMinute() - start.getMinute()) / 60.0 )); }
		else {
			duration = ((stop.getHour() - start.getHour()) + ((stop.getMinute() - start.getMinute()) / 60.0)); } 
		if (duration == 12.0) { duration = 7.0; }
		if (duration == 7.0) { duration = 3.5; }		
		return duration; }
	public void setDate(LocalDate s) { 
		start = LocalDateTime.of(s , start.toLocalTime()); 
		stop = LocalDateTime.of(s, stop.toLocalTime()); }
	public void setStart(LocalDateTime s) { start = s; }
	public void setStop(LocalDateTime s) { stop = s; }
	public void setLocation(Site l) { location = l; }
	
	public void convertDate(String s) throws ParseException {
		LocalDate newDate;
		newDate = LocalDate.parse(s, DateTimeFormatter.ofPattern("yyMMdd"));
		setDate(newDate); }
	
	public void setAreaByString (String s) {
		Site newSite = getLocation();
		if (s.equals("SF")) { newSite.setArea(MailingAddress.SF); }
		if (s.equals("SJ")) { newSite.setArea(MailingAddress.SB); }
		if (s.equals("SB")) { newSite.setArea(MailingAddress.SB); }
		if (s.equals("EV")) { newSite.setArea(MailingAddress.EB); }
		if (s.equals("EB")) { newSite.setArea(MailingAddress.EB); }
		if (s.equals("SD")) { newSite.setArea(MailingAddress.SD); }
		if (s.equals("OC")) { newSite.setArea(MailingAddress.OC); }
		setLocation(newSite); }
}