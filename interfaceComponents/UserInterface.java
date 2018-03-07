package interfaceComponents;

import controls.*;
import data.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.io.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import org.jdesktop.swingx.JXDatePicker;

public class UserInterface extends JFrame {
	private static final long serialVersionUID = -2552597894462912884L;

	//instance variables...
	private static final int MYVIEW = 0;
	private static final int OPERTR = 1;
	private static final int SHIFTS = 2;
	private static final int FISCAL = 3;

	private static final ArrayList<String> DAYSOFWEEK = new ArrayList<String>(Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"));
	
	private static final int AREA = 0;
	private static final int DAYS = 1;
	private static final int TIME = 2;

	private final DecimalFormat df0 = new DecimalFormat("#0.0");
	private final DecimalFormat df1 = new DecimalFormat("#0.00");
	
	private boolean triggered = true;
	private boolean percentage = false;
	private boolean areaNotDay = true;

	private LocalTime start = LocalTime.now();
	private LocalTime stop = LocalTime.now();
	
	private int panelZone = MYVIEW;
	private int zone = AREA;
	private int index = 0;

	private Technician editedTechnician;												//SHIFTS
	private Site targetSite = new Site();												//SHIFTS
	private Operator target;															//SHIFTS

	private Operator user;																			//UI
	private JXDatePicker calendar = new JXDatePicker();												//UI
	private JScrollPane pane = new JScrollPane();													//UI

	private JPanel dailyCosts = new JPanel();												//FISCAL v
	private JPanel siteEditor = new JPanel();
	private JPanel topperPanel = new JPanel();
	private JPanel toppestPanel = new JPanel();
	private JPanel targetWeek = new JPanel();
	private JPanel oprEditor = new JPanel();												//FISCAL ^
	private JPanel sCalendar = new JPanel();											//SHIFTS v
	private JPanel sEditor = new JPanel();
	private JPanel sStats = new JPanel();												//SHIFTS ^
 
	private JTextField unit = new JTextField(targetSite.getUnit(), 3);						//FISCAL v
	private JTextField city = new JTextField(targetSite.getCity(), 10);						
	private JTextField state = new JTextField(targetSite.getState(), 2);
	private JTextField zip = new JTextField(String.valueOf(targetSite.getZipCode()), 4);	//FISCAL ^
	private JLabel focusPoint;																		//UI
	
	private JButton value = new JButton();													//FISCAL v								
	private JCheckBox admin = new JCheckBox("Administrator");
	private JComboBox<String> operators = new JComboBox<String>();
	private JComboBox<String> combo = new JComboBox<String>();
	private SiteCombo streetAddr = new SiteCombo();											//FISCAL ^
	private AreaCombo area0 = new AreaCombo();											//SHIFTS
	private AreaCombo area1 = new AreaCombo();											//SHIFTS

	//interfaces...
	public interface DateModel {
		public LocalDate getDate();
		public void addObserver(Observer o);
		public void removeObserver(Observer o); }

	public interface MutableDateModel extends DateModel {
		public void setDate(LocalDate date); }
	
	//constructors...
	public UserInterface(Operator o, LocalDate d) {
		DefaultDateModel model = new DefaultDateModel(d);
		user = o; 
			
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//setup Menus
		if (user.getAccessPrivs().equalsIgnoreCase("OPRTR")) {
			OperatorMenus menu = new OperatorMenus(); 
			setJMenuBar(menu); }
		else if (user.getAccessPrivs().equalsIgnoreCase("ADMIN")) {
			AdminMenus menu = new AdminMenus(); 
			setJMenuBar(menu); }
		else {
			GalileoMenus menu = new GalileoMenus();
			setJMenuBar(menu); }
		
		//creates header with navigation buttons
		add(new NavButtons(model), BorderLayout.NORTH);
		
		//loads content 
		add(pane, BorderLayout.CENTER);
		viewConstructor(model); 
	
		//creates footer
		JPanel footer = new JPanel();
			footer.setLayout(new BorderLayout());
			footer.setBackground(Color.BLACK);

			focusPoint = new JLabel(DateTimeFormatter.ofPattern("E, dd MMM yyyy").format(model.getDate()));
				focusPoint.setForeground(Color.RED);
			footer.add(focusPoint, BorderLayout.WEST);

			JLabel loggedIn = new JLabel(user.getName() + "     ", JLabel.RIGHT);
				loggedIn.setForeground(Color.CYAN);
			footer.add(loggedIn, BorderLayout.EAST);
		add(footer, BorderLayout.SOUTH); 
		
		pack(); }
	
	public UserInterface(Operator user) {
		this(user, LocalDate.now()); }
	
	//methods...
	public void viewConstructor(DefaultDateModel m) {
		DefaultDateModel model = m;
					
		//set window frame's title
		if 		(panelZone == MYVIEW) { setTitle("My View"); }
		else if (panelZone == OPERTR) { setTitle("Operations"); }
		else if (panelZone == SHIFTS) { setTitle("Scheduling"); }
		else if (panelZone == FISCAL) { setTitle("Fiscal Report"); } 
				
		//builds body panel
		if 		(panelZone == MYVIEW) { pane.setViewportView(buildMyView(model.getDate())); }
		else if (panelZone == OPERTR) { pane.setViewportView(buildOperatorView(model.getDate())); }
		else if (panelZone == SHIFTS) { pane.setViewportView(buildSchedulingView(model.getDate())); }		
		else if (panelZone == FISCAL) { pane.setViewportView(buildFiscalView(model.getDate())); }

		pack(); }
	
	//landing page with hr information
	public JPanel buildMyView(LocalDate d) {
		final DefaultDateModel mod = new DefaultDateModel(d);

		JPanel myView = new JPanel();
			myView.setLayout(new BorderLayout());
			
			//Orange welcome header
			JPanel header = new JPanel();
				header.setBackground(Color.ORANGE);
				header.add(new JLabel("Welcome " + user.getName(), JLabel.RIGHT));
				if (user.getAccessPrivs().equals("OPRTR")) {
					JButton launcher = new JButton("Operator Mode");
						launcher.setBackground(Color.CYAN);
						launcher.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								panelZone = OPERTR;
								ShiftFocus update = new ShiftFocus(mod, 0); 
								update.actionPerformed(event); }
						});
					header.add(launcher); }
			myView.add(header, BorderLayout.NORTH);
			
			//Schedule panel
			JPanel schedule = new JPanel();
				schedule.setBackground(Color.YELLOW);
				schedule.setLayout(new GridLayout(0, 1));

				schedule.add(new JLabel("Schedule Info"));

				JButton scheduler = new JButton("Set Availability");
					scheduler.setToolTipText("Under Construction");
					scheduler.addActionListener(new ScheduleSetter(user));
				schedule.add(scheduler);
				
				JButton reporter = new JButton("Report Issue");
					reporter.setToolTipText("Use this to report issues about the schedule, etc...");
					reporter.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							JTextArea message = new JTextArea("Enter any message about your schedule/availability/etc... here.");
							int response = JOptionPane.showConfirmDialog(null, message, "Report Issue", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
							if (response == JOptionPane.OK_OPTION) {
								File delivery = new File("./com.lingotechsolutions.data/messages/@" + user.getName().toUpperCase());
								try {
									if (delivery.exists() == false) { delivery.createNewFile(); }
									FileWriter appender = new FileWriter(delivery, true);
									appender.write("#TO:@ADMIN__#SUBJECT:HR/SCHED__#DATE:" + LocalDateTime.now().toString() + "__#BODY:" + message.getText().trim() + "__#END\n");
									appender.close(); }
								catch (IOException e) {}							
							}
						}
					});
				schedule.add(reporter);
			myView.add(schedule, BorderLayout.EAST);
			
			//Pay report
			JPanel pay = new JPanel();
				pay.setBackground(Color.MAGENTA);
				pay.setLayout(new GridLayout(0, 1));
				pay.add(new JLabel("Next Pay Period (So far...)"));
				pay.add(new PayEntry(user, startOfWeek(LocalDate.now()).getDate(), LocalDate.now()));
			myView.add(pay, BorderLayout.CENTER);
		return myView; }

	//constructs operator view with 2 days' shifts (blue) + requests (green)
	public JPanel buildOperatorView(LocalDate d) {
		JPanel opView = new JPanel();
			opView.setLayout(new BoxLayout(opView, BoxLayout.Y_AXIS));		
			opView.add(buildDayView(d, 0));
			opView.add(buildDayView(d, 1)); 	
		return opView; }

	public JPanel buildSchedulingView(LocalDate d) {
		DefaultDateModel model = startOfWeek(d);

		//builds main panel with 3 row weekly calendar
		JPanel schedulingView = new JPanel();
			schedulingView.setLayout(new BorderLayout());
			//percentage = false;

			//create calendar
			buildCalendar(model);
			schedulingView.add(new JScrollPane(sCalendar), BorderLayout.NORTH);

			//tech editor combo (inside totals box, blue part) to choose type of stats shown (yellow box)
			removeListeners(combo);
			combo.removeAllItems();
			combo.addItem("Areas:");
			combo.addItem("Days:");
			combo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (triggered) {
						if (combo.getSelectedIndex() == 0)	{ areaNotDay = true; }
						else 								{ areaNotDay = false; }
						buildTechnicianEditor(editedTechnician); }
					else {
						triggered = true; }
				}
			});
			
			//combo used in tech editor (yellow box) for cycling areas
			removeListeners(area0);
			area0.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					triggered = false;
					value.setText(editedTechnician.getStats(combo.getSelectedIndex(), percentage, area0.getSelectedIndex())); }
			});	
			
			//JButton 'value' cycles between displaying text in percentage or numerical form
			removeListeners(value);
			value.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
