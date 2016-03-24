package uk.ac.stand.dcs.asa.plaxton.impl;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonNode;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonRemote;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicyFactory;
import uk.ac.stand.dcs.asa.simulation.SimulatedFailureException;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;
import uk.ac.stand.dcs.asa.util.RadixMethods;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Observable;



/**
 * Implementation of Plaxton node.
 *
 * @author al
 */
public class PlaxtonNodeImpl extends Observable implements Comparable, PlaxtonNode {
    
    /******************** Static Fields *********************/

    private static final int DEFAULT_DISTANCE_RL = 5;
    /**
     * Default wait duration between periodic maintenance operations.
     */
    public static final int DEFAULT_WAIT_PERIOD = 1000;
    
    // TODO Obsolete?
    // private static int DEFAULT_BITS_PER_DIGIT = 4;
    
    private static int DEFAULT_RADIX = 16;

    /******************** Public Fields *********************/
	
    public InetSocketAddress hostAddress;

    /******************** Private Fields ********************/

    private IKey key;
    
    // TODO Obsolete?
    // private EventBus bus;

    // TODO Obsolete?
    // private int wait_period;

    private boolean failed = false;

    private RoutingTable routes;
    
    private LeafSet leaves;
    
    private NeighbourhoodSet neighbours;

    // Obsolete?
    // private IDistanceCalculator dc;

    private int radix;
    
    /********************* Constructors *********************/
    
    /**
     * The constructor for a PlaxtonNodeImpl to be used for the first node in a system
     */
    public PlaxtonNodeImpl(InetSocketAddress hostAddress, IKey key, IDistanceCalculator dc, ReplacementPolicyFactory rpf, int radix ) {
        init(hostAddress, key, dc, rpf, radix);
    }
    
    public PlaxtonNodeImpl(InetSocketAddress hostAddress, IKey key, IDistanceCalculator dc, ReplacementPolicyFactory rpf ) {
        init(hostAddress, key, dc, rpf, DEFAULT_RADIX);
    }   
    
    /**
     * Default constructor used in deserialisation.
     */
    public PlaxtonNodeImpl() {/* Needed for RRT */}
    
    /********************* Node initialisation *********************/

	/**
	 * This method initialises a PlaxtonNodeImpl
	 */
	private void init(InetSocketAddress node_rep, IKey key, IDistanceCalculator dc, ReplacementPolicyFactory rpf, int radix) {
	    
	    Diagnostic.trace("PlaxtonNodeImpl " + hostAddress.getAddress().getHostAddress() + " initialising key = " + getKey(), Diagnostic.INIT);
	    initialiseLocals(node_rep, key, dc, rpf, radix );
	    try {
            Diagnostic.trace("PlaxtonNodeImpl " + hostAddress.getAddress().getHostAddress() + " initialised key = " + getKey(), Diagnostic.INIT);
        } catch (Exception e) {
            Error.exceptionError("error getting node representation", e);
        }
	}

	private void initialiseLocals(InetSocketAddress hostAddress, IKey key, IDistanceCalculator dc, ReplacementPolicyFactory rpf, int radix ) {

        this.key = key;
        this.hostAddress = hostAddress;
        // this.dc = dc;      obsolete?
        this.radix = radix;
        // this.wait_period = DEFAULT_WAIT_PERIOD;    obsolete?
        routes = new RoutingTable(getKey(),rpf.makeReplacementPolicy(this), radix );
        leaves = new LeafSet(this,DEFAULT_DISTANCE_RL);	// use a leaf set with 5 entries bigger and 5 entries less than this node
        neighbours = new NeighbourhoodSet(this,10,dc);
    }
	
	/**
	 * @param knownNode should be a node in physical proximity to this new node
	 */
	public void join( PlaxtonRemote knownNode) {
	    try {
            joinNetwork( knownNode );
        } catch (Exception e) {
            Error.exceptionError("Error joining node", e );
        }
	    // Use the knownNode which is supposed to be close to initialise our neighbourhood set
	    neighbours.addNeighbours(knownNode.getNeighbours());
        //Use the known node to initialise routing table
	    routes.suggestRouteRow( 0, knownNode );
		// Notify nodes of new nodes existence
        publiciseMe();
	}

