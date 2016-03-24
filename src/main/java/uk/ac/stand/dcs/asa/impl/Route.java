/*
 * Created on Dec 15, 2004 at 11:36:19 AM.
 */
package uk.ac.stand.dcs.asa.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.util.ReverseIterator;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author al
 *
 *  Route provides a data structure to hold routing information from some node for some key.
 */
public class Route {

    private ArrayList hops;	
    private IKey searchKey;
    private IP2PNode start;
    
    public Route( IKey searchKey ) {
        hops = new ArrayList();    
        this.start = null;
        this.searchKey = searchKey;
    }   
    
    /**
     *  Construct a Route object
     */
    public Route( IP2PNode start, IKey searchKey ) {
        hops = new ArrayList();
        this.start = start;
        this.searchKey = searchKey;
    }
    
    /**
     * @param next the next JChordNode to add to route table
     */
    public void addHop( IP2PNode next ) {
        hops.add(next);
    }

    public void addHopAtStart( IP2PNode next ) {
        hops.add(0,next);
    }
    
    public IP2PNode lastHop() {
        return (IP2PNode)( hops.get( hops.size() - 1 ) );
    }
  
    /**
     * @return the number of hops in the route
     */
    public int hop_count() {
        return hops.size();
    }
    
    /**
     * @return an iterator over the hops in the route
     */
    public Iterator iterator() {
        return hops.iterator();
    }

    /**
     * @return a reverse iterator over the hops in the route
     */
    public Iterator reverseIterator() {
        return new ReverseIterator(hops);
    }
    
    /**
     * @return the search key.
     */
    public IKey getSearchKey() {
        return searchKey;
    }
    
    /**
     * @return the start node
     */
    public IP2PNode getStart() {
        return start;
    }
    
    public Route commonPath( Route other ) {
        if( this.searchKey.compareTo( other.searchKey ) == 0 ) {
            Route r = new Route(this.searchKey);
            int index = hops.size() - 1; // last entry in the array list.
            int other_index = other.hops.size() - 1; // last entry in other array list
            while( index >= 0 &&  other_index >= 0 ) {
                // run down array list comparing
                if( hops.get(index).equals( other.hops.get(other_index))) {
                    r.addHopAtStart((IJChordRemote) hops.get(index));
                    index--;
                    other_index--;
                }
                else {
                    break;
                }
            }
        	return r;
        }
        return null; // in the event of failure
    }
}
