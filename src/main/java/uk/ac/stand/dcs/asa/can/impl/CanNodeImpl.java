package uk.ac.stand.dcs.asa.can.impl;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.can.interfaces.CanNode;
import uk.ac.stand.dcs.asa.can.interfaces.CanRemote;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.SimulatedFailureException;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;

import java.net.InetSocketAddress;
import java.util.Observable;


/**
 * Implementation of Can node.
 * 
 * @author al
 */
public class CanNodeImpl extends Observable implements Comparable, CanNode {

    /** ****************** Static Fields ******************** */

    /** ****************** Public Fields ******************** */

    public InetSocketAddress hostAddress;

    /** ****************** Private Fields ******************* */

    private boolean failed = false;

    private int dimensions; // the number of dimensions - e.g. for 3 we will have a 3D network addressed as (x,y,z)
    
    private int bitsPerDimension;   // the maximum size of any dimension e.g if set to 8 with 2 dimensions we will 
    								// yield a space going from (0,0) to (255,255).

    private HyperCube myCube;
    
    private IKey initial_key; // only used dring initialisation - once running we use key from center of hypercube.
    
    private boolean node_in_network = false; // debugging aid to check if node is initialise properly

    private String hostAddressString;
    
    /** ******************* Constructors ******************** */

    /**
     * The constructor for a CanNodeImpl
     */
    public CanNodeImpl(InetSocketAddress hostAddress, IKey key, int dimensions, int bitsPerDimension) {
        init(hostAddress, key, dimensions,bitsPerDimension);
    }

    /**
     * Default constructor used in deserialisation.
     */
    public CanNodeImpl() {
    	
    	// Deliberately empty.
    }

    /** ******************* Node initialisation ******************** */

    private void init(InetSocketAddress hostAddress, IKey key, int dimensions,int bitsPerDimension) {
        this.initial_key = key;
        this.hostAddress = hostAddress;
        this.hostAddressString = FormatHostInfo.formatHostName(hostAddress);
        this.dimensions = dimensions;
        this.bitsPerDimension = bitsPerDimension;
        this.myCube = null;
    }

    /** ********************* Public Methods *********************** */

    public void run() {
        Diagnostic.trace("Can is running", Diagnostic.RUN);
        while (!failed) {
        	// Busy wait.
        }

        Diagnostic.trace("node failed: " + getKey(),Diagnostic.RUN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        if (arg0 instanceof CanRemote) {
            try {
                return getKey().compareTo(((CanRemote) arg0).getKey());
            } catch (Exception e) {
                Error.exceptionError("cannot obtain remote key", e);
                return 1; // Clearly anything is bigger than something that doesn't exist - Descartes;
            }
        }
        Error.hardError("attempt to compare class that is not a CanRemote");
        return 0; // not called
    }

    
    /**
     * Creates a new network for a node to exist on. 
     * In CAN this amounts to taking control of the whole space.
     */
    public void createNetwork() {
        HyperCube hc = new HyperCube(this,dimensions,bitsPerDimension);
        hc.takeAll();
        myCube = hc;
        node_in_network = true;
    }
    
    /**
     * Uses a known node on the ring to discover the appropriate successor for
     * this node, and sets it as the successor.
     * 
     * @param known_node
     *            a node that is already part of the ring
     */
    public void join(CanRemote known_node) {
        if (known_node == null)
            Error.error("known node was null");
        else {
            CanRemote next = known_node;

            while (true) {
                NextHopResult result = next.nextHop(getKey());

                switch (result.getCode()) {
                	case NextHopResult.FINAL: {
                	    CanRemote owner = result.getResult();
                	    HyperCube hc = owner.requestSplit(this);
                	    myCube = hc;
                	    return;
                	}
                	case NextHopResult.NEXT_HOP: {
                	    next = result.getResult();
                	    break;
                	}
                	case NextHopResult.ERROR: {
                	    Error.errorNoEvent("nextHop call returned Error");
                	    break;
                	}
                	default: {
                	    Error.hardErrorNoEvent("nextHop call returned NextHopResult with unrecognised code");
                	}
                }
            }

        }
        node_in_network = true;
    }

    /* 
     * This method splits the region held by this node with the Can node denoted by other
     * We do this by cycling through the dimensions in turn.
     */
    public HyperCube requestSplit(CanRemote other) {
        HyperCube newRegion = myCube.requestSplit( other );
        
        if( newRegion == null ) {
            Error.error("Failed to split region");
        }
        return newRegion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.stand.dcs.asa.can.interfaces.CanRemote#nextHop(uk.ac.stand.dcs.asa.interfaces.Key)
     */
    public NextHopResult nextHop(IKey target) {
        Coord target_coord = new Coord(dimensions,bitsPerDimension,target);
        Diagnostic.trace( FormatHostInfo.formatHostName(hostAddress) + ": in nextHop target = " + target.toString(), Diagnostic.RUN);   
        Diagnostic.trace( FormatHostInfo.formatHostName(hostAddress) + ": target coord = " + target_coord.toString(), Diagnostic.RUN);   
        HyperCube next_hc = myCube.findClosestTo( target_coord );
        Diagnostic.trace( "Closest hypercube = " + next_hc.toString(), Diagnostic.RUN);          
        CanRemote next = next_hc.getOwner();
        if( this.getKey().equals( next.getKey() ) ) {
            return new NextHopResult(NextHopResult.FINAL,this);
        } else if( next == null ) {
            return new NextHopResult( NextHopResult.ERROR, null );
        } else {
            return new NextHopResult( NextHopResult.NEXT_HOP,next );
        }
    }

    public NextHopResult fakeNextHop(IKey target) {				// TODO Delete this when routing works
        new Coord(dimensions,bitsPerDimension,target);
        return new NextHopResult(NextHopResult.FINAL,this);
    }
    
    /** ********************* Utility Methods ********************** */

    public String toString() {
        return hostAddressString;
    }

    /** ********************* Predicates ********************** */

    /**
     * Tests whether the node has failed, either in reality due to failure of
     * all its successors, or due to simulation.
     * 
     * @return true if the node has failed
     */
    public boolean hasFailed() {
        return failed;
    }

    public void isAlive() throws Exception {
        if (failed)
            throw new SimulatedFailureException(
                    "PlaxtonImpl::isAlive - simulated failure");
    }

    /*********************** Setters ***********************/

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.stand.dcs.asa.can.interfaces.CanNode#setFailed(boolean)
     */
    public void setFailed(boolean failed) {
        // TODO Auto-generated method stub
    }

    /*********************** Selectors ***********************/
    
    /**
     * @return Returns the dimension.
     */
    public int getDimensions() {
        return dimensions;
    }
    
    public HyperCube getHyperCube() {
        return myCube;
    }
    
    public Coord getCentre() {
        return myCube.getCentre();
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
        if( myCube == null ) {
            if( node_in_network ) {
                Error.hardError( "Getting key of node that is not in any network" );
            }
            Diagnostic.trace( " Attempt to obtain key from can node without hypercube - using initial key", Diagnostic.RUN );
            return initial_key;
        }
        return myCube.getKey();
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