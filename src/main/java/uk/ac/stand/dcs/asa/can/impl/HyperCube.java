/*
 * Created on 10-Feb-2005 at 16:29:08.
 */
package uk.ac.stand.dcs.asa.can.impl;

import uk.ac.stand.dcs.asa.can.interfaces.CanNode;
import uk.ac.stand.dcs.asa.can.interfaces.CanRemote;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.util.Assert;
import uk.ac.stand.dcs.asa.util.BigSquareRoot;
import uk.ac.stand.dcs.asa.util.Diagnostic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author al
 *
 * This class tracks the hyper-cube for which a Can node is responsible.
 * In each dimension the upper and lower bounds of the region are kept.
 * Also tracks the neighbours of a node
 */
public class HyperCube {

    private int bitsPerDimension; 	// the size of each dimension
    private int numDims;			// the number of dimensions
    private Range[] dimensions; 	// used to store the upper and lower bounds of each dimension
    private ArrayList[] neighbours;
    private Coord centre;
    private BigInteger maxValue; // the maximum value that can be held in a coordinate
    private static BigInteger TWO = BigInteger.ONE.add( BigInteger.ONE );
    private CanNode owner;
    private IKey key;

    public HyperCube(CanNode owner, int numDims, int bitsPerDimension) {
        this.owner = owner;
        this.key = null;
        this.numDims = numDims;
        this.bitsPerDimension = bitsPerDimension;
        this.maxValue = Coord.maxValue(bitsPerDimension);
        dimensions = new Range[numDims];
        for( int i = 0; i < dimensions.length; i++) {					// cubes start off owning nothing
            dimensions[i] = new Range(BigInteger.ZERO,BigInteger.ZERO);
        }
        neighbours = new ArrayList[numDims];
        Arrays.fill( neighbours, new ArrayList() );
        assignCentre();
    }

	public void addNeighbour( HyperCube hc, int dimension ) {
	    if( dimension >= 0 && dimension < neighbours.length) {
	        ArrayList neighboursInDimensionD = neighbours[dimension];
	        if( !neighboursInDimensionD.contains(hc) ) {
	            neighboursInDimensionD.add(hc);
	        }
	    }
	}
	
	public void removeNeighbour( HyperCube hc, int dimension ) {
	    if( dimension >= 0 && dimension < neighbours.length) {
	        ArrayList neighboursInDimensionD = neighbours[dimension];	   
	        Diagnostic.trace( "Removing " + hc + " from " + this, Diagnostic.RUN );
	        neighboursInDimensionD.remove(hc);
		}
	}
	
	/**
	 *  This method is used during network initialisation and is used to make a new hyper cube
	 * take responsibility of the entire coordinate space.
	 */
	public void takeAll() {
        for( int i = 0; i < dimensions.length; i++) {
            // Careful don't optimise this out - need new values each time...
            dimensions[i] = new Range(BigInteger.ZERO,maxValue);
        }
        assignCentre();
	}
	
	public void assignCentre() {
	    ArrayList al = new ArrayList();
        for( int i = 0; i < dimensions.length; i++) {
            BigInteger low = dimensions[i].lower;
            BigInteger high = dimensions[i].upper;
            BigInteger range = high.subtract( low );
            BigInteger middle =  low.add( range.divide( TWO ) );	// would be better to use some power mechanism doubling rather than halving 
            al.add( middle ); 
        }	   
	    centre = new Coord(numDims,bitsPerDimension,al);
	    key = Coord.coordToKey( centre );
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
	    HyperCube copy = new HyperCube( null, this.numDims, this.bitsPerDimension );
	    copy.dimensions = new Range[dimensions.length];
	    for( int i = 0; i < dimensions.length; i++ ) {	
	        copy.dimensions[i] = dimensions[i];
	    }
	    copy.neighbours = new ArrayList[neighbours.length];
	    for( int i = 0; i < neighbours.length; i++ ) {
	        ArrayList al = new ArrayList();
	        Iterator iter = neighbours[i].iterator();
	        while( iter.hasNext() ) {
	            al.add( iter.next() );
	        }
	        copy.neighbours[i] = al;
	    }
	    return copy;
	}
	
