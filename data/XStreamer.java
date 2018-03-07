package data;

import com.thoughtworks.xstream.XStream;

public class XStreamer extends XStream {
	public XStreamer() {
		alias("accounts", Account.class);
		alias("checklist", Checklist.class);
		alias("links", Link.class);
		alias("script", Script.class);
		
		alias("client", Client.class);
		alias("operator", Operator.class);
		alias("tech", Technician.class);

		alias("branch", Branch.class);
		alias("site", Site.class);

		alias("schedule", Availability.class);
		alias("queue", ServiceRequest.class); }
}
