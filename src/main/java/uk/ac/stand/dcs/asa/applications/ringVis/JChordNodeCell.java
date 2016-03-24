package uk.ac.stand.dcs.asa.applications.ringVis;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import uk.ac.stand.dcs.asa.eventModel.P2PHost;
import uk.ac.stand.dcs.asa.interfaces.IFailureSuspector;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;

import java.awt.*;
import java.net.InetSocketAddress;

public class JChordNodeCell implements Comparable {
    
    private static final int STATE_OK = 0;
    private static final int STATE_MAY_HAVE_FAILED = 1;
    
    private P2PHost host;
    
    private DefaultEdge predEdge, succEdge;

    private DefaultGraphCell graph_cell;
    
    private AdminGUI admin_GUI;
    
    private JChordGraphModel graph_model;

    private JChordCellDirectory cell_directory;

    private int state = STATE_OK;

    /**
     * Creates a graphical representation of a node
     */
    public JChordNodeCell(P2PHost node_rep, AdminGUI admin_GUI) {
        
        this.host = node_rep;
        this.admin_GUI = admin_GUI;
        
        graph_model = admin_GUI.getGraphModel();
        cell_directory = admin_GUI.getCellDirectory();
        
        predEdge = null;
        succEdge = null;
        
        graph_cell = new DefaultGraphCell(node_rep.toString());
        
        AttributeMap attrib = new AttributeMap();
        GraphConstants.setAutoSize(attrib, true);
        graph_model.getGraph().getGraphLayoutCache().edit(new Object[]{graph_cell}, attrib);
        
        cell_directory.addCell(node_rep.toString(), this);
        graph_model.addCell(graph_cell);
    }

    public P2PHost getHost() {
        return host;
    }

    /**
     * Refreshes the pointer to the node's predecessor
     */
    public void changeToPredecessor(InetSocketAddress new_predecessor, IKey new_predecessor_key) {
        
        if (predEdge != null) graph_model.deleteEdge(predEdge);
        
        if (new_predecessor != null) {
            
            Diagnostic.trace(host.toString() + " pred " + FormatHostInfo.formatHostName(new_predecessor), Diagnostic.FULL);
            
            JChordNodeCell predNodeCell = cell_directory.lookup(FormatHostInfo.formatHostName(new_predecessor));
            
            if (predNodeCell == null) {
                
                // Create a JChordNodeCell for the new predecessor
                Diagnostic.trace("Creating node cell for node " + FormatHostInfo.formatHostName(new_predecessor) + "[" + new_predecessor_key + "]", Diagnostic.INIT);
                
                predNodeCell = new JChordNodeCell(new P2PHost(new_predecessor, new_predecessor_key), admin_GUI);
                admin_GUI.layoutCells();
            }
            
            DefaultGraphCell predCell = predNodeCell.getCell();
            if (predCell != null) predEdge = graph_model.addPredecessorEdge(graph_cell, predCell);
            
        }
        else predEdge = null;
        
        admin_GUI.repaint();
    }

    /**
     * Refreshes the pointer to the node's successor
     */
    public void changeToSuccessor(InetSocketAddress new_successor, IKey new_successor_key) {
        
        Diagnostic.trace(host + " succ " + FormatHostInfo.formatHostName(new_successor), Diagnostic.RUN);
        
        if (succEdge != null) graph_model.deleteEdge(succEdge);

        if (new_successor != null) {
            
            JChordNodeCell succNodeCell = cell_directory.lookup(FormatHostInfo.formatHostName(new_successor));
            
            if (succNodeCell == null) {
                
                // Create a JChordNodeCell for the new successor
                Diagnostic.trace("Creating node cell for node " + FormatHostInfo.formatHostName(new_successor) + "[" + new_successor_key + "]", Diagnostic.INIT);
                
                succNodeCell = new JChordNodeCell(new P2PHost(new_successor, new_successor_key), admin_GUI);
                admin_GUI.layoutCells();
            }
            
            DefaultGraphCell succCell = succNodeCell.getCell();
            
            if (succCell != null) {
                succEdge = graph_model.addSuccessorEdge(graph_cell, succCell);
            }
        } else succEdge = null;

        admin_GUI.repaint();
    }
    
    public void mayHaveFailed() {

        Diagnostic.trace(Diagnostic.INIT);
        state = STATE_MAY_HAVE_FAILED;
        
        admin_GUI.layoutCells();
        
        IFailureSuspector failure_suspector = admin_GUI.getFailureSuspector();
        
        if (failure_suspector.hasFailed(host.getInet_sock_addr())) {

            Diagnostic.trace("removing cell: " + host, Diagnostic.RUN);
            
            if (succEdge != null) graph_model.deleteEdge(succEdge);
            if (predEdge != null) graph_model.deleteEdge(predEdge);
            
            graph_model.removeCell(graph_cell);
            cell_directory.removeCell(host.toString());
            
            admin_GUI.layoutCells();
            admin_GUI.repaint();
        }
    }

    /** @return This DefaultGraphCell */
    public DefaultGraphCell getCell() {
        return graph_cell;
    }

    /**
     * Compares the Key of the node represented by this NodeCell with that of
     * the specified NodeCell
     * 
     * @param o the cell to compare to this one
     * @return -1, 0, or 1 if the specified node is greater, equal to, or less
     *         than this node, respectively
     */
    public int compareTo(Object o) {
        try {
            JChordNodeCell nc = (JChordNodeCell) o;
            return host.getKey().compareTo(nc.getNode_key());
            
        } catch (ClassCastException e) {
            Error.hardError("ClassCastException: could not cast " + o.getClass().getName() + "to JChordNodeCell");
            return 0; // not called
        }
    }
    
    public Color getBorderColor() {
        
        switch (state) {
            
            case STATE_OK: return Color.black;
            case STATE_MAY_HAVE_FAILED: return Color.red;
            default: return Color.green;
        }
    }

    public Color getBackgroundColor() {
        
        switch (state) {
            
            case STATE_OK: return Color.white;
            case STATE_MAY_HAVE_FAILED: return Color.red;
            default: return Color.green;
        }
    }
    /**
     * @return Returns the node_key.
     */
    public IKey getNode_key() {
        return host.getKey();
    }
}