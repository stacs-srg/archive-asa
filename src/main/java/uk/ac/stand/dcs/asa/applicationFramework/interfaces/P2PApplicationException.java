/*
 * Created on 28-Jun-2005
  */
package uk.ac.stand.dcs.asa.applicationFramework.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;

/**
 * @author stuart
 */
public class P2PApplicationException extends P2PNodeException {

    public P2PApplicationException(P2PNodeException e){
        super(e.getStatus(),e.getLocalizedMessage());
    }
    
    /**
     * @param status
     * @param msg
     */
    public P2PApplicationException(P2PStatus status, String msg) {
        super(status, msg);
    }

    /**
     * @param status
     */
    public P2PApplicationException(P2PStatus status) {
        super(status);
    }
    
}
