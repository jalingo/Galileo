package controls;

import java.time.*;
import java.io.IOException;
import java.nio.file.*;

public class ExportData {

	public ExportData() throws IOException {
		String name = "Export:" + LocalDateTime.now().toString() + "/";
		Path begins = Paths.get(".", "backups", name);
		Path toACCT = Paths.get(".", "backups", name, "accounts.xml");
		Path fromACCT = Paths.get(".", "com.lingotechsolutions.data", "accounts.xml");
		Path toBRANCH = Paths.get(".", "backups", name, "branches.xml");
		Path fromBRANCH = Paths.get(".", "com.lingotechsolutions.data", "branches.xml");
		Path toCHECK = Paths.get(".", "backups", name, "checks.xml");
		Path fromCHECK = Paths.get(".", "com.lingotechsolutions.data", "checks.xml");
		Path toCLIENT = Paths.get(".", "backups", name, "clients.xml");
		Path fromCLIENT = Paths.get(".", "com.lingotechsolutions.data", "clients.xml");
		Path toEVENT = Paths.get(".", "backups", name, "events.xml");
		Path fromEVENT = Paths.get(".", "com.lingotechsolutions.data", "events.xml");
		Path toLINKS = Paths.get(".", "backups", name, "links.xml");
		Path fromLINKS = Paths.get(".", "com.lingotechsolutions.data", "links.xml");
		Path toLOG = Paths.get(".", "backups", name, "log");
		Path fromLOG = Paths.get(".", "com.lingotechsolutions.data", "log");
		Path toOPERS = Paths.get(".", "backups", name, "operators.xml");
		Path fromOPERS = Paths.get(".", "com.lingotechsolutions.data", "operators.xml");
		Path toSCHED = Paths.get(".", "backups", name, "schedule.xml");
		Path fromSCHED = Paths.get(".", "com.lingotechsolutions.data", "schedule.xml");
		Path toSCRIPT = Paths.get(".", "backups", name, "scriptedLanguage.xml");
		Path fromSCRIPT = Paths.get(".", "com.lingotechsolutions.data", "scriptedLanguage.xml");
		Path toSITE = Paths.get(".", "backups", name, "sites.xml");
		Path fromSITE = Paths.get(".", "com.lingotechsolutions.data", "sites.xml");
		Path toTECHS = Paths.get(".", "backups", name, "technicians.xml");
		Path fromTECHS = Paths.get(".", "com.lingotechsolutions.data", "technicians.xml");
			
		Files.createDirectories(begins);
		Files.createFile(toACCT);
		Files.createFile(toBRANCH);
		Files.createFile(toCHECK);
		Files.createFile(toCLIENT);
		Files.createFile(toEVENT);
		Files.createFile(toLINKS);
		Files.createFile(toLOG);
		Files.createFile(toOPERS);
		Files.createFile(toSCHED);
		Files.createFile(toSCRIPT);
		Files.createFile(toSITE);
		Files.createFile(toTECHS);
		
		Files.copy(fromACCT, toACCT, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromBRANCH, toBRANCH, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromCHECK, toCHECK, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromCLIENT, toCLIENT, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromEVENT, toEVENT, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromLINKS, toLINKS, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromLOG, toLOG, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromOPERS, toOPERS, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromSCHED, toSCHED, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromSCRIPT, toSCRIPT, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromSITE, toSITE, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(fromTECHS, toTECHS, StandardCopyOption.REPLACE_EXISTING); }
}

