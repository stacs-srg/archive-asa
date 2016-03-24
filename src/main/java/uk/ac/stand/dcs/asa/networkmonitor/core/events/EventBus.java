/*
 * Created on Jun 16, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.events;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.util.Diagnostic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The NetworkMonitor event bus. This is responsible for transporting events between plugins.
 * 
 * @author gjh1
 */
public class EventBus implements EventConsumer {
    private static EventBus uniqueInstance = new EventBus();

    /**
     * The plugins which have requested that they should receive events from this bus.
     */
    private List consumers;

    private EventBus() {
        consumers = Collections.synchronizedList(new ArrayList());
    }

    /**
     * @return the unique instance of this singleton class
     */
    public static EventBus getInstance() {
        return uniqueInstance;
    }

    public boolean interested(Event event) {
        // The event bus wants to receive all events
        return true;
    }

    public void receiveEvent(Event event) {
        Diagnostic.trace("New event on bus: " + event.toString(), Diagnostic.FULL);
        notifyConsumers(event);
    }

    /**
     * Add a new consumer to the list of consumers the bus sends events to.
     * 
     * @param ec the consumer to add
     */
    public void registerConsumer(EventConsumer ec) {
        synchronized(consumers) {
            consumers.add(ec);
        }
    }

    /**
     * Remove a consumer from the consumer list. It will no longer receives events.
     * 
     * @param ec the consumer to remove
     */
    public void removeConsumer(EventConsumer ec) {
        synchronized (consumers) {
            int i = consumers.indexOf(ec);
            if (i >= 0) consumers.remove(i);
        }
    }

    /**
     * Send a newly-arrived event on to all registered consumers.
     * 
     * @param event the event to broadcast
     */
    private void notifyConsumers(Event event) {
        synchronized (consumers) {
            Iterator i = consumers.iterator();
            while (i.hasNext()) {
                EventConsumer ec = (EventConsumer) i.next();
                ec.receiveEvent(event);
            }
        }
    }
}
