/*
 * Created on Apr 27, 2005 at 4:38:00 PM.
 */
package uk.ac.stand.dcs.asa.util.test;

import uk.ac.stand.dcs.asa.applications.ringVis.EventVisAdapter;
import uk.ac.stand.dcs.asa.eventModel.Event;

/**
 * @author graham
 */
public class DiagnosticTestAdapter extends EventVisAdapter {
    
    public Event event;

    public DiagnosticTestAdapter(String s) {
        super(s);
    }

    public void receiveEvent(Event e) {

        event = e;
    }

}
