package controls;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import javax.swing.*;

import data.*;

public class MessageDecryption extends JPanel {
	private static final long serialVersionUID = -6774354287677590384L;
	private File failureLog = new File("./com.lingotechsolutions.data/failures");
	
	public MessageDecryption(String message) {
		
		//stores each of the message (splits on spaces) in array "words" and counts length
		String[] words = message.split(" ");
		int wordCount = words.length;
		
		//arrayLists that store what may be key info from the conversation
		ArrayList<LocalDateTime> whens = new ArrayList<LocalDateTime>();
		ArrayList<String> durations = new ArrayList<String>();
		ArrayList<String> techs = new ArrayList<String>();
		ArrayList<String> contactInfo = new ArrayList<String>();
		ArrayList<Integer> areas = new ArrayList<Integer>();
		
		//response indication booleans (set to defaults, but will be tested below)
		boolean possibleHandshake 		= false;
		boolean possibleBooking 		= false;
		boolean possibleOutCall			= false;
		boolean possibleConfirmation	= false;
		boolean possibleReference 		= false;
		boolean possibleCheckIn 		= false;
		boolean possibleServicesQuerry	= false;
		boolean possibleRateInquiry		= false;
		boolean possibleSpecialRequest	= false;
		boolean possibleWebsiteIssue	= false;
		boolean	possibleCompliment		= false;
		boolean possibleComplaint		= false;
		boolean possibleCancel			= false;
		boolean unrecognizedClient		= true;
		boolean staffer					= false;
		boolean preferredClient			= false;
		boolean banned					= false;
		
		//checks for trailing google timestamp and removes it
		if (words[words.length - 2].contains(":") && words[words.length - 1].endsWith("M")) { 
			words[words.length - 2] = "";
			words[words.length - 1] = "#END"; }

//												***keyword triggers for message content***
		//Checks each word of the message for response indicators
		int index = 0;
		for (String preword : words) {
			
			//removes any formatting or symbols that might confuse tests
			String word = preword.replaceAll("\\.", "").replaceAll(",", "").replaceAll("\\)", "").replaceAll("\\(", "").replaceAll("!", "").replaceAll("-", "").replaceAll("\\?", "").replaceAll("!", "").trim();

			if (index != 0 && word != "") {	//ignores phone number in array's first element
				
				//check for handshake
				if (word.equalsIgnoreCase("here") || word.equalsIgnoreCase("arrived") || word.equalsIgnoreCase("parking") || word.equalsIgnoreCase("parked") || wordCount < 5) {
					possibleHandshake = true; }

				//creates null time value (3 nano) and null date (y01) for incomplete dateTimes
				LocalTime time = LocalTime.of(0, 0, 0, 3);
				LocalDate date = LocalDate.of(0001, 12, 31);
				
				//store any date references in whens
				String chop = "";
				if (word.length() > 2) { chop = word.substring(0, 2); }
				if (chop.equalsIgnoreCase("JAN")	|| chop.equalsIgnoreCase("FEB")	|| chop.equalsIgnoreCase("MAR")	||
					chop.equalsIgnoreCase("APR") 	|| chop.equalsIgnoreCase("MAY") || chop.equalsIgnoreCase("JUN") ||
					chop.equalsIgnoreCase("JUL") 	|| chop.equalsIgnoreCase("AUG") || chop.equalsIgnoreCase("SEP") ||
					chop.equalsIgnoreCase("OCT") 	|| chop.equalsIgnoreCase("NOV") || chop.equalsIgnoreCase("DEC")) {
						String pre = "empty";
						if (index > 1) { pre = words[index - 1]; }
						String post = words[index + 1];
					
						//loads partial date into whens...
						if (isInteger(pre)) {
							LocalDate when = LocalDate.of(0001, monthOf(chop), Integer.valueOf(pre));
							whens.add(LocalDateTime.of(when, time)); }
						else if (isInteger(post)){
							LocalDate when = LocalDate.of(0001, monthOf(chop), Integer.valueOf(post));
							whens.add(LocalDateTime.of(when, time)); }
						else { /*nothing added to whens*/ }						
				}
				else if (chop.equalsIgnoreCase("TOD") || chop.equalsIgnoreCase("TON")) { whens.add(LocalDateTime.of(LocalDate.now(), time)); }
				else if (chop.equalsIgnoreCase("TOM")) { whens.add(LocalDateTime.of(LocalDate.now().plusDays(1), time)); }
				else if (chop.equalsIgnoreCase("SUN")) { whens.add(LocalDateTime.of(date.minusYears(1), time)); }
				else if (chop.equalsIgnoreCase("MON")) { whens.add(LocalDateTime.of(date.minusYears(1).plusDays(1), time)); }
				else if (chop.equalsIgnoreCase("TUE")) { whens.add(LocalDateTime.of(date.minusYears(1).plusDays(2), time)); }
				else if (chop.equalsIgnoreCase("WED")) { whens.add(LocalDateTime.of(date.minusYears(1).plusDays(3), time)); }
				else if (chop.equalsIgnoreCase("THU")) { whens.add(LocalDateTime.of(date.minusYears(1).plusDays(4), time)); }
				else if (chop.equalsIgnoreCase("FRI")) { whens.add(LocalDateTime.of(date.minusYears(1).plusDays(5), time)); }
				else if (chop.equalsIgnoreCase("SAT")) { whens.add(LocalDateTime.of(date.minusYears(1).plusDays(6), time)); }
				else { /*nothing added to whens*/ }	

				//store any time references in whens
				if (word.length() != 0) {
					if (isInteger(word.substring(0, 1))) {
						if (word.contains(":")) {
							if 		(word.length() == 4) {
								//checking am/pm
								int modifier;
								if (Integer.valueOf(word.substring(0, 1)) < 9) { modifier = 12; }
								else { modifier = 0; }
							
								whens.add(LocalDateTime.of(date, LocalTime.of(Integer.valueOf(word.substring(0, 1)) + modifier, Integer.valueOf(word.substring(2))))); }
							else if (word.length() == 5) {
								//checking  am/pm
								int modifier;
								if (word.substring(0, 2).equals("12")) { modifier = 12; }
								else { modifier = 0; }
						
								whens.add(LocalDateTime.of(date, LocalTime.of(Integer.valueOf(word.substring(0, 2)) + modifier, Integer.valueOf(word.substring(3))))); }
							else if (word.length() == 6) {
								//checking am/pm
								int modifier = 0;
								if (word.substring(6).equalsIgnoreCase("pm")) { modifier = 12; }
							
								whens.add(LocalDateTime.of(date, LocalTime.of(Integer.valueOf(word.substring(0, 1)) + modifier, Integer.valueOf(word.substring(2, 4))))); }
							else if (word.length() == 7) {
								//checking  am/pm
								int modifier = 0;
								if (word.substring(6).equalsIgnoreCase("pm") && Integer.valueOf(word.substring(0, 2)) != 12) { modifier = 12; }

								whens.add(LocalDateTime.of(date, LocalTime.of(Integer.valueOf(word.substring(0, 2)) + modifier, Integer.valueOf(word.substring(3, 5))))); }
						}						
						else if (word.toLowerCase().endsWith("pm") || word.toLowerCase().endsWith("am")) {
							if (word.length() < 5) {
								whens.add(LocalDateTime.of(date, LocalTime.parse(word.toUpperCase(), DateTimeFormatter.ofPattern("ha")))); }
							else {
								whens.add(LocalDateTime.of(date, LocalTime.parse(word.toUpperCase(), DateTimeFormatter.ofPattern("hmma")))); }
						}
						else if (word.toLowerCase().endsWith("ish")) {
							if (isInteger(word.replace("ish", "").replace("-", ""))) {
								if 		(Integer.valueOf(word.replace("ish", "").replace("-", "")) > 11) {
									whens.add(LocalDateTime.of(date, LocalTime.of(Integer.valueOf(word.replace("ish", "").replace("-", "")) - 1, 0))); }
								else if (Integer.valueOf(word.replace("ish", "").replace("-", "")) == 11) {
									if (LocalTime.now().isBefore(LocalTime.NOON) && whens.contains("tonight") == false) {
										whens.add(LocalDateTime.of(date, LocalTime.NOON));	}
									else {
										whens.add(LocalDateTime.of(date, LocalTime.MIDNIGHT));	}
								}
								else {
									if (LocalTime.of(Integer.valueOf(word.replace("ish", "").replace("-", "")) - 1, 0).isBefore(LocalTime.now().plusMinutes(1))) {
										whens.add(LocalDateTime.of(date, LocalTime.of(Integer.valueOf(word.replace("ish", "").replace("-", "")) + 11, 0))); }
									else {
										whens.add(LocalDateTime.of(date, LocalTime.of(Integer.valueOf(word.replace("ish", "").replace("-", "")) - 1, 0))); }
								}
							}
						}
					}
					//check for duration references
					if (word.equalsIgnoreCase("hour")	|| word.equalsIgnoreCase("hours")	|| word.toUpperCase().startsWith("HR")	|| 
						word.equalsIgnoreCase("min")	|| word.equalsIgnoreCase("mins")	|| word.toUpperCase().startsWith("MN")) {
							int element = 0;
							int target = -1;
							for (String entry : words) {
								if (entry.equals(preword)) {
									target = element; }
								element++; }
							if (target != -1) {
								durations.add(words[index - 1] + " " + words[index]); }
					}
					else if (isInteger(word.substring(0, 1))) {
						if (word.toLowerCase().endsWith("hrs") || word.toLowerCase().endsWith("hr")) {  //retaining the if + else if in case
							durations.add(word); }														//additional formating required
						else if (word.toLowerCase().endsWith("ns") || word.toLowerCase().endsWith("min") || word.toLowerCase().endsWith("mn")) {
							durations.add(word); }
					}
				}
				//check for booking
				if (whens.isEmpty()) { possibleConfirmation = false; }
				else { possibleBooking = true; }
			
				if (durations.isEmpty()) { possibleConfirmation = false; }
				else { possibleBooking = true; }
					
				if (word.equalsIgnoreCase("who") 	|| word.equalsIgnoreCase("whos") 	|| word.equalsIgnoreCase("available") 	|| 
					word.equalsIgnoreCase("next") 	|| word.equalsIgnoreCase("anyone")	|| word.equalsIgnoreCase("earliest") 	||
					word.equalsIgnoreCase("any") 	|| word.equalsIgnoreCase("ladies") 	|| word.equalsIgnoreCase("book")) {
						possibleBooking = true; }

				//check if outcall
				if (word.equalsIgnoreCase("outcall") || word.equalsIgnoreCase("house") 	|| word.equalsIgnoreCase("apartment")	||
					word.equalsIgnoreCase("place")) {
						possibleOutCall = true;
						possibleBooking = true; }
				
				//check if tech is mentioned(stored in techs),  
				if (Technician.checkTech(word)) { 
					techs.add(word);
					possibleBooking = true; }
				
				//finding and saving any area 
				if (word.equalsIgnoreCase("SF") || word.equalsIgnoreCase("francisco") || word.equalsIgnoreCase("frisco") || word.equalsIgnoreCase("union")) {
					areas.add(Site.SF); }
				if (word.equalsIgnoreCase("SB") 	|| word.equalsIgnoreCase("SJ") 		|| word.equalsIgnoreCase("southbay") ||
					word.equalsIgnoreCase("jose") 	||
					word.equalsIgnoreCase("santa")	|| word.equalsIgnoreCase("clara")	|| word.equalsIgnoreCase("milpitas")) { 
						areas.add(Site.SB); }
				if (word.equalsIgnoreCase("EV") 	|| word.equalsIgnoreCase("emeryville")	|| word.equalsIgnoreCase("EB") || 
					word.equalsIgnoreCase("east")	|| word.equalsIgnoreCase("eastbay") 	|| word.equalsIgnoreCase("berkeley") || 
					word.equalsIgnoreCase("ebay") 	|| word.equalsIgnoreCase("oakland")) {
						areas.add(Site.EB); }
				if (word.equalsIgnoreCase("outcall") || word.equalsIgnoreCase("oc") || word.equalsIgnoreCase("travel") || word.equalsIgnoreCase("visit")) {
					areas.add(Site.OC); }
				//NY, SD, LA, etc... missing
				//also missing: operational sites
				
				//if area found, triggering possibilities
				if (areas.isEmpty() == false) {
					possibleBooking = true;
					possibleCheckIn = true; }
			
				//check for reference request
				if (word.equalsIgnoreCase("client") || word.equalsIgnoreCase("reference") || word.equalsIgnoreCase("verify")) {
					possibleReference = true; }
				if (word.contains("@")) {
					possibleReference = true;
					contactInfo.add(word); }
				try {
					if (Long.valueOf(word) > 999999 && Long.valueOf(word) < 10000000000L) {
						if (Long.valueOf(word) > 999999999) {
							contactInfo.add(word); }
						else {
							contactInfo.add(new String("DIGITSOFF")); }
					}
				} catch (NumberFormatException e) { /*e.printStackTrace();*/ }

				//check for cancellation
				if (word.toLowerCase().startsWith("cancel") || word.equalsIgnoreCase("sorry")) {
					possibleCancel = true;
					possibleBooking = false;
					possibleConfirmation = false; }
				
				//check for client check in
				if (word.equalsIgnoreCase("today") || word.equalsIgnoreCase("hotel") || word.equalsIgnoreCase("where") || word.equalsIgnoreCase("location")) {
					possibleCheckIn = true; }
					
				//check for service questions
				if (word.equalsIgnoreCase("review")	|| word.equalsIgnoreCase("reviews") 	|| word.equalsIgnoreCase("gfe") || 
					word.equalsIgnoreCase("pic") 	|| word.equalsIgnoreCase("picture") 	|| word.equalsIgnoreCase("pix") ||
					word.equalsIgnoreCase("pics")	|| word.equalsIgnoreCase("pictures")	|| 
					word.equalsIgnoreCase("pse")) {
						possibleServicesQuerry = true; }
			
				//check for rate inquiry
				if (word.equalsIgnoreCase("rate") || word.equalsIgnoreCase("donation") || word.equalsIgnoreCase("gift")) {
					possibleRateInquiry = true; }

				//check for special requests
				if (word.equalsIgnoreCase("does") || word.equalsIgnoreCase("request") || word.equalsIgnoreCase("stockings")) {
					possibleSpecialRequest = true; }
			
				//check for password/website inquiry
				if (word.equalsIgnoreCase("password") || word.equalsIgnoreCase("site") || word.equalsIgnoreCase("sites")) {
					possibleWebsiteIssue = true; }

				//check for compliments
				if (word.equalsIgnoreCase("sweet") || word.equalsIgnoreCase("fantastic") || word.equalsIgnoreCase("keeper")) {
					possibleCompliment = true; }
				
				//check for cients' complaints
				if (word.equalsIgnoreCase("unhappy") || word.equalsIgnoreCase("dissatisfied") || word.contains("advertised") || word.contains("complain")) {
					possibleComplaint = true; }
			}
				
			//tracks array element of word
			index++; }
		
//														***verify suggested response***
		//pulls client by sms from first word
		if (words[0].startsWith("+1")) {
			words[0] = words[0].substring(2).replaceAll(":", ""); }
		else if (words[0].startsWith("1")) {
			words[0] = words[0].substring(1).replaceAll(":", ""); }
		else {
			words[0] = words[0].replaceAll(":", ""); }
	
		//check if client has an appointment today and save it
		ServiceRequest targetRequest = new ServiceRequest();
		for (ServiceRequest req : ServiceRequest.getServiceRequests(LocalDate.now())) {
			if (req.getClient().getSms() == Long.valueOf(words[0])) {
				targetRequest = req; }
		}
		Availability targetShift = new Availability();
		if (targetRequest.getClient().getSms() == -1) { //client has no appointment today
			possibleHandshake 	= false;
			possibleCheckIn 	= false;
			possibleCompliment	= false;
			possibleComplaint 	= false; }
		else {
			for (Availability shift : Availability.getAvailabilities(LocalDate.now())) {
				if (shift.getTech().getName().equals(targetRequest.getTech().getName())) { targetShift = shift; }
			}
		}
		
		//check client status
		for (Client sender : Client.getClients()) {
			if (sender.getSms() == Long.valueOf(words[0])) {
				if 		(sender.getStatus().equalsIgnoreCase("unknown"))		{ unrecognizedClient = true; }
				else if (sender.getStatus().equalsIgnoreCase("banned") != true) { unrecognizedClient = false; }
				else 															{ unrecognizedClient = false;
																			  	  banned = true; }
			}
		}
		
		//Senders not in the system, are ineligible for the following responses.
		if (unrecognizedClient || banned) {	
			possibleConfirmation 	= false;
			possibleHandshake 		= false;			//Several of these are probably redundant, but they act as a fail
			possibleBooking 		= false;			//safe if someone is not in the system. If the new client perception 
			possibleCheckIn 		= false;			//was flawed, however, it could cause a lot of problems here...
			possibleOutCall			= false;
			possibleServicesQuerry	= false;
			possibleRateInquiry		= false;
			possibleSpecialRequest	= false;
			possibleWebsiteIssue	= false;
			possibleComplaint 		= false; 
			possibleCompliment		= false;
			possibleCancel			= false;
			preferredClient 		= false; }

		//test if all the components are needed for a confirmation statement
		if (whens.isEmpty() || durations.isEmpty() || techs.isEmpty() || areas.isEmpty()) {
			possibleConfirmation = false; }
		else {
//			possibleBooking = false;		//uncomment when confirmation trigger is proven effective
			possibleConfirmation = true; }
		
		//check if a staffer	
		String stafferName = "empty";
		if (unrecognizedClient) {
			for (Technician tech : Technician.getTechs()) {
				if (tech.getSms() == Long.valueOf(words[0])) {
					stafferName = tech.getName();
					unrecognizedClient = false;
					staffer = true; }
			}
			for (Operator opr : Operator.getOperators()) {
				if (opr.getSms() == Long.valueOf(words[0])) {
					stafferName = opr.getName();
					unrecognizedClient = false;
					staffer = true; }				
			}		
		}
		else {	//if not a staffer or new client, checks client status
			if (targetRequest.getClient().getStatus().equalsIgnoreCase("ALISTER")) 	{ preferredClient = true; }
			if (targetRequest.getClient().getStatus().equalsIgnoreCase("BANNED")) 	{ banned = true; }
			staffer = false;
			possibleReference = false; }
				
//																***builds panel***
		setLayout(new BorderLayout());

		//builds header for message with client sms and status
		JPanel header = new JPanel();
			header.add(new JLabel(words[0]));
			
			//identifies sender's client status or (if staffer) their name
			if (staffer == false) {
				header.add(new JLabel(Client.checkStatusOf(Long.valueOf(words[0])))); }
			else {
				header.add(new JLabel(stafferName)); } 	//can turn this into 'else if' and add 'else' for recognized competitors later****
		add(header, BorderLayout.NORTH);

		//builds main message content
		JPanel body = new JPanel();
			body.setLayout(new GridLayout(0, 1));
			
			//sets possible responses and includes them in body
			if (staffer) {
				body.add(new JLabel("Number belongs to a staff member.")); }
			
			if (possibleHandshake) {
				body.add(new JLabel("Handshake Sequence"));
				if (targetRequest.getStart().isAfter(LocalDateTime.now().minusHours(1)) && targetRequest.getStart().isBefore(LocalDateTime.now().plusHours(1))) {
					JPanel bodyPanel = new JPanel();
						if (targetRequest.getLocation().getArea() != Site.OC) {
							bodyPanel.add(new JTextField("One moment."));
							bodyPanel.add(new JTextField("Ready?"));
							String siteCode = targetShift.getLocation().getRoomCode();
								if (siteCode.equalsIgnoreCase("XXX")) { siteCode = "SiteCode not found!"; }						
							bodyPanel.add(new JTextField("K, sending to " + siteCode + "."));
							bodyPanel.add(new JTextField(targetRequest.getTech().getName() + " is waiting in rm " + siteCode + ".")); }
						else {
							bodyPanel.add(new JTextField("One moment."));
							bodyPanel.add(new JTextField(targetRequest.getTech().getName() + ", how close are you to " + targetRequest.getLocation().getStreetAddress() + "?")); }
					body.add(bodyPanel); }
				else {
					possibleHandshake = false; }
			}
			
			if (possibleCancel) {
				body.add(new JLabel("Cancellation?"));
				body.add(new JTextField("Are you able to reschedule for a different date/time/provider?")); }

			if (possibleOutCall) {
				body.add(new JLabel("Possible OutCall"));
				body.add(new JTextField("At what location?")); }

			if (possibleConfirmation) {				
				//get confirmation values
				Technician requestedTech = new Technician();
				if (techs.size() == 1) { requestedTech = Technician.getTech(techs.get(0)); }
				else {	//assumes booking as muliple techs mentioned. Later can add Multi-Tech appointments but this is not compatible
					possibleConfirmation = false;
					possibleBooking = true; }
				
				Site requestedSite = new Site();
				if (areas.size() == 1) {
					if (areas.get(0) != Site.OC) { requestedSite = new Site(areas.get(0)); }
					else {
						//need to be able to establish outcall location; for now...
						requestedSite = new Site(Site.OC); }
				} else {	
					possibleConfirmation = false;	//assumes booking as multiple areas mentioned
					possibleBooking = true; }

				//gather date/time keywords and try to build service request start
				LocalDateTime start = LocalDateTime.now();
				LocalDateTime stop = LocalDateTime.now();
				if (whens.size() == 1) {
					start = whens.get(0);
					stop = start.plusHours(1); }
				else if (whens.size() == 2) {
					if (whens.get(0).isBefore(whens.get(1))) {
						start = whens.get(0);
						stop = whens.get(1); }
					else {
						start = whens.get(1);
						stop = whens.get(0); }
				}
				else if (whens.size() == 3) {
					//all same date
					if (whens.get(0).toLocalDate().equals(whens.get(1).toLocalDate()) && whens.get(0).toLocalDate().equals(whens.get(2).toLocalDate())) {
						if (whens.get(0).toLocalTime().isBefore(whens.get(1).toLocalTime()) && whens.get(0).toLocalTime().isBefore(whens.get(2).toLocalTime())) {
							start = whens.get(0);
							if (whens.get(1).toLocalTime().isAfter(whens.get(2).toLocalTime())) {
								stop = whens.get(1); }
							else {
								stop = whens.get(2); }
						}
						else if (whens.get(1).toLocalTime().isBefore(whens.get(0).toLocalTime()) && whens.get(1).toLocalTime().isBefore(whens.get(2).toLocalTime())) {
							start = whens.get(1);
							if (whens.get(0).toLocalTime().isAfter(whens.get(2).toLocalTime())) {
								stop = whens.get(0); }
							else {
								stop = whens.get(2); }
						}
						else {
							start = whens.get(2);
							if (whens.get(0).toLocalTime().isAfter(whens.get(1).toLocalTime())) {
								stop = whens.get(0); }
							else {
								stop = whens.get(1); }
						}
					}
					//three different dates
					else if (whens.get(0).toLocalDate() != whens.get(1).toLocalDate() && whens.get(0).toLocalDate() != whens.get(2).toLocalDate() && whens.get(1).toLocalDate() != whens.get(2).toLocalDate()) {
						possibleConfirmation = false;
						possibleBooking = true; }		
					//two different dates
					else {
						if (whens.get(0).toLocalDate().equals(whens.get(1))) {
							if (whens.get(0).toLocalTime().isBefore(whens.get(1).toLocalTime())) {
								start = whens.get(0);
								stop = whens.get(1); }
							else {
								start = whens.get(1);
								stop = whens.get(0); }
						}
						else if (whens.get(0).toLocalDate().equals(whens.get(2))) {
							if (whens.get(0).toLocalTime().isBefore(whens.get(2).toLocalTime())) {
								start = whens.get(0);
								stop = whens.get(2); }
							else {
								start = whens.get(2);
								stop = whens.get(0); }
						}
						else {
							if (whens.get(1).toLocalTime().isBefore(whens.get(2).toLocalTime())) {
								start = whens.get(1);
								stop = whens.get(2); }
							else {
								start = whens.get(2);
								stop = whens.get(1); }
						}
					}
				}
				else if (whens.size() == 4) {
					//isolate dates from times
					ArrayList<LocalDate> dates = new ArrayList<LocalDate>();
					ArrayList<LocalTime> times = new ArrayList<LocalTime>();

					for (LocalDateTime when : whens) {
						dates.add(when.toLocalDate());
						times.add(when.toLocalTime()); }
					
					//remove repeats and bad dates/times
					ArrayList<LocalDate> datesToDelete = new ArrayList<LocalDate>();				//starting with dates	
					for (LocalDate date : dates) {
						boolean repeat = false;
						for (LocalDate checkDate : dates) {
							if (date.equals(checkDate)) { repeat = true; }
						}
						if (date.getYear() == 1 || repeat) { datesToDelete.add(date); }
					}
					dates.removeAll(datesToDelete); 
					
					ArrayList<LocalTime> timesToDelete = new ArrayList<LocalTime>();				//repeat with times
					for (LocalTime time : times) {
						boolean repeat = false;
						for (LocalTime checkTime : times) {
							if (time.equals(checkTime)) { repeat = true; }
						}
						if (time.getNano() == 3 || repeat) { timesToDelete.add(time); }
					}
					times.removeAll(timesToDelete); 
					
					//build start and stop times
					if (dates.size() == 1) {
						if 		(times.size() == 1) { start = LocalDateTime.of(dates.get(0), times.get(0)); }
						else if	(times.size() == 2) {
							if (times.get(0).isBefore(times.get(1))) {
								start = LocalDateTime.of(dates.get(0), times.get(0));
								stop  = LocalDateTime.of(dates.get(0), times.get(1)); }
							else {
								start = LocalDateTime.of(dates.get(0), times.get(1));
								stop  = LocalDateTime.of(dates.get(0), times.get(0)); }
						}
						else {
							//should check for available times here and determine best time...
							//for now:
							possibleBooking = false; }
					}
					else {
						//should check for multiple date availabilities, but for now...
						possibleBooking = false; }
				}
				else {
					possibleConfirmation = false;
					possibleBooking = true; }
				
				//convert start to string
				String displayedStart = "";
				if (start.isAfter(LocalDateTime.now().minusDays(1))) {
					if 		(start.isBefore(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN))) { displayedStart = "today"; }
					else if (start.isBefore(LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MIN))) { displayedStart = "tomorrow"; }
				}
				else if (start.getYear() == 0) {
					displayedStart = start.getDayOfWeek().toString(); }
				else {
					displayedStart = start.format(DateTimeFormatter.ofPattern("dd MMM")); }
				
