/*
 * Created on 10-Feb-2005
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.util.Assert;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.SegmentArithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Observable;
import java.util.Observer;

/**
 * @author stuart
 */
public class DecimalGeometricSegments extends AbstractSegmentCalculator implements SegmentRangeCalculator, Observer{
	
	private static final double DEFAULT_DECIMAL_CONSTANT = 2.0;

	private BigDecimal decimalConstant;
	private IKey succKey;
	
	public DecimalGeometricSegments(IJChordNode localNode, double decimalConstant){
		super(localNode);
		((Observable)localNode).addObserver(this);
		if(decimalConstant<=1.0){
			this.decimalConstant=new BigDecimal(DEFAULT_DECIMAL_CONSTANT);
		}else{
			this.decimalConstant=new BigDecimal(decimalConstant);
		}
		lastSegmentNumber=calculateLastSegmentNumber();
	}

	protected int calculateLastSegmentNumber(){
		int max=-1;
		BigInteger distance;
		do{
			distance=calculateDistance(++max);
		}while(distance.bitLength()<keylLength);
		return max;
	}
	
	protected BigInteger calculateDistance(int exponent){
		if(exponent>0){
			BigDecimal value=decimalConstant;
			for(int i=1;i<exponent;i++){
				value=value.multiply(decimalConstant);
			}
			return value.toBigInteger();
		}else{
			return BigInteger.ONE;
		}
	}

	/**
     * We are optimising the finger table such that the first entry is always 
     * further away (in ring distance) than this node's successor.
     */
	protected int nextSegment() {
		int counter=0;
		if(succKey==null)Error.hardErrorNoSource("succKey in DecimalGeometricSegments object was null");
		do{
			currentSegment++;
			counter++;
			if(counter>lastSegmentNumber+1){
				//we've gone through all of the possible segments and
				//have failed to find a key that is further away in 
				//keyspace than this node's successor. Give up.
				currentSegment=NO_VIABLE_SEGMENTS;
				return NO_VIABLE_SEGMENTS;
			}else{
				if(currentSegment>lastSegmentNumber)currentSegment=0;
			}
			
		}while( localKey.ringDistanceTo(succKey).compareTo(localKey.ringDistanceTo(segmentLowerBound(currentSegment)))>0 );
		return currentSegment;
	}

	public void update(Observable arg0, Object arg1) {
		if (arg1 != null && arg1 instanceof Event) {
			Event e = (Event) arg1;
			if (e.getType().equals("SuccessorStateEvent"))
				newSuccessor((IJChordRemote) e.get("oldSuccessor"));
		}
	}
	
    private void newSuccessor(IJChordRemote oldSuccessor) {
        try {
            Assert.assertion(localNode.getSuccessor() != null, "Successor was null");
        } catch (Exception e) {
            Error.exceptionError("localNodeNode.getSuccessor() threw exception", e);
            return;
        }
		
        IKey newSuccessorKey=null;
		try{
			newSuccessorKey = localNode.getSuccessor().getKey();
		}catch(Exception e){
			Error.exceptionError("localNode.getSuccessor() threw exception", e);
		}
		succKey=newSuccessorKey;
		if(succKey==null)Error.hardErrorNoSource("succKey has been set to null in DecimalGeometricSegments object");
	}

	public int calculateSegmentNumber(IKey k) {
		if(k!=null){
			for(int i=lastSegmentNumber;i>=0;i--){
				KeyRange sr = segmentRange(i);
				if(sr==null){Error.hardErrorNoSource("range was null");}
				if(SegmentArithmetic.inClosedSegment(k,sr.getLowerBound(),sr.getUpperBound())){
					//all keys are in [a,a] - so make sure that the lowerBound and the upperBound
					//are not the same key - this occurs when the segment number is 0 and the constant is 2
					//since the range in this case is [k+2^0,k+(2^1)-1] or [k+1,k+1]
					if(sr.getLowerBound().compareTo(sr.getUpperBound())!=0){
						return i;
					}
				}
			}
		}
		return -1;
	}

	public int numberOfSegments() {

		int segmentCounter = -1;
		if (succKey == null) Error.hardErrorNoSource("succKey in DecimalGeometricSegments object was null");
		
		do {
			segmentCounter++;
			if (segmentCounter>lastSegmentNumber) return 0;
		}
		while (localKey.ringDistanceTo(succKey).compareTo(localKey.ringDistanceTo(segmentLowerBound(segmentCounter))) > 0);
		
		return lastSegmentNumber - segmentCounter + 1;
	}
}
