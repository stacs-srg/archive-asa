/*
 * Created on 22-Dec-2004
 */
package uk.ac.stand.dcs.asa.can.impl.viz;

import uk.ac.stand.dcs.asa.can.impl.CanGraphRenderer;
import uk.ac.stand.dcs.asa.can.simulation.CanSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

/**
 * @author stuart, al
 * 
 * This class outputs a visualisation of a Can routing graph in dot notation
 * suitable for rendering by the graphviz Unix tool.
 * 
 * Command line parameters are:
 * 1. Class name of the simulation factory
 * 2.Number of nodes
 * 
 */
public class ShowCanNetwork {
    
    public static void main(String[] args) {
        
        if( args.length < 2 ) {
            Error.hardError("Usage: SimulationFactoryName numNodes");
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
                
        Diagnostic.setLevel(Diagnostic.FINAL);
        
        SimulationFactory sf = null;
        try {
            sf = ((SimulationFactory)Class.forName( FactoryName ).newInstance());
        } catch (Exception e) {
            Error.exceptionError("Cannot instantiate simulation factory", e);
        }
        P2PSim p2psim = sf.makeSimulation(num_nodes);
        if( ! ( p2psim instanceof CanSim ) ) {
            Error.hardError("Cannot simulation factory is not a CanSim - this tool only works with Can Simulations" );
        }
        CanSim sim = (CanSim) sf.makeSimulation(num_nodes);        

        CanGraphRenderer gr = new CanGraphRenderer(sim);
        gr.show_dot_file( "CanSim:"+num_nodes+"nodes");
    }
}
