/*
 * Created on Dec 15, 2004 at 9:59:25 PM.
 */
package uk.ac.stand.dcs.asa.util.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.util.KeyImpl;
import uk.ac.stand.dcs.asa.util.SegmentArithmetic;

import java.math.BigInteger;

/**
 * Test class for SegmentArithmetic.
 *
 * @author graham
 */
public class SegmentArithmeticTest extends TestCase {

    private static KeyImpl k1 = new KeyImpl(BigInteger.ZERO);
    private static KeyImpl k2 = new KeyImpl(BigInteger.ONE);
    private static KeyImpl k3 = new KeyImpl(new BigInteger("3247823487234"));
    private static KeyImpl k4 = new KeyImpl(SegmentArithmetic.KEYSPACE_SIZE.subtract(BigInteger.ONE));

    /**
     * Tests whether segment membership works as expected.
     */
    public void testInHalfOpenSegment() {

        assertTrue(SegmentArithmetic.inHalfOpenSegment(k2, k1, k3));
        assertTrue(SegmentArithmetic.inHalfOpenSegment(k3, k1, k4));
        assertTrue(SegmentArithmetic.inHalfOpenSegment(k3, k1, k3));
        
        assertFalse(SegmentArithmetic.inHalfOpenSegment(k2, k3, k1));
        assertFalse(SegmentArithmetic.inHalfOpenSegment(k3, k4, k1));
        assertFalse(SegmentArithmetic.inHalfOpenSegment(k3, k3, k1));

        assertFalse(SegmentArithmetic.inHalfOpenSegment(k1, k1, k2));
        assertTrue(SegmentArithmetic.inHalfOpenSegment(k2, k1, k2));
    }

    /**
     * Tests whether segment membership works as expected.
     */
    public void testInOpenSegment() {

        assertTrue(SegmentArithmetic.inOpenSegment(k2, k1, k3));
        assertTrue(SegmentArithmetic.inOpenSegment(k3, k1, k4));
        assertFalse(SegmentArithmetic.inOpenSegment(k3, k1, k3));
        
        assertFalse(SegmentArithmetic.inOpenSegment(k2, k3, k1));
        assertFalse(SegmentArithmetic.inOpenSegment(k3, k4, k1));
        assertFalse(SegmentArithmetic.inOpenSegment(k3, k3, k1));

        assertFalse(SegmentArithmetic.inOpenSegment(k1, k1, k2));
        assertFalse(SegmentArithmetic.inOpenSegment(k2, k1, k2));
    }
}
