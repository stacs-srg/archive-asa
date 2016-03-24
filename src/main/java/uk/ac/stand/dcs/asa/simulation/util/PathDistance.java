package uk.ac.stand.dcs.asa.simulation.util;

import uk.ac.stand.dcs.asa.impl.Route;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

import java.util.Iterator;

/**
 * @author stuart
 */
public class PathDistance extends P2PSimWrapper{
	
    public double total_hops;
    public double total_distance;
    
    /**
     * @param simulation
     */
    public PathDistance(P2PSim simulation) {
        super(simulation);
    }
    
    /**
     * @return the length of the route rt in real distance.
     */
    public double pathlength(Route rt) {
        
        double result = 0.0;
        IP2PNode first = rt.getStart();
        Iterator i = rt.iterator();
        
        while (i.hasNext()) {

            try {
                result += simulation.getDistanceCalculator().distance(first.getHostAddress().getAddress(), ((IP2PNode) i.next()).getHostAddress().getAddress());
            }
            catch (Exception e) { Error.exceptionError("error getting node representation", e); }
        }
        return result;
    }
    
    public void analyseAllPathsToRandomNode() {
    	total_hops = 0;
        total_distance = 0;
        IKey k = SHA1KeyFactory.generateKey(Integer.toString(simulation.getRand().nextInt()));
        for( int i = 0 ; i < simulation.getNodeCount() ; i++ ) {
            Route rt = simulation.makeRoute( nodes[i],k ); // find a route to k from node i
            
            int rt_hops=0;
            double rt_len = 0.0;
            //ignore the route from the target node back to itself [A->B->C->D->A]
            if(rt.hop_count()>0 && rt.lastHop()!=rt.getStart()){
            	rt_hops = rt.hop_count();
            	rt_len = pathlength( rt );
            }
            total_hops += rt_hops;
            total_distance += rt_len;
        }
    }     
}
