/*
 * Created on 01-Nov-2004
 */
package uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces;

import uk.ac.stand.dcs.asa.eventModel.Event;

/**
 * @author stuart, graham
 */
public interface EventConsumer {
    
	boolean interested(Event event);
	
	void receiveEvent(Event event);
}
