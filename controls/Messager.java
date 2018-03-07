package controls;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

import javax.swing.*;

//import data.*;

public class Messager extends AbstractAction {
	private static final long serialVersionUID = -3577991296388245751L;
	private JScrollPane body = new JScrollPane();
	private JButton deleteButton = new JButton("Delete Message");

//	private Operator user = new Operator();
	private ArrayList<String> messages = new ArrayList<String>();
	private int index = -1;
	
	//public Messager(Operator opr) { user = opr; }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JFrame frame = new JFrame();
			
			//creates header bar with user name and today's date
			JPanel header = new JPanel();
				header.setBackground(Color.ORANGE);
				header.add(new JLabel("@" + "ADMIN" /*user.getName()*/, JLabel.LEFT));
				header.add(new JLabel(LocalDate.now().toString(), JLabel.RIGHT));
			frame.add(header, BorderLayout.NORTH);
		
			//creates body to display selected messages messages
			//body.setLayout(new GridLayout(0, 1));
			bodyBUIlder();
			frame.add(body, BorderLayout.CENTER);
			
			//creates footer with message editing controls
			JPanel footer = new JPanel();
				footer.setBackground(Color.CYAN);
	
				deleteButton.setEnabled(false);
					deleteButton.setToolTipText("Permanently remove message...");
				footer.add(deleteButton);
				
				JButton replyButton = new JButton("Reply to Sender");
					replyButton.setEnabled(false);
				footer.add(replyButton); 
				
				JButton exitButton = new JButton("Exit");
					exitButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							if (index == -1) {
								frame.dispose(); } 
							else {
								index = -1;
								bodyBUIlder(); }
						}
					});
				footer.add(exitButton);
				
			frame.add(footer, BorderLayout.SOUTH);
			
		frame.pack();
		frame.setVisible(true); }
	
	public void bodyBUIlder() {
	//	body.removeAll();
		
		//aggregate message data
		try {
			Files.walk(Paths.get("./com.lingotechsolutions.data/messages")).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			    	try {
						FileInputStream fstream = new FileInputStream(filePath.toString());
						BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
						String line;
						
						//check for messages in data files (eventually will search by passed Operator.user etc...)
						messages.clear();
						while ((line = br.readLine()) != null) {
							if (line.startsWith("#TO:@ADMIN")) {
								messages.add(line + filePath.toString().substring(filePath.toString().lastIndexOf("/"))); }
						}
						
						br.close();
					} catch (Exception e1) { e1.printStackTrace(); }
			    }
			});
		} catch (IOException e1) { e1.printStackTrace(); }
		
		if (index == -1) {
			deleteButton.setEnabled(false);
			
			//builds inbox
			JPanel bodyPanel = new JPanel();
				bodyPanel.setLayout(new GridLayout(0, 1));
				
				for (int line = 0; line < messages.size(); line++) {
					JPanel panel = new JPanel();
						panel.setBackground(Color.ORANGE);

						panel.add(new JLabel("From: " + messages.get(line).substring(messages.get(line).lastIndexOf("#END") + 5), JLabel.LEFT));
						panel.add(new JLabel("Date: " + messages.get(line).substring(messages.get(line).lastIndexOf("#DATE") + 6, messages.get(line).lastIndexOf("#DATE") + 16), JLabel.CENTER));
						panel.add(new JLabel("Subject: " + messages.get(line).substring(messages.get(line).lastIndexOf("#SUBJECT") + 9, messages.get(line).lastIndexOf("__#DATE")), JLabel.CENTER));
					
						final int element = line;
						JButton reader = new JButton("Read/Edit");
							reader.addActionListener(new ActionListener(){
								public void actionPerformed(ActionEvent event) {
									index = element;
									bodyBUIlder(); }
							});
					panel.add(reader);
				bodyPanel.add(panel); }
			body.setViewportView(bodyPanel); } 
		else {
			//recalibrates delete button
			for (ActionListener listener : deleteButton.getActionListeners()) { deleteButton.removeActionListener(listener); }
			deleteButton.setEnabled(true);
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {

					//removes message from senders holding file
					File inputFile = new File("./com.lingotechsolutions.data/messages/" + messages.get(index).substring(messages.get(index).lastIndexOf("#END") + 5));
					File tempFile = new File("./com.lingotechsolutions.data/messages/temp.txt");
					try {
						BufferedReader reader = new BufferedReader(new FileReader(inputFile));
						BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

						String lineToRemove = messages.get(index).substring(0, messages.get(index).lastIndexOf("#END")).trim();
						String currentLine;

						while((currentLine = reader.readLine()) != null) {
							// trim newline when comparing with lineToRemove
						    String trimmedLine = currentLine.trim();
						    if(trimmedLine.startsWith(lineToRemove)) continue;
						    writer.write(currentLine + System.getProperty("line.separator"));
						}
						writer.close(); 
						reader.close(); 
						/*boolean successful = */ tempFile.renameTo(inputFile); } 
					catch (IOException e) {}

					//rebuilding inbox after deletion
					index = -1;
					bodyBUIlder(); }
			});
			
			//displays individual messages			
			JPanel panel = new JPanel();
				panel.setBackground(Color.BLACK);
				panel.setLayout(new BorderLayout());
				
				JPanel header = new JPanel();
					header.setBackground(Color.ORANGE);
					
					header.add(new JLabel("From: " + messages.get(index).substring(messages.get(index).lastIndexOf("#END") + 5), JLabel.LEFT));
					header.add(new JLabel("Date: " + messages.get(index).substring(messages.get(index).lastIndexOf("#DATE") + 6, messages.get(index).lastIndexOf("#DATE") + 16), JLabel.RIGHT));					
				panel.add(header, BorderLayout.NORTH);
				
				JPanel subject = new JPanel();
					subject.setBackground(Color.ORANGE);

					subject.add(new JLabel("Subject: " + messages.get(index).substring(messages.get(index).lastIndexOf("#SUBJECT") + 9, messages.get(index).lastIndexOf("__#DATE")), JLabel.LEFT));
					subject.add(new JLabel(""));
				panel.add(subject, BorderLayout.CENTER);
				
				JPanel message = new JPanel();
					message.setBackground(Color.WHITE);
					
					//actual content of message
					JTextField field = new JTextField(messages.get(index).substring(messages.get(index).lastIndexOf("#BODY") + 6, messages.get(index).lastIndexOf("__#END")));
						field.setEditable(false);
					message.add(field);
				panel.add(message, BorderLayout.SOUTH);	
			body.setViewportView(panel); }
	}
}
