/*
 * Created on 11-Jan-2005
 */
package uk.ac.stand.dcs.asa.simulation.util;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;

/**
 * @author al
 */
public class SingletonSimulatedPhysicalDistanceModel extends SimulatedPhysicalDistanceModel implements IDistanceCalculator {

    private static IDistanceCalculator instance;
	
	public SingletonSimulatedPhysicalDistanceModel(int node_count){
		super(node_count);
		instance = this;
	}
	
    /**
     * @return Returns the instance.
     */
    public static IDistanceCalculator getInstance() {
        return instance;
    }
}
