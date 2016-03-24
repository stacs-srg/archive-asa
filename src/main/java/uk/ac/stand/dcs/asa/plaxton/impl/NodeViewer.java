/*
 * Created on 16-Jan-2005 at 15:10:17.
 */
package uk.ac.stand.dcs.asa.plaxton.impl;

import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonNode;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonRemote;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;

import java.util.Iterator;



/**
 * @author al
 *
 * Displays a PlaxtonNode
 */
public class NodeViewer {

    private PlaxtonNode node;
    
    public NodeViewer( PlaxtonNode pni ) {
        node = pni;
    }
    
    public void showAll() {
        showNodeInfo(0);
        showNeighbours(false,2);
        showLeaves(false,2);
        showRoutes(false,2);
        System.out.println( "_____________________" );
    }
    
    public void showNodeInfo(int tabs) {
        try {
            tabs(tabs);
            System.out.println("Plaxton node: " + node.hashCode());
            tabs(tabs);
            System.out.println("Key = " + node.getKey());
            tabs(tabs);
            System.out.println("IP = " + node.getHostAddress().getAddress().getHostAddress());
        } catch (Exception e) {
            // see below...
        }
    }
    
    public void showNodeKey(PlaxtonNode pni, int tabs) {
        tabs(tabs);
        try {
            System.out.println( "Plaxton node: " + FormatHostInfo.formatHostName(pni.getHostAddress()) + " Key = " + pni.getKey() );
        } catch (Exception e) {
            // I am **** :(7
        }
    }
    
    public void showNeighbours(boolean shownodeinfo,int tabs) {
        if( shownodeinfo ) { showNodeInfo(tabs); }
        tabs(tabs); System.out.println( "Neighbours:" );
        Iterator i = node.getNeighbours().iterator();
        while( i.hasNext() ) {
            PlaxtonNode pni = (PlaxtonNode) i.next();
            showNodeKey( pni,tabs + 1 );
        }
    }
    
    public void showLeaves(boolean shownodeinfo,int tabs) {
        if( shownodeinfo ) { showNodeInfo(tabs); }
        LeafSet leaves = node.getLeaves();
        tabs(tabs); System.out.println( "Leaves:" );
        Iterator i = leaves.iterator();
        while( i.hasNext() ) {
            PlaxtonNode pni = (PlaxtonNode) i.next();
            showNodeKey( pni,tabs + 1 );
        }
    }
    
    public void showRoutes(boolean shownodeinfo,int tabs) {
        if( shownodeinfo ) { showNodeInfo(tabs); }
        tabs(tabs); System.out.print( "Routes:" );
        PlaxtonRemote[][] table = node.getRoutes().getTable();
        for( int rows = 0; rows < table.length; rows++ ) {
            System.out.println();
            tabs(tabs+1); System.out.print( rows + " " );
            for( int columns = 0; columns < table[rows].length; columns++ ) {
                PlaxtonRemote pni = table[rows][columns];
                if( pni == null ) {
                    System.out.print( "/ " );
                } else {
                    System.out.print( pni + " " );
                }
            }      
        }    
        System.out.println();
    }
    
    private void tabs( int t ) {
        for( int i = 0; i < t; i++) {
            System.out.print( "\t" );
        }
    }
    
}
