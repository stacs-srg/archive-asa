/*
 * Created on 02-Nov-2004
 */
package uk.ac.stand.dcs.asa.jchord.nodeFactories;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.ApplicationRegistry;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.impl.JChordNodeImpl;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.util.Error;

import java.net.InetSocketAddress;

/**
 * @author stuart, graham
 */
public class JChordSingleton {
    
	private static IJChordNode instance = null;
	
	public static IJChordNode getInstance() {
	    
		if (JChordSingleton.instance == null) Error.error("The JChordNode has not been initialised");
		
		return instance;
	}
	
	public static IJChordNode initialise(InetSocketAddress node_rep, IKey key, IJChordRemote known_node, EventBus bus) throws P2PNodeException{
	    return initialise(node_rep, key, known_node, bus);
	}	
	
	public static IJChordNode initialise(InetSocketAddress node_rep, IKey key, IJChordRemote known_node, EventBus bus, ApplicationRegistry registry) throws P2PNodeException{
	    
		if (JChordSingleton.instance != null){
		    throw new P2PNodeException(P2PStatus.INSTANTIATION_FAILURE,"The JChordNode has already been initialised");
		}else {
		    
		    instance = new JChordNodeImpl(node_rep, key, bus, registry);

			if (known_node == null) instance.createRing();
			else {
			    //try{
			    	instance.join(known_node);
				//} catch (Exception e) {
				    
				    // TODO - need a better mechanism here which allows the application to react to the error.
					//Error.hardErrorNoEvent("error occurred while joining the ring: " + e.getMessage() + " - known node: " + FormatHostInfo.formatHostName(known_node));
				//}
			}
		}
		return instance;
	}
}
