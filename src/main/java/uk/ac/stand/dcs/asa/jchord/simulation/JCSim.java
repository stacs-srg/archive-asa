/*
 * Created on 03-Dec-2004
 */
package uk.ac.stand.dcs.asa.jchord.simulation;

import uk.ac.stand.dcs.asa.impl.Route;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.INodeFactory;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.jchord.impl.JChordNextHopResult;
import uk.ac.stand.dcs.asa.jchord.interfaces.IFingerTable;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.jchord.interfaces.INeighbourAwareJChordNode;
import uk.ac.stand.dcs.asa.jchord.util.JChordProperties;
import uk.ac.stand.dcs.asa.simulation.SimulationFramework;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.util.SimProgressWindow;
import uk.ac.stand.dcs.asa.util.Assert;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author stuart, al, graham
 */
public class JCSim extends SimulationFramework implements P2PSim {
	
	public static int keylen = JChordProperties.getProperties().getKeyLength();

	private int neighbourhood_size;		// Size of neighbourhood - TODO should this be here - al
	
    public JCSim(int node_count, int neighbourhood_size, INodeFactory nfactory, boolean showProgress){
    		super(node_count,nfactory, showProgress);
    		this.neighbourhood_size = Math.min(neighbourhood_size, node_count);
    }
	
    public JCSim(int node_count, int neighbourhood_size, INodeFactory nfactory){
    	this(node_count,neighbourhood_size,nfactory,false);
    }
    
