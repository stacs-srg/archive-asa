
package uk.ac.stand.dcs.asa.jchord.fingerTableFactories;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.jchord.impl.DecimalGeometricSegments;
import uk.ac.stand.dcs.asa.jchord.impl.GeometricFingerTable;
import uk.ac.stand.dcs.asa.jchord.impl.SegmentRangeCalculator;
import uk.ac.stand.dcs.asa.jchord.interfaces.IFingerTable;
import uk.ac.stand.dcs.asa.jchord.interfaces.IFingerTableFactory;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;

/**
 * @author stuart
 */
public class GeometricFingerTableFactory implements IFingerTableFactory {

	int searchDistance;
	IDistanceCalculator dc;
	boolean constrained;
	double decimalConstant;
	
	public GeometricFingerTableFactory(double decimalConstant, int searchDistance, IDistanceCalculator dc, boolean constrained){
		this.searchDistance=searchDistance;
		this.dc=dc;
		this.constrained=constrained;
		this.decimalConstant=decimalConstant;
	}
	
	public GeometricFingerTableFactory(){
		this(2.0,0,null,true);
	}
	
	public GeometricFingerTableFactory(int searchDistance){
		this(searchDistance,0,null,true);
	}
	
	public GeometricFingerTableFactory(double searchDistance){
		this(searchDistance,0,null,true);
	}
	
	public IFingerTable makeFingerTable(IJChordNode localNode) {
		SegmentRangeCalculator src = new DecimalGeometricSegments(localNode,decimalConstant);
		return new GeometricFingerTable(localNode,src,dc,searchDistance,constrained);
	}

}
