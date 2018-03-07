package interfaceComponents;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import org.swingplus.JHyperlink;

import data.*;

public class SideBar extends JFrame {
	private static final long serialVersionUID = 7799623292450538266L;
	private static final int ACCOUNTS = 0;
	private static final int CHECKLIST = 1;
	private static final int LINKS = 2;

	private int content = 0;
	
	//constructors
	public SideBar(int i) {
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		content = i;
		
		if (content == ACCOUNTS	) { add(new JScrollPane(new AccountsPanel())); }
		if (content == CHECKLIST) { add(new JScrollPane(new CheckList()));}
		if (content == LINKS	) { add(new JScrollPane(new Links())); }
	
		setLocation(10, 12);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setBackground(Color.RED);
		pack();
		setVisible(true); }
	public SideBar() { this(0); }	
	
	//methods
	public void setContent(int i) { content = i; }
	public int getContent() { return content; }
}

class AccountsPanel extends JPanel {
	private static final long serialVersionUID = 6471533902589823367L;

	public AccountsPanel() {
		setLayout(new GridLayout(0, 2));
		setBackground(Color.LIGHT_GRAY);
		
		ArrayList<Account> accounts = Account.getAccounts();
		for (Account a : accounts) {
			add(new JLabel(a.getSite(), JLabel.LEFT));
//			add(new JPanel());
//			add(new JPanel()); 
			JLabel label = new JLabel(a.getAccount(), JLabel.RIGHT);
				label.setPreferredSize(new Dimension(80, 30));
				label.setToolTipText(a.getPass());
			add(label); 
			add(new JPanel());
			add(new JPanel()); }

		setVisible(true); }	
}

class CheckList extends JPanel {
	private static final long serialVersionUID = 1167981686860312709L;
	private ArrayList<Checklist> checkLists = Checklist.getChecklists();
	
	public CheckList() {
		setBackground(Color.YELLOW);
		setLayout(new GridLayout(0, 3));
		
		for (final Checklist c : checkLists) {
			JPanel panel = new JPanel();
			panel.setBackground(Color.YELLOW);

			JLabel label = new JLabel(c.getItemDescription(), JLabel.RIGHT);
				label.setToolTipText(c.getItemTips());
			add(label);
			final JCheckBox s = new JCheckBox("Started");
				s.setBackground(Color.YELLOW);
				s.setToolTipText(c.getItemDescription());
				if (c.isItemStarted()) {
					s.setSelected(true); }
				else {
					s.setSelected(false); }
				s.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if (s.isSelected()) {
							c.setItemStarted(true); }
						else {
							c.setItemStarted(false); }
						Checklist.addData(c); }
				});
			add(s);
			
			final JCheckBox s0 = new JCheckBox("Finished");
				s0.setBackground(Color.YELLOW);
				s0.setToolTipText(c.getItemDescription());
				if (c.isItemClosed()) {
					s0.setSelected(true); }
				else {
					s0.setSelected(false); }
				s0.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if (s0.isSelected()) {
							c.setItemClosed(true); }
						else {
							c.setItemClosed(false); }
						Checklist.addData(c); }
				});
			add(s0); }
			
		setVisible(true); }
}

class Links extends JPanel {
	private static final long serialVersionUID = -7704465267191114074L;
	private ArrayList<Link> links = Link.getLinks();
	
	public Links() {
		setBackground(Color.white);
		setLayout(new GridLayout(0, 2));

		for(Link l : links) { 
			JHyperlink htmlString = new JHyperlink(l.getUrl(), l.getUrl());
			htmlString.setToolTipText(l.getComments());
			htmlString.setAlignmentX(LEFT_ALIGNMENT);
			add(htmlString);
			add(new JLabel(l.getAccount() + " - " + l.getPass(), JLabel.CENTER)); }
		setVisible(true); }
}