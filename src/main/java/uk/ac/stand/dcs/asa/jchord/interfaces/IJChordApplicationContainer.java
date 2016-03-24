/*
 * Created on 23-Nov-2004 at 17:03:02.
 */
package uk.ac.stand.dcs.asa.jchord.interfaces;

import uk.ac.stand.dcs.asa.util.DataUnavailableException;


/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface IJChordApplicationContainer {
    boolean hasData();
    Object getData() throws DataUnavailableException ; 
}
