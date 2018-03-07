package controls;

import interfaceComponents.gInterface;

import java.awt.*;
import java.awt.event.*;

//import java.util.*;
import javax.swing.*;

import data.gAccount;

public class gMenu extends JFrame {
	private static final long serialVersionUID = 595826608425867518L;
	private	JButton newAcct = new JButton("Create New Connection!");
	
	public gMenu() {
		setTitle("Google Accounts");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
		setLocation(50, 50);

		JPanel body = new JPanel();
			body.setLayout(new GridLayout(0, 1));
			for (final gAccount g : gAccount.getAccounts()) {
				JButton button = new JButton(g.getName());
				button.setPreferredSize(new Dimension(80, 20));
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						@SuppressWarnings("unused")
						gInterface window = new gInterface(g); }
				});
			body.add(button); }
			newAcct.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					JPanel msg = new JPanel();
						msg.setLayout(new BorderLayout());
						JPanel panel0 = new JPanel();
							panel0.add(new JLabel("Input Account:"));
							JTextField user = new JTextField("example@gmail.com", 12);
							panel0.add(user);
						msg.add(panel0, BorderLayout.NORTH);
						JPanel panel1 = new JPanel();
							panel1.add(new JLabel("Passphrase:    "));
							JTextField pass = new JTextField(12);
							panel1.add(pass);
						msg.add(panel1, BorderLayout.SOUTH);
					int response = JOptionPane.showConfirmDialog(newAcct, msg, "New Google Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (response == JOptionPane.OK_OPTION) {
						gAccount.addData(new gAccount(user.getText().trim(), pass.getText().trim())); }
				}
			});
			body.add(newAcct);
		add(body, BorderLayout.CENTER);
		
		//pack(); 
		setVisible(true); }
}
