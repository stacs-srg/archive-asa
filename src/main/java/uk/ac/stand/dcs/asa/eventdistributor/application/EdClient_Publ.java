/*
 * Created on 06-Oct-2005
 */

package uk.ac.stand.dcs.asa.eventdistributor.application;

/**
 * The publisher client generates an event and sends it to the server
 * 
 * @author markus
 */

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventdistributor.pubsub.IPublish;

public class EdClient_Publ {

	Event e;
	IPublish server_publ;
	String identity;
	int i;

	/**
	 * distributed version client 
	 * "resolves" the server adress
	 */

	public EdClient_Publ(IPublish server_publ) {
	
		this.server_publ = server_publ;

		    clientPublishing();
	    
	}
	
		

	/**
	 * sends a event description to the server (forwarder)
	 */
	private void clientPublishing() {

		while (i < 1) {
			server_publ.publish(EventFactory.make());
			i++;
		}

	}
	
}
