/*
 * Created on 03-Dec-2004
 *
 * Test of convergence of JChord nodes.
 */
package uk.ac.stand.dcs.asa.can.impl;

import uk.ac.stand.dcs.asa.can.interfaces.CanNode;
import uk.ac.stand.dcs.asa.can.simulation.CanSim;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author al
 */
public class CanGraphRenderer{

    private final String[] colours = { "red", "blue", "darkorange", "khaki", "crimson", "darkviolet", "black",
            "magenta", "gray", "mistyrose", "sandybrown", "darkorchid", "blueviolet" };
	private CanSim simulation;
	
    /**
     * @param simulation
     */
    public CanGraphRenderer(CanSim simulation) {
        this.simulation=simulation;
    }

    /**
     * generates a dot file for GraphViz tool
     */
    public void show_dot_file( String label ) {
        System.out.println( "digraph \"\" {" ); // the first thing in a dot file
        System.out.println( "node[style=filled,width=.125,height=.375,fontsize=9];");
        System.out.println( "graph[overlap=false,fontname=\"Helvetica-Oblique\",fontsize=12,label=\""+label+"\"];");
        show_paths();
        System.out.println( "}" ); // the last thing in a dot file
    }

    private String dimColour( int dimension ) {
        return colours[dimension % colours.length];
    }

    private void show_paths() {
        IP2PNode[] nodes = simulation.getNodes();
    	int length = nodes.length;
    	for( int i=0; i< length; i++ ){
    	    	IP2PNode nextNode = nodes[i];
    	    	if( !( nextNode instanceof CanNode ) ) {
    	    	    Error.hardError( "Encountered instance of " + nextNode.getClass().getName() + " when expecting CanNode");
    	    	}
            	CanNode nextCanNode = (CanNode) nextNode;
            	HyperCube hc = nextCanNode.getHyperCube();
            	if( hc == null ) {
            	    Error.hardError( "Encountered null hyperCube instance at node " + i );
            	} else {
            	    ArrayList[] neighbours = hc.getNeighbours();	// the arraylist in each of the dimensions
            	    for( int dims = 0; dims < neighbours.length; dims++ ) {
            	        ArrayList neighboursinDim = neighbours[dims];
            	        Iterator iter = neighboursinDim.iterator();
            	        while( iter.hasNext() ) {
            	            Object nextONeeb = iter.next();
            	            if( !( nextONeeb instanceof HyperCube ) ) {
                	    	    Error.hardError( "Encountered instance of " + nextONeeb.getClass().getName() + " when expecting neighbour HyperCube");
                	    	}
            	            HyperCube neeb =  (HyperCube) nextONeeb;    
            	            System.out.println(i + " -> " + simulation.key2Index(neeb.getOwner().getKey()) + "[color=" + dimColour(dims) + "];" );               	    
            	        }
            	    }
            	}
    	}
    }
}
