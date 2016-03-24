/*
 * Created on Jun 15, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.source;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.networkmonitor.core.events.EventBus;
import uk.ac.stand.dcs.asa.networkmonitor.core.events.EventProducer;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;

/**
 * Abstract class representing a NetworkMonitor source plugin.
 * 
 * @author gjh1
 */
public abstract class EventSourcePlugin extends Plugin implements EventProducer {
    public void sendEventToBus(Event event) {
        EventBus.getInstance().receiveEvent(event);
    }
}
