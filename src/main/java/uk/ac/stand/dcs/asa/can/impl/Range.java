/*
 * Created on 10-Feb-2005 at 16:33:11.
 */
package uk.ac.stand.dcs.asa.can.impl;

import uk.ac.stand.dcs.asa.util.Diagnostic;

import java.math.BigInteger;

/**
 * @author al
 *
 * This class maintains a sub-range of a can parition in one dimension
 */
public class Range {

    public BigInteger lower;
    public BigInteger upper;

    
    /**
     * @param upper - upper range of a region
     * @param lower - lower index of region
     */
    public Range(BigInteger lower,BigInteger upper) {
        super();
        this.lower = lower;
        this.upper = upper;

    }
    
    /**
     * @return the size of the range of this Range
     */
    public BigInteger range() { return upper.subtract(lower); }
    
    public String toString() {
        return "(" + lower.toString(16) + "-" + upper.toString(16) + ")";
    }
    
    public boolean overlaps( Range other ) {
        Diagnostic.trace( "Comparing" + this + " with " + other, Diagnostic.RUN );
        boolean result = ( lower.compareTo( other.upper ) < 0 && upper.compareTo( other.upper ) > 0 ) ||
        			     ( upper.compareTo( other.lower ) > 0 && lower.compareTo( other.lower ) < 0 ) ||
        			     ( lower.compareTo( other.lower ) >= 0 && upper.compareTo( other.upper ) <= 0 ); 
        Diagnostic.trace( " returning " + result, Diagnostic.RUN );
        return result;
    }
}
