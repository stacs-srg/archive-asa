/*
 * Created on 17-Aug-2004
 */
package uk.ac.stand.dcs.asa.applications.eventHarness;

import uk.ac.stand.dcs.asa.eventModel.Event;

/**
 * @author stuart, graham
 */
public class NetworkEventHandler implements NetworkEvent_WS, ErrorNetworkEvent_WS, DiagnosticNetworkEvent_WS, JChordNetworkEvent_WS {

	public static synchronized void writeMsg(String s){
		System.out.println(s);
	}
	
	public void sendErrorNetworkEvent(Event ne) {

        writeMsg((String)ne.get("msg"));
    }

	public void sendDiagnosticNetworkEvent(Event ne) {

        writeMsg((String)ne.get("msg"));
    }

	public void sendJChordNetworkEvent(Event ne) {

        writeMsg("JChordRepEvent received");
    }

	public void sendNetworkEvent(Event ne) {
	    
        String eventType = ne.getType();
        
    	writeMsg(eventType + " received");
        
        if (eventType.equals("ErrorEvent") || eventType.equals("DiagnosticEvent")) {
        	
            writeMsg("\n\t" + ne.get("msg"));
        }
    }
}