    /**
     * Splits a region in two
     * @param other - the recipient CanRemote of this region being split
     * @return a new region which is this region split along the specified dimension
     */
    public HyperCube requestSplit(CanRemote other) {
        int splitDim = chooseBiggestDimension();
        // first check that a split is possible
        if (dimensions[splitDim].upper.compareTo( TWO ) > 0 ) {		  // TODO reexamine criteria for legal splitting
                HyperCube newCube = (HyperCube)this.clone(); // create a new region which is a copy of this one 
                newCube.owner = (CanNode) other;
                // next split the old and new into 2.
                this.splitDimLow(splitDim);
                newCube.splitDimHigh(splitDim);
                // next sort out neighbours
                this.splitNeighbours(newCube,splitDim,false);
                newCube.splitNeighbours(this,splitDim,true);
                return newCube;
        }
        Diagnostic.trace( "Cannot split region", Diagnostic.RUN );
        return null;
    }
    
    /**
     * Method attempts to find the hypercube with the closest centre to the target.
     * Candidates are this cube and all its neighbours
     * @param target - the coordinate which we are trying to find the closest neighbour to (including this node).
     * @return the closest known hypercube to the target
     */
    public HyperCube findClosestTo( Coord target ) {
        HyperCube result = this;
        // First cycle through all the dimensions
        BigDecimal shortestDistance = this.distanceTo( target );
        for( int dim = 0; dim < dimensions.length; dim++ ) {
            // Get the neighbours in the appropriate dimension
            ArrayList neighboursinD = neighbours[dim];
            Iterator iter = neighboursinD.iterator();
            while( iter.hasNext() ) {
                HyperCube next = (HyperCube) iter.next();
                BigDecimal nextDistance = next.distanceTo( target );
                // see if its centre is closer than the current closest to the target
                if( nextDistance.compareTo( shortestDistance ) < 0 ) {
                    // we have found a hyper cubs with a closer centre to the target
                    shortestDistance = nextDistance;
                    result = next;
                }
            }            
        }
        return result;
    }
    
    /****************** Private Helper methods ******************/

    // indirection to permit different distance methods to be put in.
    private BigDecimal distanceTo(Coord target) {
        	return shortestDistanceTo(target);
   }
    
    /**
     * Method finds the distance from the centre of this hyper cube to the target
     * Uses standard n-dimensions Pythagoras  
     * @param target - the coordinate we are trying to find the distance to
     * @return the distance to the target from this cube
     */
    public BigDecimal centreDistanceTo(Coord target) {
        BigInteger result = BigInteger.ZERO;
        for( int dim = 0; dim < dimensions.length; dim++ ) {
            BigInteger centreOrdinate = centre.getDimension(dim);
            BigInteger targetOrdinate = target.getDimension(dim);
            BigInteger distanceInD;										// distance in dimension D to centre from target
            if( centreOrdinate.compareTo( targetOrdinate ) > 0 ) {
                distanceInD = centreOrdinate.subtract(targetOrdinate);	// centre higher than target
            } else {
                distanceInD = targetOrdinate.subtract(centreOrdinate);	// target higher than centre         
            }
            result = result.add( distanceInD.multiply(TWO) );			// square of distance
        }
        // Now have all the squares of the distances in each dimension now need to take the sqrt
        return BigSquareRoot.sqrt( result );
    }
    
    /**
     * Method finds the smallest distance from any point in this hyper cube to the target
     * Uses standard n-dimensions Pythagoras  
     * @param target - the coordinate we are trying to find the distance to
     * @return the distance to the target from this cube
     */
    private BigDecimal shortestDistanceTo(Coord target) {
        BigInteger result = BigInteger.ZERO;
        for( int dim = 0; dim < dimensions.length; dim++ ) {
            BigInteger targetOrdinate = target.getDimension(dim);
            BigInteger closestOrdinate = findClosestOrdinateTo( target.getDimension(dim), dim );
            // take absolute value of the shortest distance going in either direction.
            BigInteger distanceInD = linearDistanceBetween( closestOrdinate, targetOrdinate ); 
            result = result.add( distanceInD.multiply(TWO) );			// square of distance
        }
        // Now have all the squares of the distances in each dimension now need to take the sqrt
        return BigSquareRoot.sqrt( result );
    }

