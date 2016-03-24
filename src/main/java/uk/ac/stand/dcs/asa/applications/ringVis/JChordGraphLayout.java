package uk.ac.stand.dcs.asa.applications.ringVis;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

public class JChordGraphLayout {

    private JChordGraphModel graph_model;
    private JChordCellDirectory cell_directory;

    private Vector cells;
    
    private static final int DEFAULT_ORIGIN_OFFSET = 75;
    private static final int DEFAULT_RADIUS = 50;
    private static final int CELL_HEIGHT = 20;
    private static final int CELL_WIDTH = 90;
    private static final double DEGREES_IN_CIRCLE = 360;
    
    public JChordGraphLayout(AdminGUI admin_GUI) {
        
        graph_model = admin_GUI.getGraphModel();
        cell_directory = admin_GUI.getCellDirectory();
    }

    /**
     * Render the graphical representations of the nodes in the system as a
     * equally distributed ring. The nodes are rendered clockwise in ascending
     * key order. NOTE: The positions of the nodes in this graph do not
     * represent the exact positions of the nodes in the keyspace.
     */
    public void render() {
        
        sortCells();
        int size = cells.size();
        
        for (int i = 0; i < size; i++) {
            
            JChordNodeCell nextCell = (JChordNodeCell) cells.get(i);
            DefaultGraphCell next = nextCell.getCell();
            double currentAngle = i * getAngle(size);
            
            int xpos = 0;
            int ypos = 0;
            
            if (currentAngle <= 90) {
                xpos = getAbsoluteXPos(currentAngle, getOpposite(currentAngle));
                ypos = getAbsoluteYPos(currentAngle, getAdjacent(currentAngle));
            } else if (currentAngle <= 180) {
                xpos = getAbsoluteXPos(currentAngle, getOpposite(180 - currentAngle));
                ypos = getAbsoluteYPos(currentAngle, getAdjacent(180 - currentAngle));
            } else if (currentAngle <= 270) {
                xpos = getAbsoluteXPos(currentAngle, getOpposite(currentAngle - 180));
                ypos = getAbsoluteYPos(currentAngle, getAdjacent(currentAngle - 180));
            } else if (currentAngle <= 360) {
                xpos = getAbsoluteXPos(currentAngle, getOpposite(360 - currentAngle));
                ypos = getAbsoluteYPos(currentAngle, getAdjacent(360 - currentAngle));
            }
            
            AttributeMap attrib = new AttributeMap();
            
            Rectangle2D bounds = attrib.createRect(xpos, ypos, CELL_WIDTH, CELL_HEIGHT); // x,y,width,height
            GraphConstants.setBounds(attrib, bounds);
            GraphConstants.setBackground(attrib, nextCell.getBackgroundColor());
            GraphConstants.setBorderColor(attrib, nextCell.getBorderColor());
            
            graph_model.getGraph().getGraphLayoutCache().edit(new Object[]{next}, attrib);
        }
    }
    
    /**
     * Finds the x position that the node should be displayed according to the
     * size of its given angle, i.e. if the angle is less than 180, add the
     * given length to the origin, else subtract it.
     * 
     * @param currentAngle the angle that the node is positioned at from the origin
     * @param length the distance that the node should be from the origin
     * @return the actual position of the node from the origin
     */
    private int getAbsoluteXPos(double currentAngle, int length) {
        
        if (currentAngle > 0 && currentAngle <= 180) return getOrigin() + length;
        else return getOrigin() - length;
    }

    /**
     * Finds the y position that the node should be displayed according to the
     * size of its given angle, i.e. if the angle is between 90 and 270, add
     * the given length to the origin, else subtract it.
     * 
     * @param currentAngle the angle that the node is positioned at from the origin
     * @param length the distance that the node should be from the origin
     * @return the actual position of the node from the origin
     */
    private int getAbsoluteYPos(double currentAngle, int length) {
        
        if (currentAngle > 90 && currentAngle <= 270) return getOrigin() + length;
        else return getOrigin() - length;
    }

    /**
     * Trig method that works out the opposite side of triangle from a given
     * angle using Sine Answer calculated: radius * sin(angle). NOTE: parameter
     * is taken in Degrees.
     * 
     * @param angle
     *            The angle from the origin that a cell is positioned at
     * @return The length of the opposite side of the triangle
     */
    private int getOpposite(double angle) {

        return (int) (getRadius() * Math.sin(Math.toRadians(angle)));
    }

    /**
     * Trig method that works out the opposite side of triangle from a given
     * angle using Cosine Answer calculated: radius * cos(angle).
     * 
     * @param angle the angle from the origin that a the cell is positioned at
     * @return the length of the opposite side of the triangle
     */
    private int getAdjacent(double angle) {

        return (int) (getRadius() * Math.cos(Math.toRadians(angle)));
    }

    /**
     * Works out the angle that should be between each node for the layout to be
     * equally distributed
     * 
     * @param numberNodes the number of nodes in the system
     * @return the angle that should be between each node for the layout to be
     *         equally distributed
     */
    private double getAngle(int numberNodes) {

        return DEGREES_IN_CIRCLE / numberNodes;
    }

    /**
     * @return the radius of the ring
     */
    private int getRadius() {
        
        int model_cells_size = graph_model.getCells().size();
        
        if (model_cells_size == 0) return DEFAULT_RADIUS;
        else return model_cells_size * DEFAULT_RADIUS;
    }

    /**
     * The position of the origin of the ring. This is an equal distance for
     * both X and Y
     * 
     * @return The origin of the ring
     */
    private int getOrigin() {

        int model_cells_size = graph_model.getCells().size();
        
        if (model_cells_size == 0) return DEFAULT_ORIGIN_OFFSET;
        else return model_cells_size * DEFAULT_ORIGIN_OFFSET;
    }

    /**
     * Sort the NodeCells in the CellDirectory into ascending key order
     */
    private void sortCells() {
        
        Collection allCells = cell_directory.getMap().values();
        cells = new Vector(allCells);
        Collections.sort(cells);
    }
}