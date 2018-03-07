package controls;

import java.awt.event.*;
import javax.swing.*;
import data.*;

public class BulkMailer extends AbstractAction {
	private static final long serialVersionUID = -6995212548915668825L;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JPanel display = new JPanel();
//		display.setPreferredSize();
			JTextArea message = new JTextArea();
				message.setEditable(false);
				message.setLineWrap(true);
				message.setSize(900, 10);
				String betweenTheLines = "";
				int counter = 0;
				for (Client c : Client.getClients()) {
					if (c.getStatus().equals("MEMBER") || c.getStatus().equals("ALISTER")) {
						if (c.getContactEmail() == false || c.getEmail().contains("replace") || c.getEmail().contains("account@email") || c.getEmail().contains("unknown") || c.getEmail().isEmpty()) {
							c.setContactEmail(false); }
						else if (betweenTheLines.contains(c.getEmail())) {}
						else {
							if (counter == 0 ){
								betweenTheLines = c.getEmail();	}
							else {
								betweenTheLines = betweenTheLines + ", " + c.getEmail(); }
							counter++; }
					}
				}
				message.setText(betweenTheLines);
			display.add(new JScrollPane(message));
			//display.add(message);
		JOptionPane.showMessageDialog(null, display, "BCC eMail Listing", JOptionPane.PLAIN_MESSAGE); }
}
