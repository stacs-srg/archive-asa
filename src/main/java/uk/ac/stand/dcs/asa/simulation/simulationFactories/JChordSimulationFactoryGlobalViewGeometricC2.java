/*
 * Created on Jan 20, 2005 at 9:06:25 AM.
 */
package uk.ac.stand.dcs.asa.simulation.simulationFactories;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.interfaces.INodeFactory;
import uk.ac.stand.dcs.asa.jchord.fingerTableFactories.GlobalViewGeometricFingerTableFactory;
import uk.ac.stand.dcs.asa.jchord.interfaces.IFingerTableFactory;
import uk.ac.stand.dcs.asa.jchord.nodeFactories.JChordNodeFactory;
import uk.ac.stand.dcs.asa.jchord.simulation.JCSim;
import uk.ac.stand.dcs.asa.jchord.simulation.P2PSimulationProxy;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.util.SimulatedPhysicalDistanceModel;

/**
 * @author al/stuart with arbitrage by Graham :)
 *
* Generate a simulation using JChord and a shortest distance 
 * finger table with a search distance of 0 (SDFingerTable).
 */
public class JChordSimulationFactoryGlobalViewGeometricC2 extends AbstractSimulationFactory {

    public P2PSim makeSimulation(int num_nodes) {
    	IDistanceCalculator dc=new SimulatedPhysicalDistanceModel(num_nodes);
		P2PSimulationProxy psp=new P2PSimulationProxy();
    	IFingerTableFactory ftf=new GlobalViewGeometricFingerTableFactory(2.0,dc,psp);
		INodeFactory nf = new JChordNodeFactory(ftf);
        P2PSim sim = new JCSim(num_nodes,0,nf,showProgress);
        psp.setSim(sim);
		sim.initialiseP2PLinks();
		return sim;
    }
}
