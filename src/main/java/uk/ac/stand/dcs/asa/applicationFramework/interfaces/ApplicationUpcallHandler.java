/*
 * Created on 09-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.Message;
import uk.ac.stand.dcs.asa.interfaces.IKey;

/**
 * @author stuart
 */
public interface ApplicationUpcallHandler {
	
    //routing upcall
    String applicationNextHop(IKey k, AID a) throws P2PApplicationException;
    String applicationNextHop(IKey k, AID a, Message m) throws P2PApplicationException;
    void receiveMessage(IKey k, AID a, Message m) throws P2PApplicationException;
}