	public void initialiseP2PLinks() {
		try {
			Diagnostic.trace("Forming Chord ring", Diagnostic.RUN);
			formRing(false);
			
			Diagnostic.trace("Checking stability", Diagnostic.RUN);
			if (!ringIsStable()) Error.hardError("ring is not stable");
			
			Diagnostic.trace("Associating neighbours", Diagnostic.RUN);
			associateNeighbours();
			
			Diagnostic.trace("Ring is stable, now populating finger tables", Diagnostic.RUN);
			populateFingerTables();
			
			Diagnostic.trace("Finger tables populated", Diagnostic.RUN);
			Diagnostic.trace("Simulation initialisation complete", Diagnostic.RUN);
			
		} catch (Exception e) {
			Error.exceptionError("Instantiation exception", e);
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
        JChordNextHopResult result = null;
        IJChordRemote next = (IJChordRemote) node;
        boolean notFound = true;
        Route r = new Route( node,k );
        while (notFound) {
            try {
                result = next.next_hop(k);
                switch (result.getCode()) {
                	
                case JChordNextHopResult.NEXT_HOP:
                    next = result.getResult();
                r.addHop(next);
                break;
                
                case JChordNextHopResult.FINAL:
                    next = result.getResult();
                r.addHop(next);
                notFound = false;
                break;
                
                case JChordNextHopResult.ERROR: {
                    Error.hardError("nextHop call returned error: \"" + result.getError().getLocalizedMessage() + "\".");
                }
                
                default: {
                    Error.hardError("nextHop call returned NextHopResult with unrecognised code");
                }
                }
            } catch (Exception e) {
                Error.hardError("Error calling nextHop on closest preceding node");
            }
        }
       // showRoute(index,node,k,r);
        return r;
    }
    

	
	/**
	 * Forms the JChord ring by making appropriate join and stabilise calls.
	 * 
	 * @param randomiseJoinPosition true if nodes should be joined at random positions in the ring (takes longer)
	 * @throws Exception
	 */	
	public void formRing(boolean randomiseJoinPosition) throws Exception {

        if (nodes.length > 0) {
            
            if (randomiseJoinPosition) {
                
                int count = 0; //for progress reporting

                ((IJChordNode)nodes[0]).createRing();
                ((IJChordNode)nodes[0]).stabilise();
                
                Random rand = new Random(RANDOM_SEED);

                for (int i = 1; i < nodes.length; i++) {
                    
                    // Select a random member of the ring.
                    int random_node_index = rand.nextInt(i);
                    
                    ((IJChordNode)nodes[i]).join((IJChordNode)nodes[random_node_index]);
                    
                    ((IJChordNode)nodes[i]).stabilise();
                    ((IJChordNode)nodes[random_node_index]).stabilise();

                    showProgress(count++, progress_granularity, linebreak_granularity, Diagnostic.RUN);
                }
            }
            else {
                
                ArrayList nodelist = new ArrayList(nodes_in_key_order);

                int length = nodelist.size();

                int startingIndex = length - 1;
                int count = 0; //for progress reporting

                IJChordNode highestNode = (IJChordNode) nodelist.get(startingIndex);
                highestNode.createRing();
                highestNode.stabilise();

                IJChordNode next = null;
                startingIndex--;

                for (int i = startingIndex; i >= 0; i--) {

                    next = (IJChordNode) nodelist.get(i);
                    next.join(highestNode);
                    next.stabilise();
                    highestNode.stabilise();

                    showProgress(count++, progress_granularity, linebreak_granularity, Diagnostic.RUN);
                }
            }
        }
    }

	/**
	 * Stabilises all the nodes in the ring.
	 */
	public void stabiliseAll() {
	    
	    stabiliseAllAndReport();
	}

    /**
     * Continues stabilising all nodes in the ring until no more changes to successor and predecessor pointers are made.
     */
    public void stabiliseUntilQuiescent() {
        
        while (stabiliseAllAndReport());
    }
    
    private boolean stabiliseAllAndReport() {
        
	    ArrayList nodelist = new ArrayList(nodes_in_key_order);
	    boolean changed = false;
	    
	    try {
            for (int i = nodelist.size() - 1; i >= 0; i--) {

                IJChordNode node = ((IJChordNode) nodelist.get(i));
                
                if (!node.hasFailed()) {

                    IJChordRemote previous_predecessor = node.getPredecessor();
                    IJChordRemote previous_successor = node.getSuccessor();
                    
                    node.stabilise();
                    
                    if (node.getPredecessor() != previous_predecessor || node.getSuccessor() != previous_successor)
                        changed = true;
                }
            }
        } catch (Exception e) {
            Error.exceptionError("JChordNode.getPredecessor or JChordNode.getSuccessor threw exception", e);
        }
	    
	    return changed;
    }

	/**
	 *  Checks that each node in the ring is stable
	 */
	public boolean ringIsStable() {

	    for (int i = 0; i < nodes.length; i++)
	        if (!nodes[i].hasFailed() && !checkNodeStable((IJChordNode)nodes[i])) return false;
 
	    return true;
	}
	
    public boolean checkNodeStable(IJChordRemote node) {
        
        return (checkSuccessorStable(node) && checkPredecessorStable(node));
    }

    private boolean checkSuccessorStable(IJChordRemote node) {

        try {
            IJChordRemote successor = node.getSuccessor();
            
            return ((successor != null) && successor.getPredecessor() == node);
        }
            
        catch (Exception e) {
            
            Diagnostic.trace("error calling getPredecessor on successor", Diagnostic.RUN);
            return false;
        }
    }

    private boolean checkPredecessorStable(IJChordRemote node) {

        try {
            IJChordRemote predecessor = node.getPredecessor();
            
            return ((predecessor != null) && predecessor.getSuccessor() == node); }

        catch (Exception e) {
            
            Error.exceptionError("error calling getSuccessor on predecessor", e);
            return false;
        }
    }

	protected void getNeighbours(int index) {

	    Assert.assertion( neighbourhood_size <= nodes.length, "JCSim::getNeighbours illegal number of neighbours specified" );
	    
	    int start = index - neighbourhood_size % 2;			//start point in array
	    if( start < 0 ) { 					// have run over start of array
	        start = nodes.length + start;	// fix start point up
	    }
	    int localindex = start;
	    for( int i = 0; i < neighbourhood_size; i++ ) {
	        if( localindex >= nodes.length ) { // have run off end of array
	            localindex = 0;
	        }
	        if( localindex == index ) { // dont want to do the original node
	            localindex++; 			// skip the original
	            if (localindex >= nodes.length) localindex = 0;
	        }
	        neighbours[i] = nodes[localindex++];
	    }
	}
	
	/**
	 * Adds neighbour knowledge to each node in the simulation.
	 */
	public void associateNeighbours() {
	    IJChordNode[] neighbours = new IJChordNode[neighbourhood_size];
		
	    int count = 0;
		for (int i = 0; i < nodes.length; i++) {
		    
		    if (!nodes[i].hasFailed()) {
		        
		        Assert.assertion( neighbourhood_size <= nodes.length, "JCSim::getNeighbours illegal number of neighbours specified" );
			    
			    int start = i - neighbourhood_size % 2;			//start point in array
			    if( start < 0 ) { 					// have run over start of array
			        start = nodes.length + start;	// fix start point up
			    }
			    int localindex = start;
			    for( int j = 0; j < neighbourhood_size; j++ ) {
			        if( localindex >= nodes.length ) { // have run off end of array
			            localindex = 0;
			        }
			        if( localindex == i ) { // dont want to do the original node
			            localindex++; 			// skip the original
			            if (localindex >= nodes.length) localindex = 0;
			        }
			        neighbours[j] = (IJChordNode)nodes[localindex++];
			    }
		        
		        ((INeighbourAwareJChordNode) nodes[i]).addNeighbours( neighbours );
		        
		        showProgress(count++, progress_granularity, linebreak_granularity, Diagnostic.RUN);
		    }
		}
	}
	
	public void populateFingerTables() {
	    
	    ArrayList node_list = new ArrayList(nodes_in_key_order); //ArrayList in key-order
	    int count = 0;
  
	    int startingIndex = node_list.size() - 1;
	    
	    SimProgressWindow p=null;
	    if(showProgress){
			p = new SimProgressWindow("Populating Finger Tables",1,node_count);
		}
	    
		for (int i = startingIndex; i >= 0; i--) {
		  
		    IJChordNode node = (IJChordNode)node_list.get(i);
		    
		    if (!node.hasFailed()) {
		        
		        IFingerTable jcft = node.getFingerTable();
		        jcft.fixAllFingers();
		        
		        // TODO obsolete code?
		        //			Key k=null;
		        //			try {
		        //				k = jcn.getKey();
		        //			} catch (Exception e) { e.printStackTrace(); }
		        
		        // Disabled because of expensive string concatenation
		        //			Diagnostic.trace("JCSim::populateFingerTable",
		        //					"Finished populating finger table for node ["
		        //					+key2Index(k)+"]"+k
		        //					,Diagnostic.INIT);
		        
		        showProgress(count++, progress_granularity, linebreak_granularity, Diagnostic.RUN);
		        
		        if(showProgress){
		        	p.incrementProgress();
		        }
		    }
		}
		
		if(showProgress){
			p.dispose();
		}
	}

	public void showNode( int node_num ) {    
	    IJChordNode node = (IJChordNode)nodes[node_num];
	   
	    System.out.println( "Showing node[" + node_num +"]" );
	    if (node.hasFailed()) System.out.println("node simulating failure");
	    try {
			System.out.println( "\tkey = " + node.getKey() );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	    try {
	        System.out.println( "\tpredecessor = " + node.getPredecessor().getKey() );
	    } catch( Exception e ) {
	        System.out.println( "\tError looking up pred key" );
	    }
	    try {
	        System.out.println( "\tsuccessor = " + node.getSuccessor().getKey() );	
	    } catch( Exception e ) {
	        System.out.println( "\tError looking up succ key" );
	    }
	    try {
			System.out.println( "\tport = " + node.getHostAddress().getPort());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	    System.out.println( "\tfinger table:"+node.getFingerTable().toString_compact());
	}
	
    public void runAll() {
        
        for (int i = 0; i < nodes.length; i++) {
            
            final IJChordNode node = (IJChordNode)nodes[i];	// TODO could be typed as P2P node and put in superclass if below is true
            
            new Thread() {
                public void run() {
                    node.run();			// TODO this means that P2PNode should have run?????
                }
            }.start();
        }
    }
}
