/*
 * Created on 11-Jan-2005 at 19:12:53.
 */
package uk.ac.stand.dcs.asa.plaxton.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonNode;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonRemote;
import uk.ac.stand.dcs.asa.util.Assert;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author al
 *
 * This class maintains a plaxton leaf set which stores size size/2 entries greater than this node's key
 * and size/2 entries smaller than the key.
 * The leaf set holds the closest nodes in key space to the leaf set owners key.
 */
public class LeafSet {

    /******************** Private Fields ********************/
    
    private PlaxtonNode owner;	// The owner of this leaf set.
    private InetAddress ownerIP;
    private IKey ownerKey;				// the owner's key - just keep this in the leaf set.
    private int halfSize;
    // private int capacity = 0;		// size of the leaf set - obsolete?
    private IKey lowestKey;
    private IKey highestKey;
    // private int entries = 0;      obsolete?
    private TreeSet theSet;
    private int higherEntries;
    private int lowerEntries;

    
    /********************* Constructors *********************/
    
    /**
     *  Creates a Plaxton leaf set for node with Key @param key
     *  Table will contain @param halfSize * 2 etries
     */
    public LeafSet( PlaxtonNode owner, int halfSize ) {
        this.halfSize = halfSize;
        // this.capacity = halfSize * 2;     - obsolete?
        this.theSet = new TreeSet();
        this.owner = owner;
        try {
            this.ownerKey = owner.getKey();
        } catch (Exception e) {
            Error.exceptionError( "Cannot get owners key",e);
        }
        try {
            ownerIP = owner.getHostAddress().getAddress();
        } catch(Exception e) {
            Error.exceptionError( "Cannot get owners IP address",e);
        }
        // entries = 1;      obsolete?
        higherEntries = 0;
        lowerEntries = 0;
        lowestKey = ownerKey;	// use owners key to initialise
        highestKey = ownerKey; 	// use owners key to initialise
    }

    /*********************** Public Methods ************************/

    /**
     * @param k
     * @return a PlaxtonRemote from this leaf set if there is one owning k
     */
    public PlaxtonRemote covered(IKey k) {
        if( covers(k) ) {
            return getClosestInKeySpace(k); 
        }
        return null;
    }

    /**
     * @param k
     * @return a PlaxtonRemote which is closer in key space than the owner.
     * return null if there is no such node.
     */
    public PlaxtonRemote findCloserNode(IKey k) {
        return getClosestInKeySpace(k);
    }

    /**
     * @param leaves another LeafSet containing nodes that may be used to improve this leaf set
     */
    public void addleaves(LeafSet leaves) {
        if( leaves == this ) {
            Error.error(ownerIP + " attempt to add leaf set to itself" );
        }
//        Diagnostic.trace( "LeafSet::addleaves", "owner = " + ownerIP, Diagnostic.INIT );
        
        //was previously:
        //Iterator i = iterator();
        Iterator i = leaves.iterator();
        while( i.hasNext() ) {
            PlaxtonRemote pni = (PlaxtonRemote) i.next();
            addLeaf(pni);
        }        
    }

    
    /**
     * @param suggestedLeaf a PlaxtonRemote which may be used to improve this leaf set
     */
    public void addLeaf(PlaxtonRemote suggestedLeaf) {
        // do we know about the suggested node already - is it in the set or the owner...
    	
    	IKey suggestedLeafKey = null;
        try {
            suggestedLeafKey = suggestedLeaf.getKey();
        } catch (Exception e) {
            Error.error( "cannot obtain remote Key" );
            return;
        }
        if( suggestedLeafKey.compareTo( ownerKey ) == 0 || theSet.contains(suggestedLeaf) ) {
            if( suggestedLeafKey.compareTo( ownerKey ) == 0 )
                Diagnostic.trace("no add - attempt to add owner key", Diagnostic.INIT );
            else
                Diagnostic.trace("no add - key already added", Diagnostic.INIT );
            return; // if it is do nothing - we know about it
        }
        if( suggestedLeafKey.compareTo( ownerKey ) > 0 ) { // is new node bigger than owner?
            // the suggestedKey is bigger than than owner - so look at upper half
            if( higherEntries < halfSize ) { // is the half  filled up?
                if( suggestedLeafKey.compareTo( highestKey ) > 0 ) { // is the node bigger than the biggest in the set
                    highestKey = suggestedLeafKey;
                }
                higherEntries++;
                add(suggestedLeaf, "bigger - not filled");
            }
            else { // half is filled - is the suggestedLeaf better than leaves that we have already
                if( suggestedLeafKey.compareTo( highestKey ) < 0 ) { // this one is smaller than highest (know it is bigger than owner).
                    theSet.remove( theSet.last() ); // remove the highest entry
                    add(suggestedLeaf,"bigger - filled");
                    try {
                        highestKey = ((PlaxtonRemote) theSet.last() ).getKey();
                    } catch (Exception e1) {
                        Error.error( "cannot obtain highest Key" );
                        return; 
                    }
                }
            }
        }
        else {
            // the new node is smaller than the owner (screened for equal already) - look at lower half
            if( lowerEntries < halfSize ) { // is the half  filled up?
                if( suggestedLeafKey.compareTo( lowestKey ) < 0 ) { // is the node smaller than the smallest in the set
                    lowestKey = suggestedLeafKey;
                }
                lowerEntries++;
                add(suggestedLeaf,"smaller - not filled");
            }
            else { // half is filled - is the suggestedLeaf better than leaves that we have already
                if( suggestedLeafKey.compareTo( lowestKey ) > 0 ) { // this one is bigger than smallest (know it is smaller than owner).
                    theSet.remove( theSet.first() ); // remove the lowest entry
                    add(suggestedLeaf,"smaller - filled");
                    try {
                        lowestKey = ((PlaxtonRemote) theSet.first() ).getKey();
                    } catch (Exception e1) {
                        Error.error( "cannot obtain lowest Key" );
                        return; 
                    }
                } 
            }
        }
	}

