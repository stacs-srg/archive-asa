/*
 * Created on May 20, 2005 at 12:07:39 PM.
 */
package uk.ac.stand.dcs.asa.storage.persistence.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IGUID;

/**
 * An object with a GUID that is fixed for all time.
 *
 * @author al
 */
public interface IGUIDObject {
	
    /**
     * Gets the GUID of the object.
     * 
     * @return the GUID of the object
     */
    IGUID getGUID();
}
