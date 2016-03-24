package uk.ac.stand.dcs.asa.eventdistributor.application;

import uk.ac.stand.dcs.asa.eventdistributor.pubsub.EdServer;

/**
 * Test szenario starts a server - passes the server on to the client that 
 * subscribes and passes a reference on to the client that puplishes.
 * 
 * The publisher sends his message to the server and the server forwards the message to the subscriber
 *  
 * @author markus
 *
 */

public class Main {

	
	public static void main(String[] args) {
		
		 		
		EdServer eds = new EdServer();// Get server reference from RRT (getObjectByName)

		new EdClient_Subscr(eds);
		new EdClient_Subscr(eds);
		
		new EdClient_Publ(eds);
		new EdClient_Publ(eds);
		new EdClient_Publ(eds);
		new EdClient_Publ(eds);

	}

}