    /**
     * Calculates the distance between two points on a 1D plane and returns the shortest distance
     * between those points - either the simple distance between them (cross distance) or by moving
     * off the edge of the plane at the max value and coming back on the other side (zero).
     * Method does this in one dimension only.
     * I don't have the vocabulary for the geometry stuff! - al
     */
    private BigInteger linearDistanceBetween(BigInteger closestOrdinate, BigInteger targetOrdinate) {
        BigInteger crossDistance; // normal distance between points across the world without going over the edge.
        BigInteger edgeDistance;  // distance between two points going off edge and coming back on the opposite edge
        if( closestOrdinate.compareTo( targetOrdinate ) > 0 ) {
            crossDistance = closestOrdinate.subtract(targetOrdinate);	// closest higher than target
            edgeDistance = maxValue.subtract( closestOrdinate ).add( targetOrdinate );
        } else {
            crossDistance = targetOrdinate.subtract(closestOrdinate);	// target higher than closest
            edgeDistance = maxValue.subtract( targetOrdinate ).add( closestOrdinate );
        }
        return crossDistance.min( edgeDistance );        
    }

    /**
     * This returns the closest ordinate in dimension which_dim to to a target ordinate....
     */
    private BigInteger findClosestOrdinateTo(BigInteger target_ordinate, int which_dim) {
        Range myRangeInD = dimensions[which_dim];
        // if we point we are looking for is less than the range return the lower edge.
        if( target_ordinate.compareTo( myRangeInD.lower ) <= 0 ) {
            return myRangeInD.lower;
        }
        // if we point we are looking for is greater than the range return the upper edge.
        if( target_ordinate.compareTo( myRangeInD.upper ) >= 0 ) {
            return myRangeInD.upper;
        }
        // return the target ordinate if it is in the range.
        return target_ordinate;
        
    }

    /**
     * Helper method to choose how to split the region.
     * Achieves this by finding the dimension in which the range is greatest
     * @return the dimension in which the region is to be split in two
     */
    private int chooseBiggestDimension() {
        int chosenDimension = 0;
        BigInteger chosenSize = BigInteger.ZERO;
        for( int i = 0; i < dimensions.length; i++ ){
            BigInteger nextRange = dimensions[i].range();
            if( nextRange.compareTo( chosenSize ) > 0 ) {
                chosenDimension = i;
                chosenSize = nextRange;
            }
        }
        return chosenDimension;
    }
    /**
     *  Helper method to split a region in two
     *  This method splits the region and keeps the high half of the specified dimension
     *  @param whichDimension the dimension along which the region is to be split
     */
    private void splitDimHigh(int whichDimension) {
          Range oldRange =  dimensions[whichDimension];
          BigInteger low = oldRange.lower;
          BigInteger high = oldRange.upper;
          BigInteger range = high.subtract( low );
          BigInteger middle =  low.add( range.divide( TWO ) );	// would be better to use some power mechanism doubling rather than halving 
       //   middle =  middle.add( BigInteger.ONE ); // bottom of top range is one bigger than low
          Assert.assertion(middle.compareTo(high) < 0,"Range cannot be split into two");
          // this node gets the high half
          dimensions[whichDimension] = new Range( middle,high );
          assignCentre();
    }
    
    /**
     *  Helper method to split a region in two
     *  This method splits the region and keeps the low half of the specified dimension
     *  @param whichDimension the dimension along which the region is to be split
     */
    private void splitDimLow(int whichDimension) {
        Range oldRange =  dimensions[whichDimension];
        BigInteger low = oldRange.lower;
        BigInteger high = oldRange.upper;
        BigInteger range = high.subtract( low );
        BigInteger middle =  low.add( range.divide( TWO ) );	// would be better to use some power mechanism doubling rather than halving 
        Assert.assertion(middle.compareTo(high) < 0,"Range cannot be split into two");
        // this node gets the low half
        dimensions[whichDimension] = new Range(low,middle );  
        assignCentre();
    }
    
