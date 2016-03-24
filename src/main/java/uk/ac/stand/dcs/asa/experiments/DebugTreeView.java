/*
 * Created on 19-Jan-2005
 *
 */
package uk.ac.stand.dcs.asa.experiments;

import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.plaxton.impl.NodeViewer;
import uk.ac.stand.dcs.asa.plaxton.impl.PlaxtonNodeImpl;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.simulation.util.AllPathsToAllNodes;
import uk.ac.stand.dcs.asa.simulation.util.TreeViewGUI;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author stuart
 * 
 * This class displays the distribution of path lengths in physical distance and
 * hop counts for a specified node.
 * 
 * Command line parameters are: 1. Class name of the simulation factory 2.
 * Number of nodes in the simulation 3. Node number to be examined.
 */
public class DebugTreeView {

    public static void main(String[] args) {
        
        if (args.length < 3) {
            Error.hardError("Usage: SimulationFactoryName nodecount nodenumber");
        }
        
        String FactoryName = args[0];
        String node_count_string = args[1];
        int num_nodes = 0;
        try {
            num_nodes = Integer.parseInt(node_count_string);
        } catch (NumberFormatException e) {
            Error.hardError("Illegal parameter: " + node_count_string + "must be an integer");
        }
        
        String node_number_string = args[2];
        int nodeNumber = 0;
        try {
            nodeNumber = Integer.parseInt(node_number_string);
        } catch (NumberFormatException e) {
            Error.hardError("Illegal parameter: " + node_number_string + "must be an integer");
        }
        
        if (nodeNumber < 0 || nodeNumber >= num_nodes) {
            Error.hardError("Illegal parameter: " + node_number_string + "must be > 0 and < num_nodes");

        }
        Diagnostic.setLevel(Diagnostic.FINAL);

        SimulationFactory sf = null;
        try {
            sf = ((SimulationFactory) Class.forName(FactoryName).newInstance());
        } catch (Exception e) {
            Error.exceptionError("Cannot instantiate simulation factory", e);
        }
        P2PSim sim = sf.makeSimulation(num_nodes);

        AllPathsToAllNodes aptan = new AllPathsToAllNodes(sim, true);

        DefaultMutableTreeNode t = aptan.computePathsToNode(nodeNumber);

        new TreeViewGUI(new DefaultMutableTreeNode[] { t }, "root", "root");
        
        // TODO obsolete?
        //        		Key k = null; // the target key
        //                try {
        //                    k = sim.getNodes()[nodeNumber].getKey();
        //                } catch (Exception e1) {
        //                    Error.exceptionErrorAutoSource("Cannot get target key", e1);
        //                   
        //                }
        //
        //                for( int i = 0; i < sim.getNodeCount(); i++ ) {
        //        		    Route rt = sim.makeRoute(sim.getNodes()[i],k);
        //        		    System.out.println( "Route from " + i + " to " + nodeNumber );
        //        		    sim.showRoute(sim.getNodes()[i],k,rt);
        //        		}
        // displayAll(sim);
        
        IP2PNode source = sim.getNodes()[100];
        NodeViewer nv = new NodeViewer((PlaxtonNodeImpl) source);
        nv.showAll();
    }

    // TODO obsolete?
    //    private static void displayAll(P2PSim sim) {
    //        P2PNode[] nodes = sim.getNodes();
    //        for( int j = 0; j < nodes.length; j++ ) {
    //            P2PNode source = nodes[j];
    //            NodeViewer nv = new NodeViewer((PlaxtonNodeImpl)source);
    //            nv.showAll();
    //        }
    //    }
}
