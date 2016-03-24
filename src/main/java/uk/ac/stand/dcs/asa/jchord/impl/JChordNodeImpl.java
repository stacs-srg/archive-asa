package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.applicationFramework.impl.Message;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.ApplicationRegistry;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.interfaces.IFailureSuspector;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.INextHopResult;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.jchord.events.JChordEventFactory;
import uk.ac.stand.dcs.asa.jchord.fingerTableFactories.GeometricFingerTableFactory;
import uk.ac.stand.dcs.asa.jchord.interfaces.*;
import uk.ac.stand.dcs.asa.simulation.SimulatedFailureException;
import uk.ac.stand.dcs.asa.util.*;
import uk.ac.stand.dcs.asa.util.Error;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Observable;

/**
 * Implementation of Chord node.
 * 
 * @author sja7, stuart, al, graham
 */
public class JChordNodeImpl extends Observable implements IJChordNode,
        IJChordIterator, INeighbourAwareJChordNode, Comparable {

    //******************** Static Fields *********************

    // TODO general mechanism for exposing and controlling configuration parameters.
    // TODO analyse critical regions and possible deadlocks.
	
    /**
     * Default wait duration between periodic ring maintenance operations, in ms.
     */
    public static final int DEFAULT_WAIT_PERIOD = 1000;

    //******************** Public Fields *********************

    public InetSocketAddress hostAddress;

    public IKey key;

    //******************** Private Fields ********************

    private SuccessorList successor_list;

    private IJChordRemote predecessor, successor;

    private IFingerTable finger_table;

    private EventBus bus;

    private int wait_period;

    private boolean failed = false;

    private ActionQueue action_queue;

    private IFailureSuspector failure_suspector;

    private String hostStringRep;

    private ApplicationRegistry registry;

    /** ******************* Constructors ******************** */

    public JChordNodeImpl(IKey key) {
        this(null, key, (EventBus) null);
    }

    public JChordNodeImpl(InetSocketAddress localAddress, IKey key) {
        this(localAddress, key, (EventBus) null);
    }

    public JChordNodeImpl(InetSocketAddress localAddress, IKey key, EventBus bus) {
        this(localAddress, key, bus, new GeometricFingerTableFactory(), null);
    }

    public JChordNodeImpl(InetSocketAddress localAddress, IKey key, EventBus bus, ApplicationRegistry registry) {
        this(localAddress, key, bus, new GeometricFingerTableFactory(), registry);
    }

    public JChordNodeImpl(InetSocketAddress localAddress, IKey key, IFingerTableFactory fingerTableFactory) {
        this(localAddress, key, null, fingerTableFactory, null);
    }

    public JChordNodeImpl(InetSocketAddress localAddress, IKey key, EventBus bus, IFingerTableFactory fingerTableFactory, ApplicationRegistry registry) {

    	Assert.assertion(fingerTableFactory != null, "Finger table factory was null");

        this.key = key;
        this.hostAddress = localAddress;
        this.bus = bus;
        this.registry = registry;

        finger_table = fingerTableFactory.makeFingerTable(this);

        wait_period = DEFAULT_WAIT_PERIOD;

        predecessor = null;
        successor = null;

        successor_list = new SuccessorList(this,bus);
        action_queue = new ActionQueue();
        failure_suspector = new TimeoutFailureSuspector();
        Diagnostic.trace("initialised with key: " + getKey(), Diagnostic.INIT);
    }

    /**
     * Default constructor used in deserialisation.
     */
    public JChordNodeImpl() {
    	// Deliberately empty.
    }

    /** ********************* Methods *********************** */

    public int compareTo(Object other) {

        if (other == null) return 1;

        try {
            IKey otherKey = ((IP2PNode) (other)).getKey();

            if (otherKey == null) return 1;
            if (getKey() == null) return -1;

            return (getKey().compareTo(otherKey));
        }
        catch (Exception e) {
            Error.exceptionError("Exception getting key for NodeComparison", e);
            return -1;
        }
    }

    /**
     * Creates a new ring for a node to exist on.
     */
    public void createRing() {
        setPredecessor(null);
        setSuccessor(this);
        successor_list.refreshList(null,this);
    }

    /**
     * Uses a known node on the ring to discover the appropriate successor for
     * this node, and sets it as the successor.
     * 
     * @param known_node a node that is already part of the ring
     */
    public void join(IJChordRemote known_node) throws P2PNodeException {

        if (known_node == null) throw new P2PNodeException(P2PStatus.NODE_JOIN_FAILURE, "known node was null");
        else {

            IJChordRemote next = known_node;

            while (true) {

                JChordNextHopResult result = null;
                
                try { result = next.next_hop(getKey()); }
                catch (Exception e) { throw new P2PNodeException(P2PStatus.NODE_JOIN_FAILURE, "next.nextHop() call failed"); }
                
                switch (result.getCode()) {

	                case JChordNextHopResult.FINAL :
	                	
	                    IJChordRemote newSucc = result.getResult();
	                    
	                    if (newSucc != null) setSuccessor(newSucc);
	                    else throw new P2PNodeException(P2PStatus.NODE_JOIN_FAILURE, "next hop returned a null value.");
	
	                    return;
	                    
	                case JChordNextHopResult.NEXT_HOP :
	                	
	                    next = result.getResult();
	                    break;
	
	                case JChordNextHopResult.ERROR :
	                	
	                    throw new P2PNodeException(P2PStatus.NODE_JOIN_FAILURE,
	                            "nextHop call returned error: \"" + result.getError().getLocalizedMessage() + "\"");
	                default :
	                	
	                    throw new P2PNodeException(P2PStatus.NODE_JOIN_FAILURE,
	                            "nextHop call returned NextHopResult with unrecognised code");
                }
            }
        }
    }

    /**
     * Checks whether this node has pointers to the correct
     * successor and predecessor, and therefore properly stabilised. It does
     * this by finding the predecessor of this node's successor. If its
     * successor's predecessor is not set to this node, another node has joined
     * in between this node and this node's successor. If the new node in
     * between has a key that is between this node's key, and its successor's
     * key, this node will set its successor to be the new node and will call
     * the new node's notify method to tell the new node that it is its
     * predecessor. If the new node is not in between, this node will call
     * notify on the existing successor telling it to set its predecessor back
     * to this node.
     */
    public void stabilise() {
        if (!failed) {
            IJChordRemote predecessor_of_successor = null;
            try {
            	predecessor_of_successor = successor.getPredecessor();
            }
            catch (Exception e) {
                Error.exceptionError(this + ": error calling getPredecessor on successor", e);
                Diagnostic.trace(this + ": calling findWorkingSuccessor", Diagnostic.RUN);
                // Error communicating with current successor, so attempt to find new one.
                findWorkingSuccessor();
                return;
            }

            if (predecessor_of_successor != null) {
                IKey key_of_predecessor_of_successor, key_of_successor;
                
                // TODO shouldn't we now return if the successor already thinks
                // we are its predecessor?
                // i.e. if (key_of_predecessor_of_successor.compareTo(key) == 0)
                // return;

                try {
                    key_of_predecessor_of_successor = predecessor_of_successor.getKey();
                }
                catch (Exception e) {
                    Error.exceptionError(this + ": error calling getKey on successor's predecessor", e);
                    // TODO what should happen here? Don't think this should ever happen - hard error? - GK.
                    return;
                }
                try {
                    key_of_successor = successor.getKey();
                }
                catch (Exception e) {
                    Error.exceptionError(this + ": error calling getKey on successor", e);
                    Diagnostic.trace(this + " calling findWorkingSuccessor", Diagnostic.RUN);
                    // TODO what should happen here? Don't think this should ever happen - hard error? - GK.
                    findWorkingSuccessor();
                    return;
                }

                if (SegmentArithmetic.inOpenSegment(key_of_predecessor_of_successor, getKey(), key_of_successor)) {
                    // This node's successor's predecessor is more suitable as
                    // this node's successor.
                    setSuccessor(predecessor_of_successor);
                }
            }

            try {
                successor.notify(this);
            }
            catch (Exception e) {
                Error.exceptionError(this + ": error calling notify on successor", e);
                Diagnostic.trace(this + ": calling findWorkingSuccessor", Diagnostic.RUN);
                // Error communicating with current successor, so attempt to find new one.
                findWorkingSuccessor();
                return;
            }

            // TODO obsolete check? Don't think successor can be this - GK.
            if (successor != this) {
                try {
                    Object[] new_list = successor.getSuccessorList();
                    if (new_list != null) successor_list.refreshList(new_list, successor);
                }
                catch (Exception e) {
                    Error.exceptionError(this + ": error calling getSuccessorList on successor", e);
                    Diagnostic.trace(this + ": calling findWorkingSuccessor", Diagnostic.RUN);
                    // Error communicating with current successor, so attempt to find new one.
                    findWorkingSuccessor();
                    return;
                }
            }
        }
    }

    /**
     * [WebService] Returns the successor node, in key space, of a given key.
     * 
     * @param given_key the key to find the successor of.
     * @return the closest successor of the specified key, or null if the
     *         closest successor is the node that called the method. If the key
     *         specified is greater than this node's key, and less than or equal
     *         to this node's successor's key, return this node's successor.
     *         Else recursively call findSuccessor on nodes preceding the given
     *         key, until a suitable successor for the key is found (when the
     *         key is greater than the current node's key, and less than or
     *         equal to the current node's successor's key).
     */
    public IJChordRemote findSuccessor(IKey given_key) throws Exception {

        if (failed)
            throw ExceptionFactory.makeLabelledException(new SimulatedFailureException("simulated failure"));

        // If the key we are looking for is the same as this node's key, or if
        // this node's successor is itself, then return this node.
        if (given_key.compareTo(getKey()) == 0 || getSuccessor() == this)
            return this;

        // If the key lies between this node and its successor, return the
        // successor.
        try {
            if (SegmentArithmetic.inHalfOpenSegment(given_key, getKey(), successor.getKey()))
                return successor;
        }
        catch (Exception e) {

            Error.exceptionError(this + ": error getting representation of successor", e);

            Diagnostic.trace(this + ": calling findWorkingSuccessor", Diagnostic.RUN);

            findWorkingSuccessor();

            //TODO - deal with findSuccessor returning null.
            return null;
        }

        IJChordRemote next = closestPrecedingNode(given_key);
        int hop_count = 0;

        while (true) {
            try {
                JChordNextHopResult result = next.next_hop(given_key);

                switch (result.getCode()) {

	                case JChordNextHopResult.NEXT_HOP :
	                	
	                    next = result.getResult();
	                    break;
	
	                case JChordNextHopResult.FINAL :
	                	
	                    IJChordRemote jcr = result.getResult();
	                    return jcr;
	
	                case JChordNextHopResult.ERROR :
	                	
	                    Error.error("nextHop call returned error: \"" + result.getError().getLocalizedMessage() + "\"");
	                    return null;
	
	                default:
	                	
	                    Error.hardError("nextHop call returned NextHopResult with unrecognised code");
	                    return null;
                }

                hop_count++;
            }
            catch (Exception e) {

                Error.exceptionError(this + ": error calling nextHop on node" + FormatHostInfo.formatHostName(next.getHostAddress()), e);

                try {
                    Diagnostic.trace(this + ": signalling suspected failure of " + FormatHostInfo.formatHostName(next.getHostAddress()), Diagnostic.RUN);
                }
                catch (Exception e1) {
                    Error.exceptionError("error while gathering information for creation of node representation", e1);
                }

                // Only the first hop in the chain is to a finger.
                if (hop_count == 0) suggestSuspectedFingerFailure(next);
                else                suggestSuspectedFailure(next);

                // TODO - deal with failure of closest preceding node
                //	At this point we should do something to work
                //	around the problem that we can't contact the
                //	closest preceding node.

                // we could try another preceding node.

                return null;
            }
        }
    }

    // NextHopAPI implementations
    
    /**
     * [Web Service] Returns the next hop towards the successor node of a given
     * key in key space. If the key specified is greater this node's key, and
     * less than or equal to this node's successor's key, return this node's
     * successor. Else recursively call findSuccessor on preceding nodes until a
     * suitable successor for the key is found (when the key is greater than the
     * current node's key, and less than or equal to the current node's
     * successor's key).
     * 
     * @param given_key the key of a node
     * @return the closest successor of the node with the specified key, or null
     *         if the closest successor is the node that called the method
     */
    public JChordNextHopResult next_hop(IKey given_key) throws P2PNodeException {

        if (failed) throw new P2PNodeException(P2PStatus.NODE_FAILED, "simulated failure");

        try {
            int result_code;
            IJChordRemote next_hop;

            if (SegmentArithmetic.inHalfOpenSegment(given_key, getKey(), getSuccessor().getKey())) {

                next_hop = getSuccessor();
                result_code = JChordNextHopResult.FINAL;
            }
            else {

                next_hop = closestPrecedingNode(given_key);
                result_code = JChordNextHopResult.NEXT_HOP;
            }

            return new JChordNextHopResult(result_code, next_hop);

        }
        catch (Exception e) {
        	
            P2PNodeException generatedEx = new P2PNodeException( P2PStatus.STATE_ACCESS_FAILURE, "Failed to obtain a representation of the JChord node's sucessor");
            Error.exceptionError(this + ": error getting representation of successor", generatedEx);

            Diagnostic.trace(this + ": calling findWorkingSuccessor", Diagnostic.RUN);

            // Find a working successor, but asynchronously since the result
            // won't be used here.
            findWorkingSuccessor();

            return new JChordNextHopResult(generatedEx);
        }
    }

    public JChordNextHopResult dol_resolve(IKey k, AID appID) throws P2PNodeException {
    	
        // This node should be the root node for k.
    	
        if (SegmentArithmetic.inHalfOpenSegment(k, predecessor.getKey(), getKey())) {
        	
            JChordNextHopResult result = new JChordNextHopResult(INextHopResult.ROOT, this);
            String appString = null;
            
            try {
                appString = registry.forward(k, appID);
            }
            catch (P2PApplicationException e) {
                // Ignore the application exception and continue as if the forward call returned null.
            }
            
            if (appString != null) result.setAppObjectName(appString,true);

            return result;
        }
        else throw new P2PNodeException(P2PStatus.LOOKUP_FAILURE, "this node is not the root for the specified key (" + k + ")");
    }
    
    public JChordNextHopResult dol_next_hop(IKey k, AID appID) throws P2PNodeException {
    	
        // TODO what should happen if this node is the root node for k?
        // This should never happen since this method is called on the closest preceeding node for k.
        
        IJChordRemote next_hop;
        int result_code;

        if (SegmentArithmetic.inHalfOpenSegment(k, getKey(), successor.getKey())) {
        	
            next_hop = successor;
            result_code = JChordNextHopResult.FINAL;
        }
        else {
        	
            next_hop = closestPrecedingNode(k);
            result_code = JChordNextHopResult.NEXT_HOP;
        }

        JChordNextHopResult result = new JChordNextHopResult(result_code, next_hop);
        String appString = null;
        
        try {
            appString = registry.forward(k, appID);
        }
        catch (P2PApplicationException e) {
            // Ignore the application exception and continue as if the forward call returned null.
        }
        
        if (appString != null)  result.setAppObjectName(appString, inLocalKeyRange(k));
        
        return result;
    }

    public boolean inLocalKeyRange(IKey k) throws P2PNodeException {
    	
        if (predecessor != null) {
        	
            IKey pkey;
            try {
                pkey = predecessor.getKey();
            }
            catch (Exception e) { throw new P2PNodeException(P2PStatus.STATE_ACCESS_FAILURE, "Could not obtain the key of this P2P node's predecesor."); }
            
            return SegmentArithmetic.inHalfOpenSegment(k, pkey, key);
        }
        else throw new P2PNodeException(P2PStatus.STATE_ACCESS_FAILURE, "This JChord node's predecesor is null.");
    }
    
    public JChordNextHopResult send_next_hop(IKey k, AID appID, Message m) throws P2PNodeException {
        return null;
    }

    /**
     * [WebService] Notifies this node that a given node may be its predecessor.
     * Called by a node that thinks it is this node's most suitable predecessor.
     * If the current node has no predecessor set, or if the specified node has
     * a key that is between this node's key and this node's predecessor's key,
     * set the specified node to be this node's predecessor.
     * 
     * @param potential_predecessor a node that thinks it is this node's most suitable predecessor
     */
    public void notify(IJChordRemote potential_predecessor) throws Exception {

        if (failed) throw ExceptionFactory.makeLabelledException( new SimulatedFailureException("simulated failure"));

        // If there isn't currently a predecessor then use the suggested one.
        if (getPredecessor() == null) setPredecessor(potential_predecessor);
        else
            try {

                // Otherwise, only use the suggested predecessor if it's nearer
                // than the current predecessor.
                IKey current_pred_key = getPredecessor().getKey();
                IKey potential_pred_key = potential_predecessor.getKey();

                if (SegmentArithmetic.inOpenSegment(potential_pred_key, current_pred_key, getKey()))
                    setPredecessor(potential_predecessor);
            }
        	catch (Exception e) {
                Error.exceptionError("error getting key of current or potential predecessor", e);
            }
    }

    /**
     * @return this node's AbstractFingerTable
     */
    public IFingerTable getFingerTable() {
        return finger_table;
    }

    /**
     * Updates a node's finger table. This should be called periodically.
     */
    public void fixNextFinger() {

        if (!failed) finger_table.fixNextFinger();
    }

    /* General helper methods */

    /**
     * Checks the finger table for a node with a key that has the closest value
     * less than the specified key. If this node's key has the most suitable
     * key, it is returned.
     * 
     * @param k the key of a node
     * @return the node with the closest preceding key to k
     */
    public IJChordRemote closestPrecedingNode(IKey k) {

        IJChordRemote result = finger_table.closestPrecedingNode(k);

        if (result == null) return this;
        else                return result;
    }

    /**
     * [WebService] Used when a node stabilises. On a stabilise, the node get
     * its successor's successor list to refresh its own list.
     * 
     * @return this node's successor list
     */
    public Object[] getSuccessorList() throws Exception {

        if (failed) throw ExceptionFactory.makeLabelledException(new SimulatedFailureException("simulated failure"));

        SuccessorList result = successor_list;

        Assert.assertion(result != null, "successor list was null");

        return result.getListAsArray();
    }

    /* Selectors */

    /**
     * [WebService] Returns the successor node in key space.
     * 
     * @return the successor of the current node
     */
    public IJChordRemote getSuccessor() throws Exception {

        if (failed) throw ExceptionFactory.makeLabelledException( new SimulatedFailureException("simulated failure"));

        return successor;
    }

    /**
     * Sets the successor node in key space.
     * 
     * @param new_successor the new successor node
     */
    public synchronized void setSuccessor(IJChordRemote new_successor) {

        if (new_successor != null) {
        	
            IJChordRemote old_successor = successor;
            successor = new_successor;
            
            if (new_successor != this) {
            	
                try {
                    generateEvent(JChordEventFactory.makeSuccessorRepEvent(new_successor.getHostAddress(),new_successor.getKey()));
                }
                catch (Exception e) { Error.exceptionError("error while gathering information for creation of node representation", e); }
            }
            else generateEvent(JChordEventFactory.makeSuccessorRepEvent(hostAddress,key));
            
            setChanged();
            notifyObservers(JChordEventFactory.makeSuccessorStateEvent(successor, old_successor));
            
        }
        else Error.error("attempt to set node's successor to null - successor value is unchanged");
    }

    /**
     * [WebService] Returns the predecessor node in key space.
     * 
     * @return the predecessor of the current node
     */
    public IJChordRemote getPredecessor() throws Exception {

        if (failed) throw ExceptionFactory.makeLabelledException(new SimulatedFailureException( "simulated failure"));

        return predecessor;
    }

    /**
     * Sets the predecessor node in key space.
     * 
     * @param new_predecessor the new predecessor node
     */
    public synchronized void setPredecessor(IJChordRemote new_predecessor) {

        predecessor = new_predecessor;

        if (new_predecessor != null) {
        	
            try {
                generateEvent(JChordEventFactory.makePredecessorRepEvent(new_predecessor.getHostAddress(),new_predecessor.getKey()));
            }
            catch (Exception e) { Error.exceptionError("error while gathering information for creation of node representation", e); }
        }
        else generateEvent(JChordEventFactory.makePredecessorRepEvent(null, null));
    }

    /**
     * Checks whether predecessor has failed, and drops reference if so.
     */
    public void checkPredecessor() {

        if (!failed) {

            IJChordRemote predecessor = null;

            try {
                predecessor = getPredecessor();
            }
            catch (Exception e) { Error.hardError("local call to getPredecessor failed"); }

            try {
                if (predecessor != null) predecessor.isAlive();
            }
            catch (Exception e) {

                Diagnostic.trace(this + ": predecessor failed, setting predecessor pointer to null", Diagnostic.RUN);
                
                try {
                    Diagnostic.trace(this + ": signalling suspected failure of " + FormatHostInfo.formatHostName(predecessor.getHostAddress()), Diagnostic.RUN);
                }
                catch (Exception e1) { Error.exceptionError("error while gathering information for creation of node representation", e1); }

                suggestSuspectedFailure(predecessor);
                setPredecessor(null);
            }
        }
    }

    public void isAlive() throws Exception {

        if (failed) throw ExceptionFactory.makeLabelledException(new SimulatedFailureException( "simulated failure"));
    }

    private void generateEvent(Event e) {

        if (bus != null) bus.publishEvent(e);
    }

    /**
     * Attempts to find a working successor from the successor list.
     */
    private synchronized void findWorkingSuccessor() {

        Diagnostic.trace(this + ": findWorkingSuccessor", Diagnostic.RUN);

        // Option to perform action asynchronously disabled due to race
        // conditions.
        boolean synchronous = true;

        suggestSuspectedFailure(successor);

        Action action = new Action() {

            public synchronized void performAction() {

                Diagnostic.trace(this + " -> findWorkingSuccessor (2)", Diagnostic.RUN);

                IJChordRemote old_successor = successor;
                IJChordRemote new_successor = successor_list.findFirstWorkingNode();

                if (new_successor != null) {

                    Diagnostic.trace(this + " -> found working node in successor list", Diagnostic.RUN);
                    setSuccessor(new_successor);

                }
                else {

                    Diagnostic.trace(this + " -> could not find working node in successor list, setting successor to be null", Diagnostic.RUN);

                    // TODO implement protocol to try to recover from this, e.g. using finger table or predecessor.

                    // No successors could be found, so revert to new ring.

                    createRing();
                }

                try {
                    Diagnostic.trace(this + " -> signalling suspected failure of " + FormatHostInfo.formatHostName(old_successor.getHostAddress()), Diagnostic.RUN);
                }
                catch (Exception e1) {
                    Error.exceptionError("error while gathering information for creation of node representation", e1);
                }
            }
        };

        if (synchronous) action.performAction();
        else             action_queue.enqueue(action);
    }

    public void run() {
    	
        Diagnostic.trace("JChord node is running", Diagnostic.RUN);

        //TODO this should not be here
        // Added by gjh1, 02/08/2005
        Event infoEvent = new Event("NodeSystemInfo");
        infoEvent.put("source", hostAddress);
        infoEvent.put("OSArch", System.getProperty("os.arch"));
        infoEvent.put("OSName", System.getProperty("os.name"));
        infoEvent.put("OSVersion", System.getProperty("os.version"));
        infoEvent.put("JavaVendor", System.getProperty("java.vendor"));
        infoEvent.put("JavaVersion", System.getProperty("java.version"));
        infoEvent.put("AvailableProcessors", String.valueOf(Runtime.getRuntime().availableProcessors()));
        generateEvent(infoEvent);

        while (!failed) {
            try {
                Thread.sleep(wait_period);

                checkPredecessor();
                stabilise();
                fixNextFinger();
            }
            catch (Exception e) { Error.exceptionError("error in periodic activities", e); }
        }

        Diagnostic.trace(this + ": node failed", Diagnostic.RUN);
    }

    public Iterator iterator(IKey k, IApplicationGUID aid) {

        Error.hardError("unimplemented method");

        return null;
    }

    /*
     * Informs the node of its near (or otherwise) neighbours permitting some optimisation.
     */
    public void addNeighbours(IJChordNode[] neighbours) {

        for (int i = 0; i < neighbours.length; i++) {

            // Ignore suggestions of this node or its successor.
            IJChordNode suggestion = neighbours[i];

            if (suggestion != this && suggestion != successor)
                finger_table.exists(suggestion);
        }
    }

    public String toString() {
    	
        if (hostStringRep == null) hostStringRep = FormatHostInfo.formatHostName(hostAddress);
        return hostStringRep;
    }

    /**
     * Allows node failure to be simulated.
     * 
     * @param failed
     */
    public void setFailed(boolean failed) {

        this.failed = failed;
    }

    /**
     * Tests whether the node has failed, either in reality due to failure of
     * all its successors, or due to simulation.
     * 
     * @return true if the node has failed
     */
    public boolean hasFailed() {

        return failed;
    }

    private void suggestSuspectedFailure(IJChordRemote node) {

        if (node != null)
            try {
                generateEvent(JChordEventFactory.makeNodeFailureNotificationRepEvent(node.getHostAddress(),node.getKey()));
            }
        	catch (Exception e) { Error.exceptionError("error getting node representation", e); }
    }

    private void suggestSuspectedFingerFailure(IJChordRemote node) {

        // Notify finger table that the node may have failed.
        finger_table.dead(node);

        try {
            Diagnostic.trace(this + ": signalling suspected failure of " + FormatHostInfo.formatHostName(node.getHostAddress()), Diagnostic.RUN);
        }
        catch (Exception e1) { Error.exceptionError("error while gathering information for creation of node representation", e1); }

        suggestSuspectedFailure(node);
    }

    public IFailureSuspector getFailureSuspector() {

        return failure_suspector;
    }

    public int routingStateSize() {
    	
        return finger_table.getNumberOfEntries();
    }

    public IKey getKey() {
    	
        return key;
    }

    public InetSocketAddress getHostAddress() throws Exception {
    	
        return hostAddress;
    }

    public IP2PNode lookup(IKey k) throws P2PNodeException {
    	
        return jnl(k);
    }

    /**
     * Finds the node holding a application component which has responsibility (?) for 
     * the given key. A next hop call on a node that is the root for k will result in 
     * an error.
     */
    private JChordNextHopResult dolImpl(IKey k, AID appID) throws P2PNodeException {
    	
        // If the key lies between this node and its successor, return the successor.
        IKey succKey = null;
        succKey = successor.getKey();

        if (SegmentArithmetic.inHalfOpenSegment(k, getKey(), succKey))
            return new JChordNextHopResult(JChordNextHopResult.FINAL,successor);

        IJChordRemote next = closestPrecedingNode(k);
        int hop_count = 0;
        
        while (true) {
        	
            JChordNextHopResult result = null;
            
            try {
                result = next.dol_next_hop(k, appID);
            }
            catch (P2PNodeException e){ 
                
                // Only the first hop in the chain is to a finger.
                if (hop_count == 0) suggestSuspectedFingerFailure(next);
                else                suggestSuspectedFailure(next);
                
                throw new P2PNodeException(P2PStatus.LOOKUP_FAILURE);
            }
            
            switch (result.getCode()) {

            	case JChordNextHopResult.NEXT_HOP :
            		
            	    next = result.getResult();
                	break;
	
            	case JChordNextHopResult.FINAL :
            		
            		return result;

            	case JChordNextHopResult.INTERCEPT :
            		
            	    return result;

            	case JChordNextHopResult.ERROR :
            		
            	    Error.error("nextHop call returned error: \"" + result.getError().getLocalizedMessage() + "\"");
                	return null;

            	default :
            		
            	    Error.hardError("nextHop call returned NextHopResult with unrecognised code");
                	return null;
            }

            hop_count++;
        }
    }

    public JChordNextHopResult dol(IKey k, AID appID) throws P2PNodeException {
    	
        return dolImpl(k, appID);
    }

    public IJChordRemote jnl(IKey k) throws P2PNodeException {
    	
        try {
            return findSuccessor(k);
        }
        catch (Exception e) { throw new P2PNodeException(P2PStatus.LOOKUP_FAILURE,e.getLocalizedMessage()); }
    }

    public void sendMsg(IKey k, AID appID, Message msg) {//throws P2PNodeException {
        
    	Error.hardError("unimplemented method");
    }
}
