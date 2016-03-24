
package uk.ac.stand.dcs.asa.jchord.fingerTableFactories;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.jchord.impl.DecimalGeometricSegments;
import uk.ac.stand.dcs.asa.jchord.impl.SegmentRangeCalculator;
import uk.ac.stand.dcs.asa.jchord.interfaces.IFingerTable;
import uk.ac.stand.dcs.asa.jchord.interfaces.IFingerTableFactory;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.simulation.GlobalViewGeometricFingerTable;
import uk.ac.stand.dcs.asa.jchord.simulation.P2PSimulationProxy;

/**
 * @author stuart
 */
public class GlobalViewGeometricFingerTableFactory implements IFingerTableFactory {

	P2PSimulationProxy psp;
	IDistanceCalculator dc;
	double decimalConstant;
	
	public GlobalViewGeometricFingerTableFactory(double decimalConstant, IDistanceCalculator dc, P2PSimulationProxy psp){
		this.dc=dc;
		this.decimalConstant=decimalConstant;
		this.psp=psp;
	}
		
	public IFingerTable makeFingerTable(IJChordNode localNode) {
		SegmentRangeCalculator src = new DecimalGeometricSegments(localNode,decimalConstant);
		return new GlobalViewGeometricFingerTable(localNode,src,dc,psp);
	}

}
