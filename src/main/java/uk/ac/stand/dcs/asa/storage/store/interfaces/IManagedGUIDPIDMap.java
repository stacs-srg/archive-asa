/*
 * Created on 09-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;

/**
 * Version of GUIDPIDMap that allows GUIDs and PIDs to be removed.
 *
 * @author stuart, graham
 */
public interface IManagedGUIDPIDMap extends IGUIDPIDMap {
	
    /**
     * Removes the given GUID from the map.
     * 
     * @param guid the GUID to be removed
     */
    void removeGUID(IGUID guid);
    
    /**
     * Removes the given version from the map.
     * 
     * @param guid the GUID to be removed
     * @param versionPID the PID to be removed
     */
    void removeVersion(IGUID guid, IPID versionPID);
}
