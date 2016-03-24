/*
 * Created on 25-Oct-2004
 */
package uk.ac.stand.dcs.asa.applications.store.interfaces;

import uk.ac.stand.dcs.asa.applications.store.StorageException;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.util.InvalidKeyException;
import uk.ac.stand.dcs.asa.util.KeyNotFoundException;

/**
 * Interface defining local storage service.
 * 
 * @author stuart, graham
 */
public interface DataStore {
    
	/**
	 * Stores a data item with a given key, and initiates
	 * the propagation of appropriate replicas. If the key is already present the existing
	 * data is overwritten.
	 * 
	 * @param k the key for the data
	 * @param d the data to be stored
	 * @throws InvalidKeyException if k is not managed by this node
	 * @throws StorageException if the data cannot be stored
	 */
	public void store(IKey k, IData d) throws InvalidKeyException, StorageException;
	
	/**
	 * Looks up a data item with a given key.
	 * 
	 * @param k the key for the data
	 * @return the data item
	 * @throws KeyNotFoundException if k is not found
	 */
	public IData lookup(IKey k) throws KeyNotFoundException;
	
	/**
	 * Deletes the data item with a given key, and initiates
	 * the deletion of any replicas.
	 * 
	 * @param k the key for the data
	 * @throws KeyNotFoundException if k is not found
	 */
	public void delete(IKey k) throws KeyNotFoundException;
	
	/**
	 * Stores a replica of a data item with a given key.
	 * 
	 * @param k the key for the data
	 * @param d the data to be stored
	 */
	public void storeReplica(IKey k, IData d);
	
	/**
	 * Deletes the replica data item with a given key.
	 * 
	 * @param k the key for the data
	 * @throws KeyNotFoundException if k is not found
	 */
	public void deleteReplica(IKey k) throws KeyNotFoundException;
	
	public int numberOfPrimaryCopies();
	
	public int numberOfReplicaCopies();
}
