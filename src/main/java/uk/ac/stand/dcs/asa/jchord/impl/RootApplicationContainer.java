/*
 * Created on 23-Nov-2004 at 17:05:58.
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordApplicationContainer;

/**
 * @author al
 */
public class RootApplicationContainer implements IJChordApplicationContainer {

    public RootApplicationContainer() {
        super();
    }

    /**
     * @see uk.ac.stand.dcs.asa.jchord.interfaces.IJChordApplicationContainer#hasData()
     */
    public boolean hasData() {

        return false;
    }

    /**
     * @see uk.ac.stand.dcs.asa.jchord.interfaces.IJChordApplicationContainer#getData()
     */
    public Object getData() {

        return null;
    }
}