/*
 * Created on Jan 20, 2005 at 3:38:06 PM.

 */
package uk.ac.stand.dcs.asa.experiments;

import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
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
public class InstantiationTest {
	public static void main(String[] args) {
		if (args.length < 2) {
			Error.hardError("Usage: SimulationFactoryName nodecount");
		}
		String FactoryName = args[0];
		String node_count_string = args[1];
		int num_nodes = 0;
		try {
			num_nodes = Integer.parseInt(node_count_string);
		} catch (NumberFormatException e) {
			Error.hardError("Illegal parameter: " + node_count_string
					+ "must be an integer");
		}
		Diagnostic.setLevel(Diagnostic.FINAL);

		try {
			System.out.println("Instantiating simulation factory");
			SimulationFactory sf = ((SimulationFactory) Class.forName(
					FactoryName).newInstance());
			System.out.println("Creating simulation");
			sf.showProgress();
			sf.makeSimulation(num_nodes);
		} catch (Exception e) {
			Error.exceptionError("Cannot instantiate simulation factory", e);
		}
	}
}
