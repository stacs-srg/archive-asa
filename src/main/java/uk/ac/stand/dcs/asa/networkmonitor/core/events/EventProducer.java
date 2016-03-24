/*
 * Created on Jun 28, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.events;

import uk.ac.stand.dcs.asa.eventModel.Event;

/**
 * A producer of events. This could be, for example, a source plugin.
 * 
 * @author gjh1
 */
public interface EventProducer {
    /**
     * Takes the given event and passes it on to the event bus for delivery.
     * 
     * @param event the event to deliver
     */
    public void sendEventToBus(Event event);
}
