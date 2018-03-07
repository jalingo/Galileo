package interfaceComponents;

import javax.swing.*;

public class GalileoMode extends JComboBox<String> {
	private static final long serialVersionUID = 2007520076734463272L;

	public GalileoMode() {
		addItem("MyView Report");
		addItem("Operator Mode");
		addItem("Scheduler Mode");
		addItem("Fiscal Report"); 
		setEnabled(false); }

	public void setAccess(boolean access) {
		setEnabled(access); }
	
	public void setAccess() {
		setAccess(true); }
}
