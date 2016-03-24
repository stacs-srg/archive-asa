/*
 * Created on 19-Jan-2005
 *
 */
package uk.ac.stand.dcs.asa.experiments;

import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.Output;
import uk.ac.stand.dcs.asa.util.StatsCalculator;

/**
 * @author stuart
 * 
 * This class displays the mean routing state size for all nodes in the simulation.
 * 
 * Command line parameters are:
 * 1. Class name of the simulation factory
 * 2. Number of nodes in the simulation
 */
public class ShowAveragePeerStateSize {

	private static final String SHOW_PROGRESS_STR = "progress";

	public static void main(String[] args) {
	    if( args.length <3 ) {
	        Error.hardError("Usage: <SimulationFactoryName : Strint> <nodecount : int> "+SHOW_PROGRESS_STR);
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
		    
			Diagnostic.setLevel(Diagnostic.NONE);

			Output ef = new Output();
			SimulationFactory sf=null;
		  
            try {
                sf = ((SimulationFactory)Class.forName( FactoryName ).newInstance());
               
            } catch (Exception e) {
                Error.exceptionError("Cannot instatiate simulation factory", e);
            }  
            if(sf!=null){
            	ef.printlnSeparated("Nodes", "Average Peer State Size");
            	if(args.length==3 && args[2].compareTo(SHOW_PROGRESS_STR)==0);
            	sf.showProgress();
            	P2PSim sim = sf.makeSimulation(num_nodes);
            	int stateSizes[] = new int[num_nodes];
            	for(int i=0;i<num_nodes;i++){
            		stateSizes[i]=sim.getNodes()[i].routingStateSize();
            	}
            	ef.printlnSeparated(num_nodes, StatsCalculator.mean(stateSizes));
	       }	
	}
}
