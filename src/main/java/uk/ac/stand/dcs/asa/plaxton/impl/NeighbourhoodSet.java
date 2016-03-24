/*
 * Created on 11-Jan-2005 at 19:13:09.
 */
package uk.ac.stand.dcs.asa.plaxton.impl;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonNode;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonRemote;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author al
 *
 * This class maintains a plaxton neighbourhood set
 * The neighbourhood set holds the N closest nodes in physical space to the owner
 */
public class NeighbourhoodSet {
    
    /******************** Private Fields ********************/
    
    private ArrayList theHood;
    private PlaxtonNode owner;
    private InetAddress ownerIP;
    private PlaxtonRemote furthestMemberNode;
    private int capacity;
    private IDistanceCalculator dc;
    
    /********************* Constructors *********************/
    
    public NeighbourhoodSet(PlaxtonNode owner, int capacity, IDistanceCalculator dc ) {
        theHood = new ArrayList();
        this.owner = owner;
        try {
            this.ownerIP = owner.getHostAddress().getAddress();
        } catch (Exception e) {
            Error.exceptionError( "Cannot obtain owners IP address", e);
            this.ownerIP = null;
        }
        this.furthestMemberNode = null;
        this.capacity = capacity;
        this.dc = dc;
    }
    
    /*********************** Methods ************************/
    
	public void addNeighbours(NeighbourhoodSet other) {
	    Iterator i = other.iterator();
	    while( i.hasNext() ) {
	        PlaxtonRemote pni = (PlaxtonRemote)i.next();
	        addNeighbour( pni );
	    }
    }
	
	public void addNeighbour( PlaxtonRemote pni ) {
	    if( pni == owner ) {
//            Diagnostic.trace( "NeighbourhoodSet::addNeighbour", owner + " attempted to add owner", Diagnostic.RUN );
	        return;
	    }
	    if( theHood.contains(pni) ) {
//	        Diagnostic.trace( "NeighbourhoodSet::addNeighbour", owner + " attempted to add node in hood already - " + pni, Diagnostic.RUN );
	        return;
	    }
	    InetAddress pniIPAddress = null;
        try {
            pniIPAddress = pni.getHostAddress().getAddress();
        } catch (Exception e) {
            Error.exceptionError( "Cannot obtain remote IP address", e);
            return;
        }
        if( theHood.size() < capacity ) {		// if we are not at capacity add the new node
	        add( pni, "not full" );
	        // check to see if this is further away than any other node we know about
	        if( furthestMemberNode == null ) {
	            furthestMemberNode = pni;
//	            Diagnostic.trace( "NeighbourhoodSet::addNeighbour", owner + " less than capacity " + pni + " added", Diagnostic.RUN );    
	        }
	        else {
	            InetAddress furthestIPAddress = null;
	            if( furthestMemberNode != null ) { // if the node is null we never use the IP address.
	                try {
	                    furthestIPAddress = furthestMemberNode.getHostAddress().getAddress();
	                } catch (Exception e1) {
	                    Error.exceptionError( "Cannot obtain furthest IP address", e1);
	                    return;
	                }
	            }
	            if( dc.distance(ownerIP,pniIPAddress ) > dc.distance(ownerIP,furthestIPAddress ) ) {  
	                furthestMemberNode = pni;
//	                Diagnostic.trace( "NeighbourhoodSet::addNeighbour", owner + " less than capacity " + pni + " added", Diagnostic.RUN );
	            }
	        }     
	    } else  { // check to see if the new node is closer than the ones we know about
	           // if it is add it drop the furthest and carry on.
            InetAddress furthestIPAddress = null;
            try {
                furthestIPAddress = furthestMemberNode.getHostAddress().getAddress();
            } catch (Exception e1) {
                Error.exceptionError( "Cannot obtain furthest IP address", e1);
                return;
            }
	        if( dc.distance(ownerIP,pniIPAddress ) < dc.distance(ownerIP,furthestIPAddress ) ) {
	            theHood.remove(furthestMemberNode);
//	            Diagnostic.trace( "NeighbourhoodSet::addNeighbour", owner + " replacing node with " + pni,  Diagnostic.RUN );
	            add( pni, "full - closer" );
	            findFurthest();
	        }
//	        Diagnostic.trace( "NeighbourhoodSet::addNeighbour", ownerIP + "ignoring add - node " + pni + " too far away", Diagnostic.RUN );
	    }
	}

	
    /**
     **  Finds the furthest away node from owner which is in the set
     */
    private void findFurthest() {
        double maxDistance = 0;
	    Iterator i = iterator();
	    while( i.hasNext() ) {
	        PlaxtonRemote pni = (PlaxtonRemote)i.next();
		    InetAddress pniIPAddress;
	        try {
	            pniIPAddress = pni.getHostAddress().getAddress();
	        } catch (Exception e) {
	            Error.exceptionError( "Cannot obtain remote IP address", e);
	            pniIPAddress = null;
	        }	        
	        double distanceToNext = dc.distance(ownerIP,pniIPAddress );
	        if(  distanceToNext > maxDistance ) {
	            maxDistance = distanceToNext;
	            furthestMemberNode = pni;
	        }
	    }
    }

    /**
     * @return an iterator over the neighbourhood set
     */
    public Iterator iterator() {      
        return theHood.iterator();
    } 
    
    /**
     * @param pni - the neighbour to the hood
     * Factored to help with diagnostics
     */
    private void add(PlaxtonRemote pni, String where ) {
        Diagnostic.trace(ownerIP + " adding node " + where + " " + pni.toString(), Diagnostic.INIT );
        theHood.add(pni);
    }
}
