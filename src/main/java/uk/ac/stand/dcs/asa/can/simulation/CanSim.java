/*
 * Created on 29 March 2005
 */
package uk.ac.stand.dcs.asa.can.simulation;

import uk.ac.stand.dcs.asa.can.impl.CanNodeImpl;
import uk.ac.stand.dcs.asa.can.impl.NextHopResult;
import uk.ac.stand.dcs.asa.can.impl.NodeViewer;
import uk.ac.stand.dcs.asa.can.interfaces.CanNode;
import uk.ac.stand.dcs.asa.can.interfaces.CanRemote;
import uk.ac.stand.dcs.asa.impl.Route;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.INodeFactory;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.jchord.util.JChordProperties;
import uk.ac.stand.dcs.asa.simulation.SimulationFramework;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * @author stuart, al, graham
 */
public class CanSim extends SimulationFramework implements P2PSim {
	
	public static int keylen = JChordProperties.getProperties().getKeyLength();
	
    public CanSim(int node_count, INodeFactory nfactory){
    		super(node_count,nfactory,true);
    }
	
	public void initialiseP2PLinks() {
		try {		
			Diagnostic.trace("Creating network", Diagnostic.RUN);		    
		    makeNetwork(false);
		    fixKeyMappings();
			Diagnostic.trace("Simulation initialisation complete", Diagnostic.RUN);
			
		} catch (Exception e) {
			Error.exceptionError("Instantiation exception", e);
		}
	}
	
    /**
     * Method to reassociate the key mappings once the CAN network is stable
     */
    private void fixKeyMappings() {
        key_to_index.clear();
        for( int i = 0 ; i < nodes.length; i++ ) {
            IKey k = nodes[i].getKey();
            Diagnostic.trace( "Adding Key mapping from " + k + " to " + i, Diagnostic.RUN );
            key_to_index.put(k, new Integer(i));
        }
    }

    /**
     * This method returns the route from a given node searching for a given key
     * 
     * @param node the node from which we are searching
     * @param k the hey to be found starting at node node
     * @return the route from node to the node responsible for key k
     */
    public Route makeRoute(IP2PNode node, IKey k ) {
        Diagnostic.trace( "Making route to " + k, Diagnostic.RUN );
        NextHopResult result = null;
        CanRemote next = (CanRemote) node;
        boolean notFound = true;
        Route r = new Route( node,k );
        while (notFound) {
            try {
                result = next.nextHop(k);
                switch (result.getCode()) {
                case NextHopResult.NEXT_HOP:
                    next = result.getResult();
                r.addHop(next);
                break;
                case NextHopResult.FINAL:
                    next = result.getResult();
                r.addHop(next);
                notFound = false;
                break;
                case NextHopResult.ERROR: {
                    Error.hardError("nextHop call returned error: \"" + result.getError().get("msg") + "\".");
                }
                default: {
                    Error.hardError("nextHop call returned NextHopResult with unrecognised code");
                }
                }
            } catch (Exception e) {
                Error.exceptionError( "Error calling nextHop on closest preceding node",e );
            }
        }
        return r;
    }
    

	
	/**
	 * Forms the JChord ring by making appropriate join and stabilise calls.
	 * 
	 * @param randomiseJoinPosition true if nodes should be joined at random positions in the ring (takes longer)
	 * @throws Exception
	 */	
	public void makeNetwork(boolean randomiseJoinPosition) throws Exception {
        if (nodes.length > 0) {   
            if (randomiseJoinPosition) {       
                int count = 0; //for progress reporting
                ((CanNode)nodes[0]).createNetwork();               
                Random rand = new Random(RANDOM_SEED);
                for (int i = 1; i < nodes.length; i++) {                    
                    // Select a random member of the ring.
                    int random_node_index = rand.nextInt(i);                    
                    ((CanNode)nodes[i]).join((CanNode)nodes[random_node_index]); 
                    showProgress(count++, progress_granularity, linebreak_granularity, Diagnostic.RUN);
                }
            }
            else {               
                ArrayList nodelist = new ArrayList(nodes_in_key_order);
                int length = nodelist.size();
                int startingIndex = length - 1;
                int count = 0; //for progress reporting
                CanNode highestNode = (CanNode) nodelist.get(startingIndex);
                highestNode.createNetwork();
                CanNode next = null;
                startingIndex--;
                for (int i = startingIndex; i >= 0; i--) {
                    next = (CanNode) nodelist.get(i);
                    next.join(highestNode);
                    showProgress(count++, progress_granularity, linebreak_granularity, Diagnostic.RUN);
                }
            }
        }
    }
	
    public void runAll() {
        // TODO fix this later - al
//        for (int i = 0; i < nodes.length; i++) {
//            
//            final CanNode node = (CanNode)nodes[i];	// TODO could be typed as P2P node and put in superclass if below is true
//            
//            new Thread() {
//                public void run() {
//                    node.run();			// TODO this means that P2PNode should have run?????
//                }
//            }.start();
//        }
    }

    /* (non-Javadoc)
     * @see uk.ac.stand.dcs.asa.simulation.SimulationFramework#showNode(int)
     */
    public void showNode(int index) {
        showNode( (CanNodeImpl) nodes[index] );
    }
    
    public void showNode( CanNodeImpl cni ) {
        NodeViewer nv = new NodeViewer( cni );
        nv.showAll();       
    }
    
    public void showRoute( IP2PNode node, IKey k, Route route ) {
        Iterator iter = route.iterator();
        int hops = 1;
        
        try {
            Diagnostic.trace("index = " + key2Index(node.getKey()) + "\tip= "+ node.getHostAddress().getAddress() + "\ttarget   " + k + " keylen = " + k.toString().length(), Diagnostic.NONE);
        } catch (Exception e) {
            Error.exceptionError("error getting representation of node", e);
        }
        
        while( iter.hasNext() ) {
            IP2PNode next_node = (IP2PNode) iter.next();
            try {
                IKey resultKey = next_node.getKey();
                
                Integer resultindex = (Integer) key_to_index.get( resultKey );               
                Diagnostic.trace( "index = " + resultindex + "\tip= "+ node.getHostAddress().getAddress() + "\tHop    ", + hops++ + " " + resultKey + " keylen = " + resultKey.toString().length(), Diagnostic.NONE);
                showNode( resultindex.intValue() );
            }
            catch (Exception e) { Error.exceptionError("error getting representation of node", e); }
        }
    }
        
}
