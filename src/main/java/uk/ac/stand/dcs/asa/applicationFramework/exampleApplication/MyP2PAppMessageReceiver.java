/*
 * Created on 28-Jun-2005
  */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.applicationFramework.impl.Message;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;

/**
 * @author stuart
 */
public interface MyP2PAppMessageReceiver {
    public void receiveMsg(Message m) throws P2PApplicationException;
}
