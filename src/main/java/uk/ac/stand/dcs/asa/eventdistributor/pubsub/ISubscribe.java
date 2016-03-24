/*
 * Created on 24-Oct-2005
 */
package uk.ac.stand.dcs.asa.eventdistributor.pubsub;



/**
 * ISubscribe provides the methodes to tell the system that 
 * a particullar participant (ISubscriber) is interested in a particullar event. 
 * The event - or the search pattern is described in IPattern
 * <P>
 * It provides also the methodes to tell the system that 
 * a participant is not interested anymore in receving events
 * 
 * TODO Implementation of a "Pattern Syntax"
 *  
 * 
 * @author markus
 */

public interface ISubscribe {
	
	/**
	* Adds a given interested participant to the lois and the discription of the event to lois 
	* 
	* @param s An entity that is interested in the event and wants to subscribe 
	* @param p a reference to an object that includes some pattern and or value matching methods
	*/
	public void subscribe(ISubscriber s, IPredicate p);
	
	/**
	* Removes a given interested Participant s to the lois
	*  
	* @param s An entity that WAS interested in the event and still is subscribed (former subscriber) 
	* @param p a reference to an object that includes some pattern and or value matching methods
	*/
	
	public void unsubscribe(ISubscriber s,IPredicate p);

		

	
}
