/*
 * Created on 24-Feb-2005 at 16:17:10.
 */
package uk.ac.stand.dcs.asa.can.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.util.Assert;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.KeyImpl;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author al
 *
 * Representation of a coordinate for use in a CAN network
 */
public class Coord {
    private BigInteger coord[];
    private int bitsPerDimension;
    private BigInteger maxValue;

    /**
     * Instantiates a new coordinate of appropriate dimensions set to (0,0) or (0,0,0) etc.
     * 
     * @param dimensions - the dimensions of the coordinate
     * @param bitsPerDimension
     */    
    public Coord(int dimensions, int bitsPerDimension) {
    	
        this.bitsPerDimension = bitsPerDimension;
        this.coord = new BigInteger[dimensions]; 
        maxValue = maxValue(bitsPerDimension);
        Arrays.fill( coord, new BigInteger("0") );
    }
    
    /**
     * @param dimensions - the dimensions of the coordinate
     * @param bitsPerDimension
     * @param k - a key used to initialise the coordinate
     * Instantiates a new coordinate of appropriate dimensions set to the key value suitably split up
     */
    public Coord(int dimensions, int bitsPerDimension, IKey k) {
        this(dimensions,bitsPerDimension);
        fillKey(bitsPerDimension,k);
        Assert.assertion( k.equals( coordToKey(this) ),"Coordinate construction failure: " + this + " original key = " + k.toString() + "\ncoordtoKey = " + coordToKey(this) );
    }
    
    /**
     * @param dimensions - the dimensions of the coordinate
     * @param bitsPerDimension
     * @param l - a list of BigIntegers used to initialise the coordinate
     * 
     */   
    public Coord(int dimensions, int bitsPerDimension, List l) {
        this(dimensions,bitsPerDimension);
        fillList(bitsPerDimension,l);   
    }
    
    public String toString() {
        String result = "[ ";
        for( int i = 0 ; i < coord.length; i++ ) {
            result = result + coord[i].toString(16);
            if( i != coord.length - 1 ) {
                result = result + ",";
            }
        }
        result = result + " ]";
        return result;
    }

    
    private void fillList(int bitsPerDimension, List l) {
        Iterator iter = l.iterator();
        // build a new coordinate from a list of BigIntegers
        // lock step march through the list and the dimensions.
        for( int i = 0 ; i < coord.length; i++ ) {
            Object next = iter.next();
            if( ! ( next instanceof BigInteger ) ) {
               Error.hardError( "Encountered illegal element in coord initialisation" );
            }
            BigInteger bi = (BigInteger) next;
            if( bi.compareTo( maxValue ) > 0 ) { 
                Error.hardError( "Element in coord initialisation larger than max allowed" );
            }
            coord[i] = bi;       
        }
        if( iter.hasNext() ) {
            Error.error( "List passed to coord initialisation has extra elements" );
        }     
    }

    private void fillKey(int bitsPerDimension, IKey k ) {
        // now split up the key into maxDimension sized chunks
       // BigInteger bi = k.bigIntegerRepresentation();
        String stringRep = k.toString();		// zero padded base 16 representation
        int chunkSize = maxValue.toString(16).length();

        // load it up into the coord array
        for( int i = 0 ; i < coord.length; i++ ) {
            //BigInteger[] split = bi.divideAndRemainder( oneBiggerThanMax );
           // coord[coord.length - 1 - i ] = split[1];	// fill in array backwards (least significant first using the remainder
           // bi = split[0];
           coord[i] = new BigInteger( stringRep.substring(0,chunkSize),16 ); // convert from String to BigInteger radix=16.
           stringRep = stringRep.substring(chunkSize);
        }        
    }
    
    public BigInteger getMaxValue() {
        return maxValue;
    }
    
    public BigInteger getDimension( int n ) {
        return coord[n];
    }
    
    /**
     * @return Calculates and returns the maxValue.
     */
    public static BigInteger maxValue(int bitsPerDimension) {
        String maxbitrep = "";
        for( int i = 0; i < bitsPerDimension; i++) {
            maxbitrep = maxbitrep + "1";
        }
        return new BigInteger( maxbitrep,2 );
    }
    
    /**
     * Generates a Key from a Coord
     * @param convert_this - a coord to be converted
     * @return a Key constructed from the coordinate
     */
    public static IKey coordToKey( Coord convert_this ) {
        BigInteger[] rep = convert_this.coord;
        String keyrep = "";
        for( int i = 0 ; i < rep.length; i++ ) {
            BigInteger bi = rep[i];
            String bi_StringRep = bi.toString(16);	// Hexidecimal representation of the dimesion
            // pad the String rep of this dimension to the appropriate String length
            int expectedHexDimensionLength = convert_this.bitsPerDimension / 4;	// 4 bits = 1 hex character
            while( bi_StringRep.length() < expectedHexDimensionLength ) { bi_StringRep = "0" + bi_StringRep; }
            keyrep = keyrep + bi_StringRep;           
        }     
        // TODO al - this should be in a factory method passed in somewhere!
		BigInteger bi = new BigInteger(keyrep, 16);  // Convert to decimal.
		return new KeyImpl(bi);
    }
}
