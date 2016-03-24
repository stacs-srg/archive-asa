/*
 * Created on 03-Nov-2004
 */
package uk.ac.stand.dcs.asa.applications.ringVis;

import uk.ac.stand.dcs.asa.eventModel.Event;

/**
 * @author stuart, graham
 */
public class JChordEventVisAdapter extends EventVisAdapter{

	public JChordEventVisAdapter(String name) {
		super(name);
	}
	
	public boolean interested(Event event) {

		return true;
	}
}
