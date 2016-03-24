/*
 * Created on Jan 20, 2005 at 9:06:25 AM.
 */
package uk.ac.stand.dcs.asa.simulation.simulationFactories;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.plaxton.factories.Base16PlaxtonNodeFactory;
import uk.ac.stand.dcs.asa.plaxton.factories.ClosestReplacementPolicyFactory;
import uk.ac.stand.dcs.asa.plaxton.simulation.PlaxtonSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.util.SimulatedPhysicalDistanceModel;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class ClosestReplacementPolicyPlaxtonBase16SimulationFactory extends AbstractSimulationFactory {


    public P2PSim makeSimulation(int num_nodes) {

        IDistanceCalculator dc=new SimulatedPhysicalDistanceModel(num_nodes);
        ClosestReplacementPolicyFactory rpf = new ClosestReplacementPolicyFactory(dc);
        Base16PlaxtonNodeFactory pnf = new  Base16PlaxtonNodeFactory(dc,rpf);
        P2PSim sim = new PlaxtonSim( num_nodes, pnf );
		sim.initialiseP2PLinks();
		return sim;
    }

}
