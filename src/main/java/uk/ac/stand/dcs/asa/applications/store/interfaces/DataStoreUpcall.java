/*
 * Created on 25-Oct-2004
 */
package uk.ac.stand.dcs.asa.applications.store.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;

/**
 * Interface defining upcalls to local storage service.
 * 
 * @author stuart, graham
 */
public interface DataStoreUpcall {
    
	/**
	 * Informs the storage service of a change to this node's predecessor.
	 * Either a new node has joined between this node and its old predecessor, or
	 * this node's old predecessor has failed.
	 * 
	 * @param new_predecessor_key the key of the node's new predecessor
	 */
	public void predecessorChange(IKey new_predecessor_key);
	
	/**
	 * Informs the storage service of a new entry in this node's successor list.
	 * 
	 * Either a new node has joined somewhere between this node and the previously
	 * last entry in this node's successor list, or a node previously in this
	 * node's successor list has failed and another node has been added to
	 * this node's successor list in its place.
	 * 
	 * @param new_successor_node the new successor node
	 */
	public void successorListAddition(IJChordRemote new_successor_node);
	
	/**
	 * Informs the storage service of the removal of an entry from this node's
	 * successor list.
	 * 
	 * Either a new node has joined somewhere between this node and the previously
	 * last entry in this node's successor list, with the result that the given
	 * node is removed from the end of this node's successor list, or the given
	 * node has failed.
	 * 
	 * @param old_successor_node the old successor node
	 */
	public void successorListRemoval(IJChordRemote old_successor_node);
}
