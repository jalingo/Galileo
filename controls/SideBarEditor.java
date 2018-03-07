package controls;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import data.*;

public class SideBarEditor extends AbstractAction {
	private static final long serialVersionUID = -872356832089383929L;
	private JPanel message = new JPanel();
	private JPanel panel = new JPanel();
	private JComboBox<String> panels = new JComboBox<String>();
	private ArrayList<JCheckBox> boxes = new ArrayList<JCheckBox>();
	private ArrayList<JTextField> accts = new ArrayList<JTextField>();
	private ArrayList<JTextField> passes = new ArrayList<JTextField>();
	private ArrayList<JTextField> fields = new ArrayList<JTextField>();
	private ArrayList<JButton> buttons = new ArrayList<JButton>();
	private Account newAccount;
	private Checklist newCheck;
	private Link newLink;
	
	public SideBarEditor() {
		message.add(new JLabel("Select Panel to edit:"));
		panels.addItem("Accounts");
		panels.addItem("Checklists");
		panels.addItem("Links");
		message.add(panels); }
	
	public void loadAccounts() {
		boxes.clear();
		accts.clear();
		passes.clear();
		buttons.clear();
		
		//creates JComponents for each Account and loads into relevant array
		for (final Account account : Account.getAccounts()) {
			JCheckBox box = new JCheckBox(account.getSite());
				box.setBackground(Color.LIGHT_GRAY);
				box.setName(String.valueOf(account.getId()));
			boxes.add(box);
			JTextField user = new JTextField(account.getAccount());
				user.setBackground(Color.LIGHT_GRAY);
				user.setName(String.valueOf(account.getId()));
			accts.add(user);
			JTextField pass = new JTextField(account.getPass());
				pass.setBackground(Color.LIGHT_GRAY);
				pass.setName(String.valueOf(account.getId()));
			passes.add(pass); 
			JButton button = new JButton("-");
				button.setName(String.valueOf(account.getId()));
				button.setPreferredSize(new Dimension(40, 25));
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						int response = JOptionPane.showConfirmDialog(null, "Remove " + account.getSite(), "WARNING", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
						if (response == JOptionPane.OK_OPTION) {
							Account.removeData(account);
							loadAccounts();
							buildAccountPanel(); }
					}
				});
			buttons.add(button); }
	}

	public void loadChecks() {
		boxes.clear();
		accts.clear();
		passes.clear();
		buttons.clear();
		
		//creates JComponents for each Checklist and loads into relevant array
		for (final Checklist check : Checklist.getChecklists()) {
			JCheckBox box = new JCheckBox();
			//	box.setAlignmentY(JCheckBox.RIGHT_ALIGNMENT);
				box.setBackground(Color.YELLOW);
				box.setName(check.getItemDescription());
			boxes.add(box);
			JTextField itemDescription = new JTextField(check.getItemDescription());
				itemDescription.setName(check.getItemDescription());
				itemDescription.setBackground(Color.YELLOW);
			accts.add(itemDescription);
			JTextField toolTip = new JTextField(check.getItemTips());
				toolTip.setName(check.getItemDescription());
				toolTip.setBackground(Color.YELLOW);
			passes.add(toolTip);
			JButton button = new JButton("-");
				button.setName(check.getItemDescription());
				button.setPreferredSize(new Dimension(40, 25));
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						int response = JOptionPane.showConfirmDialog(null, "Remove " + check.getItemDescription(), "WARNING", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
						if (response == JOptionPane.OK_OPTION) {
							Checklist.removeData(check);
							loadChecks();
							buildChecksPanel(); }
					}
				});
			buttons.add(button); }
	}
	
	public void loadLinks() {
		boxes.clear();
		accts.clear();
		passes.clear();
		fields.clear();
		buttons.clear();
		
		//creates JComponents for each Link and loads into relevant array
		for (final Link link : Link.getLinks()) {
			JCheckBox box = new JCheckBox();
				box.setName(link.getUrl());
			boxes.add(box);
			JTextField user = new JTextField(link.getAccount());
				user.setName(link.getUrl());
			accts.add(user);
			JTextField pass = new JTextField(link.getPass());
				pass.setName(link.getUrl());
			passes.add(pass); 
			JTextField comment = new JTextField(link.getComments());
				comment.setName(link.getUrl());
			fields.add(comment);
			JButton button = new JButton("-");
				button.setName(link.getUrl());
				button.setPreferredSize(new Dimension(40, 25));
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						int response = JOptionPane.showConfirmDialog(null, "Remove " + link.getUrl(), "WARNING", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
						if (response == JOptionPane.OK_OPTION) {
							Link.removeData(link);
							loadLinks();
							buildLinksPanel(); }
					}
				});
				buttons.add(button); }
	}
	
	public void buildAccountPanel() {
		panel.removeAll();
		
		//add title elements
		panel.add(new JLabel("Sites"));
		panel.add(new JLabel("Accounts"));
		panel.add(new JLabel("Passwords"));
		panel.add(new JLabel(""));

		//add existing/editable entries
		for (JCheckBox box : boxes) {
			panel.add(box);
			for (JTextField field : accts) {
				if (field.getName().equals(box.getName())) {
					panel.add(field); }
			}
			for (JTextField field : passes) {
				if (field.getName().equals(box.getName())) {
					panel.add(field); }
			}
			for (JButton button : buttons) {
				if (button.getName().equals(box.getName())) {
					panel.add(button); }
			}
		}
		
		//New Account Creation elements
		newAccount = new Account();
		final JTextField newSite = new JTextField(newAccount.getSite());
			newSite.setEnabled(true);
			newSite.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newAccount.setSite(newSite.getText().trim()); }
			});
		panel.add(newSite);
		final JTextField newUser = new JTextField(newAccount.getAccount());
			newUser.setEnabled(true);
			newUser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newAccount.setAccount(newUser.getText().trim()); }
			});				
		panel.add(newUser);
		final JTextField newPass = new JTextField(newAccount.getPass());
			newPass.setEnabled(true);
			newPass.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newAccount.setPass(newPass.getText().trim()); }
			});
		panel.add(newPass);
		final JButton newButton = new JButton("+");
			newButton.setPreferredSize(new Dimension(40, 25));
			newButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					Account.addData(newAccount); 
					loadAccounts();
					buildAccountPanel(); }
			});
		panel.add(newButton); }
	
	public void buildChecksPanel() {
		panel.removeAll();
		
		//Title elements
		panel.add(new JLabel("Item Description"));
		panel.add(new JLabel("ToolTip: Help"));
		panel.add(new JLabel(""));
		
		//add existing/editable entries
		for (JCheckBox box : boxes) {

			for (JTextField field : accts) {
				if (field.getName().equals(box.getName())) {
					panel.add(field); }
			}
			for (JTextField field : passes) {
				if (field.getName().equals(box.getName())) {
					panel.add(field); }
			}
			JPanel pane = new JPanel();
				pane.setBackground(Color.YELLOW);
				pane.setLayout(new BorderLayout());
				pane.add(box, BorderLayout.WEST);
				for (JButton button : buttons) {
					if (button.getName().equals(box.getName())) {
						pane.add(button, BorderLayout.EAST); }
				}
			panel.add(pane); }
		
		//New Checklist Creation elements
		newCheck = new Checklist();
	//	panel.add(new JLabel(""));
		final JTextField newItemDescription = new JTextField(newCheck.getItemDescription());
			newItemDescription.setEnabled(true);
			newItemDescription.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newCheck.setItemDescription(newItemDescription.getText().trim()); }
				});
		panel.add(newItemDescription);
		final JTextField newHelp = new JTextField(newCheck.getItemTips());
			newHelp.setEnabled(true);
			newHelp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newCheck.setItemTips(newHelp.getText().trim()); }
			});		
		panel.add(newHelp);
		final JButton newButton = new JButton("+");
			newButton.setPreferredSize(new Dimension(40, 25));
			newButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					Checklist.addData(newCheck); 
					loadChecks();
					buildChecksPanel(); }
			});
		panel.add(newButton); }
	
	public void buildLinksPanel() {
		panel.removeAll();
		
		//title elements
		panel.add(new JLabel("URL", JLabel.CENTER));
		panel.add(new JLabel("Account", JLabel.CENTER));
		panel.add(new JLabel("Password", JLabel.CENTER));
		panel.add(new JLabel("Comments", JLabel.CENTER));
		panel.add(new JLabel(""));
		
		//add existing/editable entries
		for (JCheckBox box : boxes) {
			panel.add(box);
			for (JTextField field : accts) {
				if (field.getName().equals(box.getName())) {
					panel.add(field); }
			}
			for (JTextField field : passes) {
				if (field.getName().equals(box.getName())) {
					panel.add(field); }
			}
			for (JTextField field : fields) {
				if (field.getName().equals(box.getName())) {
					panel.add(field); }
			}
			for (JButton button : buttons) {
				if (button.getName().equals(box.getName())) {
					panel.add(button); }
			}
		}
		
		//New Link Creation elements
		newLink = new Link();
		final JTextField url = new JTextField(newLink.getUrl());
			url.setEnabled(true);
			url.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newLink.setUrl(url.getText().trim()); }
			});
		panel.add(url);
		final JTextField newUser = new JTextField(newLink.getAccount());
			newUser.setEnabled(true);
			newUser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newLink.setAccount(newUser.getText().trim()); }
			});				
		panel.add(newUser);
		final JTextField newPass = new JTextField(newLink.getPass());
			newPass.setEnabled(true);
			newPass.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newLink.setPass(newPass.getText().trim()); }
			});
		panel.add(newPass);
		final JTextField newComment = new JTextField(newLink.getComments());
			newComment.setEnabled(true);
			newComment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					newLink.setComments(newComment.getText().trim()); }
			});
		panel.add(newComment);
		final JButton newButton = new JButton("+");
			newButton.setPreferredSize(new Dimension(40, 25));
			newButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					Link.addData(newLink); 
					loadLinks();
					buildLinksPanel(); }
			});
		panel.add(newButton); }
	
	public void actionPerformed(ActionEvent e) {
		//Ask user to select panel to edit
		int response = JOptionPane.showConfirmDialog(null, message, "Operator Panels Editor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		//If user confirms, creates editor panels
		if (response == JOptionPane.OK_OPTION) {
			if (panels.getSelectedItem().equals("Accounts")) {
				panel.setLayout(new GridLayout(0, 4));
				panel.setBackground(Color.LIGHT_GRAY);
				loadAccounts();
				buildAccountPanel(); }

			if (panels.getSelectedItem().equals("Checklists")) {
				panel.setLayout(new GridLayout(0, 3));
				panel.setBackground(Color.YELLOW);
				loadChecks();
				buildChecksPanel(); }

			if (panels.getSelectedItem().equals("Links")) {
				panel.setLayout(new GridLayout(0, 5));
				panel.setBackground(Color.WHITE);
				loadLinks();
				buildLinksPanel(); }
			
			//Present editable panel:
			int reply = JOptionPane.showConfirmDialog(null, panel, panels.getSelectedItem().toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			//If changes are confirmed:
			if (reply == JOptionPane.OK_OPTION) {
				
				//Changes for "Accounts" panel
				Account acct = new Account();
				if (panels.getSelectedItem().equals("Accounts")) {
					for (JCheckBox box : boxes) {
						if (box.isSelected()) {
							for (JTextField field : accts) {
								if (field.getName().equals(box.getName())) {
									for (Account account : Account.getAccounts()) {
										if (box.getName().equals(String.valueOf(account.getId()))) {
											account.setAccount(field.getText().trim()); 
											acct = account; } 
									}
									Account.addData(acct); }
							}
							for (JTextField field : passes) {
								if (field.getName().equals(box.getName())) {
									for (Account account : Account.getAccounts()) {
										if (box.getName().equals(String.valueOf(account.getId()))) {
											acct.setPass(field.getText()); }
									}
									Account.addData(acct); }				
							}
						}
					}
				}
				
				//Changes for "Checklists" panel
				Checklist editCheck = new Checklist();
				if (panels.getSelectedItem().equals("Checklists")) {
					for (JCheckBox box : boxes) {
						if (box.isSelected()) {
							for (JTextField field : passes) {
								if (field.getName().equals(box.getName())) {
									for (Checklist checklist : Checklist.getChecklists()) {
										if (box.getName().equals(checklist.getItemDescription())) {
											checklist.setItemTips(field.getText().trim()); 
											editCheck = checklist; }
									}
								}
							}
							for (JTextField field : accts) {
								if (field.getName().equals(box.getName())) {
									for (Checklist checklist : Checklist.getChecklists()) {
										if (box.getName().equals(checklist.getItemDescription())) {
											editCheck.setItemDescription(field.getText().trim()); } 
									}
								}
							}
							Checklist.addData(editCheck); }
					}
				}
				
				//Changes for "Links" panel
				Link editLink = new Link();
				if (panels.getSelectedItem().equals("Links")) {
					for (JCheckBox box : boxes) {
						if (box.isSelected()) {
							for (JTextField field : passes) {
								if (field.getName().equals(box.getName())) {
									for (Link links : Link.getLinks()) {
										if (box.getName().equals(links.getUrl())) {
											links.setPass(field.getText().trim()); 
											editLink = links; }
									}
								}
							}
							for (JTextField field : accts) {
								if (field.getName().equals(box.getName())) {
									for (Link links : Link.getLinks()) {
										if (box.getName().equals(links.getUrl())) {
											editLink.setAccount(field.getText().trim()); }
									}
								}
							}
							for (JTextField field : fields) {
								if (field.getName().equals(box.getName())) {
									for (Link links : Link.getLinks()) {
										if (box.getName().equals(links.getUrl())) {
											editLink.setComments(field.getText().trim()); }
									}
								}
							}
							Link.addData(editLink); }
					}
				}
			}
		}
	}
}
