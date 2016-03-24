/*
 * Created on Dec 9, 2004 at 11:53:22 AM.
 */
package uk.ac.stand.dcs.asa.jchord.interfaces;


import uk.ac.stand.dcs.asa.interfaces.IKey;

import java.util.Iterator;

/**
 * Interface defining finger tables.
 * 
 * @author al, stuart, graham
 */
public interface IFingerTable {
	
    /**
     * Accepts a suggestion of a node that could be added to the finger table
     * NB This is soft state primarily used to accelerate the initiation of systems.
     * No position is taken on what the finger table implementations do with this information.
     * This may also be used to incorporate nodes that are encountered opportunistically during run.
     * @param extantNode the node that could be added to the finger table
     */
    public abstract void exists(IJChordRemote extantNode);

    /**
     * Accepts a suggestion of a node that could be removed from the finger table
     * This may also be used to improve performance by suggesting that nodes (perhaps known to be dead) be removed.
     * @param deadNode the node that could be removed from the finger table
     */
    public abstract void dead(IJChordRemote deadNode);

    public abstract void owned(IKey k, IJChordRemote ownerNode);
    
    /**
     * @return a string representing the contents of the finger table
     */
    public abstract String toString();

    /**
     * @return a string representing the contents of the finger table omitting duplicate keys
     */
    public abstract String toString_compact();
    
    /**
     * @return the size of the finger table
     */
    public abstract int size();
    
    /**
     * Returns an iterator over the finger table entries; each element implements JChordRemote.
     * 
     * @return an iterator over the finger table entries
     */
    public abstract Iterator iterator();

    /**
     * Returns a reverse iterator over the finger table entries; each element implements JChordRemote.
     * 
     * @return a reverse iterator over the finger table entries
     */
    public abstract Iterator reverseIterator();

    /**
     * Checks the finger table for a node with a key that has the closest
     * value less than the specified key. If this node's key is the most suitable, it is returned.
     * 
     * @param k the key of a node
     * @return the node with the closest preceding key to k
     */
    public abstract IJChordRemote closestPrecedingNode(IKey k);

    /**
     * Fixes all the entries in the finger table.
     */
    public abstract void fixAllFingers();
    
    /**
     * Fixes the next entry in the finger table.
     */
    public abstract void fixNextFinger();

	/**
	 * @return the number of entries in the finger table
	 */
	public abstract int getNumberOfEntries();
}