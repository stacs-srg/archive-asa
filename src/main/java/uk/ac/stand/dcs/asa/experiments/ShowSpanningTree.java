/*
 * Created on 22-Dec-2004
 */
package uk.ac.stand.dcs.asa.experiments;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.simulation.interfaces.SpanTreeGen;
import uk.ac.stand.dcs.asa.simulation.util.GraphGen;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/**
 * @author stuart
 * 
 * This class outputs a spanning tree for a specified node in dot notation
 * suitable for rendering by the graphviz Unix tool.
 * 
 * Command line parameters are:
 * 1. Class name of the simulation factory
 * 2. Number of nodes in the simulation
 * 3. Node number to be examined.
 * 
 */
public class ShowSpanningTree {
    
    public static void main(String[] args) {
        
        if( args.length < 2 ) {
            Error.hardError("Usage: SimulationFactoryName nodecount nodenum");
        }
        
        String FactoryName = args[0];
        String node_count_string = args[1];
        int num_nodes = 0;
        try {
            num_nodes = Integer.parseInt(node_count_string);
        }
        catch ( NumberFormatException e) {
            Error.hardError("Illegal parameter: " + node_count_string + "must be an integer" );
        }
        
        String node_number_string = args[2];
        int nodeNumber = 0;
        try {
            nodeNumber = Integer.parseInt(node_number_string);
        }
        catch ( NumberFormatException e) {
            Error.hardError("Illegal parameter: " + node_number_string + "must be an integer" );
        }
        
        if (nodeNumber < 0 || nodeNumber >= num_nodes) {
            Error.hardError("Illegal parameter: " + node_number_string + "must be > 0 and < num_nodes" );
        }
        
        Diagnostic.setLevel(Diagnostic.FINAL);
        
        SimulationFactory sf = null;
        try {
            sf = ((SimulationFactory)Class.forName( FactoryName ).newInstance());
        } catch (Exception e) {
            Error.exceptionError("Cannot instantiate simulation factory", e);
        }
        P2PSim sim = sf.makeSimulation(num_nodes);        
        
        SpanTreeGen stg = sim.getSpanTreeGen();
        
        IKey k = null;
        try {
            k = sim.getNodes()[nodeNumber].getKey();
        } catch (Exception e) {
            Error.exceptionError("Cannot get key for specified node", e);
        }
        
        DefaultMutableTreeNode tree = stg.allPathsToNode(k);
        Enumeration e = tree.depthFirstEnumeration();
        int count = 0;
        while (e.hasMoreElements()) {
            e.nextElement();
            count++;
        }

        GraphGen gg = new GraphGen(sim);
        gg.show_dot_file(tree, "JCSimShowSpanningTree: " + Integer.toString(num_nodes) + " nodes");
    }
}
