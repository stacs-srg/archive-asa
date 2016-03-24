/*
 * Created on Jun 15, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;

/**
 * Abstract class representing a NetworkMonitor output plugin.
 * 
 * @author gjh1
 */
public abstract class OutputPlugin extends Plugin implements EventConsumer {
    public boolean interested(Event event) {
        // By default, receive all events
        return true;
    }
}