				//determine duration
				String requestedDuration;
				if (durations.isEmpty() || durations.size() != 1) {
					requestedDuration = ((stop.toLocalTime().toSecondOfDay() - start.toLocalTime().toSecondOfDay()) / 360.0) + " hrs"; }
				else {
					requestedDuration = durations.get(0); }
				
				//verify availability here...
								
				//if still a potential confirmation
				if (possibleConfirmation) { 
					String confirmationStatement = "Can you confirm " + requestedDuration + " with " + requestedTech.getName() + " at " + start.toLocalTime().toString() + " on " + displayedStart + " in " + requestedSite.getCity() + "?";
					body.add(new JLabel("Confirmation Statement"));

					//remove this warning after availability verification is built/tested
					JLabel warning = new JLabel("WARNING: System not verifying availability of request yet!");
						warning.setForeground(Color.RED);
						warning.setFont(warning.getFont().deriveFont(Font.BOLD));
					body.add(warning);
					
					body.add(new JTextArea(confirmationStatement)); }				
			}
			
			if (possibleBooking) {
				body.add(new JLabel("Potential Booking"));
			
				//if no times are mentioned (whens == empty)
				if (whens.isEmpty() && durations.isEmpty()) { 
					body.add(new JTextField("What time/duration are you inquiring about booking? I can let you know if that’s available.")); }
				else if (whens.isEmpty()) {
					body.add(new JTextField("What time are you inquiring about booking? I can let you know if that’s available.")); }
				else if (durations.isEmpty()) {
					body.add(new JTextField("What duration?"));	}

				//responding to any tech/area sender brought up
				if (techs.isEmpty() == false) {															//should use areas to determine 
					for (Availability shift : Availability.getAvailabilities(LocalDate.now())) {		//which provider, eventually
						if (techs.contains(shift.getTech().getName())) {
							body.add(new JTextField(shift.getTech().getName() + " is at " + shift.getLocation().getComments())); }				
					}
				}
				if (areas.isEmpty() == false) {
					for (int area : areas) {
						for (Availability shift : Availability.getAvailabilities(LocalDate.now())) {
							//checks that shift area matches up and shift hasn't already been mentioned (from techs above)
							if (shift.getLocation().getArea() == area && techs.contains(shift.getTech().getName()) == false) {
								body.add(new JTextField(shift.getTech().getName() + " is at " + shift.getLocation().getComments())); }				
						}										
					}
				} 
			}

			if (possibleReference) {
				body.add(new JLabel("Reference Check"));
				body.add(new JTextField("I’d be happy to help you, but we are very careful with our clients’ information. Can you please supply a link to a site/ad which matches your information? Thanks in advance."));
				if (contactInfo.isEmpty()) {
					body.add(new JTextField("He has booked with us in the past, without incident."));
					body.add(new JTextField("We have not yet booked with this client."));
					body.add(new JTextField("He has been BANNED. We do not recommend booking him."));
					body.add(new JTextField("I’m sorry, I don’t recognize that contact information.")); }
				else {
					//should check from contactInfo to determine appropriate response
					boolean isBanned = false;
					boolean isMember = false;
					boolean isApprov = false;
					
					for (String info : contactInfo) {
						if (info.equalsIgnoreCase("DIGITSOFF")) {
							body.add(new JTextField("Please supply the client's phone (with area code) and email.")); }
						else if (info.contains("@")) {
							for (Client client : Client.getClients()) {
								if (client.getEmail().equalsIgnoreCase(info)) {
									if (client.getStatus().equalsIgnoreCase("BANNED")) { isBanned = true; }
									else if (client.getStatus().equalsIgnoreCase("MEMBER") || client.getStatus().equalsIgnoreCase("ALISTER")) { 
										isMember = true; }
									else if (client.getStatus().equalsIgnoreCase("APPROVED")) { isApprov = true; }
								}
							}
						}
						else {
							try {
								for (Client client : Client.getClients()) {
									if (Long.valueOf(info) == client.getSms()) {
										if (client.getStatus().equalsIgnoreCase("BANNED")) { isBanned = true; }
										else if (client.getStatus().equalsIgnoreCase("MEMBER") || client.getStatus().equalsIgnoreCase("ALISTER")) { 
											isMember = true; }
										else if (client.getStatus().equalsIgnoreCase("APPROVED")) { isApprov = true; }
									}
								}
							} catch (NumberFormatException e) { /*e.printStackTrace();*/}
							
							if (isBanned) {
								body.add(new JTextField("He has been BANNED. We do not recommend booking him.")); }
							if (isMember) {
								body.add(new JTextField("He has booked with us in the past, without incident.")); }
							else if (isApprov) {
								body.add(new JTextField("We have not yet booked with this client.")); }
							else {
								body.add(new JTextField("I’m sorry, I don’t recognize that contact information.")); }
						}
					}
				}
			}
			
			if (possibleCheckIn) {
				body.add(new JLabel("Site Assignment"));
				if (targetShift.getComments().equalsIgnoreCase("No Comments")) {
					body.add(new JTextField("We will let you know as soon as we hear back from " + targetShift.getTech().getName() + " about it."));
					if (targetShift.getLocation().getArea() == Site.SF || targetShift.getLocation().getArea() == Site.SB) { 
						body.add(new JTextField("Hotel prices are prohibitively high today. " + targetShift.getTech().getName() + " would be able to see you for an outcall or nearby hot tubs?")); }
					else {
						body.add(new JTextField("Hotel prices are prohibitively high today. " + targetShift.getTech().getName() + " would be able to see you for an outcall?")); }
				} else {
					body.add(new JTextField(targetShift.getTech().getName() + " will be at " + targetShift.getComments() + " today for your " + targetRequest.getStart().toLocalTime().format(DateTimeFormatter.ofPattern("hh:mma")) + "Text this number when you arrive for the room number.")); }
			}
			if (possibleServicesQuerry){
				body.add(new JLabel("Info Querry"));
				body.add(new JTextField("The website has all available information about each provider, as well as pictures and links to any reviews.")); }

			if (possibleRateInquiry) {
				body.add(new JLabel("Rate Inquiry"));
				body.add(new JTextField("That would be $" + targetShift.getTech().getPublicRate() + ".")); }
			
			if (possibleSpecialRequest) {
				body.add(new JLabel("Special Request"));
				body.add(new JTextField("I’ll have to ask her. Let me get back to you.")); }
			
			if (possibleWebsiteIssue) {
				body.add(new JLabel("Website Authentication"));
				body.add(new JTextField("Each member’s account has a unique password that can be set/recovered at XOReferrals.com."));
				body.add(new JTextField("We approve member accounts after they’ve completed their first successful booking."));
				body.add(new JTextField("I just need the email address you used to make your account, and I can approve it right now."));
				body.add(new JTextField("I’ve just approved your account.")); }
			
			if (possibleCompliment) {
				body.add(new JLabel("Compliment"));
				body.add(new JTextField("Great! I know she'd appreciate that being mentioned in a review.")); }
			
			if (possibleComplaint) {
				body.add(new JLabel("Complaint"));
				body.add(new JTextField("I’m sorry to hear that. Is there anything else you’d like to add before I let Samantha know?")); }
			
			if (banned) {
				JLabel label = new JLabel("WARNING: Client Banned!");
					label.setForeground(Color.RED);
					label.setFont(label.getFont().deriveFont(Font.BOLD));
				body.add(label);
				body.add(new JLabel("Do not respond.")); }

			if (preferredClient) {
				body.add(new JLabel("A-Lister"));
				body.add(new JTextField("Thanks for your long patronage! You are one of a select few who can continue to see providers from the Front Page section at Mystery Lady rates.")); }
			
			if (unrecognizedClient) {
				body.add(new JLabel("New Client"));
				body.add(new JTextField("I’m not seeing you on our client list. Have you booked with us before?"));
				//will need to count messages from newClient and check contactInfo for email
				body.add(new JTextField("What’s your email? I can also try and find your last appointment, if you remember when it was.")); 
				body.add(new JTextField("Thanks for your interest. Our screening process is quick and easy. Are you a P411 member?")); }
			
			//when no responses have been offered...
			if (possibleHandshake 		== false && possibleBooking 	== false && possibleReference 		== false && 
				possibleServicesQuerry	== false && possibleRateInquiry == false && possibleSpecialRequest 	== false &&
				possibleWebsiteIssue	== false && possibleCompliment 	== false && possibleComplaint 		== false && 
				unrecognizedClient 		== false && possibleCheckIn 	== false && 
				preferredClient 		== false && staffer 			== false && banned == false) {
					body.add(new JLabel("No Suggested Messages...")); }
			
			//buffer from footer
			body.add(new JLabel());
		add(body, BorderLayout.CENTER);
		
		//builds custom input, for when responses fail
		JPanel footer = new JPanel();
			footer.setLayout(new GridLayout(0, 1));
