package interfaceComponents;

import javax.swing.*;

import data.*;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ManageClient extends JFrame {
	private static final long serialVersionUID = -6478375427058843638L;
	private JComboBox<String> combo = new JComboBox<String>();
	private JTextField field = new JTextField("(10 digit SMS #)", 8);
	private JTextField comments = new JTextField("No Comments...", 18);
	private JTextField email = new JTextField("replace@email.com", 12);
	private JLabel details = new JLabel("...invalid SMS", JLabel.CENTER);
	private JLabel induction = new JLabel(LocalDate.now().toString());
	private JLabel lastWin = new JLabel("N/A");
	private JLabel lastFail = new JLabel("N/A");
	private Client targetClient = new Client();
	private JPanel top = new JPanel();
	private JButton save = new JButton("Create/Update");

	public ManageClient() {  //should be modified to pass branch...
		setLayout(new GridLayout(0, 1));
		setLocation(700, 70);
		
		//builds header with SMS number input
		top.add(new JLabel("Target SMS: ", JLabel.LEFT));
		top.add(new JLabel("               "));
		field.setEnabled(true);
		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ArrayList<Client> clientList = Client.getClients();
				Boolean test = false;
				for (Client c : clientList) {
					try {
						if (Long.valueOf(field.getText()) == c.getSms()) { 
							test = true;
							targetClient = c;
							updatePanel(c); }				
						} catch (NumberFormatException e) { test = false; }
				}
				if (test.equals(false)) {
					updatePanel(); }
			}
		});
		top.add(field);
		top.setAlignmentX(RIGHT_ALIGNMENT);
		add(top);
		
		//builds panel with email and status
		JPanel statusPanel = new JPanel();
			email.setToolTipText("eMail");
			email.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					targetClient.setEmail(email.getText().trim()); }
			});
			email.setEnabled(true);
			statusPanel.add(email);
			combo.addItem("UNKNOWN"); 
			combo.addItem("APPROVED");
			combo.addItem("MEMBER"); 
			combo.addItem("ALISTER"); 
			combo.addItem("DEFUNCT"); 
			combo.addItem("BANNED"); 
			combo.setToolTipText("Status");
			combo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (combo.getSelectedItem().equals("BANNED")) {
						int response = JOptionPane.showConfirmDialog(null, "WARNING: Ban " + targetClient.getSms() + "?!");
						if (response == JOptionPane.OK_OPTION) {
							targetClient.setStatus("BANNED");
							JOptionPane.showMessageDialog(null, "WebSite Access must be REMOVED manually!!");
							for (ServiceRequest s : ServiceRequest.getServiceRequests()) {
								if (s.getClient().getSms() == targetClient.getSms() || s.getClient().getEmail().equals(targetClient.getEmail())) {
									s.getClient().setStatus("BANNED");
									if (s.getStatusCycle() == ServiceRequest.CONFRM) {
										s.setStatusCycle(ServiceRequest.CANCEL); }
								}
							}
						}
					}
					else {
						targetClient.setStatus(combo.getSelectedItem().toString()); }
				}
			});
			statusPanel.add(combo);
		add(statusPanel);
		
		//Client Details
		details.setToolTipText("Average Duration / Closure Rate / Service Requests");
		add(details);
		
		//Green (last CLOSED) and Red (last CANCEL) fields
		JPanel lastsPanel = new JPanel();
			JPanel indoc = new JPanel();
				indoc.setBackground(Color.GRAY);
				indoc.setToolTipText("Inducted to System Date");
				indoc.add(induction);
			lastsPanel.add(indoc);
			JPanel success = new JPanel();
				success.setBackground(Color.GREEN);
				success.setToolTipText("Last CLOSED Service Request");
				success.add(lastWin);
			lastsPanel.add(success);
			JPanel failure = new JPanel();
				failure.setBackground(Color.RED);
				failure.setToolTipText("Last CANCEL Service Request");
				failure.add(lastFail);
			lastsPanel.add(failure);
		add(lastsPanel);

		//add comments
		comments.setToolTipText("Comments");
		comments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				targetClient.setComments(comments.getText().trim()); }
		});
		add(comments);
		
		JPanel footerPanel = new JPanel();  
			footerPanel.add(new JLabel("            "));
			JButton cancel = new JButton("Cancel");
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) { exitFrame(); }
				});
			footerPanel.add(cancel);
			save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					Client.addData(targetClient);
					exitFrame(); }
			});
			footerPanel.add(save);
		add(footerPanel);
		
		pack();
		setVisible(true); }
	
	public void exitFrame() { this.dispose(); }
	
	public void updatePanel(Client target) {
		//checks for and sets up create vs update member
		field.setForeground(Color.GREEN); 
		save.setText("Update");
		save.revalidate();
		save.repaint();

		//update fields
		email.setText(target.getEmail());
		combo.setSelectedIndex(target.getIndex());
		targetClient = target;
		details.setText(targetClient.getStats());
		induction.setText(targetClient.getInductionDate().toString());
		if (targetClient.getLastSuccess().getDate().equals(LocalDate.now().minusDays(180))) {
			lastWin.setText("Never"); }
		else {
			lastWin.setText(DateTimeFormatter.ofPattern("dd MMM yyyy").format(targetClient.getLastSuccess().getDate())); }
		if (targetClient.getLastUnsuccess().getDate().equals(LocalDate.now().minusDays(180))) {
			lastFail.setText("Never"); }
		else {
			lastFail.setText(DateTimeFormatter.ofPattern("dd MMM yyyy").format(targetClient.getLastUnsuccess().getDate())); }
		pack(); }
	
	public void updatePanel() {
		//checks for and sets up create vs update member
		field.setForeground(Color.RED);
		save.setText("Create");
		save.revalidate();
		save.repaint();
		
		email.setText("replace@email.com");
		combo.setSelectedItem("UNKNOWN");
		try {
			if (Long.valueOf(field.getText()) != null) {
				details.setText("New Client?");
				targetClient = new Client(email.getText(), Long.valueOf(field.getText())); }
			else { 
				if (Long.valueOf(field.getText()) > 999999999 && Long.valueOf(field.getText()) < 10000000000L) { 
					details.setText("New Client?"); } 
				else { details.setText("...invalid SMS #"); } 
				}
			} catch (NumberFormatException e) { details.setText("...invalid SMS #"); }
		pack(); }
}