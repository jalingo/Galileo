package controls;

import java.text.*;
import java.time.*;
import java.util.*;
import java.util.regex.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import data.*;

public class ImportEinstienData {
	//instance variables
	private JFileChooser chooser = new JFileChooser();
	private String DELIM = "__";	
	private String[] field = new String[15];

	//constructors
	public ImportEinstienData() {}
		
	//methods
	public void ImportChooser() throws IOException, ParseException {
		//File Chooser window
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileNameExtensionFilter("Einstien Data", "mbr", "tch", "opr", "db"));
		int result = chooser.showOpenDialog(null);
		
		//After righteous decision
		if (result == JFileChooser.APPROVE_OPTION) { 
			File[] targetFiles = chooser.getSelectedFiles();
			for (int i = 0; i < targetFiles.length; i++) {
				List<String> lines = Files.readAllLines(Paths.get(targetFiles[i].getPath()), StandardCharsets.US_ASCII);
System.out.print(".");
				//"Checking for .tch files...");
				Pattern pattern = Pattern.compile(".*tch");
				Matcher matcher = pattern.matcher(chooser.getDescription(targetFiles[i]));
				if (matcher.matches()) {
					String fields = lines.get(0);
					for (int x = 0; x < 4; x++) { field[x] = Arrays.asList(fields.split(DELIM, 4)).get(x); }
					Technician newTech = new Technician(field[1].replace("-", " "), Long.valueOf(field[2]), field[3]);

					//".tch file found!");
					Technician.addData(newTech); 
					for (int y = 1; y < lines.size(); y++) {
						fields = lines.get(y);
						for(int x = 0; x < 6; x++) { field[x] = Arrays.asList(fields.split(DELIM, 6)).get(x); }

						Availability newAvailability = new Availability(newTech);
						Site newSite = new Site(new MailingAddress(field[2], field[1], "CA"));
						newSite.setSiteCosts(Double.parseDouble(field[4]));
						if (field[3] != "XXX") { newSite.setRoomCode(field[3]); }
						newAvailability.setLocation(newSite);
						newAvailability.convertDate(field[0]);
						newAvailability.setAreaByString(field[1]);
						newAvailability.setComments(field[5]);

						Availability.addData(newAvailability); }
				}
				
				//"Checking for .mbr files...");				
				pattern = Pattern.compile(".*mbr");
				matcher = pattern.matcher(chooser.getDescription(targetFiles[i]));
				if (matcher.matches()) {
					//".mbr found!");
					String fields = lines.get(0);
					for (int x = 0; x < 7; x++) { field[x] = Arrays.asList(fields.split(DELIM, 7)).get(x); }
					Client newClient;

					try {
						newClient = new Client(field[1], Long.parseLong(field[0]), field[2]);
					
						if (field[3].equals("CONTACT")) { newClient.setContactEmail(true); }
							else { newClient.setContactEmail(false); }
						newClient.setComments(field[6]);
						if (field[4].length() < 7) {
							SimpleDateFormat corruptDate = new SimpleDateFormat("ddMMM");
							Date c = corruptDate.parse(field[4]);
							field[4] = c.toString().substring(4, 7) + "2015"; }
						SimpleDateFormat dateFormat = new SimpleDateFormat("MMMyyyy");
						Date d = new Date();
						if (field[4].equals("PRE2014")) { d = dateFormat.parse("Jan2014"); }
							else { d = dateFormat.parse(field[4]); }
						LocalDate l = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(d));
						newClient.setInductionDate(l);	

						Client.addData(newClient);

						for (int y = 1; y < lines.size(); y++) {
							fields = lines.get(y);
							for(int x = 0; x < 10; x++) { field[x] = Arrays.asList(fields.split(DELIM, 10)).get(x); }
							Technician newTech = Technician.getTech(field[4]);
							SimpleDateFormat timeFormat = new SimpleDateFormat("yyMMdd hhmma");			
							Instant instant0 = Instant.ofEpochMilli(timeFormat.parse(field[0] + " " + field[1]).getTime());
							Instant instant1 = Instant.ofEpochMilli(timeFormat.parse(field[0] + " " + field[2]).getTime());
						
							ServiceRequest request = new ServiceRequest(newClient, newTech);
							request.setAreaByString(field[3]);
							request.setStart(LocalDateTime.ofInstant(instant0, ZoneId.systemDefault()));
							request.setStop(LocalDateTime.ofInstant(instant1, ZoneId.systemDefault()));
							request.setStatusCycleByString(field[5]);
							request.setComments(field[7].replace("-", " "));
							int stringLengthCap = field[8].length();
							if (stringLengthCap > 7) { stringLengthCap = 7; }
							request.setInitiator(new Operator(field[8].substring(0, stringLengthCap)));
							request.setConfirmer(new Operator(field[8].substring(0, stringLengthCap)));
							stringLengthCap = field[9].length();
							if (stringLengthCap > 7) { stringLengthCap = 7; }
							request.setCloser(new Operator(field[9].substring(0, stringLengthCap)));

							if (y == lines.size() - 1 && request.getStatusCycle() == ServiceRequest.CLOSED) { 
								newClient.setLastSuccess(request); 
								Client.addData(newClient); }
							else if (y == lines.size() - 1 && request.getStatusCycle() != ServiceRequest.CLOSED) {
								newClient.setLastUnsuccess(request); 
								Client.addData(newClient); }
							
							ServiceRequest.addData(request); } 
						}
						catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(null, "Import failed at: " + targetFiles[i].getPath()); }
					}
				
