/*
 * Created on 25-Oct-2004
 */
package uk.ac.stand.dcs.asa.applications.store;

import uk.ac.stand.dcs.asa.applications.store.interfaces.DataStore;
import uk.ac.stand.dcs.asa.applications.store.interfaces.DataStoreUpcall;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.util.*;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of local storage service. Deals with storing data locally and organising
 * replication of data.
 * 
 * The replication factor is assumed to be less than or equal to the successor list length.
 * Thus each data item is replicated on some or all of the primary storage node's
 * successors.
 * 
 * Since there is currently no mechanism for a failed node to recover, the data is stored
 * in transient memory only.
 * 
 * @author stuart, graham
 */
public class DataStoreImpl implements DataStore, DataStoreUpcall {
    
    /* TODO move code for binding to remote stores into general utility class.
       Add mechanism for associative retrieval of one remote object based on another,
       and for caching these associations. */
    
    /******************** Static Fields *********************/
    
    // TODO general mechanism for exposing and controlling configuration parameters.
    
    public static int REPLICATION_FACTOR = 3;

    public static final int RETRIES = 5;

    public static final int RETRY_DELAY = 2000;
    
    /************************ Fields ************************/
	
	private IJChordNode jchord_node;
	
	private Map primary_map;
	private Map replica_map;
	
	private ActionQueue action_queue;
	
    /********************* Constructors *********************/
    
	/**
	 * Initialises the storage node.
	 * 
	 * @param jchord_node the JChord node co-resident with this storage node
	 */
	public DataStoreImpl(IJChordNode jchord_node) {
	    
		Diagnostic.trace("Initialising Data Storage layer", Diagnostic.INIT);
		
		this.jchord_node = jchord_node;
		this.primary_map = new HashMap();
		this.replica_map = new HashMap();
		
		action_queue = new ActionQueue();
	}

    /*********************** DataStore Methods ************************/

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
	public void store(final IKey k, final IData d) throws InvalidKeyException, StorageException {
	    
	    try {
	        // Get predecessor key to check for validity of k being stored on this node.
	        IKey predecessor_key = jchord_node.getPredecessor().getKey();
	        
	        IKey local_key = jchord_node.getKey();
	        
	        // Check that k is after this node's predecessor's key, but before or equal to this node's key.
	        if (!(local_key.firstCloserInRingThanSecond(predecessor_key, k) || local_key.compareTo(k) == 0))
		        throw new InvalidKeyException("DataStoreImpl::store - key not managed by this node");
		    
	        // Valid key.
		    primary_map.put(k, d);
	    }
	    catch (InvalidKeyException e) {throw e;}   // Just throw the exception again.
	    catch (Exception e) {
	        
	        // Error contacting successor; should not occur with current implementation
	        // since key is cached locally.
	        Error.exceptionError("error obtaining successor's key", e);
	        
	        throw new StorageException("DataStoreImpl::store - could not verify key range");
	    }
	    
	    try {
	        // For each node in the successor list, limited by the replication factor,
	        // queue an action to store a replica of this data.
	        Object[] successor_list = jchord_node.getSuccessorList();
	        
	        for (int i = 0; i < Math.min(successor_list.length, REPLICATION_FACTOR); i++) {
	            
	            final IJChordRemote successor_list_member = (IJChordRemote) successor_list[i];
	            
	            action_queue.enqueue(new Action() {
	                
	                public void performAction() {
	                    
	                    getDataStore(successor_list_member).storeReplica(k, d);
	                }
	            });
	        }
	        
        } catch (Exception e) {
            
            // Error obtaining successor list.
            Error.exceptionError("error obtaining successor list", e);
            
            throw new StorageException("DataStoreImpl::store - could not obtain successor list for replication");
        }
	}
	
	protected DataStore getDataStore(IJChordRemote remote) {

	    try { return (DataStore) RemoteRRTRegistry.getInstance().getService(remote.getHostAddress(), DataStore.class, RETRIES, RETRY_DELAY); }
	    catch (Exception e) {
	        
	        Error.exceptionError("could not get remote data store service", e);
	        return null;
	    }
	}
	
	/**
	 * Looks up a data item with a given key.
	 * 
	 * @param k the key for the data
	 * @return the data item
	 * @throws KeyNotFoundException if k is not found
	 */
	public IData lookup(IKey k) throws KeyNotFoundException {
	    
	    Object o = primary_map.get(k);
	    
	    if (o == null) {
	        throw new KeyNotFoundException("DataStoreImpl::lookup - key " + k + " not found");
	    }
	    else {
	        try {
	            return (IData) o;
	        }
	        catch (ClassCastException e) {
	            Error.hardError("result should be of type Data, actually " + o.getClass().getName());
	            return null; // Not reached.
	        }
	    }
	}
	
	/**
	 * Deletes the data item with a given key, and initiates
	 * the deletion of any replicas.
	 * 
	 * @param k the key for the data
	 * @throws KeyNotFoundException if k is not found
	 */
	public void delete(IKey k) throws KeyNotFoundException {

	    Object o = primary_map.get(k);
	    
	    if (o == null) throw new KeyNotFoundException("DataStoreImpl::delete - key " + k + " not found");
	    else primary_map.remove(k);
	    
	    // TODO initiate replica deletion
//	    for each s in successor list up to distance of replication factor
//        queue action to delete replica of this data
	}
	
	/**
	 * Stores a replica of a data item with a given key.
	 * 
	 * @param k the key for the data
	 * @param d the data to be stored
	 */
	public void storeReplica(IKey k, IData d) {
        // TODO implement method
	}
	