System.out.println("flag0");
					if (triggered) {								//boolean 'triggered' is instance variable used to check if 
						if (percentage) {							//action is caused by human or interface builder.
							percentage = false; }
						else {
							percentage = true; }
System.out.println("flag1");
						buildTechnicianEditor(editedTechnician); }
					else {
System.out.println("flag2");
						triggered = true; }
System.out.println("flag3");
				}
			});
			
			buildTechnicianEditor();
			schedulingView.add(sEditor, BorderLayout.WEST);
			
			buildBranchStatistics();
			schedulingView.add(sStats, BorderLayout.CENTER);
			
		return schedulingView; }

	//creates red fiscal view with expenses, site editor, operators panel (magenta), and weekly net (dark grey)
	public JPanel buildFiscalView(LocalDate d) {
		final DefaultDateModel model = startOfWeek(d);
		JPanel body = new JPanel();
		body.setBackground(Color.RED);
		
		//load operators into JComboBox
		removeListeners(operators);
		operators.removeAllItems();
		for (Operator opr : Operator.getOperators()) {
			operators.addItem(opr.getName() + ":"); }
		operators.setSelectedIndex(index);
		operators.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				index = operators.getSelectedIndex();
				CalculateData update = new CalculateData(model);
					update.loadOprEditor(); }
		});

		//sets admin checkbox action listeners
		removeListeners(admin);
		for (Operator opr : Operator.getOperators()) {
			if (operators.getSelectedItem().toString().replaceAll(":", "").equals(opr.getName()) && opr.getAccessPrivs().equals("ADMIN")) {
				admin.setSelected(true); }		
			else {
				admin.setSelected(false); }
		}
		admin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				for (Operator opr : Operator.getOperators()) {
					if (operators.getSelectedItem().toString().replaceAll(":", "").equals(opr.getName())) {
						if (admin.isSelected()) { opr.setAccessPriv("ADMIN"); }
						else {opr.setAccessPriv("OPRTR"); }
					Operator.addData(opr); }
				}
			}
		});
		
		//resets action listener for streetAddr and removes items
		removeListeners(streetAddr);
		streetAddr.removeAllItems();		
		streetAddr = new SiteCombo();
		streetAddr.setSelectedItem(targetSite.getStreetAddress());
		streetAddr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean found = false;
				for (Site s : Site.getSites()) {
					if (s.getStreetAddress().equals(streetAddr.getSelectedItem().toString())) {
						targetSite = s;
						found = true; }
				}
				if (found == true) {
					CalculateData update = new CalculateData(model);
					update.loadSiteEditor(); }
			}
		});
		
		//sets all dynamic values
		CalculateData catalyst = new CalculateData(model); 
			catalyst.Calculations();	
		
		//builds left panel with expenses column and site editor
		JPanel westPanel = new JPanel();
			westPanel.setLayout(new GridLayout(0, 1));
			
			dailyCosts.setBackground(Color.RED);
			westPanel.add(dailyCosts);
			
			siteEditor.setBackground(Color.RED);
			westPanel.add(siteEditor);
		body.add(westPanel, BorderLayout.WEST);
			
		//builds right panel with operator editor and branch statistics
		JPanel eastPanel = new JPanel();
			eastPanel.setLayout(new BorderLayout());
				topperPanel.removeAll();
				topperPanel.setBackground(Color.MAGENTA);
				topperPanel.setLayout(new GridLayout(0, 1));
					
				toppestPanel.setBackground(Color.MAGENTA);
				topperPanel.add(toppestPanel);			

				oprEditor.setBackground(Color.MAGENTA);
				topperPanel.add(oprEditor);
			eastPanel.add(topperPanel, BorderLayout.NORTH);
			
			targetWeek.setBackground(Color.GRAY);
			eastPanel.add(targetWeek, BorderLayout.CENTER);
		body.add(eastPanel, BorderLayout.CENTER); 
			
		return body; }

	//builds individual days for operator view
	public JPanel buildDayView(LocalDate d, int integer) {
		DefaultDateModel model = new DefaultDateModel(d);
		int offset = integer;

		JPanel DayView = new JPanel();
			DayView.setLayout(new BorderLayout());		
		
			//upper blue panels with days' availabilities
			JPanel schedule = new JPanel();
				schedule.setBackground(Color.BLUE);
				schedule.setLayout(new GridLayout(0, 1));

				//Sets Titled Border around blue Service Shift panel
				Border line = BorderFactory.createLineBorder(Color.WHITE);
				Border titled = BorderFactory.createTitledBorder(line, DateTimeFormatter.ofPattern("E, dd MMM yyyy").format(model.getDate().plusDays(offset)), TitledBorder.DEFAULT_JUSTIFICATION , TitledBorder.DEFAULT_POSITION, this.getFont(), Color.WHITE);
				schedule.setBorder(titled);
					
				//load array of availabilities into panel by offset date; 
				ArrayList<Availability> workSchedule = Availability.getAvailabilities(model.getDate().plusDays(offset));
				for (Availability a : workSchedule) { 
					schedule.add(new ScheduleEntry(a, false)); }
					
				//add an empty availability at the bottom
				Availability newShift = new Availability();
					newShift.setDate(model.getDate().plusDays(offset));
				schedule.add(new ScheduleEntry(newShift, true));
			DayView.add(schedule, BorderLayout.NORTH);
			
			//green service queue
			JPanel body = new JPanel();
				body.setBackground(Color.GREEN);
				body.setLayout(new GridLayout(0, 1));

				//Sets Titled Border around blue Service Shift panel
				line = BorderFactory.createLineBorder(Color.BLACK);
				titled = BorderFactory.createTitledBorder(line, DateTimeFormatter.ofPattern("E, dd MMM yyyy").format(model.getDate().plusDays(offset)));
				body.setBorder(titled);
				
				//load array of service requests into panel by offset date
				ArrayList<ServiceRequest> serviceRequests = ServiceRequest.getServiceRequests(model.getDate().plusDays(offset));
				for (ServiceRequest s : serviceRequests) { 
					body.add(new ServiceEntry(s, false, user)); }
				
				//add an empty service request at the bottom
				ServiceRequest newEntry = new ServiceRequest();
					newEntry.setDate(model.getDate().plusDays(offset));
				body.add(new ServiceEntry(newEntry, true, user));
			DayView.add(body, BorderLayout.CENTER);
			
		return DayView; }

	//removes any and all action listeners
	public void removeListeners(JComboBox<String> combo) {
		ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
		for (ActionListener l : combo.getActionListeners()) {
			listeners.add(l); }
		for (ActionListener l : listeners) {
			combo.removeActionListener(l); }
	}
	public void removeListeners(JButton button) {
		ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
		for (ActionListener l : button.getActionListeners()) {
			listeners.add(l); }
		for (ActionListener l : listeners) {
			button.removeActionListener(l); }
	}
	public void removeListeners(JCheckBox c) {
		JCheckBox checkBox = c;
		ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
		for (ActionListener l : checkBox.getActionListeners()) {
			listeners.add(l); }
		for (ActionListener l : listeners) {
			checkBox.removeActionListener(l); }
	}	
	
	//takes date and returns Sunday of date's week in date model format
	public DefaultDateModel startOfWeek(LocalDate d) {
		DefaultDateModel mod = new DefaultDateModel(d);

		//determine current day of week(conditional statement), then adjust appropriately (conditional commands)
		if 		(d.getDayOfWeek().equals(DayOfWeek.SUNDAY)) 	{ mod = new DefaultDateModel(d); }
		else if (d.getDayOfWeek().equals(DayOfWeek.MONDAY)) 	{ mod = new DefaultDateModel(d.minusDays(1)); }
		else if (d.getDayOfWeek().equals(DayOfWeek.TUESDAY)) 	{ mod = new DefaultDateModel(d.minusDays(2)); }
		else if (d.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) 	{ mod = new DefaultDateModel(d.minusDays(3)); }
		else if (d.getDayOfWeek().equals(DayOfWeek.THURSDAY)) 	{ mod = new DefaultDateModel(d.minusDays(4)); }
		else if (d.getDayOfWeek().equals(DayOfWeek.FRIDAY)) 	{ mod = new DefaultDateModel(d.minusDays(5)); }
		else if (d.getDayOfWeek().equals(DayOfWeek.SATURDAY)) 	{ mod = new DefaultDateModel(d.minusDays(6)); }
		
		//adjusts monthly calendar
		calendar.setDate(Date.from(mod.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		
		return mod; }
	
	//interface component builders for scheduling view
	public void buildCalendar(DefaultDateModel m) {
		sCalendar.removeAll();
		DefaultDateModel mod = m;
		sCalendar.setBackground(Color.BLACK);
		
		int counter = 0;
		for(@SuppressWarnings("unused") String day : DAYSOFWEEK) {
			JPanel column = new JPanel();
				column.setLayout(new GridLayout(0, 1));
				column.setBackground(Color.YELLOW);
			
				//Current week/First row
				column.add(buildSpecificDay(mod.getDate().plusDays(counter)));
			
				//Next week/Second row
				column.add(buildSpecificDay(mod.getDate().plusDays(counter + 7)));

				//Last week/Third row
				column.add(buildSpecificDay(mod.getDate().plusDays(counter + 14)));
			
			sCalendar.add(column);	
			counter++; }
		sCalendar.revalidate();
		sCalendar.repaint(); }

	public JPanel buildSpecificDay(LocalDate d) {
		JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			panel.setLayout(new GridLayout(0, 1));

			//yellow header with dayOfWeek and dayOfMonth
			JPanel header = new JPanel();
				header.setBackground(Color.YELLOW);
				JLabel day = new JLabel(d.getDayOfWeek().toString().substring(0, 3), JLabel.LEFT);
					day.setPreferredSize(new Dimension(55, 20));
				header.add(day);
				JLabel num = new JLabel(String.valueOf(d.getDayOfMonth()), JLabel.RIGHT);
					num.setPreferredSize(new Dimension(55, 20));
				header.add(num);
			panel.add(header);
				
			//creates operator shifts
			JPanel operatorShift = new JPanel();
				operatorShift.setLayout(new GridLayout(0, 1));
				
				//pre-existing shifts generated
				for (JButton button : buildShift(d, WorkShift.OPERATIONS)) { operatorShift.add(button); }

				//new shift button
				JButton newOpr = new JButton("+");
					newOpr.setBackground(Color.MAGENTA);
					newOpr.setToolTipText("Operations");
					newOpr.addActionListener(new WorkShifter(new WorkShift(new Operator(), d, Site.OK0, WorkShift.POTENTIAL)));
				operatorShift.add(newOpr);
			panel.add(operatorShift);

			//creates tech shifts
			JPanel technicianShift = new JPanel();
				technicianShift.setLayout(new GridLayout(0, 1));
				
				//pre-existing shifts generated
				for (JButton button : buildShift(d, WorkShift.SERVICES)) { technicianShift.add(button); }
								
				//new shift button
				JButton newTech = new JButton("+");
					newTech.setBackground(Color.CYAN);
					newTech.setToolTipText("Services");
					newTech.addActionListener(new WorkShifter(new WorkShift()));
				technicianShift.add(newTech);
			panel.add(technicianShift);
			
			panel.revalidate();
			panel.repaint();
		return panel; }
	
	public ArrayList<JButton> buildShift(LocalDate date, int option) {
		ArrayList<JButton> buttons = new ArrayList<JButton>();
		
		//checks the date for availabilities and converts them to 
		//shift buttons for the calendar
		for (WorkShift w : WorkShift.getShifts(date, option)) {
			JButton button = new JButton();
				if (w.getPrivacy() == WorkShift.POTENTIAL) {
					button.setBackground(Color.PINK); }
				if (option == WorkShift.OPERATIONS) {
					button.setText(w.getOperator() + ": " + w.getArea()); 
					if (w.getPrivacy() != WorkShift.POTENTIAL) {
						button.setBackground(Color.GRAY); } 
					button.addActionListener(new WorkShifter(w)); }
				else {
					button.setText(w.getTechnician() + ": " + w.getArea()); 
					if (w.getPrivacy() == WorkShift.PRIVATE) {
						button.setBackground(Color.CYAN); }
					else if (w.getPrivacy() == WorkShift.PUBLIC) {
						button.setBackground(Color.BLUE); }
					button.addActionListener(new WorkShifter(w)); }
		}
		return buttons; }
	
	public void buildBranchStatistics() {
		sStats.removeAll();
		sStats.setBackground(Color.YELLOW);
		sStats.setLayout(new BorderLayout());
		
		//grey panel at the bottom right of Fiscal Report
		JPanel allTime = new JPanel();
		allTime.setBackground(Color.YELLOW);
			allTime.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "All Time"));
			final JComboBox<String> combo = new JComboBox<String>();
				combo.addItem("Area");
				combo.addItem("Days");
				combo.addItem("Time");
				combo.setSelectedItem(zone);
				combo.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent event) {
						zone = combo.getSelectedIndex(); 
						if (triggered == true) {
							buildBranchStatistics(); }
						else {
							triggered = true; }
						pack(); }
				});
			allTime.add(combo);
			
			//used in the following if statement
			JPanel innerPanel = new JPanel();					
				innerPanel.setLayout(new GridLayout(1, 0));
				innerPanel.setBackground(Color.DARK_GRAY);
			allTime.add(innerPanel);
				
			//generates info selected by zone
			if (zone == AREA) {
				triggered = false;
				combo.setSelectedIndex(0);
				
				int allTotal = 0;
				for (@SuppressWarnings("unused") ServiceRequest req : ServiceRequest.getServiceRequests()) { allTotal++; } 
				String[] regions = {"SF", "SB", "EB", "OC", "SD", "LA", "NY"}; //replace with Branch.getRegions();
				JPanel[] panels = new JPanel[7];
				for (int x = 0; x < 7; x++) {
					panels[x] = new JPanel();
					panels[x].setBackground(Color.YELLOW);
					panels[x].setLayout(new GridLayout(0, 1));
					panels[x].add(new JLabel(regions[x], JLabel.CENTER));
					
					int subTotal = 0;
					double hours = 0;
					for (ServiceRequest req : ServiceRequest.getServiceRequests()) {
						if (req.getLocation().getArea() == x) {
							subTotal++;
							hours += req.getDuration(); }
					}
					
					if (subTotal == 0) { subTotal++; }
					panels[x].add(new JLabel(" " + df0.format(hours / subTotal) + "hrs ", JLabel.CENTER));
					panels[x].add(new JLabel(df0.format(subTotal * 100 / allTotal) + "%", JLabel.CENTER));
					innerPanel.add(panels[x]); }
			}
			else if (zone == DAYS) {
				triggered = false;
				combo.setSelectedIndex(1);
				int[] closed = new int[7];
				int allTotal = 0;
				for (ServiceRequest req : ServiceRequest.getServiceRequests()) { 
					if (req.getStatusCycle() == ServiceRequest.CLOSED) {
						allTotal++; } 
				}

				int counter = 0;
				for (String day : DAYSOFWEEK) {
					JPanel dayPanel = new JPanel();
						dayPanel.setBackground(Color.YELLOW);
						dayPanel.setLayout(new GridLayout(0, 1));
						JLabel label = new JLabel(day, JLabel.CENTER);
							label.setPreferredSize(new Dimension(45, 30));
						dayPanel.add(label);
						
						for (ServiceRequest req : ServiceRequest.getServiceRequests()) {
							if (req.getDate().getDayOfWeek().toString().substring(0, 3).equalsIgnoreCase(day)) {
								if (req.getStatusCycle() == ServiceRequest.CLOSED) { 
									closed[counter]++; }						
							}
						}
						
						if(allTotal == 0) { allTotal++; }
						dayPanel.add(new JLabel(" " + closed[counter] * 100 / allTotal + "% ", JLabel.CENTER));
					innerPanel.add(dayPanel);
					counter++; }				
			}
			else if (zone == TIME){
				triggered = false;
				combo.setSelectedIndex(2);

				int total = 0;
				int[] durations = new int[7];			
				for (ServiceRequest req : ServiceRequest.getServiceRequests()) {
					if (req.getDuration() < .6) { durations[0]++; }
					else if (req.getDuration() < 1.1) { durations[1]++; }
					else if (req.getDuration() < 1.6) { durations[2]++; }
					else if (req.getDuration() < 3.1) { durations[3]++; }
					else if (req.getDuration() < 9.1) { durations[4]++; }
					else { durations[5]++; }
					total++; }
				if (total == 0) { total++; }
				
				JPanel[] panels = new JPanel[6];
					String[] headerTexts = {"<1hr", "1hr", "1.5hr", "-3hr", "3+", "12+"};
				
					for (int x = 0; x < 6; x++) {
						panels[x] = new JPanel();
						panels[x].setBackground(Color.YELLOW);
						panels[x].setLayout(new GridLayout(0, 1));
						panels[x].add(new JLabel(headerTexts[x], JLabel.CENTER));
						panels[x].add(new JLabel(df0.format(durations[x]) + "hrs", JLabel.CENTER));
						panels[x].add(new JLabel(df0.format(durations[x] * 100 / total) + "%", JLabel.CENTER));
						innerPanel.add(panels[x]); }
			}
		sStats.add(allTime, BorderLayout.NORTH);
		
		sStats.revalidate();
		sStats.repaint(); }	
	
	public void buildTechnicianEditor(Technician t) {
		editedTechnician = t;

		sEditor.removeAll();
		sEditor.setBackground(Color.BLUE);
		sEditor.setLayout(new BorderLayout());

		JPanel header = new JPanel();
			header.setBackground(Color.BLUE);
			
			final TechCombo tech = new TechCombo();
				tech.setEditable(true);
				tech.setSelectedItem(editedTechnician.getName());
				tech.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						Boolean test = false;
							test = Technician.checkTech(tech.getSelectedItem().toString()); 
							if (test) { 
								editedTechnician = Technician.getTech(tech.getSelectedItem().toString()); }
							else {
								String newSms = JOptionPane.showInputDialog("Input " + tech.getSelectedItem().toString() + "'s SMS number:", "(10 digit SMS #)");
								if (Long.valueOf(newSms) > 999999999 && Long.valueOf(newSms) < 10000000000L) {
									int response = JOptionPane.showConfirmDialog(null, "Confirm creation of " + tech.getSelectedItem().toString() + "/ " + newSms + "?", "Add Service Provider", JOptionPane.OK_CANCEL_OPTION);
									if (response == JOptionPane.OK_OPTION) {
										editedTechnician.setName(tech.getSelectedItem().toString());
										editedTechnician.setSms(Long.valueOf(newSms));
										Technician.addData(editedTechnician); }
								} else {
									JOptionPane.showMessageDialog(null, "Proposed sms number is NOT a 10 digit number!"); }
							}
						buildTechnicianEditor(editedTechnician); }
				});
			header.add(tech);
			
			JLabel labelC = new JLabel(":   ");
				labelC.setForeground(Color.WHITE);
			header.add(labelC);
			
			final JTextField sms = new JTextField();
				sms.setText(String.valueOf(editedTechnician.getSms()));
				sms.setToolTipText("SMS capable cellphone.");
				sms.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						editedTechnician.setSms(Long.parseLong(sms.getText().toString().trim()));
						Technician.addData(editedTechnician); }
				});
			header.add(sms);
			
			JLabel labelD = new JLabel("    $");
				labelD.setForeground(Color.WHITE);
			header.add(labelD);
			
			final JTextField rate = new JTextField();
				DecimalFormat df = new DecimalFormat("#0.00");
				rate.setText(df.format(editedTechnician.getPublicRate()));
				rate.setToolTipText("Public Rate");
				rate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						editedTechnician.setPublicRate(Double.parseDouble(rate.getText().toString().trim()));
						Technician.addData(editedTechnician); }
				});
			header.add(rate);
			
			header.add(new JLabel("    "));
			header.add(new JLabel("    "));
			header.add(new JLabel("    "));
			header.add(new JLabel("    "));
			
			final JButton deleteButton = new JButton("-");
				deleteButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						int response = JOptionPane.showConfirmDialog(null, "Permanently remove " + editedTechnician.getName() + "?", "Removal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
						if (response == JOptionPane.OK_OPTION) {
							Technician.removeData(editedTechnician); 
							buildTechnicianEditor(); }
					}
				});
			header.add(deleteButton);
		sEditor.add(header, BorderLayout.NORTH);
			
		//creates blue area inside 'Totals' black border
		JPanel body = new JPanel();
			body.setBackground(Color.BLUE);
			body.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Totals"));

			//gathers and separates tech stats, then displays them in (white) labels
			String[] pieces = editedTechnician.getTotals().split("/");
			
			//average duration
			JLabel label0 = new JLabel(pieces[0]);			
				label0.setForeground(Color.WHITE);
				label0.setFont(label0.getFont().deriveFont(Font.BOLD));
				label0.setToolTipText("Average Duration");
			body.add(label0);
			body.add(new JLabel("  "));

			JLabel label1 = new JLabel(pieces[1]);
				label1.setForeground(Color.WHITE);
				label1.setFont(label0.getFont().deriveFont(Font.BOLD));
				label1.setToolTipText("Completion Rate");
			body.add(label1);
			body.add(new JLabel("  "));
			
			JLabel label2 = new JLabel(pieces[2]);
				label2.setForeground(Color.WHITE);
				label2.setFont(label0.getFont().deriveFont(Font.BOLD));
				label2.setToolTipText("Total Requests");
			body.add(label2);
			body.add(new JLabel("    "));
			body.add(new JLabel("    "));

			//sets and adds day/area combo (blue) left of the stats display box (yellow)
			if (areaNotDay) {
				triggered = false;
				combo.setSelectedIndex(0); }
			else {
				triggered = false;
				combo.setSelectedIndex(1); }
			body.add(combo);
			
			//creates yellow stats box
			JPanel panel = new JPanel();
				panel.setBackground(Color.YELLOW);
				value.setPreferredSize(new Dimension(120, 28));
				
				if (combo.getSelectedIndex() == 0) { 
//					triggered = false;
					value.setText(editedTechnician.getStats(combo.getSelectedIndex(), percentage, area0.getSelectedIndex()));
					panel.add(area0); }
				else {
					final JComboBox<String> days = new JComboBox<String>();
					for (String day : DAYSOFWEEK) {
						days.addItem(day); }
					days.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent event) {
//							triggered = false;
							value.setText(editedTechnician.getStats(combo.getSelectedIndex(), percentage, days.getSelectedIndex())); }
					});
