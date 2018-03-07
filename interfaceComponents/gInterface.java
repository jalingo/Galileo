package interfaceComponents;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.*;

import com.techventus.server.voice.Voice;

import data.*;

public class gInterface extends JFrame {
	private static final long serialVersionUID = 5941133503988684081L;
	private static Voice voice;
	
	private String userName;
	
	public gInterface(gAccount g) {
		gAccount acct = g;
		try {
			voice = new Voice(acct.getName(), acct.getPass()); 
System.out.println(voice.getInbox());
		} 
		catch (IOException e) { 
			JOptionPane.showMessageDialog(null, "Google connection failed...");
			e.printStackTrace(); }

		setTitle("Google Connection: " + acct.getName());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
/*		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);	*/

		JPanel body = new JPanel();
			String inbox = new String();
			try {
				 inbox = voice.getInbox(); } 
			catch (IOException e) {
				e.printStackTrace(); }
System.out.println(inbox);
		add(body, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
			userName = acct.getName();
			footer.add(new JLabel(userName));
			JButton refresh = new JButton("Refresh");
			footer.add(refresh);
		add(footer, BorderLayout.SOUTH);
		
		pack(); }
}