    private void joinNetwork( PlaxtonRemote knownNode ) throws Exception {        
        // Try and find current owner of the new node's key
        // At each stage grab the routing information from the node.
        
	    Diagnostic.trace(hostAddress.getAddress().getHostAddress() + " joining node = " + knownNode , Diagnostic.INIT);

	    PlaxtonRemote next = knownNode;
		boolean not_found = true;
		int hop = 0;
        
		this.getStateFrom( knownNode,hop  );
		
		while (not_found) {

			NextHopResult result = next.nextHop(getKey());
			
			switch (result.getCode()) {
			
				case NextHopResult.NEXT_HOP: {
					next = result.getResult();
			    	Diagnostic.trace("next hop: " + next.getHostAddress().getAddress().getHostAddress(), Diagnostic.INIT);	
			    	this.getStateFrom( next, hop++ );
					break;
				}
				case NextHopResult.FINAL: {
					not_found = false;
					next = result.getResult();
			    	Diagnostic.trace("final hop: " + next.getHostAddress().getAddress().getHostAddress(), Diagnostic.INIT);	
			    	// use the current owner of this nodes key to initialise this nodes data structures
			    	this.getStateFrom( next, hop++ );
					break;
				}
				case NextHopResult.ERROR: {
				    Error.error("nextHop call returned error: " + result.getError().get("msg"));
					throw new Exception("PlaxtonRemote::join - nextHop call returned error: \"" + result.getError().get("msg") + "\"");
				}
				default: {
					Error.hardErrorNoEvent("nextHop call returned NextHopResult with unrecognised code");
				}
			}
		}
		
		// Check for extra information
    }
    
    public void getStateFrom( PlaxtonRemote pni, int hop ) {
		leaves.addleaves(pni.getLeaves());
		leaves.addLeaf(pni);	// TODO  - is this right Al added this
		routes.suggestRouteRow( hop, pni );
		routes.addNode(pni);	// TODO  - is this right Al added this
		//neighbours.addNeighbour(next); // TODO  - is this right Al added this
    }
    
    /**
     *  Notify other nodes of this nodes existence using the entries in the route table, leaves and neighbours
     */
    private void publiciseMe() {
        publiciseMe( routes.iterator() );
        publiciseMe( neighbours.iterator() );
        publiciseMe( leaves.iterator() );  
    }
    
    /**
     * @param iter an iterator over PlaxtonRemote to which we are going to publicise the existence of this
     */
    private void publiciseMe( Iterator iter ) {
        while( iter.hasNext() ) {
            Object inext = iter.next();
            if( ! ( inext instanceof PlaxtonRemote  ) ) {
                Error.hardError( "Encountered an unexpected object" );
            }
            PlaxtonRemote next = (PlaxtonRemote) inext;
            next.receiveNewNodePublicity( this );     
        }       
    }
   
    
    /**
     * @param newNode is a newly added PlaxtonRemote which is adverising their existence to nodes
     * in the route table, leaves and neighbours (using @see publiciseMe )
     */
    public void receiveNewNodePublicity( PlaxtonRemote newNode ) {
        routes.addNode(newNode);
        neighbours.addNeighbour(newNode);
        leaves.addLeaf(newNode);        
    }
    /*********************** Public Methods ************************/
	
    // Obsolete?
//    private void generateEvent(Event e) {
//        
//        if (bus != null) bus.publishEvent(e);
//    }

    public NextHopResult nextHop(IKey k) {
//    	Diagnostic.trace("PlaxtonNodeImpl::nextHop", "*** callee = " + ipAddress + " search key = " + k, Diagnostic.RUN);	

    	// First see if node is in the cover of the leaf set
        PlaxtonRemote next = leaves.covered( k );
        if( next != null ) { // the key is in the leaf set
//        	Diagnostic.trace("PlaxtonNodeImpl::nextHop", "found node in leaf set:" + next , Diagnostic.RUN);	
            return new NextHopResult( NextHopResult.FINAL,next );
        }
//    	Diagnostic.trace("PlaxtonNodeImpl::nextHop", "Node not in leaf set - searching routes", Diagnostic.RUN);	
        // Not in leaf set - try to find a closer node in the routing table
//    	Diagnostic.trace("PlaxtonNodeImpl::nextHop comparing", "\n\t\t"+ key + "with\n\t\t" + k , Diagnostic.RUN);	
        int firstDiffIndex = k.baseXPrefixMatch( getKey(), radix ); // find the shared key length between our key and search key
//        Diagnostic.trace("PlaxtonNodeImpl::nextHop", "first different index = " + firstDiffIndex , Diagnostic.RUN);
        char firstDifferentChar = k.charAtIndexBaseX(firstDiffIndex,radix);
        int charIndex = RadixMethods.baseXCharToInt( firstDifferentChar, radix );  
//        Diagnostic.trace("PlaxtonNodeImpl::nextHop", "first different char = " + firstDifferentChar + " = " + firstDifferentChar + "[DEC]", Diagnostic.RUN);
        next = routes.lookupIndex( firstDiffIndex,charIndex );
        if( next != null ) { 
//            Diagnostic.trace("PlaxtonNodeImpl::nextHop", "found node in routes", Diagnostic.RUN);	
            return new NextHopResult( NextHopResult.NEXT_HOP,next ); //************** This next hop might be the final one but we cannot tell from here
        }
//    	Diagnostic.trace("PlaxtonNodeImpl::nextHop", "Node not routes - searching numerically close nodes", Diagnostic.RUN);        
        // Not in leaf set or in routing table
        // rare case - try and find a numerically closer node from leaf set
        next = leaves.findCloserNode(k);
        if( next != null ) {
            if( next == this ) {
                return new NextHopResult( NextHopResult.FINAL,next );
            } else {
                return new NextHopResult( NextHopResult.NEXT_HOP,next );
            }
        }
        // If we get here, the node is not in the nodes covered by leaf set or the route table and
        // no closer nodes and not handled by this node.
        Error.error(" Didn't find target for key" + k );
        return new NextHopResult( NextHopResult.ERROR, null );
    }
    