//					triggered = false;
					value.setText(editedTechnician.getStats(combo.getSelectedIndex(), percentage, days.getSelectedIndex()));
					panel.add(days); }
				panel.add(value); 
			body.add(panel);
		sEditor.add(body, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
			footer.setBackground(Color.BLUE);
			int element = 0;
			LocalTime[] starts = editedTechnician.getStarts();
			LocalTime[] stops = editedTechnician.getStops();
			for (String day : DAYSOFWEEK) {
				final JButton button = new JButton(day);
					button.setPreferredSize(new Dimension(68, 25));

					if (starts[element] != null) {
						button.setToolTipText(starts[element].toString() + " - " + stops[element].toString());
						button.addActionListener(new StartStopper(editedTechnician, element, null, null));
						button.setBackground(Color.GREEN); }
					else {					
						final int e = element;
						final JPanel message = new JPanel();
							message.setLayout(new GridLayout(0, 1));
							
							final JComboBox<Integer> startHour = new JComboBox<Integer>();
							final JComboBox<Integer> startMin = new JComboBox<Integer>();
							final JComboBox<Integer> stopHour = new JComboBox<Integer>();
							final JComboBox<Integer> stopMin = new JComboBox<Integer>();
							final JCheckBox startPM = new JCheckBox("pm");
							final JCheckBox stopPM = new JCheckBox("pm");
							//JComboBox<Integer>'s are being loaded to be used for 
							//setting shift hours in TechnicianEditor's footer
							startMin.addItem(0);
							stopMin.addItem(0);
							for (int x = 1; x < 13; x++) {
								startHour.addItem(x);
								stopHour.addItem(x);
								startMin.addItem(x);
								stopMin.addItem(x); }
							for (int x = 13; x < 60; x++) {
								startMin.addItem(x);
								stopMin.addItem(x); }
							
							JPanel top = new JPanel();
								top.add(startHour);
								top.add(new JLabel(":"));
								top.add(startMin);
								top.add(startPM);
							message.add(top);
							
							message.add(new JLabel("-to-", JLabel.CENTER));
							
							JPanel bot = new JPanel();
								bot.add(stopHour);
								bot.add(new JLabel(":"));
								bot.add(stopMin);
								bot.add(stopPM);
							message.add(bot);
							
						button.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								//Set Red/Green button color (times if needed)
								if (button.getBackground() == Color.RED) {
									int response = JOptionPane.showConfirmDialog(null, message, "Shift Availability", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);									
									if (response == JOptionPane.OK_OPTION) {
										int hour = startHour.getSelectedIndex() + 1;
										if (startPM.isSelected()) {
											hour += 12;
											if (hour == 24) { hour = 0; }
										}
										start = LocalTime.of(hour, startMin.getSelectedIndex());
										
										hour = stopHour.getSelectedIndex() + 1;
										if (stopPM.isSelected()) {
											hour += 12;
											if (hour == 24) { hour = 0; }
										}
										stop = LocalTime.of(hour, stopMin.getSelectedIndex());

										button.setToolTipText("Available");
										StartStopper updater = new StartStopper(editedTechnician, e, start, stop);
										updater.actionPerformed(event); }									
								} else {
									start = null;
									editedTechnician.setStart(start, e);
									Technician.addData(editedTechnician);
System.out.println("T: " + editedTechnician.getStart(e).toString());
									button.setToolTipText("Unavailable");
									button.setBackground(Color.RED); }
								button.revalidate();
								button.repaint(); }
						});
						
						//Set Red/Green button color
						if (editedTechnician.getStart(e) != null) {
							button.setToolTipText("Available");
							button.setBackground(Color.GREEN);
							button.revalidate();
							button.repaint(); } 
						else {
							button.setToolTipText("Unavailable");
							button.setBackground(Color.RED); 
							button.revalidate();
							button.repaint(); }
					}
					
				footer.add(button);
				element++; }
		sEditor.add(footer, BorderLayout.SOUTH);
		
		sEditor.revalidate();
		sEditor.repaint(); }
	
	public void buildTechnicianEditor() {
		buildTechnicianEditor(Technician.getTechs().get(0)); }
	
