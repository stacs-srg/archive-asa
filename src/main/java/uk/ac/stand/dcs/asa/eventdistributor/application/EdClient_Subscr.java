/*
 * Created on 06-Oct-2005
 */

package uk.ac.stand.dcs.asa.eventdistributor.application;


/**
 * starts the subscriber client, who is sending a subscription to the server (forwarder)
 * by implemention ISubscriber it provides the receive method where the message is sent to by the server.
 * 
 * @author markus
 */
import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventdistributor.pubsub.IPredicate;
import uk.ac.stand.dcs.asa.eventdistributor.pubsub.ISubscribe;
import uk.ac.stand.dcs.asa.eventdistributor.pubsub.ISubscriber;

public class EdClient_Subscr implements ISubscriber {

	IPredicate descr;
	Event e;
	ISubscribe server_subscr;


	/**
	 * distributed version constructor 
	 * 
	 * @param server_subscr
	 */
	
	public EdClient_Subscr(ISubscribe server_subscr) {
		this.server_subscr = server_subscr;

		clientSubscribing();
	    
	}
	
	
	private void clientSubscribing() {
		// creates new Pattern .. in this case its only the type TestEvent we
		// are interested in
		descr = new Predicate();

		// get local host and pass it on to subsriber
		// subscriber=new Subscriber("livet");

		// tells the server about its interests
		server_subscr.subscribe(this, descr);
	}


	
	public void receive(Event e) {
		// TODO Auto-generated method stub
		this.e = e;
		
	
		
		System.out.println("Client  received event with type " + e.getType()
				+ " and msg 1" + e.get("Warning")+" and free "+e.get("free"));

	}
	
}
