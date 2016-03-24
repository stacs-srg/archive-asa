/*
 * Created on 09-Aug-2004
 */
package uk.ac.stand.dcs.asa.applications.eventHarness;

import uk.ac.stand.dcs.asa.applications.ringVis.JChordNetworkEventFactory;
import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.P2PHost;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.RemoteRRTRegistry;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author stuart, graham
 */
public class EventGenerator {

	private NetworkEvent_WS errorEvents, diagnosticEvents, jchordEvents;

	private JChordNetworkEventFactory network_event_factory;

	public EventGenerator(P2PHost p2pes) {
	    errorEvents = null;
	    diagnosticEvents = null;
	    jchordEvents = null;
	    
	    network_event_factory = JChordNetworkEventFactory.initialiseJNEFactory(p2pes);
	}

	public boolean setErrorEvent_WShost(InetAddress address, int port, String name) {
	    
        if (errorEvents != null) return false;

        int servicePort;
        String serviceName;
        
        if (port== 0) servicePort = EventViewer.EventDeliveryServices_port;
        else servicePort = port;

        if (name == "" || name == null) serviceName = EventViewer.ErrorEventServiceName;
        else serviceName = name;

        try {
            errorEvents = (NetworkEvent_WS) RemoteRRTRegistry.getInstance().getService(new InetSocketAddress(address,servicePort), NetworkEvent_WS.class, serviceName);
        } catch (Exception e) {
            Error.exceptionError("could not get remote network event service", e);
        }

        return true;
    }
	
	public boolean setDiagnosticEvent_WShost(InetAddress address, int port, String name) {
	    
		if (diagnosticEvents != null) return false;

        int servicePort;
        String serviceName;
        
        if (port == 0) servicePort = EventViewer.EventDeliveryServices_port;
        else servicePort = port;

        if (name == "" || name == null) serviceName = EventViewer.DiagnosticEventServiceName;
        else serviceName = name;

        try {
            diagnosticEvents = (NetworkEvent_WS) RemoteRRTRegistry.getInstance().getService(new InetSocketAddress(address,servicePort), NetworkEvent_WS.class, serviceName);
        } catch (Exception e) {
            Error.exceptionError("could not get remote network event service", e);
        }
		
		return true;
	}
	
	public boolean setJChordEvent_WShost(InetAddress address, int port, String name) {
	    
        if (jchordEvents != null) return false;

        int servicePort;
        String serviceName;
         
        if (port == 0) servicePort = EventViewer.EventDeliveryServices_port;
        else servicePort = port;

        if (name == "" || name == null) serviceName = EventViewer.JChordEventServiceName;
        else serviceName = name;

        try {
            jchordEvents = (NetworkEvent_WS) RemoteRRTRegistry.getInstance().getService(new InetSocketAddress(address,servicePort), NetworkEvent_WS.class, serviceName);
        } catch (Exception e) {
            Error.exceptionError("could not get remote network event service", e);
        }

        return true;

	}
	
	public void sendErrorEvent(Event e) {
		
		if (errorEvents != null) {
			
			Event ne = network_event_factory.makeNetworkEvent(e);
			Diagnostic.trace("error event msg: " + e.get("msg"), Diagnostic.RUN);
			errorEvents.sendNetworkEvent(ne);
		} else {
			Error.error("Could not send ErrorEvent");
		}
	}
	
	public void sendDiagnosticEvent(Event e) {
		
		if (diagnosticEvents != null) {
			
			Event ne = network_event_factory.makeNetworkEvent(e);
			Diagnostic.trace("diagnostic event msg: " + e.get("msg"), Diagnostic.RUN);
			diagnosticEvents.sendNetworkEvent(ne);
		} else {
			Error.error("Could not send DiagnosticEvent");
		}
	}
	
	public void sendJChordEvent(Event e) {
		
		if (jchordEvents != null) {
			jchordEvents.sendNetworkEvent(network_event_factory.makeNetworkEvent(e));
		} else {
			Error.error("Could not send JChordRepEvent");
		}
	}
}
