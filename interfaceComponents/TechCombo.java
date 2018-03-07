package interfaceComponents;

import javax.swing.*;
import data.*;

public class TechCombo extends JComboBox<String> {
	private static final long serialVersionUID = 5717360872848109790L;

	public TechCombo() { //will eventually pass branch as an argument	
		for (Technician t : Technician.getTechs()) { addItem(t.getName()); }
		addItem("Assign Tech");
		
		setEnabled(true); }
}