package uk.ac.stand.dcs.asa.eventdistributor.pubsub;

import uk.ac.stand.dcs.asa.eventModel.Event;


public interface IPredicate {
	
	public boolean match(Event e);

}
