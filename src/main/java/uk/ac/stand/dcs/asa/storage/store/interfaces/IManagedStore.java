/*
 * Created on 08-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IPID;

/**
 * Version of IStore that allows PIDs to be removed.
 *
 * @author stuart, graham
 */
public interface IManagedStore extends IStore {
	
    /**
     * Removes the given PID and associated data from the store.
     * 
     * @param pid the PID to be removed
     */
    void removePID(IPID pid);
}
