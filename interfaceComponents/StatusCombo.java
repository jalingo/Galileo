package interfaceComponents;

import javax.swing.JComboBox;

public class StatusCombo extends JComboBox<String> {
	private static final long serialVersionUID = -1182769752963399075L;

	//constructor
	public StatusCombo() {
		addItem("PENDNG");
		addItem("CONFRM");
		addItem("OPENED");
		addItem("CLOSED");
		addItem("CANCEL"); }
}