    /**
     * Helper method to split a region in two
     * This method splits up the neighbours - at the point of call the neighbours are those before the split
     * These neighbours need thinned out (like carrots) leaving only the real neighbours of this cube
     * @param splitDim the dimension along which the region is to be split
     * @param otherCube the hypercube from which this cube has been split
     * @param isNew if true indicates that this is the newly split off cube
     * isNew also indicates that this cube is reponsible for the high range following the split
     * In this case the new cube must notify its neighbours of its existence
     */
    private void splitNeighbours(HyperCube otherCube, int splitDim, boolean isNew ) {
        for( int dim = 0; dim < dimensions.length; dim++ ) {

            ArrayList oldNeighbours = neighbours[dim];  // the neighbours in need of adjustment
            ArrayList newNeighbours = new ArrayList();	// a new neighbour set we are going to construct from the old

            if( dim == splitDim ) { 			
                // this is the dimension that has been split
                Diagnostic.trace( "Splitting dim = " + dim, Diagnostic.RUN );
                addMeeting( newNeighbours,oldNeighbours,splitDim );  ////////////////// TODO need to check upper and lower - AL HERE
                Diagnostic.trace( "Adding neighbour " + otherCube, Diagnostic.RUN );
                newNeighbours.add(otherCube);	// lastly add the oldCube as a neighbour     
                
            } else {
                Diagnostic.trace( "Overlapping dim " + dim, Diagnostic.RUN );
                // this is one of the dimesions that have not been split
                // We need to add the neighbours that are overlapping with our range in that dimension
                addOverlapping( newNeighbours,oldNeighbours,dim );        
                
            }
            // lastly assign in the new Arraylist to the appropriate dimension
            neighbours[dim] = newNeighbours;
            if( !isNew ) {	// must tell removed neighbours of our relocation
                notifyOldNeighbours( newNeighbours,oldNeighbours, dim ) ;
            }
        }
    }
    
    /**
     * Method notifies old neighbours that this node has relocated 
     * @param newNeighbours
     * @param oldNeighbours
     */
    private void notifyOldNeighbours(ArrayList newNeighbours, ArrayList oldNeighbours, int dim ) {
        // first remove newNeighbours from oldNeighbours
        // newNeighbours is a strict subset.
        Diagnostic.trace( "notifying OLD neighbours", Diagnostic.RUN );
        Iterator iter = newNeighbours.iterator();
        while( iter.hasNext() ) {
            Object next = iter.next();
            oldNeighbours.remove(next);
        }
        // Those neighbours left in oldNeighbours truely are old neighbours - so tell them
        iter = oldNeighbours.iterator();
        while( iter.hasNext() ) {
            HyperCube oldNeighbour = (HyperCube)iter.next();
            Diagnostic.trace( this + " removing " + oldNeighbour, Diagnostic.RUN );
            oldNeighbour.removeNeighbour( this, dim );
        }
    }

    /**
     * Add those neighbours who share a common edge in dimension splitDim
     * @param newNeighbours - the ArrayList of newNeighbours being built up
     * @param oldNeighbours - the ArrayList of oldNeighbours from the progenitor being examined
     * @param splitDim - the dimension along which the hypercube has been split
     */
    private void addMeeting(ArrayList newNeighbours, ArrayList oldNeighbours, int splitDim) {
        Diagnostic.trace( "Node " + owner + " addMeeting" , Diagnostic.RUN);
        Iterator iter = oldNeighbours.iterator();
        while( iter.hasNext() ) {
            HyperCube prospective = (HyperCube) iter.next();
            Diagnostic.trace( "Prospective neighbour =  " + prospective, Diagnostic.RUN );
            if( prospective != this && hasMeetingEdge( prospective, splitDim ) ) {
                Diagnostic.trace( "Added prospective", Diagnostic.RUN );
                newNeighbours.add(prospective);
                prospective.addNeighbour(this,splitDim);
            } else {
                Diagnostic.trace( "Rejected prospective", Diagnostic.RUN );
            }
        }
    }
    
