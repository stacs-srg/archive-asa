/*
 * Created on 12-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.util;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;

/**
 * @author stuart
 */
public class WriterEventAdapter implements EventConsumer {

    public String getAdapterName() {
        return "Stuart's dumb event adapter";
    }

	public boolean interested(Event event) {

		return true;
	}

	public void receiveEvent(Event event) {

        System.out.println(event.getType());
	}
}
