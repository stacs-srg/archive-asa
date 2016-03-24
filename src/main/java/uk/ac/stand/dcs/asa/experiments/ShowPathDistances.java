/*
 * Created on Jan 20, 2005 at 3:38:06 PM.

 */
package uk.ac.stand.dcs.asa.experiments;

import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.simulation.util.PathDistance;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

/**
 * @author al
 *
 * This class displays total hops, average hop count,
 * total hop distance and average hop distance for a randomly chosen node.
 * 
 * Command line parameters are:
 * 1. Class name of the simulation factory
 * 2. Number of nodes in the simulation
 * 
 */
public class ShowPathDistances {

	  public static void main(String[] args) {
	      if( args.length <2 ) {
	          Error.hardError("Usage: SimulationFactoryName nodecount");
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
	      
	      try {
	          SimulationFactory sf = ((SimulationFactory)Class.forName( FactoryName ).newInstance());
	          P2PSim sim = sf.makeSimulation(num_nodes);        
	          
	          sim.getSpanTreeGen();
	          
	          PathDistance pd = new PathDistance( sim );
	          pd.analyseAllPathsToRandomNode();
	          System.out.println( "Simulation of " + pd.node_count + " nodes" );
	          System.out.println( "Total hops = " + pd.total_hops + " average hops = " + pd.total_hops / pd.node_count );
	          System.out.println( "Total distance = " + pd.total_distance + " average distance = " + pd.total_distance / pd.node_count );
	      }
	      catch (Exception e) {
	          Error.exceptionError("Cannot instantiate simulation factory", e);
	      }	
	  }
}
