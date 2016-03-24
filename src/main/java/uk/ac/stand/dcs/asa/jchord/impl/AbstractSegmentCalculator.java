/*
 * Created on 10-Feb-2005
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.util.JChordProperties;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.KeyImpl;

import java.math.BigInteger;
import java.net.InetSocketAddress;


/**
 * @author stuart
 */
public abstract class AbstractSegmentCalculator implements SegmentRangeCalculator{
	protected static final int NO_VIABLE_SEGMENTS = -1;
	
	protected static int keylLength = JChordProperties.getProperties().getKeyLength();
	protected int lastSegmentNumber;
	
	protected IKey localKey;
	protected BigInteger localKeyValue;
	protected IJChordNode localNode;
	protected InetSocketAddress hostAddress;
	protected int currentSegment;
	
	public AbstractSegmentCalculator(IJChordNode localNode){
		this.localNode=localNode;
		this.localKey=localNode.getKey();
		this.currentSegment=NO_VIABLE_SEGMENTS;
		try {
			this.hostAddress=localNode.getHostAddress();
			this.localKeyValue=localKey.bigIntegerRepresentation();
		} catch (Exception e) {
			Error.exceptionError("Could not get NodeRep from local node",e);
		}
	}
	
	protected abstract int calculateLastSegmentNumber();
	protected abstract BigInteger calculateDistance(int exponent);
	
	private boolean validSegmentNumber(int segmentNumber){
		return segmentNumber>=0&&segmentNumber<=lastSegmentNumber;
	}
	
	private IKey lowerBound(int segmentNumber){
		if(validSegmentNumber(segmentNumber)){
			BigInteger distance_round_ring = calculateDistance(segmentNumber);
			IKey lowerBound = new KeyImpl(localKeyValue.add(distance_round_ring));
			return lowerBound;
		}else{
			return null;
		}
	}
	
	private IKey upperBound(int segmentNumber){
		if(validSegmentNumber(segmentNumber)){
			BigInteger distance_round_ring = calculateDistance(segmentNumber+1);
		    distance_round_ring=distance_round_ring.subtract(BigInteger.ONE);
		    IKey upperBound = new KeyImpl(localKeyValue.add(distance_round_ring));
			return upperBound;
		}else{
			return null;
		}
	}
	
	public IKey nextSegmentLowerBound(){
		return segmentLowerBound(nextSegment());
	}
	
	public IKey currentSegmentLowerBound(){
		return segmentLowerBound(currentSegment);
	}
	
	protected IKey segmentLowerBound(int segmentNumber) {
		return lowerBound(segmentNumber);
	}
	
	public KeyRange nextSegmentRange(){
		return segmentRange(nextSegment());
	}
	
	public KeyRange currentSegmentRange(){
		return segmentRange(currentSegment);
	}
	
	public KeyRange segmentRange(int segmentNumber) {
		if(validSegmentNumber(segmentNumber)){
			return new KeyRange(lowerBound(segmentNumber),upperBound(segmentNumber));
		}else{
			return null;
		}
	}
	
	//changes the currentSegment value to the next segment;
	protected abstract int nextSegment();
	
	public int getCurrentSegment(){
		return currentSegment;
	}

}
