/*
 * Created on 16-Jan-2005 at 15:10:17.
 */
package uk.ac.stand.dcs.asa.can.impl;

import uk.ac.stand.dcs.asa.can.interfaces.CanNode;

import java.util.ArrayList;
import java.util.Iterator;



/**
 * @author al
 *
 * Displays a CanNode
 */
public class NodeViewer {

    private CanNode node;
    
    public NodeViewer( CanNode cni ) {
        node = cni;
    }
    
    public void showAll() {
        showNodeInfo(0);
        showHyperCube(false,2);
        showNeighbours(false,2);
        System.out.println( "_____________________" );
    }
    
    public void showNodeInfo(int tabs) {
        try {
            tabs(tabs);
            System.out.println("Can node: " + node.hashCode());
            tabs(tabs);
            System.out.println("Key = " + node.getKey());
            tabs(tabs);
            System.out.println("IP = " + node.getHostAddress().getAddress().getHostAddress());
            tabs(tabs);
            System.out.println("Centre = " + node.getCentre() );
        } catch (Exception e) {
            // see below...
        }
    }
    
    public void showNodeKey(CanNode cni, int tabs) {
        tabs(tabs);
        try {
            System.out.println( "CanNode node: " + cni.getHostAddress().getAddress().getHostAddress() + " Key = " + cni.getKey() );
        } catch (Exception e) {
            // I am **** :(
        }
    }
    
    public void showHyperCube( boolean shownodeinfo, int tabs ) {
        if( shownodeinfo ) { showNodeInfo(tabs); }
        HyperCube hc = node.getHyperCube();
        tabs(tabs); System.out.println( "HyperCube:" );  
        tabs(tabs); System.out.println("Centre = " + hc.getCentre() ); 
        tabs(tabs); System.out.println("Bits per dimension = " + hc.getBitsPerDimension() );        
        tabs(tabs); System.out.println("Number of dimensions = " + hc.getNumberDimensions() );            
        Range[] ranges = hc.getDimensions();
        tabs(tabs+1); System.out.print("Ranges - [ " );
        for( int i = 0; i < ranges.length; i++ ) {
            System.out.print( ranges[i].toString() );
            if( i != ranges.length - 1 ) {
                System.out.print( "," );
            }
        }
        System.out.println( "]" );
    }
    
    
    public void showNeighbours(boolean shownodeinfo,int tabs) {
        if( shownodeinfo ) { showNodeInfo(tabs); }
        HyperCube hc = node.getHyperCube();
        tabs(tabs); System.out.println( "Neighbours:" );
        ArrayList[] neighbours = hc.getNeighbours();
        for( int i = 0; i < neighbours.length; i++ ) {     
            ArrayList al = neighbours[i];
            Iterator iter = al.iterator();
            int count = 0;
            while( iter.hasNext() ) {
                HyperCube nxt = (HyperCube) iter.next();
                NodeViewer nv = new NodeViewer( nxt.getOwner() );
                tabs(tabs+1); System.out.println( "Neighbour:" + count++ );
                nv.showNodeInfo(tabs+2);
            }
        }

    }
      
    private void tabs( int t ) {
        for( int i = 0; i < t; i++) {
            System.out.print( "\t" );
        }
    }
    
}
