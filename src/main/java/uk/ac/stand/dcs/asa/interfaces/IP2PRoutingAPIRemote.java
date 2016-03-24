/*
 * Created on 22-Jun-2005
  */
package uk.ac.stand.dcs.asa.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.DOLResult;
import uk.ac.stand.dcs.asa.applicationFramework.impl.Message;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID;

/**
 * @author stuart
 */
public interface IP2PRoutingAPIRemote {
    void deliverMessage(IKey k, AID appID, Message m) throws P2PNodeException;
    DOLResult dol(IKey k, AID appID) throws P2PNodeException;
}