    /**
     * Determines if this cube and prospective share an edge in common in dimension splitDim
     * @param prospective - a prospective neighbour
     * @param splitDim - the dimension along whch the cube has been split
     * @return true if the cubes share an edge in common
     */
    private boolean hasMeetingEdge( HyperCube prospective, int splitDim) {
        Range thisRange = dimensions[splitDim];
        Range other = prospective.dimensions[splitDim];
        Diagnostic.trace( "Comparing " + thisRange + " to " + other , Diagnostic.RUN );
        return
        	/* normal case */ ( thisRange.upper.compareTo( other.lower ) == 0 || thisRange.lower.compareTo( other.upper ) == 0 ||
            /* wrap case */    ( thisRange.upper.compareTo( maxValue ) == 0 && other.lower.compareTo( BigInteger.ZERO ) == 0 ) ||
            			       ( thisRange.lower.compareTo( BigInteger.ZERO ) == 0 && other.upper.compareTo( maxValue ) == 0 ) ); 
    }

    /**
     * This method adds the cubes that overlap with us in the appropriate dimension.
     * The neighbours are filtered out of the oldNeighbours which are drawn from the progenitor cube.
     * @param newNeighbours - the ArrayList of newNieghbours being buit up
     * @param oldNeighbours - the ArrayList of oldNeighbours from the progenitor being examined
     * @param currentDim - the dimension along which the hypercube has been split
     */
    private void addOverlapping( ArrayList newNeighbours, ArrayList oldNeighbours, int currentDim  ) {
        Iterator iter = oldNeighbours.iterator();
        Diagnostic.trace( "Node " + owner + " splitting size = " + oldNeighbours.size(), Diagnostic.RUN );
        while( iter.hasNext() ) {
            HyperCube prospective = (HyperCube) iter.next();
            Diagnostic.trace( "Node " + owner + " checking " + prospective, Diagnostic.RUN );
            if( prospective != this && checkOverlaps( prospective, currentDim ) ) {
                Diagnostic.trace( "Node " + owner + " adding " + prospective, Diagnostic.RUN );
                newNeighbours.add(prospective);
                Diagnostic.trace( "Node " + owner + " notifying " + prospective, Diagnostic.RUN );
                prospective.addNeighbour(this,currentDim);
            }
        }
    }
 
    /**
     * This method checks for overlaps in all dimensions apart from current.
     * @param prospective - a prospectibe neighbour
     * @param currentDim - the dimension along which the hypercube has been split
     */
    private boolean checkOverlaps(HyperCube prospective, int currentDim) {
        boolean doesOverlap = false;
        for( int dim = 0 ; dim < dimensions.length; dim++ ) {
            if( dim != currentDim ) {
                Range thisRange = dimensions[dim];
                Range otherRange = prospective.dimensions[dim];
                doesOverlap = thisRange.overlaps( otherRange );
                if( doesOverlap )
                    return doesOverlap;
            }
        }
        return doesOverlap;
    }

    /************************* toString *************************/
    
    public String toString() {
        return "Cube[" + owner.toString() + "]";
    }
    
    /************************* Getters and Setters *************************/

    /**
     * @return Returns the centre.
     */
    public Coord getCentre() {
        return centre;
    }
    /**
     * @return Returns the bitsPerDimension.
     */
    public int getBitsPerDimension() {
        return bitsPerDimension;
    }
    /**
     * @return Returns the dimensions.
     */
    public Range[] getDimensions() {
        return dimensions;
    }
    /**
     * @return Returns the neighbours.
     */
    public ArrayList[] getNeighbours() {
        return neighbours;
    }
    /**
     * @return Returns the numDims.
     */
    public int getNumberDimensions() {
        return numDims;
    }
    /**
     * @return Returns the owner.
     */
    public CanNode getOwner() {
        return owner;
    }

    /**
     * @return the centre of the HyperCube as a Key
     */
    public IKey getKey() {
        return key;
    }
    /**
     * @return Returns the maxValue.
     */
    public BigInteger getMaxValue() {
        return maxValue;
    }
}
