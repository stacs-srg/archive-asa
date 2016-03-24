/*
 * Created on Jul 5, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.overlay;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.networkmonitor.core.events.EventBus;
import uk.ac.stand.dcs.asa.networkmonitor.core.events.EventProducer;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;

/**
 * A plugin which provides supplimentary information.
 * This can be rendered by an output plugin in an appropriate manner, if desired.
 * 
 * @author gjh1
 */
public abstract class OverlayPlugin extends Plugin implements EventProducer, EventConsumer {
    public boolean interested(Event event) {
        // By default, receive all events
        return true;
    }

    public void sendEventToBus(Event event) {
        EventBus.getInstance().receiveEvent(event);
    }
}
