/*
 * Created on 17-Aug-2004
 */
package uk.ac.stand.dcs.asa.applications.eventHarness;

import uk.ac.stand.dcs.asa.eventModel.Event;

/**
 * @author stuart
 */
public interface DiagnosticNetworkEvent_WS {
    
	public void sendDiagnosticNetworkEvent(Event ne) throws Exception;
}
