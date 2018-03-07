package controls;

import javax.swing.*;

import java.awt.*;
//import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import data.*;

public class LogIn {
	private Operator user = new Operator();
	private Boolean test = false;
	private JTextField userName = new JTextField(15);
	private JPasswordField phrase = new JPasswordField(15);
	
	public LogIn() {
		JPanel message = new JPanel();
			message.setLayout(new GridLayout(0, 1));
			
			JPanel account = new JPanel();
				account.add(new JLabel("User:        ", JLabel.RIGHT));
				account.add(userName);
			message.add(account);
			
			JPanel pass = new JPanel();
				pass.add(new JLabel("Password:", JLabel.CENTER));
				pass.add(phrase);
			message.add(pass);
			
			JCheckBox newAccount = new JCheckBox("Create New Account");
			message.add(newAccount);		
		int response = JOptionPane.showConfirmDialog(null, message, "Welcome", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (response == JOptionPane.OK_OPTION) {
			user.setUserName(userName.getText());
			user.setName(userName.getText());
			//char[] p = phrase.getPassword();
			user.setPassPhrase(phrase.getPassword());
			if (newAccount.isSelected()) { Operator.addData(user); }
			else {
				ArrayList<Operator> ops = Operator.getOperators();
				for (Operator o : ops) {
					if (o.getUserName().equals(user.getUserName()) && o.getPassPhrase().equals(user.getPassPhrase())) {
						if (o.getAccessPrivs().equals("ADMIN") || o.getAccessPrivs().equals("OPRTR")) { 
							user = o;
							test = true; }
					}
				}
				if (test) {
					try { 
						File f = new File("./com.lingotechsolutions.data/log");
						if (f.exists() == false) { f.createNewFile(); }
						FileWriter appender = new FileWriter(f, true);
						appender.write(user.getName() + "__" + LocalDateTime.now().toString() + "__IN\n");
						appender.close(); }
					catch (IOException e) { e.printStackTrace(); }
				}
				else { JOptionPane.showMessageDialog(null, "Account / Password not recognized!"); } 					
			}
		}
	}
	
	public Operator getUser() {
		return user; }
	public Boolean getTruth() {
		return test; }
}
