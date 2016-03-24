/*
 * Created on 23-Nov-2004 at 17:05:58.
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordApplicationContainer;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class IntermediateApplicationContainer implements IJChordApplicationContainer {

    /**
     * 
     */
    public IntermediateApplicationContainer() {
        super();
    }

    public boolean hasData() {
        return false;
    }

    public Object getData() {
        return null;
    }
}
