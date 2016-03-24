/*
 * Created on 03-Dec-2004
 */
package uk.ac.stand.dcs.asa.simulation;

import uk.ac.stand.dcs.asa.impl.Route;
import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.INodeFactory;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SpanTreeGen;
import uk.ac.stand.dcs.asa.simulation.util.SimProgressWindow;
import uk.ac.stand.dcs.asa.simulation.util.SimulatedPhysicalDistanceModel;
import uk.ac.stand.dcs.asa.simulation.util.TopDownSpanTreeGen;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;


/**
 * @author stuart, al, graham
 * A generic Simulation framework from which specific tests can be constructed.
 * Not specific to any node kind.
 */
public abstract class SimulationFramework implements P2PSim {

    /******************* Protected Simulation constants *******************/
    
    protected static final int DEFAULT_NODE_COUNT = 1000;
    protected static final int DEFAULT_NEIGHBOURHOOD_SIZE = 10;
    protected static final int DEFAULT_PROGRESS_GRANULARITY = 1000;
    protected static final int DEFAULT_LINEBREAK_GRANULARITY = 80000;
    
    protected static final long RANDOM_SEED = 4837275;   
	
    /******************* Protected State *******************/

    protected int progress_granularity = DEFAULT_PROGRESS_GRANULARITY;
    protected int linebreak_granularity = DEFAULT_LINEBREAK_GRANULARITY;
    protected int seed = 43523897;
	
    protected boolean showProgress; //should a progress bar be displayed
    
	protected int node_count;				// Number of nodes in simulation
	protected INodeFactory nfactory;		// Factory for constructing nodes
	protected Random rand;				// The random number generator used in simulations
	protected IP2PNode[] nodes;			// Holds P2P nodes - use distance in this array as real life distance metric
	protected TreeSet nodes_in_key_order; // stores nodes in key order
	protected HashMap key_to_index; 		//setup hash map from key to index in node array - used to calc distance.
	protected IP2PNode[] neighbours; 		// they made me do this - al
	protected IDistanceCalculator dc;
	protected SpanTreeGen spanTreeGen;
	
    /******************* Constructors *******************/
    
	public SimulationFramework(int node_count, INodeFactory node_factory, boolean showProgress) {
	    
	    rand = new Random( seed );
	    
	    this.node_count = node_count;
	    this.dc = new SimulatedPhysicalDistanceModel(node_count);
	    this.nfactory = node_factory;    
	    this.showProgress=showProgress;
	    if (node_factory == null) Error.error("node factory was null");
	    
	    try {	        
	        nodes = new IP2PNode[node_count]; 		// Holds P2PNodes
	        
	        nodes_in_key_order = new TreeSet(); 	// use this to store nodes in key order
	        key_to_index = new HashMap(); 			//setup hash map from key to index in node array 
	        
	        SimProgressWindow p=null;
	        if(showProgress){
	        	p = new SimProgressWindow("Creating nodes",1,node_count);
	        }
	        
	        for (int i = 0; i < nodes.length; i++) {
	            
	            String ip = int2IPString(i);
	            IKey k = SHA1KeyFactory.generateKey(ip + i);
	            IP2PNode newNode = node_factory.makeNode(new InetSocketAddress(ip, i),k);
	            
	            nodes[i] = newNode;
	            nodes_in_key_order.add(newNode); 		//add node to the treeset to get them in key order (smallest key first)
	            key_to_index.put(k, new Integer(i)); 	//add mapping from key to array index for this node
	            
	            showProgress(i, progress_granularity, linebreak_granularity, Diagnostic.RUN );
	            
	            if(showProgress){
	            	p.incrementProgress();
	            }
	        }
	        
	        if(showProgress){
	        	p.dispose();
	        }
	        spanTreeGen = new TopDownSpanTreeGen(this);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}  
    
    /******************* Selectors *******************/
  
    /**
     * @return returns the node_count
     */
    public int getNodeCount() {
        return node_count;
    }
    
    /**
     * @return Returns the hm.
     */
    public HashMap getKey_to_index() {
        return key_to_index;
    }
    
    /**
     * @return Returns the nodes.
     */
    public IP2PNode[] getNodes() {
        return nodes;
    }

    /**
     * @return returns the nodes_in_key_order
     */
    public TreeSet getNodesInKeyOrder() {
        return nodes_in_key_order;
    }
    
    /**
     * @return Returns the rand.
     */
    public Random getRand() {
        return rand;
    }

    /* (non-Javadoc)
     * @see uk.ac.stand.dcs.asa.simulation.P2PSim#getDistanceCalculator()
     */
    public IDistanceCalculator getDistanceCalculator() {
        return dc;
    }

	public SpanTreeGen getSpanTreeGen() {
		return spanTreeGen;
	}
    /******************* Utility Methods *******************/

    protected static void showProgress(int progressCount, int granularity, int lineBreak, Diagnostic level ){
		if(progressCount>0){
			if(progressCount>0 && progressCount % granularity==0) Diagnostic.traceNoSourceNoLn(".",level);
			if(progressCount>0 && progressCount % lineBreak==0) Diagnostic.traceNoSource( progressCount + " ", level);
		}
	}
		
	/**
	 * @param k
	 * @return Returns the index in the node array of the node with key k. If 
	 * no such node exists, -1 is returned.
	 */
	public int key2Index(IKey k) {
		int result = -1;
		Integer index=(Integer)this.key_to_index.get(k);
		if(index!=null)result=index.intValue();
		return result;
	}

	public static String int2IPString(int ip) { // TODO is this used now we have DC?
		StringBuffer buf = new StringBuffer();
	    buf.append((ip >> 24) & 0xff);
	    buf.append('.');
	    buf.append((ip >> 16) & 0xff);
	    buf.append('.');
	    buf.append((ip >>  8) & 0xff);
	    buf.append('.');
	    buf.append( ip  & 0xff);
	    
	    return new String(buf);
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
            }
            catch (Exception e) { Error.exceptionError("error getting representation of node", e); }
        }
    }
    
    public String toString(){
        return "Simulation nodes: "+node_count;
    }
	
    public void showAll() {
        for (int i = 0; i < nodes.length; i++) showNode(i);
    }

    /******************* Abstract Methods *******************/
    
	public abstract void showNode(int index);
	
    public abstract void runAll();
}
