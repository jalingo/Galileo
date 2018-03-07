package interfaceComponents;

import java.awt.event.*;
import javax.swing.*;
import controls.*;
import data.*;

public class AdminMenus extends OperatorMenus {
	private static final long serialVersionUID = 5320366041642016792L;
	private JMenu adminMenu 			= new JMenu("Admin");
	private JMenuItem inBox 			= new JMenuItem("Check Messages");
	private JMenuItem sideBarEditor 	= new JMenuItem("Edit SideBar Data");
	private JMenuItem eMailBlaster 		= new JMenuItem("Send eMail Update");
	private JMenuItem branchManager 	= new JMenuItem("Branch Manager");
	private JMenuItem purgeData 		= new JMenuItem("Update Client Data");
	private JMenuItem initData 			= new JMenuItem("Inititialize Data");
	
	public AdminMenus() {
//		inBox.setEnabled(false);
		inBox.addActionListener(new Messager());
		adminMenu.add(inBox);
		
		sideBarEditor.addActionListener(new SideBarEditor());			
		adminMenu.add(sideBarEditor);
		
		eMailBlaster.addActionListener(new BulkMailer());		
		adminMenu.add(eMailBlaster);
		
		branchManager.setEnabled(false);
		adminMenu.add(branchManager);
		
		adminMenu.addSeparator();
		
		purgeData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(null, "This process may take more than a moment.\nPlease do not quit/interupt the process...", "Correct Client Statuses", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (response == JOptionPane.OK_OPTION) {
					Client.updateClientsStatus(); }
			}
		});
		adminMenu.add(purgeData);
		
		initData.addActionListener(new InitData());
		adminMenu.add(initData);
		
		this.add(adminMenu); }
}
