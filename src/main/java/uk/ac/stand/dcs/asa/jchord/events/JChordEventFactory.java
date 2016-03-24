package uk.ac.stand.dcs.asa.jchord.events;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class JChordEventFactory {
    public static final String PRED_REP_EVENT = "PredecessorRepEvent";
    public static final String SUCC_REP_EVENT = "SuccessorRepEvent";
    public static final String NFN_REP_EVENT = "NodeFailureNotificationRepEvent";
    public static final String SUCC_STATE_REP_EVENT = "SuccessorStateEvent";
    
    public static final String SUCCESSORLIST_CHANGE_EVENT = "SuccessorListChangeEvent";
    public static final String SUCCESSORLIST_CHANGE_NEW_LIST = "new_list";
    public static final String SUCCESSORLIST_CHANGE_KEY_RANGE = "key_range";
    public static final String SUCCESSORLIST_CHANGE_REMOVED = "removed";
    public static final String SUCCESSORLIST_CHANGE_ADDED = "added";
            
	public static Event makeNodeFailureNotificationRepEvent(InetSocketAddress failed, IKey failed_key) {
		Event event = new Event();
		
		event.setType("NodeFailureNotificationRepEvent");
		
		event.put("failed", failed);
		event.put("failed_key", failed_key);
		
		return event;
	}
	
	public static Event makePredecessorRepEvent(InetSocketAddress pred, IKey pred_key) {		
		Event event = new Event();
		
		event.setType(PRED_REP_EVENT);
		
		event.put("pred", pred);
		event.put("pred_key", pred_key);
		
		return event;
	}
	
	public static Event makeSuccessorRepEvent(InetSocketAddress succ, IKey succ_key) {
		Event event = new Event();
		
		event.setType(SUCC_REP_EVENT);
		
		event.put("succ", succ);
		event.put("succ_key", succ_key);
		
		return event;
	}
	
	public static Event makeSuccessorStateEvent(IJChordRemote newSuccessor, IJChordRemote oldSuccessor) {

		Event event = new Event();
		
		event.setType(SUCC_STATE_REP_EVENT);
		
		event.put("newSuccessor", newSuccessor);
		event.put("oldSuccessor", oldSuccessor);
		
		return event;
	}

    public static Event makeSuccessorListChangeEvent(ArrayList store, KeyRange keyRange, ArrayList new_list, ArrayList added, ArrayList removed) {
        Event event = new Event();
        event.setType(SUCCESSORLIST_CHANGE_EVENT);
        event.put(SUCCESSORLIST_CHANGE_KEY_RANGE,keyRange);
        event.put(SUCCESSORLIST_CHANGE_NEW_LIST,new_list);
        event.put(SUCCESSORLIST_CHANGE_ADDED,added);
        event.put(SUCCESSORLIST_CHANGE_REMOVED,removed);
        return event;
    }
    
}
