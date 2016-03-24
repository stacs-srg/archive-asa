/*
 * Created on 10-Feb-2005
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;

/**
 * @author stuart
 */
public interface SegmentRangeCalculator {
	public IKey nextSegmentLowerBound();
	public IKey currentSegmentLowerBound();
	public KeyRange nextSegmentRange();
	public KeyRange currentSegmentRange();
	public int getCurrentSegment();
	public int calculateSegmentNumber(IKey k);
	public int numberOfSegments();
}