				//"Checking for .opr files...");					
				pattern = Pattern.compile(".*opr");
				matcher = pattern.matcher(chooser.getDescription(targetFiles[i]));
				if (matcher.matches()) {
					String fields = lines.get(0);
					for (int x = 0; x < 6; x++) { field[x] = Arrays.asList(fields.split(DELIM, 6)).get(x); }
					Operator newOperator = new Operator(field[0]);

					newOperator.setAccessPriv(field[3]);
					newOperator.setPayRate(Double.valueOf(field[5]));
					newOperator.setUserName(field[0]);
					newOperator.setPassPhrase(field[1]); 

					Operator.addData(newOperator); }

				//"Checking for ACCTS.db..."):
				pattern = Pattern.compile("ACCTS.db");
				matcher = pattern.matcher(chooser.getDescription(targetFiles[i]));
				if (matcher.matches()) {
					//"ACCTS.db found!");
					for (int x = 0; x < lines.size(); x++) {
						String fields = lines.get(x);
						for (int y = 0; y < 3; y++) { field[y] = Arrays.asList(fields.replace("-", " ").split(DELIM, 3)).get(y); }
						Account newACCT = new Account(field[0], field[1], field[2]);
						newACCT.setId(x);
						Account.addData(newACCT); }
				}
				
				//"Checking for FORUM.db...");
				pattern = Pattern.compile("FORUM.db");
				matcher = pattern.matcher(chooser.getDescription(targetFiles[i]));
				if (matcher.matches()) {
					//"FORUM.db found!");
					for (int x = 0; x < lines.size(); x++) {
						String fields = lines.get(x);
						for (int y = 0; y < 4; y++) {
							field[y] = Arrays.asList(fields.replace("-", " ").split(DELIM, 4)).get(y); }
						Link l = new Link(field[0], field[1], field[2], field[3]);
						
						Link.addData(l); }			
				}
				
				//"Checking for CHLST.db...");
				pattern = Pattern.compile("CHLST.db");
				matcher = pattern.matcher(chooser.getDescription(targetFiles[i]));
				if (matcher.matches()) {
					//"CHLST.db found!");
					for (int x = 0; x < lines.size(); x++) {
						String fields = lines.get(x);
						for (int y = 0; y < 5; y++) {
							field[y] = Arrays.asList(fields.replace("-", " ").split(DELIM, 5)).get(y); }
						Checklist list = new Checklist(field[0]);
						
						Checklist.addData(list); }
				}
						
				//"Checking for SITES.db...");
				pattern = Pattern.compile("SITES.db");				
				matcher = pattern.matcher(chooser.getDescription(targetFiles[i]));
				if (matcher.matches()) {
					//"SITES.db found!");
					for (int x = 0; x < lines.size(); x++) {
						String fields = lines.get(x);
						for (int y = 0; y < 7; y++) { field[y] = Arrays.asList(fields.replace("-", " ").split(DELIM, 7)).get(y); }
						Site newSite = new Site();
						String[] addr = field[2].split(",", 2);

						newSite.setStreetAddress(addr[0]);
						newSite.setAreaByString(field[1]);
						newSite.setComments(field[0] + "\n" + field[6]);
						newSite.setSiteCosts(Double.valueOf(field[5]));
						if (field[1].equals("OC")) { newSite.setOnSite(true); }
							else { newSite.setOnSite(false); }

						Site.addData(newSite); }
				}
			}
//System.out.println("COMPLETE!");
JOptionPane.showConfirmDialog(null, "Complete!", "Import Data...", JOptionPane.OK_CANCEL_OPTION); }
	}
}