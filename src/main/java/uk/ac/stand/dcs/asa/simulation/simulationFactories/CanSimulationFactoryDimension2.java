/*
 * Created on March 29 2005 at coffee time
 */
package uk.ac.stand.dcs.asa.simulation.simulationFactories;

import uk.ac.stand.dcs.asa.can.factories.CanNodeFactory;
import uk.ac.stand.dcs.asa.can.simulation.CanSim;
import uk.ac.stand.dcs.asa.interfaces.INodeFactory;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;

/**
 * @author al
 *
 * Generate a simulation using CAN.
 */
public class CanSimulationFactoryDimension2 implements SimulationFactory {

	public static int DIMENSIONS = 2;
	public static int BITSPERDIMENSION = 80; 
	
	public P2PSim makeSimulation(int num_nodes) {
		
		INodeFactory nf = new CanNodeFactory(DIMENSIONS,BITSPERDIMENSION);
		P2PSim sim = new CanSim(num_nodes,nf);
		sim.initialiseP2PLinks();
		return sim;
	}

	public void showProgress() {
		// No action.
	}
}
