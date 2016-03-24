/*
 * Created on 19-Jan-2005
 */
package uk.ac.stand.dcs.asa.experiments;

import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.simulation.util.AllPathsToAllNodes;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

/**
 * @author stuart
 * 
 * This class displays the mean path distance, and average hop count and the popularity for
 * each spanning tree - that is for all paths to each node individually as opposed to
 * averaging over all paths to all nodes (as is performed by ShowAverageStatsForAllNodes).
 * 
 *
 * Command line parameters are:
 * 1. Class name of the simulation factory
 * 2. Number of nodes in the simulation
 */
public class ShowStatsForAllNodes {

    public static void main(String[] args) {
        
        if (args.length < 2)  Error.hardError("Usage: SimulationFactoryName nodecount");

        String FactoryName = args[0];
        String node_count_string = args[1];
        int num_nodes = 0;
        
        try { num_nodes = Integer.parseInt(node_count_string); }
        catch ( NumberFormatException e) {
            Error.hardError("Illegal parameter: " + node_count_string + "must be an integer" );
        }
        //Diagnostic.setLevel(Diagnostic.FINAL);
        Diagnostic.setLevel(Diagnostic.NONE);
        
        try {
            SimulationFactory sf = ((SimulationFactory)Class.forName( FactoryName ).newInstance());
            P2PSim sim = sf.makeSimulation(num_nodes);
            
            AllPathsToAllNodes aptan = new AllPathsToAllNodes(sim,true);
            
            // TODO DELETEME!!!
//            JChordNodeImpl node78=(JChordNodeImpl)(sim.getNodes()[78]);
//            System.out.println(">>>>>>>>>>>>>>>>>>\nfinger table for node 78 - " + node78.nodeRep.key);
//            System.out.println(node78.getFingerTable().toString_compact());
//            
//            SDFingerTable sdft = (SDFingerTable)node78.getFingerTable();
//            sdft.showIndicesInReverseKeyOrder();
//            
//            System.out.println("<<<<<<<<<<<<<<<<<<");
//            //!!!!!!!!!!!
            
            System.out.println(aptan.getStats());
        } catch (Exception e) {
            Error.exceptionError("Cannot instantiate simulation factory", e);
        }
    }
}
