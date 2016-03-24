/*
 * Created on 25-Oct-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl;

import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IGUIDStore;

import java.util.Iterator;

/**
 * A wrapper class for IGUidStore implmentations. When an IGUIDStore interface
 * method call is made on the wrapper, the corresponding call is made on the the
 * wrapped IGUIDStore implmentation. If the call on the wrapped IGUIDStore
 * throws an exception the wrapper will wait for a specified time, defined by
 * the 'delay' vlaue, and then make the method call again. This process is
 * repeated a number of times as define by the 'retries' value. If the call
 * cannot be made with out throwing an exception then the exception is thrown to
 * the wrapper's caller.
 * 
 * @author stuart
 */

public class ErrorHandlingGUIDStore implements IGUIDStore {

    private static final int retries = 10; //number of times to retry operation before throwing exception
    private static final int delay = 5000; //ms delay between retries
    
    private IGUIDStore store;
    
    public ErrorHandlingGUIDStore(IGUIDStore store) {
        this.store=store;
    }

    private void retryDelay(){
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e){/* No action */}
    }
    
    public IData get(IPID pid) throws StoreGetException {
		int attempts = 0;
		while (attempts++ < retries) {
			try {
				return store.get(pid);
			} catch (StoreGetException e) {
				if (attempts == retries) {
					throw e;
				} else {
					retryDelay();
				}
			}
		}
		return null;
	}

    public IPID put(IData data) throws StorePutException {
		int attempts = 0;
		while (attempts++ < retries) {
			try {
				return store.put(data);
			} catch (StorePutException e) {
				if (attempts == retries) {
					throw e;
				} else {
					retryDelay();
				}
			}
		}
		return null;
	}

    public long getPIDPutDate(IPID pid) throws StoreGetException {
		int attempts = 0;
		while (attempts++ < retries) {
			try {
				return store.getPIDPutDate(pid);
			} catch (StoreGetException e) {
				if (attempts == retries) {
					throw e;
				} else {
					retryDelay();
				}
			}
		}
		return -1;

	}

    public IPID getLatestPID(IGUID guid) throws StoreGetException {
		int attempts = 0;
		while (attempts++ < retries) {
			try {
				return store.getLatestPID(guid);
			} catch (StoreGetException e) {
				if (attempts == retries) {
					throw e;
				} else {
					retryDelay();
				}
			}
		}
		return null;
	}

    public Iterator getAllPIDs(IGUID guid) throws StoreGetException {
		int attempts = 0;
		while (attempts++ < retries) {
			try {
				return store.getAllPIDs(guid);
			} catch (StoreGetException e) {
				if (attempts == retries) {
					throw e;
				} else {
					retryDelay();
				}
			}
		}
		return null;
	}

    public void put(IGUID guid, IPID pid) throws StorePutException {
		int attempts = 0;
		while (attempts++ < retries) {
			try {
				store.put(guid, pid);
				return;
			} catch (StorePutException e) {
				if (attempts == retries) {
					throw e;
				} else {
					retryDelay();
				}
			}
		}
	}

    public long getGUIDPutDate(IGUID guid) throws StoreGetException {
		int attempts = 0;
		while (attempts++ < retries) {
			try {
				return store.getGUIDPutDate(guid);
			} catch (StoreGetException e) {
				if (attempts == retries) {
					throw e;
				} else {
					retryDelay();
				}
			}
		}
		return -1;
	}
}
