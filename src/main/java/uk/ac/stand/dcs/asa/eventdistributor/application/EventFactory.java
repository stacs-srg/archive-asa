/*
 * Created on 19-Oct-2005
 */
package uk.ac.stand.dcs.asa.eventdistributor.application;

import uk.ac.stand.dcs.asa.eventModel.Event;

import java.util.HashMap;
import java.util.Random;

/**
 * @author markus
 */

// randomly generates events when called
public class EventFactory {
	static HashMap messages;
	static {
		messages = new HashMap();
		messages.put("0", "Node failed");
		messages.put("1", "Node joined");
		messages.put("2", "Node is happy");
		messages.put("2", "");
	}

	public static Event make() {
		int MaxE = new Random().nextInt(3);
		String nr = "" + MaxE;
		Event e = new Event();
		e.setType("TestEvent");
		e.put("Warning", messages.get(nr));
		e.put("free",""+new Random().nextInt(4));
		return e;
	}

	public static void main(String[] args) {
//		Event e = new Event(); // BAD
	//	Event e = EventFactory.make(); // GOOD
	}

}
