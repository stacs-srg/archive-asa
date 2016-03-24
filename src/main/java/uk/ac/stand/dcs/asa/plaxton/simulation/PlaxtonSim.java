/*
 * Created on 17-Jan-2005
 * Shamelessly copied from JCSim
 */
package uk.ac.stand.dcs.asa.plaxton.simulation;

import uk.ac.stand.dcs.asa.impl.Route;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.INodeFactory;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.plaxton.impl.NextHopResult;
import uk.ac.stand.dcs.asa.plaxton.impl.NodeViewer;
import uk.ac.stand.dcs.asa.plaxton.impl.PlaxtonNodeImpl;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonNode;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonRemote;
import uk.ac.stand.dcs.asa.simulation.SimulationFramework;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;


/**
 * @author al
 */
public class PlaxtonSim extends SimulationFramework implements P2PSim {

    public PlaxtonSim(int node_count, INodeFactory nfactory, boolean showProgress){
        super(node_count,nfactory, showProgress);
    }
    
    public PlaxtonSim(int node_count, INodeFactory nfactory){
        this(node_count,nfactory, false);
    }
    
	public void initialiseP2PLinks() {
	       if (nodes.length > 0) {
               int count = 0; 									//for progress reporting
               PlaxtonNode firstNode = (PlaxtonNode) nodes[0];

               for (int i = 1; i < nodes.length; i++) {
                   PlaxtonNode next = (PlaxtonNode) nodes[i];
                   next.join(firstNode);
                   showProgress(count++, progress_granularity, linebreak_granularity, Diagnostic.RUN);
               }	           
	       }
	}
	
	public void showNode( int node_num ) {    
	    NodeViewer nv = new NodeViewer((PlaxtonNodeImpl)nodes[node_num]);
	    nv.showNodeInfo(0);
	}
	
    public void runAll() {	// TODO see comments in same method in JCSim...
        
        for (int i = 0; i < nodes.length; i++) {
            
            final PlaxtonNodeImpl node = (PlaxtonNodeImpl)nodes[i];
            
            new Thread() {
                public void run() {
                    node.run();
                }
            }.start();
        }
    }

    /**
     * @see uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim#makeRoute(uk.ac.stand.dcs.asa.interfaces.IP2PNode, uk.ac.stand.dcs.asa.interfaces.IKey)
     */
    public Route makeRoute(IP2PNode node, IKey k) {
        NextHopResult result = null;
        PlaxtonRemote next = (PlaxtonRemote) node;
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
                Error.hardError("Error calling nextHop on closest preceding node");
            }
        }
       // showRoute(index,node,k,r);
        return r;
    }
}
