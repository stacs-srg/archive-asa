/*
 * Created on 10-Feb-2005
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.util.KeyImpl;
import uk.ac.stand.dcs.asa.util.SegmentArithmetic;

import java.math.BigInteger;

/**
 * An inclusive (includes lowerBound and upperBound values) range of Key values.
 * 
 * @author stuart
 */
public class KeyRange {
	private IKey lowerBound; //inclusive
	private IKey upperBound; //inclusive
	
	/**
	 * @param lowerBound
	 * @param upperBound
	 */
	public KeyRange(IKey lowerBound, IKey upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
    
    public static KeyRange makeClosedRangeFromHalfOpen_includeUpper(IKey lowerBound, IKey upperBound){
        return new KeyRange(new KeyImpl(lowerBound.bigIntegerRepresentation().add(BigInteger.ONE)),upperBound);
    }

    public static KeyRange makeClosedRangeFromHalfOpen_includeLower(IKey lowerBound, IKey upperBound){
        return new KeyRange(lowerBound,new KeyImpl(upperBound.bigIntegerRepresentation().subtract(BigInteger.ONE)));
    }
    
    public static KeyRange makeClosedRangeFromOpen(IKey lowerBound, IKey upperBound){
        return new KeyRange(new KeyImpl(lowerBound.bigIntegerRepresentation().add(BigInteger.ONE)),
                new KeyImpl(upperBound.bigIntegerRepresentation().subtract(BigInteger.ONE)));
    }
    
    public IKey getLowerBound() {
        return lowerBound;
    }

    public IKey getUpperBound() {
        return upperBound;
    }
    
    /** 
     * Tests if the specified Key value lies within the range of Key values represented by
     * this KeyRange object. A Key k is within the range if k>=lowerBound && k<=upperBound.
     * 
     * @param k the Key value to test
     * @return true if the the specified Key value lies within the range of Key values represented by
     * this KeyRange object, otherwise false
     */
    public boolean inRange(IKey k){
        return SegmentArithmetic.inClosedSegment(k,lowerBound,upperBound);
    }
    
    
}
