/*
 * Created on 22-Dec-2004
 * Utility class for debugging.
 * Displays the keys at each index in the simulation.
 */
package uk.ac.stand.dcs.asa.experiments;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.simulation.SimulationFramework;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author stuart
 * 
 * Utility class for debugging.
 * Displays the keys at each index in the simulation.
 * 
 * Command line parameters are:
 * 1. Class name of the simulation factory
 * 2. Number of nodes in the simulation
 * 3. starting index to be displayed
 * 4. finishing index of nodes to be displayed
 */
public class ShowKey2IndexTable {

	  public static void main(String[] args) {
    	
	      if( args.length < 2 ) {
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

	      try {
	          Diagnostic.setLevel(Diagnostic.NONE);
	          SimulationFactory sf = ((SimulationFactory)Class.forName( FactoryName ).newInstance());
	          P2PSim sim = sf.makeSimulation(num_nodes);        
	          HashMap hm = ((SimulationFramework) sim ).getKey_to_index();
	          Set s = hm.entrySet();
	          Iterator i = s.iterator();
	          
	          while( i.hasNext() ) {
	              java.util.Map.Entry e = (java.util.Map.Entry) i.next();
	              IKey k = (IKey) e.getKey();
	              Integer ii = (Integer) e.getValue();
	              int iii = ii.intValue();
	              System.out.println( iii + "\t" + k );
	          }
	          
          } catch (Exception e) {
              Error.exceptionError("Cannot instantiate simulation factory", e);
          }
   }
}
