/*
 * Created on 10-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationComponent;

/**
 * @author stuart
 */
public class ApplicationRegistryException extends Exception {
	
    private P2PApplicationComponent ac;

    public ApplicationRegistryException(P2PApplicationComponent ac) {
        this(ac,null);
    }
    
    public ApplicationRegistryException(P2PApplicationComponent ac, String message) {
        super(message);
        this.ac=ac;
    }

    /**
     * @return Returns the ac.
     */
    public P2PApplicationComponent getAc() {
        return ac;
    }
}
