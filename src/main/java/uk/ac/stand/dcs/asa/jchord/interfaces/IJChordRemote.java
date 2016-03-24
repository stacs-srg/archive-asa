package uk.ac.stand.dcs.asa.jchord.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.Message;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.jchord.impl.JChordNextHopResult;

public interface IJChordRemote extends IP2PNode { 

	/**
	 * Returns the successor node, in key space, of a given key
	 * 
	 * @param k the key for which we want the successor.
	 * @return The successor node of k, or null if an error occurs
	 *         while looking for the successor.
	 */
	public IJChordRemote findSuccessor( IKey k ) throws Exception; 
	
    /**
     * [WebService] Notifies this node that a given node may be its predecessor.
     * 
     * @param n
     *            a node that thinks it is this node's most suitable predecessor
     */
	public void notify( IJChordRemote n ) throws Exception;
    
    /**
     * [WebService] Returns this node's successor list.
     * 
     * @return this node's successor list
     */
	public Object[] getSuccessorList() throws Exception;

	/**
	 * [WebService]
	 * @return this node's predecessor node in the key space
	 */
	public IJChordRemote getPredecessor() throws Exception;

	/**
	 * [WebService]
	 * @return this node's successor node in the key space
	 */
	public IJChordRemote getSuccessor() throws Exception;
	
	/**
	 * Used to check the availability of this node
	 */
	public void isAlive() throws Exception;

	/**
     * Returns the next hop towards the successor node of a given key in
     * keyspace.
     * 
     * @param k the key of a node
     * @return the closest successor of the node with the specified key, or null
     *         if the closest successor is the node that called the method
     */
    public JChordNextHopResult next_hop(IKey k) throws P2PNodeException;
    
    public JChordNextHopResult dol_next_hop(IKey k, AID appID) throws P2PNodeException;
   
    public JChordNextHopResult dol_resolve(IKey k, AID appID) throws P2PNodeException;
 
    public JChordNextHopResult send_next_hop(IKey k, AID appID, Message m) throws P2PNodeException;
}