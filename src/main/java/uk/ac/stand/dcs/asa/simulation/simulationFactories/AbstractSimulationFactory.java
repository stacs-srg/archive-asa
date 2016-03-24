/*
 * Created on 05-Apr-2005
 */
package uk.ac.stand.dcs.asa.simulation.simulationFactories;

import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;

/**
 * @author stuart
 */
public abstract class AbstractSimulationFactory implements SimulationFactory{
	protected static boolean showProgress=false;;
	
	public abstract P2PSim makeSimulation(int num_nodes);
    public void showProgress(){
    	showProgress=true;
    }
}
