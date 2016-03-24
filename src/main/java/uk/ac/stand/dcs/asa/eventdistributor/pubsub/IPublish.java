/*
 * Created on 25-Oct-2005
 */
package uk.ac.stand.dcs.asa.eventdistributor.pubsub;

import uk.ac.stand.dcs.asa.eventModel.Event;

/**
 * IPublish provides the methods to publish an event to the system
 * where it will be forwarded according to the ISubscriber infromation
 * 
 * @author markus
 */



public interface IPublish {
	/**
	 * That method is needed to publish a particullar event to the pubsub system.
	 * 
	 * @param e Event
	 * 
	 */	
	 public void publish(Event e);
	 
}