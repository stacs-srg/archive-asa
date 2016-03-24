package uk.ac.stand.dcs.asa.jchord.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID;
import uk.ac.stand.dcs.asa.interfaces.IFailureSuspector;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.impl.JChordNextHopResult;

public interface IJChordNode extends IJChordRemote, Runnable {
	
    /**
     * Creates a new ring with one node.
     * Used if no node on an existing ring is known.
     */
	public void createRing(); 
	
	/**
	 * Joins an existing ring containing a specified known node.
	 * 
	 * @param known_node a node on an existing ring
	 */
	public void join(IJChordRemote known_node) throws P2PNodeException;
	
	/**
	 * Stabilises the node by checking its successor's predecessor, p. If p is not this node, and
	 * p's key is less than this node's key, this node will notify it's successor that it is the correct 
	 * predecessor.  If p's key is greater, it will notify p that it is p's predecessor.
	 * If p is null, this node will simply notify its successor.
	 */
	public void stabilise(); 
	    
    /**
     * Check whether the node's predecessor has failed, and drops reference if so.
     */
	public void checkPredecessor(); 

	/**
	 * Sets the successor node in key space.
	 * 
	 * @param s the new successor node
	 * */
    public void setSuccessor(IJChordRemote s); 
    
    /**
	 * Sets the predecessor node in key space.
	 * 
	 * @param p the new predecessor node
	 */
    public void setPredecessor(IJChordRemote p);

    /**
     * Allows node failure to be simulated.
     * 
     * @param failed
     */
    public void setFailed(boolean failed);

    /**
     * Tests whether the node has failed, either in reality due to failure of all its successors,
     * or due to simulation.
     * 
     * @return true if the node has failed
     */
    public boolean hasFailed();
    
    public IFingerTable getFingerTable();
    
    public IFailureSuspector getFailureSuspector();

    /**
     * Decentralised Object Location - map a key to some application component for a specifed application
     */
    public JChordNextHopResult dol(IKey k, AID appID) throws P2PNodeException;
    
    /**
     * JChord Node Lookup - map a key to a JChord node (typed as JChordRemote)
     */
    public IJChordRemote jnl(IKey k) throws P2PNodeException;
    
    public boolean inLocalKeyRange(IKey k) throws P2PNodeException;
}