//inner classes...
	//Panel components for Operator Mode
	public class ServiceShift extends JComponent implements Observer {
		private static final long serialVersionUID = 8232550749288693259L;
	
		//instance variables
		private DateModel model;
		private LocalDate targetDate = LocalDate.now();
		private int offset;
	
		//constructors
		public ServiceShift(int i) {
			offset = i;
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBackground(Color.BLUE);

			//load array by targetDate; 
			ArrayList<Availability> workSchedule = Availability.getAvailabilities(targetDate);
			for (Availability a : workSchedule) { 
				add(new ScheduleEntry(a, false)); }
		
			Availability newShift = new Availability();
				newShift.setDate(targetDate);
			add(new ScheduleEntry(newShift, true));

			//Sets Titled Border around blue Service Shift panel
			Border line = BorderFactory.createLineBorder(Color.WHITE);
			Border titled = BorderFactory.createTitledBorder(line, DateTimeFormatter.ofPattern("E, dd MMM yyyy").format(targetDate), TitledBorder.DEFAULT_JUSTIFICATION , TitledBorder.DEFAULT_POSITION, this.getFont(), Color.WHITE);

			setBorder(titled);
			setVisible(true); }
	
		public ServiceShift() { this(0); }
	
		//methods
		public void setTarget(LocalDate d) { targetDate = d; }
		public LocalDate getTarget() { return targetDate; }
	
		public void setModel(DateModel value) {
			if (model != null) {
				model.removeObserver(this); }
			this.model = value;
			if (model != null) {
				model.addObserver(this); }
			updateComponents(); }
	
		private void updateComponents() {
			DateModel model = getModel();
		
			removeAll();
			revalidate();
			
			if (model != null) {
				targetDate = model.getDate().plusDays(offset);
			
				Border line = BorderFactory.createLineBorder(Color.BLACK);
				Border titled = BorderFactory.createTitledBorder(line, DateTimeFormatter.ofPattern("E, dd MMM yyyy").format(targetDate));
				setBorder(titled);
			
				ArrayList<Availability> workSchedule = Availability.getAvailabilities();
				for (Availability a : workSchedule) { 
					if (a.getDate().equals(model.getDate().plusDays(offset))) { add(new ScheduleEntry(a, false)); }
				}	
				Availability newShift = new Availability();
					newShift.setDate(targetDate);
				add(new ScheduleEntry(newShift, true)); }
		}

		public DateModel getModel() {
			return model; }
	
		@Override
		public void update(Observable o, Object arg) {
			updateComponents(); }
	}
	
	public class ScheduleEntry extends JPanel {
		private static final long serialVersionUID = 5499860633881144947L;
		
		//instance variablse
		private JLabel sms = new JLabel("XXXXXXXXX");
		private TechCombo name = new TechCombo();
		private AreaCombo area = new AreaCombo();
		private JTextField rm = new JTextField("XXX", 3);
		private String addr = "STREET NOT SET, ????";
		private JButton comments = new JButton("No Comments");
		private JLabel expenses = new JLabel("$0.00");
		private JButton delEntry = new JButton("-");
		private JButton newEntry = new JButton("+");
		private double totalNet = 0;
		private Availability targetAvailability;
		
		//constructors
		ScheduleEntry(Availability a, Boolean b) {
			targetAvailability = a;
			
			//set dynamic values
			sms.setText(Long.toString(targetAvailability.getTech().getSms()));
			sms.setForeground(Color.WHITE);
			final double rate = targetAvailability.getTech().getPublicRate();
			sms.setToolTipText("$" + rate + "0 / hour");
			add(sms);
			
			JLabel label0 = new JLabel(": ");
				label0.setForeground(Color.WHITE);
			add(label0);		
			
			name.setEditable(true);
			name.setSelectedItem(targetAvailability.getTech().getName());
			name.addActionListener(new UpdateName());
			add(name);
	
			JLabel label1 = new JLabel(" in ");
				label1.setForeground(Color.WHITE);
			add(label1);		
			
			area.setSelectedIndex(targetAvailability.getLocation().getArea());
			area.addActionListener(new UpdateArea());
			add(area);
			
			JLabel label2 = new JLabel("SiteCode: ");
				label2.setForeground(Color.WHITE);
			add(label2);
		
			rm.setText(targetAvailability.getLocation().getRoomCode());
			rm.addActionListener(new UpdateCode());
			add(rm);
			
			addr = targetAvailability.getLocation().getStreetAddress() + ", " + targetAvailability.getLocation().getCity();		
			comments.setText(targetAvailability.getComments());
			comments.setPreferredSize(new Dimension(340, 25));
			comments.setToolTipText(addr);
			comments.addActionListener(new CommentClick());
			add(comments);
			
			totalNet = Technician.getFees(targetAvailability.getDate(), targetAvailability.getTech()) - targetAvailability.getLocation().getSiteCosts();		
			DecimalFormat df = new DecimalFormat("#0.00");
			expenses.setText("$" + String.valueOf(df.format(totalNet))); 
			expenses.setToolTipText("$" + df.format(targetAvailability.getLocation().getSiteCosts()));
			expenses.setForeground(Color.WHITE);
			expenses.setPreferredSize(new Dimension(60, 25));
			expenses.setHorizontalAlignment(JLabel.RIGHT);
			add(expenses);

			if (b.equals(true)) {
				newEntry.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						UpdateQueue editor = new UpdateQueue(targetAvailability, comments);
						editor.setTitle("Add Tech to Schedule");
						editor.setVisible(true); }
				});
				add(newEntry); }		
			else { 
				delEntry.setPreferredSize(new Dimension(40, 25));
				delEntry.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) { 
						new JOptionPane();
						int selection = JOptionPane.showConfirmDialog(newEntry, "Delete event from the schedule?", "Caution", JOptionPane.OK_CANCEL_OPTION);
						if (selection == JOptionPane.OK_OPTION) { 
							Availability.removeData(targetAvailability); 
							DefaultDateModel mod = new DefaultDateModel(targetAvailability.getDate());
							ShiftFocus update = new ShiftFocus(mod, 0);
							update.actionPerformed(event); }
						}
				});
				add(delEntry); }	
			setBackground(Color.BLUE); }

		ScheduleEntry(Availability a) { this(a, true); }
		ScheduleEntry() { this(new Availability()); }
			
		//inner class
		private class UpdateName implements ActionListener {
			private Technician tech = new Technician();		

			public void actionPerformed(ActionEvent event) {
				Boolean test = false;
					test = Technician.checkTech(name.getSelectedItem().toString()); 
					if (test == true) { 
						tech = Technician.getTech(name.getSelectedItem().toString()); 
						targetAvailability.setTech(tech); 
						Availability.addData(targetAvailability); }
					else {
						String newSms = JOptionPane.showInputDialog("Input " + name.getSelectedItem().toString() + "'s SMS number:", "(10 digit SMS #)");
						if (Long.valueOf(newSms) > 999999999 && Long.valueOf(newSms) < 10000000000L) {
							int response = JOptionPane.showConfirmDialog(null, "Confirm creation of " + name.getSelectedItem().toString() + "/ " + newSms + "?", "Add Service Provider", JOptionPane.OK_CANCEL_OPTION);
							if (response == JOptionPane.OK_OPTION) {
								tech.setName(name.getSelectedItem().toString());
								tech.setSms(Long.valueOf(newSms));
								Technician.addData(tech); }
						} 
						else JOptionPane.showMessageDialog(null, "Proposed sms number is NOT a 10 digit number!"); }
					
			}
		}
		
		private class UpdateArea implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				targetAvailability.getLocation().setArea(area.getSelectedIndex());
				Availability.addData(targetAvailability); } 
			}
		private class UpdateCode implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				targetAvailability.getLocation().setRoomCode(rm.getText().trim());
				Availability.addData(targetAvailability);
			}
		}		
		private class CommentClick implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				UpdateQueue update = new UpdateQueue(targetAvailability, comments);
				update.setTitle("Tech Schedule Editor");
				update.setVisible(true);
			}
		}
	}
	public class ServiceQueue extends JComponent implements Observer {
		private static final long serialVersionUID = 5301624782691813345L;
		private MutableDateModel model;
		private LocalDate targetDate = LocalDate.now();
		private int shift = 0;
		private Operator user;
		
		public ServiceQueue(int i, Operator o) throws IOException {
			shift = i;
			user = o;

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
			setVisible(true); }	
		public ServiceQueue(Operator o) throws IOException { this(0, o); }

		public void setModel(MutableDateModel value) {
			if (model != null) {
	            model.removeObserver(this); }
	        this.model = value;
	        if (model != null) {
	            model.addObserver(this); }
	        updateComponents(); }
		
		public void updateComponents() {
			MutableDateModel model = getModel();
			
			removeAll();
			revalidate();
			if (model != null) {
				targetDate = model.getDate().plusDays(shift);
				
				Border line = BorderFactory.createLineBorder(Color.BLACK);
				Border titled = BorderFactory.createTitledBorder(line, DateTimeFormatter.ofPattern("E, dd MMM yyyy").format(model.getDate().plusDays(shift)));
				setBorder(titled);
				
				ArrayList<ServiceRequest> serviceRequests = ServiceRequest.getServiceRequests(targetDate);
				for (ServiceRequest s : serviceRequests) { 
					ServiceEntry oldEntry = new ServiceEntry(s, false, user);
					oldEntry.setModel(model);
					add(oldEntry); }
				ServiceRequest newEntry = new ServiceRequest();
				newEntry.setDate(targetDate);
				add(new ServiceEntry(newEntry, true, user)); }
		}

		public MutableDateModel getModel() {
			return model; }
		
		@Override
		public void update(Observable o, Object arg) {
			updateComponents();	}
	}
	
	public class ServiceEntry extends JComponent implements Observer {
		private static final long serialVersionUID = -5674823721197577437L;
		private JLabel client = new JLabel("", JLabel.CENTER);
		private TechCombo tech = new TechCombo();
		private JButton times = new JButton();
		private StatusCombo serviceCycle = new StatusCombo();
		private AreaCombo area = new AreaCombo();
		private ServiceRequest entry = new ServiceRequest();
		private Boolean newRequest = false;
		private Operator user;
		private JButton comments = new JButton();
		private MutableDateModel model;
		
		//constructors
		public ServiceEntry(ServiceRequest e, Boolean n, Operator o) {
			entry = e;
			newRequest = n;
			user = o;
			
			setLayout(new FlowLayout());

			//fills out client sms label
			client.setText(Long.toString(entry.getClient().getSms()));
			this.add(client);
		
			//setup tech combo box
			removeListeners(tech);
			if (entry.getTech().getName().equals("empty")) {
				tech.setSelectedItem("Assign Tech"); }
			else {
				tech.setSelectedItem(entry.getTech().getName()); }
			tech.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					entry.setTechByString(tech.getSelectedItem().toString());
					if (newRequest.equals(true)) {
						entry.setInitiator(user); }
					setModel(model); }
			});
			this.add(tech);
			
			//setup area combo
			removeListeners(area);
			area.setSelectedIndex(entry.getLocation().getArea());
			area.setToolTipText(entry.getLocation().getStreetAddress() + ", " + entry.getLocation().getCity());
			area.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					//Sets entry's area/region
					int newArea = area.getSelectedIndex();
					if (newArea != 7) { 
						entry.setLocation(new Site(new MailingAddress(newArea))); }
					else { 
						JOptionPane.showMessageDialog(null, "XX is not a valid region!", "ERROR", JOptionPane.WARNING_MESSAGE);
						System.out.println("Address constructor triggered..."); }
					
					//if an existing appointment, changes the appointment's current region
					if (newRequest.equals(false)) { 
						ServiceRequest.addData(entry); }
					else {
						entry.setInitiator(user); }
				}
			});
			this.add(area);

			//setup ServicePeriod button
			removeListeners(times);
				times.addActionListener(new TimeSelector(entry, times)); 
				times.setText(DateTimeFormatter.ofPattern("h:mma").format(entry.getStart()) + " - " + DateTimeFormatter.ofPattern("h:mma").format(entry.getStop()));
				times.setPreferredSize(new Dimension(165, 25));
				times.setToolTipText("Service Period");
			this.add(times);

			//service cycle with triggers to update client info
			removeListeners(serviceCycle);
			serviceCycle.setSelectedIndex(entry.getStatusCycle());
			serviceCycle.setSize(8, this.getPreferredSize().height);
			serviceCycle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) { 
					if (newRequest.equals(false)) { 
						entry.setStatusCycle(serviceCycle.getSelectedIndex());	
						if (entry.getStatusCycle() == ServiceRequest.CONFRM) {
							entry.setConfirmer(user); 
							JOptionPane.showMessageDialog(null, new JTextField("Confirmed for " + entry.getDate().toString()), "Scripted Response", JOptionPane.PLAIN_MESSAGE);}
						else if (entry.getStatusCycle() == ServiceRequest.CLOSED) {
							entry.setCloser(user);
							entry.getClient().setLastSuccess(entry);
							if (entry.getClient().getStatus().equals("DEFUNCT") || entry.getClient().getStatus().equals("APPROVED")) {
								entry.getClient().setStatus("MEMBER"); }
						}
						else if (entry.getStatusCycle() == ServiceRequest.CANCEL) {
							entry.getClient().setLastUnsuccess(entry); }
						Client.addData(entry.getClient());
						ServiceRequest.addData(entry); }
					else {
						entry.setStatusCycle(serviceCycle.getSelectedIndex()); }
				serviceCycle.revalidate(); 
				serviceCycle.repaint(); }
			});
			this.add(serviceCycle);
			
			comments = new JButton(entry.getComments());
			comments.setPreferredSize(new Dimension(300, 25));
			comments.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					UpdateQueue queue = new UpdateQueue(entry, comments, user, entry.getDate()); 
						queue.setTitle("Service Queue Editor");
					queue.setVisible(true); }
			});
			add(comments);
			
			if (newRequest.equals(false)) { 
				JButton removeRequest = new JButton("-");
				removeRequest.setPreferredSize(new Dimension(40, 25));
				removeRequest.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						int selection = JOptionPane.showConfirmDialog(null, "Delete event from the schedule?", "Caution", JOptionPane.OK_CANCEL_OPTION);
						if (selection == JOptionPane.OK_OPTION) { 
							ServiceRequest.removeData(entry); 
							removeAll();
							revalidate();
							repaint(); 
							DefaultDateModel mod = new DefaultDateModel(entry.getDate());
							ShiftFocus update = new ShiftFocus(mod, 0);
							update.actionPerformed(event); }
					}
				});
				add(removeRequest); }
			else {
				JButton addRequest = new JButton("+");
					addRequest.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							UpdateQueue queue = new UpdateQueue(entry, comments, user, entry.getDate()); 
								queue.setTitle("Add Service Request");
							queue.setVisible(true); }
					});
				add(addRequest); }
			revalidate();
			repaint(); }
		public ServiceEntry(Operator o) { this(new ServiceRequest(), true, o); }

		@Override
		public void update(Observable o, Object arg) {
System.out.println("TRIGGERED");
			revalidate(); 
			repaint(); }
		
		public void setModel(MutableDateModel value) {
			if (model != null) {
	            model.removeObserver(this); }
	        this.model = value;
	        if (model != null) {
	            model.addObserver(this); }
		}
		
		public MutableDateModel getModel() {
			return model; }
	}
	public class UpdateQueue extends JFrame {
		private static final long serialVersionUID = 2651920352620828639L;

		//instance variables (will be used by actionPerformed()'s
		private JButton cancel = new JButton("Cancel");
		private JButton parent;
		private JCheckBox commentBox = new JCheckBox();
		private JCheckBox addrBox = new JCheckBox();
		private JCheckBox smsBox = new JCheckBox();
		private JTextField newComment;
		private JTextField streetAddress;
		private JTextField unit;
		private JTextField expense;
		private JTextField city;
		private JTextField state;
		private JTextField zipcode; 
		private JTextField newSms;

		private Operator user;
		private ServiceRequest request;
		private Availability schedule;
		private	DefaultDateModel mod;
		
		//constructors
		public UpdateQueue(ServiceRequest event, JButton p, Operator o, LocalDate d) {
			setTitle("Service Queue Editor");
			getContentPane().setLayout(new GridLayout(0, 1));
			mod = new DefaultDateModel(d);
			parent = p;
			request = event;
			user = o;

			//creates text field that allows editing of comment
			editComments(request.getComments());
			
			//loads address and sms panels
			setAddr(request.getLocation());
			editSms(request.getClient().getSms(), "Client's");

			JPanel panel = new JPanel();
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) { exitFrame(); }
			});
			panel.add(cancel);
			
			JButton update = new JButton("Update");
			update.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (commentBox.isSelected()) { 
						parent.setText(newComment.getText().trim());
						request.setComments(newComment.getText().trim()); }
					if (addrBox.isSelected()) { 
						request.getLocation().setStreetAddress(streetAddress.getText());
						request.getLocation().setUnit(unit.getText().trim());
						request.getLocation().setCity(city.getText().trim());
						request.getLocation().setState(state.getText().trim());
						request.getLocation().setZipCode(Integer.parseInt(zipcode.getText().trim())); }
					if (smsBox.isSelected()) { 
						Long newNumber = Long.valueOf(newSms.getText().trim());
						if (Client.checkStatusOf(newNumber).toString().equalsIgnoreCase("unknown")) {
							request.getClient().setSms(newNumber); 
							request.getClient().setTelephone(newNumber);
							request.setInitiator(user);
							Client.addData(request.getClient()); }
						else {
							for (Client c : Client.getClients()) {
								if (c.getSms() == newNumber) { request.setClient(c); }
							}
						}
					}
					ServiceRequest.addData(request);
					ShiftFocus update = new ShiftFocus(mod, 0);
						update.actionPerformed(e);
					exitFrame();
					JOptionPane.showMessageDialog(null, new JTextField(request.getConfirmationStatement())); }
			});
			panel.add(update);
			
			add(panel);
			setLocation(480, 240);
			pack(); }
		
		//triggered when new availability is created
		public UpdateQueue(Availability event, JButton p) {
			getContentPane().setLayout(new GridLayout(0, 1));
			mod = new DefaultDateModel(event.getDate());
			schedule = event;
			parent = p;

			//creates a site editor button
			JButton siteSelector = new JButton("Site Editor");
			siteSelector.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SiteFinder finder = new SiteFinder(schedule.getDate());
					exitFrame();
					finder.setVisible(true); }
			});
			add(siteSelector);

			editComments(schedule.getComments());
			editSms(schedule.getTech().getSms(), "Tech's");

			JPanel panel = new JPanel();		
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) { exitFrame(); }
			});
			panel.add(cancel);
			JButton update = new JButton("Update");
			update.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (commentBox.isSelected()) { schedule.setComments(newComment.getText()); }
					if (smsBox.isSelected()) { schedule.getTech().setSms(Long.valueOf(newSms.getText().trim())); }
					Availability.addData(schedule);
					parent.setText(newComment.getText().trim());
					parent.getParent().validate();
					ShiftFocus update = new ShiftFocus(mod, 0);
						update.actionPerformed(e);
					exitFrame(); }
			});
			panel.add(update);

			add(panel);
			setLocation(480, 240);
			pack(); }
		
		//methods
		private void exitFrame() { this.dispose(); }
		
		private void editComments(String comments) {
			JPanel panel = new JPanel();
			panel.add(commentBox);
			panel.add(new JLabel("Edit Comments:"));
			newComment = new JTextField(comments, 19);
			newComment.setEditable(true);
			panel.add(newComment);		
			this.add(panel); }
		
		private void setAddr(Site site) {
			JPanel topPanel = new JPanel();
			topPanel.add(addrBox);
			streetAddress = new JTextField(site.getStreetAddress(), 15);
			streetAddress.setToolTipText("Street Address");
			streetAddress.setEditable(true);
			topPanel.add(streetAddress);

			unit = new JTextField(site.getUnit(), 3);
			unit.setEditable(true);
			unit.setToolTipText("Unit/Suite/Apt");
			topPanel.add(unit);

			if (schedule != null) {
				topPanel.add(new JLabel("$"));
				expense = new JTextField(String.valueOf(site.getSiteCosts()), 4);
				expense.setAlignmentX(RIGHT_ALIGNMENT);
				topPanel.add(expense); }
			
			JPanel bottomPanel = new JPanel();
			city = new JTextField(site.getCity(), 10);
			city.setEnabled(true);
			city.setToolTipText("City");
			bottomPanel.add(city);
				
			state = new JTextField(site.getState(), 2);
			state.setEnabled(true);
			state.setToolTipText("state");
			bottomPanel.add(state);
				
			zipcode = new JTextField(String.valueOf(site.getZipCode()), 4);
			zipcode.setEnabled(true);
			zipcode.setToolTipText("Zipcode");
			bottomPanel.add(zipcode);
				
			this.add(topPanel);
			this.add(bottomPanel); }
		
		private void editSms(long sms, String type) {
			JPanel panel = new JPanel();
			panel.add(smsBox);
			panel.add(new JLabel(type + " SMS contact: ", JLabel.LEFT));
			
			newSms = new JTextField(String.valueOf(sms), 15);
			newSms.setAlignmentX(RIGHT_ALIGNMENT);
			newSms.setEnabled(true);
			panel.add(newSms); 
			
			this.add(panel); }
	}
	
	//Calculations for Fiscal Report
	class CalculateData {
		private DefaultDateModel model;
		
		CalculateData(DefaultDateModel mod) {
			model = mod;
			/*Calculations();*/ }	
		
		//All math needed to refresh/build Fiscal Report
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void Calculations() {

			//(re)builds red expenses panel + sets expense value for grey weekly totals
			double expenses = loadExpenses();

			//Clearing out and (re)building red Site Editor
			loadSiteEditor();
			
			//(re)builds operators report + calculates period wages for grey weekly totals
			double periodWages = loadOprReport();

			//(re)builds operator editor
			loadOprEditor();
					
			//builds grey weekly totals
			targetWeek.removeAll();
			double totalGross = 0.00;
			
			//calculating total service hours x fee rate = expected gross
			for (int i = 0; i < 7; i++) {
				for (ServiceRequest r : ServiceRequest.getServiceRequests(model.getDate().plusDays(i))) {
					if (r.getStatusCycle() == 3) {
						totalGross = totalGross + ( r.getDuration() * 100); }
				}
			}
			double net = totalGross - periodWages - expenses; 
		
			GroupLayout layoutG = new GroupLayout(targetWeek);
			targetWeek.setLayout(layoutG);
			layoutG.setAutoCreateGaps(true);
			layoutG.setAutoCreateContainerGaps(true);

			JLabel lab0 = new JLabel("Week's Total Gross:");
			JLabel lab1 = new JLabel("Period Wages:");
			JLabel lab2 = new JLabel("Expenses:");
			JLabel lab3 = new JLabel("Expected Net:");
			JLabel labA = new JLabel("$" + df1.format(totalGross));
			JLabel labB = new JLabel("-$" + df1.format(periodWages));
			JLabel labC = new JLabel("-$" + df1.format(expenses));
			Font font = labC.getFont();
			Map attributes = font.getAttributes();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			labC.setFont(font.deriveFont(attributes));
			JLabel labD = new JLabel("$" + df1.format(net));

			layoutG.setHorizontalGroup(
				layoutG.createSequentialGroup()
					.addGroup(layoutG.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lab0)
						.addComponent(lab1)
						.addComponent(lab2)
						.addComponent(lab3))
					.addGap(200)
					.addGroup(layoutG.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(labA)
						.addComponent(labB)
						.addComponent(labC)
						.addComponent(labD)));

			layoutG.setVerticalGroup(
				layoutG.createSequentialGroup()
					.addGroup(layoutG.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lab0)
						.addComponent(labA))
					.addGap(10)
					.addGroup(layoutG.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lab1)
						.addComponent(labB))
					.addGap(10)
					.addGroup(layoutG.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lab2)
						.addComponent(labC))
					.addGap(25)
					.addGroup(layoutG.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lab3)
						.addComponent(labD))); }

		private void loadOprEditor() {
			oprEditor.removeAll();		
			oprEditor.setLayout(new GridLayout(0, 1));

			Border line = BorderFactory.createLineBorder(Color.BLACK);
			oprEditor.setBorder(line);
				JPanel topOprEditor = new JPanel();
					topOprEditor.setBackground(Color.MAGENTA);
					
					topOprEditor.add(operators);
					topOprEditor.add(new JLabel("                                  "));
				
					admin.setAlignmentY(RIGHT_ALIGNMENT);
					admin.setBackground(Color.MAGENTA);
				topOprEditor.add(admin);
			oprEditor.add(topOprEditor);

			JPanel midOprEditor = new JPanel();
				midOprEditor.setBackground(Color.MAGENTA);
				double hps = 0;
				double con = 0;
				double can = 0;
				for (Operator o : Operator.getOperators()) {
					if (o.getName().equals(operators.getSelectedItem().toString().replaceAll(":", ""))) {
						con = o.getConfirmRate(model.getDate(), model.getDate().plusDays(7));
						can = o.getCancelRate(model.getDate(), model.getDate().plusDays(7));
						hps = o.getHoursPerShift(model.getDate(), model.getDate().plusDays(7)); }
				}
				DecimalFormat df1 = new DecimalFormat("#0.0");
				midOprEditor.add(new JLabel("Hrs/Day: " + df1.format(hps), JLabel.LEFT));
				midOprEditor.add(new JLabel("   "));
				midOprEditor.add(new JLabel("Confirms: " + df1.format(con) + "%"));
				midOprEditor.add(new JLabel("   "));
				midOprEditor.add(new JLabel("Cancels: " + df1.format(can) + "%", JLabel.RIGHT));
			oprEditor.add(midOprEditor);

			JPanel botOprEditor = new JPanel();
				botOprEditor.setBackground(Color.MAGENTA);
				
				JButton schedButton = new JButton("Schedule");
					schedButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							JPanel message = new JPanel();
							message.setLayout(new GridLayout(0, 1));
							for (Operator o: Operator.getOperators()) {
								if (o.getName().equals(operators.getSelectedItem().toString().replace(":", ""))) {
									target = o; }
							}
							LocalDateTime[] starts = target.getStarts();
							LocalDateTime[] stops = target.getStops();
							int i = 0;
							for (final String day : DAYSOFWEEK) {
								final JButton button = new JButton(day + ": " + starts[i].toLocalTime().toString() + " - " + stops[i].toLocalTime().toString());
								button.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent event) {
										@SuppressWarnings("unused")
										TimeSelector update = new TimeSelector(target, day, button); }
								});
								message.add(button);
								i++; }
							JOptionPane.showMessageDialog(null, message); }
					});
				botOprEditor.add(schedButton);

				JButton acctButton = new JButton("Acct Info");
					acctButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							String name = operators.getSelectedItem().toString().replace(":", "").trim();
							Operator target = new Operator();
							for (Operator o : Operator.getOperators()) {
								if (o.getName().equals(name)) {
									target = o; }
							}
							//message for pop up with username and password fields
							JPanel message = new JPanel();
								message.setLayout(new GridLayout(0, 1));
								JPanel top = new JPanel();
									JCheckBox uBox = new JCheckBox("User Name:");
									top.add(uBox);
									JTextField user = new JTextField(target.getUserName(), 15);
									top.add(user);
								message.add(top);
								JPanel bot = new JPanel();
									JCheckBox pBox = new JCheckBox("Password:  ");						
									bot.add(pBox);
									JPasswordField pass = new JPasswordField(target.getPassPhrase(), 15);
									bot.add(pass);
								message.add(bot);

							//if OK selected, makes changes...
							int response = JOptionPane.showConfirmDialog(null, message, name + "'s Account Info", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
							if (response == JOptionPane.OK_OPTION) {
								if (uBox.isSelected()) {
									target.setUserName(user.getText().trim()); }
								if (pBox.isSelected()) {
									target.setPassPhrase(pass.getPassword()); }
								Operator.addData(target); }
						}
					});
				botOprEditor.add(acctButton);
			
				botOprEditor.add(new JLabel("                         "));

				JButton delButton = new JButton("-");
					delButton.setToolTipText("Remove Operator");
					delButton.setPreferredSize(new Dimension(40, 25));
					delButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							int opt = JOptionPane.showConfirmDialog(null, "Permanently remove " + operators.getSelectedItem().toString().replaceAll(":", "") + "?", "WARNING!", JOptionPane.CANCEL_OPTION); 
							if (opt == JOptionPane.OK_OPTION) {
								for (Operator o : Operator.getOperators()) {
									if (o.getName().equals(operators.getSelectedItem().toString().replaceAll(":", ""))) {
										Operator.removeData(o); 
										CalculateData update = new CalculateData(model);
											update.loadOprEditor(); }
								}
							}
						}
					});
				botOprEditor.add(delButton);
			oprEditor.add(botOprEditor);
			
		oprEditor.revalidate();
		oprEditor.repaint(); }

		private double loadOprReport() {
			toppestPanel.removeAll();
			toppestPanel.setLayout(new GridLayout(0, 1));

			double periodWages = 0;
			LocalDate dater = model.getDate();

			//creates entries for each operator
			for (Operator o : Operator.getOperators()) {
				final Operator opr = o;

				JPanel panel = new JPanel();
					panel.setBackground(Color.MAGENTA);

					opr.setHoursAndBonuses(dater, dater.plusDays(6)); 

					JLabel label = new JLabel(opr.getName());
						label.setPreferredSize(new Dimension(50, 28));
					panel.add(label);
					panel.add(new JLabel( ": ("));

					JButton hoursButton = new JButton(String.valueOf(opr.getHours()));
						hoursButton.setToolTipText("Hours worked.");
						hoursButton.setPreferredSize(new Dimension(65, 25));
						hoursButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								JTextField message = new JTextField(String.valueOf(df1.format(opr.getHours())));
								int response = JOptionPane.showConfirmDialog(null, message, "Edit Hours", JOptionPane.OK_CANCEL_OPTION);
								if (response == JOptionPane.OK_OPTION) {									
									opr.setHours(Double.valueOf(message.getText().trim())); 
									ShiftFocus update = new ShiftFocus(model, 0);
										update.actionPerformed(event); }
							}
						});
					panel.add(hoursButton);

					panel.add(new JLabel(" x $")); 

					final JTextField rateField = new JTextField(4);
						rateField.setText(String.valueOf(df1.format(opr.getPayRate())));
						rateField.setPreferredSize(new Dimension(80, 25));
						rateField.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								opr.setPayRate(Double.valueOf(rateField.getText())); 
								Operator.addData(opr); }
						});
					panel.add(rateField);

					panel.add(new JLabel(") + "));

					JButton bonusButton = new JButton(String.valueOf(df1.format(opr.getBonuses())));
						bonusButton.setPreferredSize(new Dimension(80, 25));
						bonusButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								JTextField message = new JTextField(String.valueOf(df1.format(opr.getBonuses())));
								int response = JOptionPane.showConfirmDialog(null, message, "Edit Bonuses", JOptionPane.OK_CANCEL_OPTION);
								if (response == JOptionPane.OK_OPTION) {
									opr.setBonuses(Double.valueOf(message.getText().trim())); 

									ShiftFocus update = new ShiftFocus(model, 0);
										update.actionPerformed(event); }
							}
						});
					panel.add(bonusButton);

					JLabel payLabel = new JLabel(" = " + df1.format(opr.getTotalPay()));
						payLabel.setPreferredSize(new Dimension(65, 28));
					panel.add(payLabel);
				toppestPanel.add(panel); 

				periodWages = periodWages + opr.getTotalPay(); }
			
			toppestPanel.revalidate();
			toppestPanel.repaint();
			return periodWages; }

		private void loadSiteEditor() {
			siteEditor.removeAll();
			siteEditor.setLayout(new GridLayout(0, 1));
			Border line = BorderFactory.createLineBorder(Color.BLACK);
			Border titled = BorderFactory.createTitledBorder(line, "Site Editor");
			siteEditor.setBorder(titled);

			//Street address JComboBox
			JPanel topPanel = new JPanel();	
				topPanel.setBackground(Color.RED);
				topPanel.setAlignmentX(BOTTOM_ALIGNMENT);
				streetAddr.setToolTipText("Street Address");
				topPanel.add(streetAddr);
			siteEditor.add(topPanel);
			
			//Address lines
			JPanel midPanel = new JPanel();
				midPanel.setBackground(Color.RED);
				
				//grey suite + regionCombo
				JPanel insert = new JPanel();
					unit.setToolTipText("Unit/Suite/Apt");
					unit.setText(targetSite.getUnit());
					insert.add(unit);
					area1.setToolTipText("Region Code");
					area1.setSelectedIndex(targetSite.getArea());
					insert.add(area1);
				midPanel.add(insert);

				city.setToolTipText("City");
				city.setText(targetSite.getCity());
				midPanel.add(city);

				state.setToolTipText("State");
				state.setText(targetSite.getState());
				midPanel.add(state);

				zip.setText(String.valueOf(targetSite.getZipCode()));
				zip.setToolTipText("Zipcode");
				midPanel.add(zip);
			siteEditor.add(midPanel);

			//delete + save buttons
			JPanel botPanel = new JPanel();
				botPanel.setBackground(Color.RED);
				botPanel.setAlignmentX(TOP_ALIGNMENT);
				botPanel.add(new JLabel("                      "));
				
				JButton delete = new JButton("Delete");
					delete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Site.removeData(targetSite);
							streetAddr.revalidate();
							streetAddr.repaint();
							
							CalculateData update = new CalculateData(model);
								update.loadSiteEditor(); }
					});
				botPanel.add(delete);

				JButton save = new JButton("Save");
					save.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							targetSite.setArea(area1.getSelectedIndex());
							targetSite.setStreetAddress(streetAddr.getSelectedItem().toString());
							targetSite.setUnit(unit.getText());
							targetSite.setCity(city.getText());
							targetSite.setState(state.getText());
							targetSite.setZipCode(Integer.valueOf(zip.getText()));
							Site.addData(targetSite); 
							streetAddr.setForeground(Color.GREEN); }
					});
				botPanel.add(save);	
			siteEditor.add(botPanel); 
		
			siteEditor.revalidate();
			siteEditor.repaint(); }

		private double loadExpenses() {
			//Red column of expenses by day of week
			dailyCosts.removeAll();
			dailyCosts.setLayout(new GridLayout(0, 2));
			
			int count = 0;
			double expenses = 0;
			
			//panel title
			dailyCosts.add(new JLabel("Expenses:", JLabel.LEFT));
			dailyCosts.add(new JLabel("Selected Week", JLabel.CENTER));

			//calculating expenses for each day of the week
			for (String day : DAYSOFWEEK) {
				double dayCost = 0;

				dailyCosts.add(new JLabel(day + ":", JLabel.RIGHT));

				for (Availability a : Availability.getAvailabilities(model.getDate().plusDays(count))) {
					dayCost = dayCost + a.getLocation().getSiteCosts(); }
				final int counter = count;

				JButton button = new JButton("$" + df1.format(dayCost));
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							JPanel message = new JPanel();
								message.setLayout(new GridLayout(0, 1));
								for (Availability a : Availability.getAvailabilities(model.getDate().plusDays(counter))) {
									message.add(new ScheduleEntry(a, false)); }
								Availability newShift = new Availability();
									newShift.setDate(model.getDate().plusDays(counter));
								message.add(new ScheduleEntry(newShift, true));						
							JOptionPane.showMessageDialog(null, message, model.getDate().plusDays(counter).toString(), JOptionPane.INFORMATION_MESSAGE); }
					});
				dailyCosts.add(button);
				expenses = expenses + dayCost;
				count++; }	
			
			dailyCosts.revalidate();
			dailyCosts.repaint();
		return expenses; }
	}

	//Anchor date used by all interface items
	public class DefaultDateModel extends Observable implements MutableDateModel {
		private LocalDate date;
		
		public DefaultDateModel(LocalDate d) { date = d; }

		@Override
		public void setDate(LocalDate d) {
			date = d;
			setChanged();
			notifyObservers(); }
	
		@Override
		public LocalDate getDate() {
			return date; }

		@Override
		public void removeObserver(Observer o) {
			deleteObserver(o); }
	}
	
	//Adjusts anchor date 
	public class ShiftFocus extends AbstractAction implements Observer {
		private static final long serialVersionUID = -8916489155567210778L;

		//instance variables
		MutableDateModel model;
		int shift = 0;
		
		//constructors
		public ShiftFocus(MutableDateModel dateModel, int integer) {
			model = dateModel;
			shift = integer; }

		//methods
		public void actionPerformed(ActionEvent e) {
			if (model != null) { 
				model.setDate(model.getDate().plusDays(shift));
				viewConstructor(new DefaultDateModel(model.getDate()));
				focusPoint.setText(DateTimeFormatter.ofPattern("E, dd MMM yyyy").format(model.getDate()));
				calendar.setDate(Date.from(model.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
				pack(); }
		}
		public void update(Observable o, Object arg) {/*Observers' ping*/}
	}
	
	//Navigation Bar
	class NavButtons extends JPanel implements Observer {
		private static final long serialVersionUID = -7400268324519382974L;
		
		//instance variables
		private JToolBar toolBar = new JToolBar("Navigation");
		private JButton weekBack = new JButton("<<");
		private JButton dayBack = new JButton("<");
		private JButton returnToday = new JButton("Today");
		private JButton nextDay = new JButton(">");
		private JButton nextWeek = new JButton(">>");
		private MutableDateModel model;
		private GalileoMode modeCombo;
		private BranchCombo branchChooser = new BranchCombo();
		
		//constructor
		public NavButtons(MutableDateModel mod) {
			model = mod;
			
			weekBack.addActionListener(new ShiftFocus(model, -7));
			dayBack.addActionListener(new ShiftFocus(model, -1));
			returnToday.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					MutableDateModel m = getModel();
					if (m != null) {
						if (panelZone == OPERTR) {
							m.setDate(LocalDate.now());
							setModel(m);

							ShiftFocus shift = new ShiftFocus(m, 0); 
							shift.actionPerformed(event); }
						else {
							long diff = ChronoUnit.DAYS.between(m.getDate(), startOfWeek(LocalDate.now()).getDate()); 	
							ShiftFocus update = new ShiftFocus(m, Integer.valueOf(Long.toString(diff))); 
							update.actionPerformed(event); }
					pack(); }
				}
			});
			nextDay.addActionListener(new ShiftFocus(model, 1)); 
			nextWeek.addActionListener(new ShiftFocus(model, 7));
		
			toolBar.add(weekBack);
			toolBar.add(dayBack);
			toolBar.add(returnToday);
			toolBar.add(nextDay);
			toolBar.add(nextWeek);

			toolBar.add(new JLabel("   "));

			calendar.setEditable(true);
			calendar.setFormats("E, dd MMM yyyy");
			calendar.setDate(Date.from(model.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
			calendar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					MutableDateModel m = new DefaultDateModel(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getDate())));
					if (m != null) { 
						long diff = ChronoUnit.DAYS.between(m.getDate(), model.getDate());
							diff = diff * -1;
						int difference = Integer.valueOf(Long.toString(diff));
						ShiftFocus update = new ShiftFocus(model, difference);
							update.actionPerformed(event);
						pack(); }
				}
			});
			toolBar.add(calendar);

			toolBar.add(new JLabel("   "));

			modeCombo = new GalileoMode();
			if (user.getAccessPrivs().equals("ADMIN")) { 
				modeCombo.setEnabled(true); 
				modeCombo.setSelectedIndex(0);
				modeCombo.addActionListener(new ActionListener() { //changes body panel and zone variable
					public void actionPerformed(ActionEvent event) {
						panelZone = modeCombo.getSelectedIndex();
						DefaultDateModel mod = new DefaultDateModel(getModel().getDate());
						viewConstructor(mod);
						
						revalidate();
						repaint();
						pack(); }
				});
			}
			toolBar.add(modeCombo);
			
			toolBar.add(new JLabel("   "));
			
			toolBar.add(branchChooser);
			
			add(toolBar); 
			setModel(model); }			
	
		public void setModel (MutableDateModel value) {
			if (model != null) {
                model.removeObserver(this); }
            this.model = value;
            if (model != null) {
                model.addObserver(this); }
		}
		public MutableDateModel getModel() {
			return model; }
		
		@Override
		public void update(Observable o, Object arg) {
			/* models data changes */}
	}			
}
