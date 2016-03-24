/*
 * Created on 08-Dec-2004
 *
 */
package uk.ac.stand.dcs.asa.util;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.util.JChordProperties;

import java.math.BigInteger;

/**
 * Implementation of segment/ring arithmetic.
 * 
 * @author stuart, al, graham
 */
public class SegmentArithmetic {
    
    /******************** Static Fields *********************/
    
    // 'getKeyLength' handles any errors accessing properties.
    public static final int KEY_LENGTH = JChordProperties.getProperties().getKeyLength();
    
    public static final BigInteger TWO = BigInteger.ONE.add(BigInteger.ONE);
    
    public static final BigInteger KEYSPACE_SIZE = TWO.pow(KEY_LENGTH);
    
    /******************** Segment Arithmetic Methods *********************/
    
    /**
     * Tests whether k is in the segment of the ring obtained by moving clockwise from (but not including) 'start'
     * until reaching (and including) 'end'. There are two cases, depending on whether the segment spans the
     * end/beginning of the key space:
     * 
     * if ( start < end ) then ( start < k && k <= end )   // segment does not span end/beginning
     *                    else ( start < k || k <= end )   // segment does span end/beginning
     * 
     * @param k the key to be tested
     * @param start key defining the start of the segment
     * @param end key defining the end of the segment
     * @return true if the test key is in the specified half open segment (including the end key)
     */
    public static boolean inHalfOpenSegment(IKey k, IKey start, IKey end) {
        if (start.compareTo(end) < 0) {
            return start.compareTo(k) < 0 && k.compareTo(end) <= 0;
        }
        else {
            return start.compareTo(k) < 0 || k.compareTo(end) <= 0;
        }
    }

    /**
     * Tests whether k is in the segment of the ring obtained by moving clockwise from (but not including) 'start'
     * until reaching (but not including) 'end'. There are two cases, depending on whether the segment spans the
     * end/beginning of the key space:
     * 
     * if ( start < end ) then ( start < k && k < end )   // segment does not span end/beginning
     *                    else ( start < k || k < end )   // segment does span end/beginning
     * 
     * @param k the key to be tested
     * @param start key defining the start of the segment
     * @param end key defining the end of the segment
     * @return true if the test key is in the specified open segment
     */
    public static boolean inOpenSegment(IKey k, IKey start, IKey end) {
        if (start.compareTo(end) < 0) {
            return start.compareTo(k) < 0 && k.compareTo(end) < 0;
        }
        else {
            return start.compareTo(k) < 0 || k.compareTo(end) < 0;
        }
    }
    
    /**
     * Tests whether k is in the segment of the ring obtained by moving clockwise from (and including) 'start'
     * until reaching (and including) 'end'. There are two cases, depending on whether the segment spans the
     * end/beginning of the key space:
     * 
     * if ( start < end ) then ( start <= k && k <= end )   // segment does not span end/beginning
     *                    else ( start <= k || k <= end )   // segment does span end/beginning
     * 
     * @param k the key to be tested
     * @param start key defining the start of the segment
     * @param end key defining the end of the segment
     * @return true if the test key is in the specified closed segment
     */
    public static boolean inClosedSegment(IKey k, IKey start, IKey end) {
        if (start.compareTo(end) < 0) {
            return start.compareTo(k) <= 0 && k.compareTo(end) <= 0;
        }
        else {
            return start.compareTo(k) <= 0 || k.compareTo(end) <= 0;
        }
    }
}
