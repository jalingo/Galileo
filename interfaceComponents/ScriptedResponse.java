package interfaceComponents;

import java.awt.*;
//import java.io.*;
import javax.swing.*;

public class ScriptedResponse extends JFrame {
	private static final long serialVersionUID = -5633814279177271632L;
	
	public ScriptedResponse() {
		JPanel body = new JPanel();
		body.setLayout(new GridLayout(0, 1));
		body.setBackground(Color.CYAN);
		body.setForeground(Color.BLACK);
		
		body.add(new JTextField("One moment."));
//		body.add(new JTextArea("Next is here, ready?\n\nK, sending client to rm REPLACE.\n\nRm REPLACE, head on over."));
//		body.add(new JTextArea("I’m not seeing you on our client list. \nHave you booked with us before?\n"));
//		body.add(new JTextArea("What’s your email? I can also try and find your \nlast appointment, if you remember when it was."));
		body.add(new JTextField("This is a text only line."));
//		body.add(new JTextArea("What time/duration are you inquiring about? \nI can let you know if that is available.\n"));
//		body.add(new JTextArea("The website has all available information \nabout each provider, as well as pictures \nand links to any reviews."));
		body.add(new JTextField("I’ll have to ask. Let me get back to you."));		
		body.add(new JTextField("Great! I know she'd appreciate that being mentioned in a review."));
		add(new JScrollPane(body), BorderLayout.CENTER);
		
		setTitle("Scripted Responses");
		setLocation(50, 70);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true); }
}