    /**
     * @return an iterator over this leaf set
     */
    public Iterator iterator() {
        return theSet.iterator();
    }
    
    /*********************** Private Methods ************************/

    /**
     * @param k
     * @return a PlaxtonRemote from this leaf set if there is one owning k
     */
    private boolean covers(IKey k) {
        Assert.assertion( highestKey.compareTo( lowestKey ) >= 0,"Highest and lowest keys in wrong order h:" + highestKey + "  l:" + lowestKey );
        boolean result=k.compareTo( lowestKey ) >= 0 && k.compareTo( highestKey ) <= 0;
//        System.out.println("k="+k+"\nlowest="+lowestKey+"\nhighestKey="+highestKey+"\n+covers(k)="+result);
//        boolean foundLowest=false;
//        boolean foundHighest=false;
//        for(Iterator i=this.theSet.iterator();i.hasNext();){
//            PlaxtonNode current=(PlaxtonNode)i.next();
//            Key currentKey=null;
//            try {
//                currentKey = current.getKey();
//            } catch (Exception e) {
//                Error.errorAutoSource("getKey call on leaf set entry failed");
//                e.printStackTrace();
//            }
//            if(currentKey!=null){
//                if(currentKey.compareTo(lowestKey)==0){
//                    if(foundLowest){
//                        Error.errorAutoSource("duplicate leaf set entries");
//                    }else{
//                        foundLowest=true;
//                    }
//                }
//                if(currentKey.compareTo(highestKey)==0){
//                    if(foundHighest){
//                        Error.errorAutoSource("duplicate leaf set entries");
//                    }else{
//                        foundHighest=true;
//                    }
//                }
//            }
//        }
//        System.out.println("lowestKey is in leafset: "+foundLowest+"\nhighestKey is in leaf set: "+foundHighest);
        return result;
    }

    /**
     * PREREQ: key is in the closed interval associated with this leaf set
     * This should be checked using covers above.
     * @param k - a key which is known to be u
     */
    private PlaxtonRemote getClosestInKeySpace(IKey k) {
        PlaxtonRemote closest = owner;
        Iterator i = iterator(); // could perhaps do this more efficiently but leaf sets are small!
        while( i.hasNext() ) {
            PlaxtonRemote pni = (PlaxtonRemote) i.next();
            if( closerInKeySpace( k, pni,closest ) ) {
                closest = pni;
                Diagnostic.trace( "Found closer node than owner in leaf set", Diagnostic.INIT );
            }
        }
        Diagnostic.trace( "Didn't find closer node than owner in leaf set", Diagnostic.INIT );
        return closest;
    }
    
    /**
     * @param k - some key
     * @param first   - the first PlaxtonRemote
     * @param second - duh!
     * @return true if first is closer to key k than second
     */
    private boolean closerInKeySpace( IKey k, PlaxtonRemote first, PlaxtonRemote second ) {
        try {
            return k.firstNumericallyCloserThanSecond(first.getKey(),second.getKey());
        } catch (Exception e) {
            Error.exceptionError( "Cannot obtain keys", e);
            return false;
        }
    }
    
    /**
     * @param suggestedLeaf - add a leaf to theSet
     * Factored to help with diagnostics
     */
    private void add(PlaxtonRemote suggestedLeaf, String where ) {
        Diagnostic.trace(ownerIP + " adding node " + where + " " + suggestedLeaf.toString(), Diagnostic.INIT );
        theSet.add(suggestedLeaf);
    }
    
}