	/**
	 * Deletes the replica data item with a given key.
	 * 
	 * @param k the key for the data
	 * @throws KeyNotFoundException if k is not found
	 */
	public void deleteReplica(IKey k) throws KeyNotFoundException {
        // TODO implement method
	}


    public int numberOfPrimaryCopies() {

        return primary_map.size();
    }


    public int numberOfReplicaCopies() {

        return replica_map.size();
    }

    /******************** DataStoreUpcall Methods *********************/

	/**
	 * Informs the storage service of a change to this node's predecessor.
	 * Either a new node has joined between this node and its old predecessor, or
	 * this node's old predecessor has failed.
	 * 
	 * <pre>
	 * Terminology:
	 *
	 *     N - local node with key KN
	 *     OP - old predecessor node with key KOP
	 *     NP - new predecessor node with key KNP
	 * 
	 * N is responsible for storing all data items with key KD where
	 * KD in (KOP, KN].
	 * 
	 * We attempt to preserve the following constraints, or at least maximise the
	 * time for which they are true:
	 * 
	 * A) A primary copy of a data item D with key KD is held at KD's successor node.
	 * B) Replicas of D are held on some number (the node's replication factor)
	 *    of immediate successors of KD's successor node.
	 * 
	 * -----------------
	 * 
	 * Case 1:
	 * 
	 * If KNP > KOP then a new node has joined between N and its old predecessor.
	 * The new predecessor takes over as primary node for some data items previously
	 * stored at N.
	 * 
	 * N becomes the first replica node for those data items, and the replicas can
	 * be deleted from the last of N's replica nodes.
	 * 
	 * Algorithm:
	 * 
	 * For each data item D with key KD <= KNP stored in the primary map at N -
	 * 
	 *   a) send D to be stored at its new primary node NP
	 *   b) move KD and D from the primary map to the replica map on N
	 *   c) ask the last of N's replica nodes to delete its replica of D
	 * 
	 * -----------------
	 * 
	 * Case 2:
	 * 
	 * If KNP < KOP then N's old predecessor has failed. N takes over as primary
	 * node for some data items previously stored at its old predecessor.
	 * 
	 * New replicas for those data items must be created on the last of N's
	 * replica nodes.
	 * 
	 * Algorithm:
	 * 
	 * For each data item D with key KD such that KNP < KD <= KOP stored in the
	 * replica map at N -
	 * 
	 *   a) move KD and D from the replica map to the primary map on N
	 *   b) ask the last of N's replica nodes to add a replica of D
	 * </pre>
	 * 
	 * @param new_predecessor_key the key of the node's new predecessor
	 */
	public void predecessorChange(IKey new_predecessor_key) {
	    
        // TODO implement method
	}

	/**
	 * Informs the storage service of a new entry in this node's successor list.
	 * 
	 * <pre>
	 * Either a new node has joined somewhere between this node and the previously
	 * last entry in this node's successor list, or a node previously in this
	 * node's successor list has failed and another node has been added to
	 * this node's successor list in its place.
	 * 
	 * Assumption: this node's successor list has already been updated before this
	 *             call.
	 * 
	 * Algorithm:
	 * 
	 *   a) determine the index of the new node in this node's successor list
	 *      (already updated)
	 *   b) if the index is less than the replication factor for this node then
	 *        i) for every data item in this node's primary map, ask the new node
	 *           to add a replica of the data item
	 * 
	 * </pre>
	 * 
	 * @param new_successor_node the new successor node
	 */
	public void successorListAddition(IJChordRemote new_successor_node) {
	    
	    // TODO implement method
	}
	
	/**
	 * Informs the storage service of the removal of an entry from this node's
	 * successor list.
	 * 
	 * <pre>
	 * Either a new node has joined somewhere between this node and the previously
	 * last entry in this node's successor list, with the result that the given
	 * node is removed from the end of this node's successor list, or the given
	 * node has failed. In either case the given node is no longer a replica node
	 * for this node.
	 * 
	 * In the former case the node may be holding replicas for this node, which
	 * can now be removed. In the latter case no action is necessary: if the
	 * failed node was holding replicas for this node then new replicas will
	 * be created when successorListAddition is called as a result of successor
	 * list repair.
	 * 
	 * Algorithm:
	 * 
	 *   a) if the replication factor is equal to the length of the successor
	 *      list, then
	 *        i) for every data item in this node's primary map, ask the given
	 *           node to remove its replica of the data item
	 * 
	 * </pre>
	 * 
	 * @param old_successor_node the old successor node
	 */
	public void successorListRemoval(IJChordRemote old_successor_node) {
	    
	    // TODO implement method
	}
	
    /******************** Utility Methods *********************/

	public String toString() {
	    
	    String s = "";
        try {
            s = "DataStore on node " + jchord_node.getKey() + "\n";
        } catch (Exception e) {
            Error.exceptionError("JChordNode.getKey threw exception", e);
        }
        s += "\nPrimary Map:\n";
	    s += mapToString(primary_map);
	    
	    s += "\nReplica Map:\n";
	    s += mapToString(replica_map);
	    
        return s;
	}
	
	private static String mapToString(Map m) {
	    
	    String s = "";
	    
	    Iterator key_iterator = m.keySet().iterator();
	    
	    while (key_iterator.hasNext()) {
	        Object k = key_iterator.next();
	        s += k.toString() + " -> " + m.get(k).toString() + "\n";
	    }
	    
	    return s;
	}
}
