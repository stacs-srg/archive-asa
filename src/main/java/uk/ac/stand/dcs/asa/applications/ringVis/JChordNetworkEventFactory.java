/*
 * Created on 13-Aug-2004
 */
package uk.ac.stand.dcs.asa.applications.ringVis;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.P2PHost;
import uk.ac.stand.dcs.asa.eventModel.networkEventFactory.factoryInterfaces.INetworkEventFactory;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

/**
 * @author stuart
 */
public class JChordNetworkEventFactory implements INetworkEventFactory {
    
	private P2PHost node;
	private static JChordNetworkEventFactory instance; 
	
	public static JChordNetworkEventFactory initialiseJNEFactory(P2PHost node) {
	    
		Diagnostic.trace("initialising JChordNetworkEventFactory singleton", Diagnostic.INIT);
		
        if (instance == null) instance = new JChordNetworkEventFactory(node);
        else Error.error("attempt to reinitialise JChordNetworkEventFactory singleton");

        return instance;
	}
	
	/**
	 * @return Returns the instance.
	 */
	public static JChordNetworkEventFactory getInstance() {
	    
		if (instance == null) Diagnostic.trace("JChordNetworkEventFactory instance is null", Diagnostic.INIT);
		
        return instance;
	}
	
	public Event makeNetworkEvent(Event e) {
				
		e.put("source", node);
		return e;
	}

	/**
	 * @param node
	 */
	private JChordNetworkEventFactory(P2PHost node) {
		this.node = node;
	}
	
	/**
	 * @return Returns the node.
	 */
	public P2PHost getNode() {
		return node;
	}
}
