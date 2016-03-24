/*
 * Created on 23-Nov-2004 at 17:05:58.
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordApplicationContainer;
import uk.ac.stand.dcs.asa.util.DataUnavailableException;

/**
 * @author al
 */
public class ErrorApplicationContainer implements IJChordApplicationContainer {

    private String error = null;

    public ErrorApplicationContainer( String error ) {
        this.error = error;
    }

    public boolean hasData() {
        return false;
    }

    public Object getData() throws DataUnavailableException {
        throw new DataUnavailableException();
    }

    /**
     * @return Returns the error.
     */
    public String getError() {
        return error;
    }
}
