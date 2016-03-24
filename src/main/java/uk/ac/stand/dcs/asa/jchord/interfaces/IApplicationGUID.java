/*
 * Created on 23-Nov-2004 at 16:37:31.
 */
package uk.ac.stand.dcs.asa.jchord.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IKey;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class IApplicationGUID implements Comparable {

    IKey key;
    
    /**
     * @param key
     */
    public IApplicationGUID(IKey key) {
        this.key = key;
    }
    
    /**
     * @return Returns the key.
     */
    public IKey getKey() {
        return key;
    }
    
    public int compareTo(Object arg0) throws ClassCastException {
            return this.key.compareTo( ((IApplicationGUID)arg0).getKey() );
    }
}
