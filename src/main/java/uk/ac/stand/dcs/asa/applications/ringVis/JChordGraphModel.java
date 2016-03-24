package uk.ac.stand.dcs.asa.applications.ringVis;

import org.jgraph.JGraph;
import org.jgraph.graph.*;
import uk.ac.stand.dcs.asa.util.Diagnostic;

import java.awt.*;
import java.util.Map;
import java.util.Vector;

public class JChordGraphModel {
    
	private Vector cells;
	private JGraph graph;
	private DefaultGraphModel model;
	
    public JChordGraphModel() {
        
        model = new DefaultGraphModel();
        graph = new JGraph(model);
        cells = new Vector();
    }

	public DefaultGraphModel getModel() {
		return model;
	}
	
	public JGraph getGraph() {
	    return graph;
	}

	public Map getAttributes() {
		return model.getAttributes();
	}

    /**
     * Deletes a specified edge (pointing to a successor or predecessor) from the graph
     * @param edge the edge to be deleted
     */
    public void deleteEdge(DefaultGraphCell edge) {
        model.remove(new Object[] { edge });
    }

    /**
     * Adds a DefaultGraphCell object into a Vector (containing all existing DefaultGraphCells) at a specified index.
     * Once added, all Cells at and after this index are shifted right.  This is used if the node has a predecessor already in
     * the Vector- the index should be it's successor's index + 1.
     * @param dgc the cell to be added
     * @param index the index where the cell should be stored in the Vector
     */
    public void addCell(DefaultGraphCell dgc, int index) {
        Diagnostic.trace("adding cell " + dgc, Diagnostic.FULL);
        cells.add(index, dgc);
    }

    /**
     * @return The number of cells that the Vector cells currently stores 
     */
    public int cellSize() {
        return cells.size();
    }

    /**
     * Adds a DefaultGraphCell object to the end of the Vector containing all existing DefaultGraphCells.
     * This should be used if the node has no predecessor node.
     * @param dgc the cell to be added
     */
    public void addCell(DefaultGraphCell dgc) {
        Diagnostic.trace("adding cell " + dgc, Diagnostic.FULL);
        cells.add(dgc);
    }

    /**
     * Removes a DefaultGraphCell from the Vector containing all Cells.  This is mostly used when a cell's
     * position in the vector changes- it is removed and then reinserted at a new position.
     * @param dgc the cell to be removed
     */
    public void removeCell(DefaultGraphCell dgc) {
        Diagnostic.trace("removing cell " + dgc, Diagnostic.FULL);
        cells.remove(dgc);
        model.remove(new Object[] {dgc});
    }

	/**
	 * @return the Vector that contains all the DefaultGraphCells in the JGraph
	 */
	public Vector getCells() {
		return cells;
	}

    /**
     * If adding a successor, 'from' will be the Cell representing the current node, and 'to' will be the Cell representing the
     * current node's successor
     * 
     * @return The DefaultEdge that has been created and added to the Graph
     * @param from the cell that the edge is coming from
     * @param to the cell that the edge should point to
     * @param label the label for the edge e.g. "Successor"
     * @param col the colour of the edge
     */
    private DefaultEdge addEdge( DefaultGraphCell from, DefaultGraphCell to, String label, Color col ) {
        
        DefaultEdge edge = new DefaultEdge( label );
        
        AttributeMap edgeAttrib = new AttributeMap();
        edge.setAttributes(edgeAttrib);
        
        GraphConstants.setLineEnd( edgeAttrib, GraphConstants.ARROW_CLASSIC );
        GraphConstants.setLineStyle( edgeAttrib, GraphConstants.STYLE_BEZIER );
        GraphConstants.setEndFill( edgeAttrib, true );
        GraphConstants.setLineColor( edgeAttrib, col );
        
        DefaultPort fromPort = new DefaultPort();
        from.add( fromPort );
        DefaultPort toPort = new DefaultPort();
        to.add( toPort );
        
        ConnectionSet cs = new ConnectionSet( edge, fromPort, toPort );
        Object[] cells = new Object[] { edge, from, to };
        
        model.insert(cells, GraphConstants.createAttributesFromModel(cells,  model) , cs, null, null);
        
        return edge;
    }

    public DefaultEdge addFinger( DefaultGraphCell from, DefaultGraphCell to ){
        return addEdge(from, to, null, Color.green);
    }
    
    /**
     * Adds an edge going from one node to its successor
     * @param from the cell representing the node that the edge should start at
     * @param to the cell representing the node that the edge should point to
     * */
    public DefaultEdge addSuccessorEdge(DefaultGraphCell from, DefaultGraphCell to) {
        return addEdge(from, to, "succ", Color.blue);
    }

    /**
     * Adds an edge going from one node to its predecessor
     * @param from the cell representing the node that the edge should start at
     * @param to the cell representing the node that the edge should point to
     * */
    public DefaultEdge addPredecessorEdge(DefaultGraphCell from, DefaultGraphCell to) {
        return addEdge(from, to, "pred", Color.red);
    }
}
