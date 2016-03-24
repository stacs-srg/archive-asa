package uk.ac.stand.dcs.asa.eventdistributor.application;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventdistributor.pubsub.IPredicate;


/**
 * Predicate class for event type = TestEvent ( and free > 1) 
 *
 * @author markus
 */

public class Predicate implements IPredicate {

	private String eventtype;
	
	public Predicate(){
		this.eventtype=new String("TestEvent");
	}
	
	public Predicate(String eventtype){
		this.eventtype=eventtype;
		
	}
	
	public boolean match(Event e) {
		//check if type TestEvent and key free are available 
		//if((e.getType()!=null)&&(e.get("free")!=null))
		if((e.getType()!=null)&&(e.get("free")!=null))
		{
			int free=Integer.parseInt((String)e.get("free"));
			
			
			if((e.getType().equalsIgnoreCase(this.eventtype))&&(free>1))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;	
		}
		
	}

}
