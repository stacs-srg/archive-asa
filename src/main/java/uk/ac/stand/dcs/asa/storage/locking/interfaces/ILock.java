/*
 * Created on Aug 11, 2005 at 1:37:20 PM.
 */
package uk.ac.stand.dcs.asa.storage.locking.interfaces;

import uk.ac.stand.dcs.asa.storage.exceptions.LockException;

import java.net.URI;
import java.util.Iterator;


/**
 * Abstract lock interface.
 *
 * @author al, graham
 */
public interface ILock {
	
    void addResource(IResourceLockInfo resource_lock_info) throws LockException;

    void removeResource(URI uri, String lock_token) throws LockException;

    /**
     * Returns the owner of the lock.
     * 
     * @return the owner of the lock
     */
    String getOwner();
    
    String getTokenPrefix();

    Iterator resourceIterator();   // Over IResourceLockInfo.
}