//			footer.setLayout(new GridLayout(0, 2));
			footer.setBackground(Color.GREEN);
			footer.setToolTipText("Only use this panel if there are no appropriate responses above.");

			//text headers for the fields
			footer.add(new JLabel("Failed Message:"));
//			footer.add(new JLabel("Custom Response:"));

			//records the message that failed
			final JTextField evasiveMessage = new JTextField();
				evasiveMessage.setToolTipText("Only use this field if there are no appropriate responses above.");
				evasiveMessage.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						try {				
							if (failureLog.exists() == false) { failureLog.createNewFile(); }
							FileWriter appender = new FileWriter(failureLog, true);
							appender.write("FAILED_MESSAGE:" + evasiveMessage.getText().trim() + "\n");
							appender.close(); }
						catch (IOException e) {}
					}
				});
			footer.add(evasiveMessage);

/*			//records the response generated by the operator
			final JTextField generatedResponse = new JTextField();
				generatedResponse.setToolTipText("Only use this field if there are no appropriate responses above.");
				generatedResponse.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						try {				
							if (failureLog.exists() == false) { failureLog.createNewFile(); }
							FileWriter appender = new FileWriter(failureLog, true);
							appender.write("MESSAGE_RESPONSE:" + generatedResponse.getText().trim() + "\n");
							appender.close(); }
						catch (IOException e) {}
					}
				});
			footer.add(generatedResponse);*/

		add(footer, BorderLayout.SOUTH); }
	
	//utility to quickly determine if integer
	public static boolean isInteger(String str) {
		if (str == null) {
			return false; }
		
		int length = str.length();
		if (length == 0) {
			return false; }
		
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false; }
			i = 1; }
		
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false; }
		}
		return true; }
	
	//utility to quickly determine month
	public static int monthOf(String str) {
		int i = -1;
		String month = str.toUpperCase();
		
		if 		(month.equalsIgnoreCase("JAN")) { i =  1; }
		else if (month.equalsIgnoreCase("FEB")) { i =  2; }
		else if (month.equalsIgnoreCase("MAR")) { i =  3; }
		else if (month.equalsIgnoreCase("APR")) { i =  4; }
		else if (month.equalsIgnoreCase("MAY")) { i =  5; }
		else if (month.equalsIgnoreCase("JUN")) { i =  6; }
		else if (month.equalsIgnoreCase("JUL")) { i =  7; }
		else if (month.equalsIgnoreCase("AUG")) { i =  8; }
		else if (month.equalsIgnoreCase("SEP")) { i =  9; }
		else if (month.equalsIgnoreCase("OCT")) { i = 10; }
		else if (month.equalsIgnoreCase("NOV")) { i = 11; }
		else if (month.equalsIgnoreCase("DEC")) { i = 12; }
		
		return i; }
}
