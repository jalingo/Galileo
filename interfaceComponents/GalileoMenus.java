package interfaceComponents;

import java.awt.event.*;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;

import controls.*;

public class GalileoMenus extends JMenuBar {
	private static final long serialVersionUID = 1839056834899422062L;

	//instance variables
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem importFile = new JMenuItem("Import Legacy Data");
	private JMenuItem connectSMS = new JMenuItem("Connect to Google Account");
	private JMenuItem quitItem = new JMenuItem("Quit");
	private JMenu editMenu = new JMenu("Edit");
	private JMenuItem cutItem = new JMenuItem(new DefaultEditorKit.CutAction());	
	private JMenuItem copyItem = new JMenuItem(new DefaultEditorKit.CopyAction());
	private JMenuItem pasteItem = new JMenuItem(new DefaultEditorKit.PasteAction());
	private ImportEinstienData chooser = new ImportEinstienData();

	//constructrors
	public GalileoMenus() {}

	//utilites
	void BasicBUIlder() {
		importFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) { 
				try { chooser.ImportChooser(); } 
					catch (IOException | ParseException e) { e.printStackTrace(); }
			}
		});
		fileMenu.add(importFile);
		connectSMS.addActionListener(new ActionListener(){
			//public void actionPerformed(ActionEvent event) { JOptionPane.showMessageDialog(connectSMS, "Functionality not yet present...", "Error", JOptionPane.DEFAULT_OPTION); }
			
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				gMenu menu = new gMenu(); }
		});
//		connectSMS.setEnabled(false);
		JMenuItem exportFile = new JMenuItem("BackUp Data");
		exportFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					@SuppressWarnings("unused")
					ExportData data = new ExportData(); }
				catch (IOException e1) {
					e1.printStackTrace(); }
			}
		});
		fileMenu.add(exportFile);
		fileMenu.add(connectSMS);
		fileMenu.addSeparator();
		quitItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) { System.exit(0); } });
		fileMenu.add(quitItem);
		this.add(fileMenu);
		
		cutItem.setText("Cut");
		editMenu.add(cutItem);
		copyItem.setText("Copy");
		editMenu.add(copyItem);
		pasteItem.setText("Paste");
		editMenu.add(pasteItem);
		this.add(editMenu); }
}