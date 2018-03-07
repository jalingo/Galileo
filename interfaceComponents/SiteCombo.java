package interfaceComponents;

import javax.swing.*;
import java.util.*;
import data.*;

public class SiteCombo extends JComboBox<String> {
	private static final long serialVersionUID = 8579306282950459668L;
	private ArrayList<Site> sites = Site.getSites();
			
	public SiteCombo() {
		setEditable(true);

		//addItem("Street Addr");
		for (Site s : sites) {
			if (s.getStreetAddress() != "") { addItem(s.getStreetAddress()); }
		}
	}
}
