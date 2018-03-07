import interfaceComponents.*;

import java.awt.*;
import java.io.*;
import java.time.*;

import controls.*;
import data.*;

/**
 * @author JA Lingo
 * @version 1.5
 */

public class Galileo {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable(){			
			@Override
			public void run() {
				
				//backup data files
				try { 
					@SuppressWarnings("unused")
					ExportData data = new ExportData(); }
				catch (IOException e1) { e1.printStackTrace(); }

				//update client status information
				if (LocalTime.now().isBefore(LocalTime.of(10, 15))) { 
					Client.updateClientsStatus();
					Checklist.reset(); }

				//attempt login
				LogIn log = new LogIn();
				if (log.getTruth() == true) {
					UserInterface frame = new UserInterface(log.getUser()); 
					frame.setVisible(true); }
				else {
					System.exit(-1); }
				
				File f = new File("./com.lingotechsolutions.data/log");
				try {
					if (f.exists() == false) { f.createNewFile(); }
					FileWriter appender = new FileWriter(f, true);
					appender.write(log.getUser().getName() + "__" + LocalDateTime.now().toString() + "__OUT\n");
					appender.close(); } 
				catch (IOException e) { e.printStackTrace(); }
			}
		});
	}
}
