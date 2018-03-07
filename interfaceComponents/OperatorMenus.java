package interfaceComponents;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.io.*;

import javax.swing.*;

import controls.*;
import data.*;

public class OperatorMenus extends GalileoMenus {
	private static final long serialVersionUID = -3545956805482107813L;

	//instance variables
	private JMenu operatorMenu 			= new JMenu("Operator");
//	private JMenuItem verifyItem 		= new JMenuItem("Verify Client");
	private JMenuItem manageItem 		= new JMenuItem("Manage Client");
	private JMenuItem searchClient 		= new JMenuItem("Look up eMail");
	private JMenuItem checklistItem 	= new JMenuItem("Daily Checklist");
	private JMenuItem accountItem 		= new JMenuItem("Accounts");
	private JMenuItem linksItem 		= new JMenuItem("Links / Forums");
	private JMenuItem locationServices 	= new JMenuItem("Location Services");
	private JMenuItem hiringAds			= new JMenuItem("Hiring Ads Primer");
	
	private JMenu languageLayer 		= new JMenu("Communications");
	private JMenuItem scriptItem 		= new JMenu("Scripted Responses");
	private JMenuItem quickScript		=	new JMenuItem("Cheat Sheet");
	private JMenuItem slowScript		=	new JMenuItem("Full Script");
	private JMenuItem wakeProviders 	= new JMenuItem("Sign On w/Techs");
	private JMenuItem wakeClients 		= new JMenuItem("Check In w/Clients");
	private JMenuItem decrypter 		= new JMenuItem("Incoming Message (beta)");

	//constructors
	public OperatorMenus() {
		BasicBUIlder();
		
		//build Operator Menu
/*		verifyItem.addActionListener(new VerifyClient());
		verifyItem.setEnabled(false);
		operatorMenu.add(verifyItem);*/

		manageItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ManageClient frame = new ManageClient();
				frame.setTitle("Manage Client");
				frame.setVisible(true); }
		});		
		operatorMenu.add(manageItem);

		searchClient.addActionListener(new SearchEmail());
		operatorMenu.add(searchClient);
		
		checklistItem.addActionListener(new SidebarCaller(SidebarCaller.CHECKLIST));
		operatorMenu.add(checklistItem);
		
		//sidebar items
		operatorMenu.addSeparator();
		
		accountItem.addActionListener(new SidebarCaller(SidebarCaller.ACCOUNTS));
		operatorMenu.add(accountItem);
		
		//linksItem.addActionListener(new SidebarCaller(SidebarCaller.LINKS));
		linksItem.setEnabled(false);
		operatorMenu.add(linksItem);
		
		locationServices.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				if (Desktop.isDesktopSupported()) {
				    try {
				        Desktop.getDesktop().open(new File("./com.lingotechsolutions.data/LocationServices.pdf"));
				    } catch (IOException ex) { /* no application registered for PDFs */ }
				}
			}
		});
		operatorMenu.add(locationServices);
	
		hiringAds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (Desktop.isDesktopSupported()) {
				    try {
				        Desktop.getDesktop().open(new File("./com.lingotechsolutions.data/HiringAds.pdf"));
				    } catch (IOException ex) { /* no application registered for PDFs */ }
				}
			}
		});
		operatorMenu.add(hiringAds);
		
		add(operatorMenu); 
		
		//build scripts sub menu
		quickScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new ScriptedResponse();	}
		});
		scriptItem.add(quickScript);

		slowScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (Desktop.isDesktopSupported()) {
				    try {
				        Desktop.getDesktop().open(new File("./com.lingotechsolutions.data/LongFormScript.pdf"));
				    } catch (IOException ex) { /* no application registered for PDFs */ }
				}
			}
		});
		scriptItem.add(slowScript);

		languageLayer.add(scriptItem);
		languageLayer.addSeparator();

		//build rest of language layer menu
		wakeProviders.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//constructs copy/pastable message for JOptionPane.show
				JPanel message = new JPanel();
					message.setLayout(new GridLayout(0, 1));
					message.setBackground(Color.ORANGE);

					//loads details for each shift's technician/etc...
					for (Availability shift : Availability.getAvailabilities(LocalDate.now())) {
						JPanel panel = new JPanel();
							panel.setBackground(Color.ORANGE);
							panel.add(new JLabel(shift.getTech().getSms() + ": "));
							//JTextField field = new JTextField("Good Morning, " + shift.getTech().getName());
							panel.add(new JTextField("Good Morning, " + shift.getTech().getName() + "."));
						message.add(panel); }
					
				//prints results
				JOptionPane.showMessageDialog(null, message, "Sign On w/Service Providers", JOptionPane.PLAIN_MESSAGE); }
		});
		languageLayer.add(wakeProviders);

		wakeClients.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//constructs copy/pastable message for JOptionPane.show
				JPanel message = new JPanel();
					message.setLayout(new GridLayout(0, 1));
					message.setBackground(Color.ORANGE);

					//loads details for each pre-booked service request
					for (ServiceRequest req : ServiceRequest.getServiceRequests(LocalDate.now())) {
						JPanel panel = new JPanel();
							panel.setBackground(Color.ORANGE);
							panel.add(new JLabel(req.getClient().getSms() + ": "));							 
							panel.add(new JTextField("Today, " + req.getTech().getName() + " will be at " + req.getTech().getSite(LocalDate.now())));
						message.add(panel); }
				
				//prints results
				JOptionPane.showMessageDialog(null, message, "Sign On w/Today's Clients", JOptionPane.PLAIN_MESSAGE); }
		});
		languageLayer.add(wakeClients);

		//message decrypter beta
		languageLayer.addSeparator();
		decrypter.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				String response = JOptionPane.showInputDialog(null, "Paste Incoming Message (including sms number):", "Decryptor (beta)", JOptionPane.PLAIN_MESSAGE);

				//dercypt response/return possible replies
				if (response != null) {
					JOptionPane.showMessageDialog(null, new MessageDecryption(response), "Potential Responses", JOptionPane.PLAIN_MESSAGE); }
			}
		});
		languageLayer.add(decrypter);
		add(languageLayer); }	
}

class SidebarCaller extends AbstractAction {
	private static final long serialVersionUID = 2444120456462492402L;

	public static final int ACCOUNTS = 0;
	public static final int CHECKLIST = 1;
	public static final int LINKS = 2;
	public static final int MANAGE = 3;
	public static final int SCRIPT0 = 4;
	public static final int SCRIPT1 = 5;
	
	private int selection;
	
	public SidebarCaller(int i) { selection = i; }
	public SidebarCaller() { this(ACCOUNTS); }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SideBar frame = new SideBar(selection);
		
		if (selection == ACCOUNTS	) { frame.setTitle("Accounts"); }
		if (selection == CHECKLIST	) { frame.setTitle("Checklist"); } 
		if (selection == LINKS		) { frame.setTitle("links2MONITOR"); }
		if (selection == CHECKLIST	) { frame.setTitle("Manage Clients"); }
		if (selection == SCRIPT0	) { frame.setTitle("Language Layer"); }
		
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
		frame.setVisible(true); }
}

