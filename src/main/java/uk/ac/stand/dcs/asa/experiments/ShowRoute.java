/*
 * Created on 22-Dec-2004
 */
package uk.ac.stand.dcs.asa.experiments;

import uk.ac.stand.dcs.asa.impl.Route;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

/**
 * @author stuart
 * 
 * Displays a route from from to to
 * 
 * Command line parameters are:
 * 1. Class name of the simulation factory
 * 2. Number of nodes in the simulation
 * 3. starting index to be displayed
 * 4. finishing index of nodes to be displayed
 */
public class ShowRoute {

	  public static void main(String[] args) {
    	
	      if( args.length < 4 ) {
	          Error.hardError("Usage: SimulationFactoryName nodecount from to ");
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
	      
	      String from_string = args[2];
	      int from_node = 0;
	      try {
	          from_node = Integer.parseInt(from_string);
	      }
	      catch ( NumberFormatException e) {
	          Error.hardError("Illegal parameter: " + from_string + "must be an integer" );
	      }
	      
	      String to_string = args[3];
	      int to_node = 0;
	      try {
	          to_node = Integer.parseInt(to_string);
	      }
	      catch ( NumberFormatException e) {
	          Error.hardError("Illegal parameter: " + to_string + "must be an integer" );
	      }
	      
	      Diagnostic.setLevel(Diagnostic.RUN);
	      
	      try {
	          SimulationFactory sf = ((SimulationFactory)Class.forName( FactoryName ).newInstance());
	          P2PSim sim = sf.makeSimulation(num_nodes);        
	          IP2PNode from = sim.getNodes()[from_node];
	          IKey tokey = sim.getNodes()[to_node].getKey();
	          Route rt = sim.makeRoute( from,tokey );
	          sim.showRoute( from,tokey,rt );
	          System.out.println( "ShowRoute finished" );
          } catch (Exception e) {
              Error.exceptionError("Cannot instantiate simulation factory", e);
          }
     }
}
