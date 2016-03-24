package uk.ac.stand.dcs.asa.eventdistributor.pubsub;

import uk.ac.stand.dcs.asa.eventModel.Event;

public class Subscriber implements ISubscriber{

	private String identity;
	
	public Subscriber(String identity) {
		
		this.identity = identity;
		
	}
	
	public void receive(Event e) {
		// TODO Auto-generated method stub
		
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

}
