/*
 * Created on 10-Jan-2005 at 19:20:18.
 */
package uk.ac.stand.dcs.asa.plaxton.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonRemote;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicy;
import uk.ac.stand.dcs.asa.util.*;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.Iterator;

/**
 * @author al
 */
public class RoutingTable {

    /******************** Private Static Fields ********************/
    
	// TODO obsolete?
    // private static int DEFAULTBITSPERDIGIT = 4;
    
    /******************** Private Fields ********************/
    
    private int bitsperdigit; // no bits for each digit of key
    private int rows;
    private int radix;
    private int columns;
    private PlaxtonRemote[][] table;
    private IKey key;
    private ReplacementPolicy rp;
    
    /********************* Constructors *********************/
    
    /**
     *  Creates a plaxton route table for node with Key @param key
     *  Table will have 'columns' colums and keylen / columns rows
     */
    public RoutingTable( IKey key, ReplacementPolicy rp, int radix ) {
        this.key = key;
        this.rp = rp;
        this.radix = radix;
        this.bitsperdigit = RadixMethods.bitsNeededTORepresent(radix);
        // Can only do bit lengths from "1" to digit "z"
        Assert.assertion( bitsperdigit >= 1 && bitsperdigit < 36, "Illegal number of bits per digit specified " );
        this.radix = (int) Math.pow(bitsperdigit,2);
        this.columns = radix;
        rows = key.bitLength() / bitsperdigit;
        Diagnostic.trace("Creating a table with " + rows + " rows and " + columns + " columns" , Diagnostic.INIT );
        table = new PlaxtonRemote[rows][columns];
    }
    
//  temporarily commented  public RoutingTable( Key key, ReplacementPolicy rp  ) {
//        this( key, rp, DEFAULTBITSPERDIGIT );
//    }
//    /*********************** Methods ************************/

    /**
     * @param i
     * @param knownNode
     */
    public void suggestRouteRow(int i, PlaxtonRemote knownNode) {
        // Implement this the stupid way for now - as a sanity check!
        Diagnostic.trace("row = " + i, Diagnostic.INIT );
        Iterator iter = knownNode.getRoutes().rowIterator(i);
        while( iter.hasNext() ) {
            PlaxtonRemote pni = (PlaxtonRemote) iter.next();
            if( pni != null ) { // ArrayIterator doesn't really deal with sparse arrays - that's free software for you
                addNode( pni );
            }
        }
    }
    
    public void addNode( PlaxtonRemote pni ) {
        IKey k;
        try {
            k = pni.getKey();
        } catch (Exception e) {
            Error.error("cannot obtain owner key");
            return;
        }
        if( k.compareTo(key) == 0 ) {
            Diagnostic.trace("attempt to add owner key", Diagnostic.INIT);	
        	return;
        }
        // first determine what row this node should be added..
    	Diagnostic.trace("comparing\n\t\t"+ key + " with\n\t\t" + k , Diagnostic.INIT);	
        int firstDiffIndex = key.baseXPrefixMatch( k,radix ); // find the shared key length between our key and search key
        Diagnostic.trace("first different index = " + firstDiffIndex , Diagnostic.INIT);
        char firstDifferentChar = k.charAtIndexBaseX(firstDiffIndex,radix);
        int charIndex = RadixMethods.baseXCharToInt( firstDifferentChar,radix );  
        Diagnostic.trace("first different char = " + firstDifferentChar + " = " + charIndex + "[DEC]", Diagnostic.INIT);
        if( table[ firstDiffIndex ][ charIndex ] == null ) {
            Diagnostic.trace("added new Route["+ firstDiffIndex + "][" +  charIndex + "]", Diagnostic.INIT);
            table[ firstDiffIndex ][ charIndex ] = pni;      
        } else {
            // do we replace the node we have just encountered
            
            if(rp.replace(table[ firstDiffIndex ][ charIndex ],pni)){
                table[ firstDiffIndex ][ charIndex ]=pni;
                Diagnostic.trace("REPLACEMENT POLICY INDICATES THAT A NODE SHOULD BE REPLACED", Diagnostic.INIT);
            }
        }   
    }
    
    public PlaxtonRemote lookupIndex(int index1, int index2 ) {
    	Diagnostic.trace("indexing with index:" + index1 + " " + index2, Diagnostic.INIT );
        if( index1 >= 0 && index1 < table.length && index2 >= 0 && index2 < table[index1].length ) {
            return table[index1][index2];
        } else {
            Error.hardError("Illegal indices into routing table - " + index1 + " " + index2 );
            return null;
        }
    }
 
    public Iterator iterator() {
        return new RouteIterator();
    }
    
    public Iterator rowIterator( int i ) {
        return new ArrayIterator( table[i] );
    }
    
    /*********************** Utility Methods ************************/
 
    /**
     * @return the routing table
     * INTENDED FOR DEBUGGING ONLY.
     */
    public PlaxtonRemote[][] getTable() {
        return (PlaxtonRemote[][]) table.clone(); // stay paranoid :)
    }
    
    private class RouteIterator implements Iterator {
        
        int row = 0;
        int column = 0;

        public void remove() {
            Error.error("unimplemented" );
        }

        public boolean hasNext() {
            // This is the same code as next which is necessary to accommodate null entries in the table.
            int c = column;
            int r = row;
            Object returnValue = null;
            while( returnValue == null ) {
	            if( r >= table.length ) {	// we have run off the end
	            	return false;
	            }
	            // have a legal row - check not past end of it
	            if( c >= table[r].length ) { // at the end of the row - reset row and column
	                c = 0;
	            	r++;
	            }
	            // may now be on an illegal row
	            if( r >= table.length ) {	// we have run off the end
	            	return false;
	            }            
	            returnValue = table[r][c++];
            }
            return true;
        }

        public Object next() {
            Object returnValue = null;
            while( returnValue == null ) {
	            if( row >= table.length ) {	// we have run off the end
	            	return null;
	            }
	            // have a legal row - check not past end of it
	            if( column >= table[row].length ) { // at the end of the row - reset row and column
	                column = 0;
	            	row++;
	            }
	            // may now be on an illegal row
	            if( row >= table.length ) {	// we have run off the end
	            	return null;
	            }            
	            returnValue = table[row][column++];
            }
            return returnValue;
        }
    }
}
