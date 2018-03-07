package controls;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.util.*;

import data.*;

public class SearchEmail extends AbstractAction {
	private static final long serialVersionUID = 2351647574704466506L;

	public SearchEmail() {} 		//will eventually pass USER as arg to allow ADMIN to search more than clients

	@Override
	public void actionPerformed(ActionEvent e) {
		String querry = JOptionPane.showInputDialog("Search by eMail:");
		if (querry.isEmpty()) {}
		else {
			ArrayList<Client> discoveries = new ArrayList<Client>();
			ArrayList<Client> clients = Client.getClients();
			for (Client c : clients) {
				if (c.getEmail().toLowerCase().contains(querry.toLowerCase())) {
					discoveries.add(c); }
			}
			if (discoveries.isEmpty()) {}
			else {
				StringBuilder discovery = new StringBuilder(300);
				for (Client c : discoveries) {					
					discovery.append(c.getStatus() + ": " + c.getSms() + " / " + c.getEmail() + "\n"); }
				JOptionPane.showMessageDialog(null, discovery, "Search Results", JOptionPane.OK_OPTION); }
		}
	}
}