	public void run() {
	    
		Diagnostic.trace("Plaxton is running", Diagnostic.RUN);
		
		while (!failed) {
			// Housekeeping processes?
		}
		
		Diagnostic.trace("node failed: " + getKey(), Diagnostic.RUN);
	}
	
    /* 
     * Informs the node of its near (or otherwise) neighbours 
     */
    public void addNeighbours(PlaxtonRemote[] nearbynodes ) {
        for( int i = 0; i < nearbynodes.length; i++ ) {
            neighbours.addNeighbour(nearbynodes[i]);
        }
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        if( arg0 instanceof PlaxtonRemote) {
            try {
                return getKey().compareTo( ((PlaxtonRemote) arg0).getKey() );
            } catch (Exception e) {
                Error.exceptionError("cannot obtain remote key",e);
                return 1; // Clearly anything is biggesr than something that doesn't exist - Descartes;
            }
        }
        Error.hardError( "attempt to compare class that is not a PlaxtonRemote" );
        return 0; // not called
    }
    
    /*********************** Utility Methods ***********************/
    
    public String toString() {
        
        return FormatHostInfo.formatHostName(hostAddress);
    }
	
    /*********************** Predicates ***********************/
    
    /**
     * Tests whether the node has failed, either in reality due to failure of all its successors,
     * or due to simulation.
     * 
     * @return true if the node has failed
     */
    public boolean hasFailed() {
        
        return failed;
    }
    
    public void isAlive() throws Exception {
        
        if (failed) throw new SimulatedFailureException("PlaxtonImpl::isAlive - simulated failure");
    }
    
    /*********************** Setters ***********************/
    
    /**
     * Allows node failure to be simulated.
     * 
     * @param failed
     * 
     * @uml.property name="failed"
     */
    public void setFailed(boolean failed) {

        this.failed = failed;
    }
    
    /*********************** Selectors ***********************/
    
    /**
     * @return Returns the leaves.
     */
    public LeafSet getLeaves() {
        return leaves;
    }
    /**
     * @return Returns the neighbours.
     */
    public NeighbourhoodSet getNeighbours() {
        return neighbours;
    }
    /**
     * @return Returns the routes.
     */
    public RoutingTable getRoutes() {
        return routes;
    }

	/* (non-Javadoc)
	 * @see uk.ac.stand.dcs.asa.interfaces.P2PNode#routingStateSize()
	 */
	public int routingStateSize() {
		// TODO Auto-generated method stub
		return 0;
	}

    /* (non-Javadoc)
     * @see uk.ac.stand.dcs.asa.interfaces.P2PNode#getKey()
     */
    public IKey getKey() {
        return key;
    }

    /* (non-Javadoc)
     * @see uk.ac.stand.dcs.asa.interfaces.P2PNode#getHostAddress()
     */
    public InetSocketAddress getHostAddress() throws Exception {
        return hostAddress;
    }

    public P2PStatus getStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see uk.ac.stand.dcs.asa.interfaces.IP2PNode#lookup(uk.ac.stand.dcs.asa.interfaces.IKey)
     */
    public IP2PNode lookup(IKey k) throws P2PNodeException {
        // TODO Auto-generated method stub
        return null;
    }
   
}
