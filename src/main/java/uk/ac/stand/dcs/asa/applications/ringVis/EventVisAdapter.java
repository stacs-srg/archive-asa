/*
 * Created on 03-Nov-2004
 */
package uk.ac.stand.dcs.asa.applications.ringVis;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.RemoteRRTRegistry;

import java.net.InetSocketAddress;

/**
 * @author stuart, graham
 */
public class EventVisAdapter implements EventConsumer {
    
	static int ADMIN_NODE_RETRY = 5;		
	static int ADMIN_NODE_DELAY = 1000;	//ms
	
	protected String name;
	protected EventConsumer admin_node = null;
	protected JChordNetworkEventFactory network_event_factory = null;
	
	public EventVisAdapter(String name) {
		this.name = name;
	}
	
	public void setAdminNode(InetSocketAddress adminNodeAddresss, String service_name, JChordNetworkEventFactory network_event_factory) {
	    
	    setAdminNode(adminNodeAddresss, service_name, network_event_factory, ADMIN_NODE_RETRY, ADMIN_NODE_DELAY);
	}
	
	public void setAdminNode(InetSocketAddress adminNodeAddresss, String service_name, JChordNetworkEventFactory network_event_factory, int retry, int delay) {
	    
	    if (admin_node == null) {
	        
	        try { admin_node = (EventConsumer) RemoteRRTRegistry.getInstance().getService(adminNodeAddresss, EventConsumer.class, service_name, retry, delay); }
	        catch (Exception e) { Error.exceptionError("could not get remote admin service", e); }
	        
	        this.network_event_factory = network_event_factory;
	        
	    } else Error.errorNoEvent("(" + name + ") attempt to initialise WSHost when it is already set.");
	}
	
	public boolean interested(Event event) {

		return true;
	}

	/**
	 * Output a message to an AdminNode at an address specified when this node was created.
	 */
	public void receiveEvent(Event event) {
		
		if (admin_node != null) {
			try {
				admin_node.receiveEvent(network_event_factory.makeNetworkEvent(event));	
			} catch (Exception e) {
				Error.exceptionErrorNoEvent("EventVisAdapter object (" + name + ") could not send NetworkEvent", e);
			}
		} else Error.errorNoEvent("EventVisAdapter object (" + name + ") could not send NetworkEvent. No target has been specified.");

	}
}
