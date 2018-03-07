package interfaceComponents;

import javax.swing.*;
import javax.swing.border.*;
import java.time.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import data.*;

public class SiteFinder extends JFrame {
	private static final long serialVersionUID = -2050094715026507178L;
	private ArrayList<Availability> schedule = new ArrayList<Availability>();
	private ArrayList<Availability> selection = new ArrayList<Availability>();
	private ArrayList<JRadioButton> buttonGroup = new ArrayList<JRadioButton>();
	private ButtonGroup reimbursement = new ButtonGroup();
	private JCheckBox service = new JCheckBox("Branch: OK0");
	private JCheckBox costBox = new JCheckBox();
	private JCheckBox splitOption = new JCheckBox("Even Split");
	private JCheckBox siteBox = new JCheckBox();
	private JCheckBox lastSlot = new JCheckBox();		
	private SiteCombo street = new SiteCombo();
	private TechCombo newEntry 	= new TechCombo();			
	private AreaCombo area = new AreaCombo();
	private JTextField siteCost = new JTextField(4);
	private JTextField city = new JTextField(8);
	private JTextField state = new JTextField(2);
	private Availability branch = new Availability();
	
	public SiteFinder(LocalDate d) {
		setLayout(new BorderLayout());
		setTitle("Work Site Editor");
		setLocation(200, 120);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		JPanel topLeft = new JPanel();
		JRadioButton radioButton = new JRadioButton();
		radioButton.setName("branch");
		reimbursement.add(radioButton);
		buttonGroup.add(radioButton);
		topLeft.add(radioButton);
		service.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (service.isSelected()) {
					selection.add(branch); }
				else {
					selection.remove(branch); }
			}
		});
		topLeft.add(service);
		topLeft.setAlignmentX(topLeft.getAlignmentX());
		leftPanel.add(topLeft);
		
		schedule = Availability.getAvailabilities(d);
		ArrayList<JCheckBox> slots = new ArrayList<JCheckBox>();
		for (Availability a : schedule) {
				slots.add(new JCheckBox(a.getTech().getName())); }

		for (final JCheckBox j : slots) {
			j.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (j.isSelected()) {
						putSelection(j.getText()); }
					else {
						pullSelection(j.getText()); }
				}
			});
			JPanel panel = new JPanel();
			JRadioButton button = new JRadioButton();
			button.setName(j.getText());
			reimbursement.add(button);
			buttonGroup.add(button);
			panel.add(button);
			panel.add(j);
			panel.setAlignmentY(LEFT_ALIGNMENT);
			leftPanel.add(panel); }

		JPanel newPanel = new JPanel();
		JRadioButton lastRad = new JRadioButton();
		reimbursement.add(lastRad);
		buttonGroup.add(lastRad);
		newPanel.add(lastRad);
		lastSlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (lastSlot.isSelected()) {
					putSelection(newEntry.getSelectedItem().toString()); }
				else {
					pullSelection(newEntry.getSelectedItem().toString()); }
			}
		});
		newPanel.add(lastSlot);
		newPanel.add(newEntry);
		leftPanel.add(newPanel);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(0, 1));

		JPanel upperRight = new JPanel();
		siteCost.setEditable(true);
		siteCost.setText("0.00");
		upperRight.add(costBox);
		upperRight.add(new JLabel("$"));
		upperRight.add(siteCost);
		splitOption.setSelected(true);
		upperRight.add(splitOption);
		rightPanel.add(upperRight);
			
		JPanel lowerRight = new JPanel();
		street.setEditable(true);
		street.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<Site> sites = Site.getSites();
				for (Site s : sites) {
					if (s.getStreetAddress().equals(street.getSelectedItem().toString())) {
						city.setText(s.getCity());
						state.setText(s.getState()); }
				}
			}
		});
		city.setEditable(true);
		state.setEditable(true);		
		Border line = BorderFactory.createLineBorder(Color.BLACK);
		lowerRight.setBorder(line);
		lowerRight.setLayout(new GridLayout(0, 1));
		lowerRight.add(street);
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(siteBox);
		bottomPanel.add(city);
		bottomPanel.add(area);
		bottomPanel.add(state);
		lowerRight.add(bottomPanel);
		rightPanel.add(lowerRight);
		
		JPanel footer = new JPanel();
		footer.setAlignmentY(RIGHT_ALIGNMENT);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitFrame(); }
		});
		footer.add(cancel);
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JRadioButton selectedButton = new JRadioButton();
				for (JRadioButton b : buttonGroup) {
					if (b.isSelected()) {
						selectedButton.setName(b.getName()); }
				}
				for (Availability a : selection) {
					if (selectedButton.getName().equals(a.getTech().getName())) {
						if (costBox.isSelected() && splitOption.isSelected() == false) { 
							a.getLocation().setSiteCosts(Double.valueOf(siteCost.getText())); }
						if (costBox.isSelected() && splitOption.isSelected()) {
								a.getLocation().setSiteCosts(Double.valueOf(siteCost.getText()) - (Double.valueOf(siteCost.getText()) / selection.size())); }
					}
					else {
						if (costBox.isSelected() && splitOption.isSelected() == false) { 
							a.getLocation().setSiteCosts(0 - Double.valueOf(siteCost.getText())); }
						if (costBox.isSelected() && splitOption.isSelected()) {
								a.getLocation().setSiteCosts(0 - (Double.valueOf(siteCost.getText()) / selection.size())); }
					}
					if (siteBox.isSelected()) {
						a.getLocation().setArea(area.getSelectedIndex());
						a.getLocation().setStreetAddress(String.valueOf(street.getSelectedItem()));
						a.getLocation().setCity(city.getText().trim());
						a.getLocation().setState(state.getText().trim()); }
					Site.addData(a.getLocation());
					Availability.addData(a); }
							
				exitFrame(); }
		});
		footer.add(save);
		
		add(leftPanel, BorderLayout.WEST);
		add(rightPanel, BorderLayout.EAST); 
		add(footer, BorderLayout.SOUTH);
		
		pack(); }
	
	public void exitFrame() { 
		Availability.removeData(branch);
		this.dispose(); }
	
	public void putSelection(String s) {
		for (Availability a : schedule) {
			if (Technician.getTech(s).getName().equals(a.getTech().getName())) {
				selection.add(a); }
		}
	}
	public void pullSelection(String s) {
		for (Availability a : schedule) {
			if (Technician.getTech(s).equals(a.getTech())) {
				selection.remove(a); }
		}
	}
}
