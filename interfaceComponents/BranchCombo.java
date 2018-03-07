package interfaceComponents;

import javax.swing.*;

import data.*;

public class BranchCombo extends JComboBox<String> {
	private static final long serialVersionUID = 4540324740919412901L;
	private Operator usr;
	
	public BranchCombo(Operator user) { 
		usr = user;
		
		for (Branch br : usr.getBranches()) {
			addItem(br.getName()); }
		
		if (usr.getAccessPrivs().equals("ADMIN")) {
			addItem("New"); }
		
		setToolTipText("Select Branch to edit"); }
	
	public BranchCombo() {
		setToolTipText("Future Release");
		addItem("Ok0");
		addItem("Ok1");
		addItem("Sf0"); 
		addItem("New"); }
}
