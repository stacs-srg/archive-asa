/*
 * Created on 25-Oct-2005
 */
package uk.ac.stand.dcs.asa.eventdistributor.pubsub;
import uk.ac.stand.dcs.asa.eventModel.Event;

/**
 * ISubscriber provides methodes that describe an intersted entity.
 * The entity is refered to as Subscriber. 
 * It also provides the method for receiving an event.
 * 
 * @author markus
 */


public interface ISubscriber {

	/**
	 * Receives an event from the pubsub system 
	 *  
	 * @param e Event 
	 * @see uk.ac.stand.dcs.asa.eventModel.Event 
	 */
	public void receive(Event e);
	

}