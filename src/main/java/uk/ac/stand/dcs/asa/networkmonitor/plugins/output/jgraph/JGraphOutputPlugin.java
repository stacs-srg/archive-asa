/*
 * Created on Jun 21, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output.jgraph;


import org.jgraph.JGraph;
import org.jgraph.graph.*;
import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.P2PHost;
import uk.ac.stand.dcs.asa.networkmonitor.core.*;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.JChordAttributes;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.output.GraphButtonsPanel;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.output.OutputPlugin;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * Output plugin which uses JGraph (http://www.jgraph.com/) to visualise the network.
 * 
 * @author gjh1
 */
public class JGraphOutputPlugin extends OutputPlugin {
    static class JGraphOutputPluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new JGraphOutputPlugin();
        }
    }

    private static final int CELL_HEIGHT = 20;
    private static final int CELL_WIDTH = 90;
    private static final int DEFAULT_ORIGIN_OFFSET = 75;
    private static final int DEFAULT_RADIUS = 50;
    private static final double DEGREES_IN_CIRCLE = 360;
    private static final String FRIENDLY_NAME = "JGraph Output";
    private static final String LOG_PREFIX_overridden = "[JGraph] ";
    private static final String SYSTEM_NAME = "JGraphOutput";
    private static final String VERSION = "1.0.0.0";

    static {
        hasUserInterface = true;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, JGraphOutputPlugin.class, new JGraphOutputPluginFactory(), hasUserInterface);
    }

    private GraphButtonsPanel buttonPanel;
    private JGraph graph;
    private JScrollPane graphScroller;
    private GraphModel model;

    public String getFriendlyName() {
        return FRIENDLY_NAME;
    }

    public String getSystemName() {
        return SYSTEM_NAME;
    }

    public String getVersion() {
        return VERSION;
    }

    public void load() {
        super.load();
        Diagnostic.traceNoSource(LOG_PREFIX_overridden + "Version is: " + JGraph.VERSION, Diagnostic.FINAL);
        pluginPanel = new JPanel();
        pluginPanel.setLayout(new BorderLayout());
        pluginPanel.setPreferredSize(new Dimension(640, 480));
        buttonPanel = new GraphButtonsPanel() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == buttonPanel.getZoomInButton()) {
                    // Zoom in the graph display
                    graph.setScale(graph.getScale() + 0.1);
                    Diagnostic.traceNoSource(LOG_PREFIX_overridden + "Scale is now " + graph.getScale(), Diagnostic.FULL);
                }
                else if (e.getSource() == buttonPanel.getZoomOutButton()) {
                    // Zoom out the graph display
                    graph.setScale(graph.getScale() - 0.1);
                    Diagnostic.traceNoSource(LOG_PREFIX_overridden + "Scale is now " + graph.getScale(), Diagnostic.FULL);
                }
            }};
        pluginPanel.add(buttonPanel, BorderLayout.PAGE_START);
        model = new DefaultGraphModel();
        graph = new JGraph(model);
        // Allow heavyweight Swing components to appear in vertices
        graph.getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {
            protected VertexView createVertexView(Object cell) {
                if (cell instanceof DefaultGraphCell)
                    return new JGraphHeavyweightView(cell);
                else
                    return super.createVertexView(cell);
        }});
        // Ensure mouse events are delivered to the appropriate components
        graph.addMouseListener(new JGraphHeavyweightRedirector());
        // Register the JGraph instance for tooltip management
        ToolTipManager.sharedInstance().registerComponent(graph);
//        graph.setEnabled(false);
        graph.setEditable(false);
        graph.setPortsVisible(false);
        graphScroller = new JScrollPane(graph);
        graphScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        graphScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pluginPanel.add(graphScroller, BorderLayout.CENTER);
        importExistingNodesAndConnections();
    }

    public void receiveEvent(Event event) {
        String eventType = event.getType();
        if (eventType.equals("ADD_NODE")) {
            Node addedNode = (Node) event.get(Attributes.NODE);
            addNode(addedNode);
        }

        else if (eventType.equals("REMOVE_NODE")) {
            Node removedNode = (Node) event.get(Attributes.NODE);
            removeNode(removedNode);
        }

        else if (eventType.equals("ADD_CONNECTION")) {
            Connection connection = (Connection) event.get(Attributes.CONNECTION);
            if (connection.getAttribute(Attributes.TYPE).equals(JChordAttributes.PREDECESSOR)) {
                updatePredecessor(connection.getSource(), connection.getTarget());
            }
            else if (connection.getAttribute(Attributes.TYPE).equals(JChordAttributes.SUCCESSOR)) {
                updateSuccessor(connection.getSource(), connection.getTarget());
            }
        }

        else if (eventType.equals("OVERLAY")) {
            if (event.get(Attributes.OVERLAY).equals("GeoIPOverlay")) {
                Node node = (Node) event.get(Attributes.NODE);
                node.setAttribute("COUNTRY_NAME", event.get("COUNTRY_NAME"));
            }
            else if (event.get(Attributes.OVERLAY).equals("NodeSystemInfoOverlay")) {
                P2PHost reportingHost = (P2PHost) event.get("source");
                String reportingHostName = FormatHostInfo.formatHostName(reportingHost);
                Node node = ModelImpl.getInstance().getNodeMatching(Attributes.HOST, reportingHostName);
                if (node != null) {
                    node.setAttribute("NODE_INFO", event.get("OSName") + " " + event.get("OSVersion"));
                    node.setAttribute("JAVA_INFO", event.get("JavaVendor") + " " + event.get("JavaVersion"));
                    node.setAttribute("AVAILABLE_PROCESSORS", event.get("AvailableProcessors"));
                }
            }
        }
    }

    /**
     * Add a new node to the graph.
     * 
     * @param node the node to add
     */
    private void addNode(Node node) {
        // Create cell
//        DefaultGraphCell cell = new DefaultGraphCell(node);
        DefaultGraphCell cell = new DefaultGraphCell(new NodeComponent(node));
        GraphConstants.setBounds(cell.getAttributes(), new Rectangle(CELL_WIDTH, CELL_HEIGHT));
        GraphConstants.setBackground(cell.getAttributes(), Color.GREEN);
        GraphConstants.setOpaque(cell.getAttributes(), true);
        GraphConstants.setBorderColor(cell.getAttributes(), Color.BLACK);
        GraphConstants.setAutoSize(cell.getAttributes(), true);
        // Create port
        DefaultPort port = new DefaultPort();
        cell.add(port);
        port.setParent(cell);
        graph.getGraphLayoutCache().insert(cell);
        layout();
    }

    /**
     * Draws a directed edge between two nodes in the graph.
     * 
     * @param source the source node
     * @param target the target node
     * @param edgeColour the colour to draw the edge in
     * @param data the data object to be associated with the edge
     */
    private void drawEdge(DefaultGraphCell source, DefaultGraphCell target, Color edgeColour, JChordEdge data) {
        DefaultEdge edge = new DefaultEdge(data);
        edge.setSource(source.getChildAt(0));
        edge.setTarget(target.getChildAt(0));
        // Use improved routing algoritm unless this edge is a loop
        if (source != target) {
            GraphConstants.setRouting(edge.getAttributes(), JGraphParallelEdgeRouter.getSharedInstance());
        }
        else {
            GraphConstants.setRouting(edge.getAttributes(), GraphConstants.ROUTING_SIMPLE);
        }
        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        GraphConstants.setLineColor(edge.getAttributes(), edgeColour);
        graph.getGraphLayoutCache().insert(edge);
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
     * Determine the JGraph cell object for the given node.
     * 
     * @param node the <code>Node</code> object to search for
     * @return the corresponding JGraph cell
     */
    private DefaultGraphCell getCellForNode(Node node) {
        Object[] cells = graph.getGraphLayoutCache().getCells(false, true, false, false);
        for (int i = 0; i < cells.length; i++) {
            DefaultGraphCell cell = (DefaultGraphCell) cells[i];
            if (((NodeComponent) cell.getUserObject()).getNode() == node) {
                return cell;
            }
        }
        return null;
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
     * The position of the origin of the ring. This is an equal distance for
     * both X and Y
     * 
     * @return The origin of the ring
     */
    private int getOrigin() {
        int model_cells_size = ModelImpl.getInstance().getNodes().size();
        if (model_cells_size == 0) return DEFAULT_ORIGIN_OFFSET;
        else return model_cells_size * DEFAULT_ORIGIN_OFFSET;
    }

    /**
     * @return the radius of the ring
     */
    private int getRadius() {
        int model_cells_size = ModelImpl.getInstance().getNodes().size();
        if (model_cells_size == 0) return DEFAULT_RADIUS;
        else return model_cells_size * DEFAULT_RADIUS;
    }

    /**
     * Add any nodes and connections currently in the model into the graph.
     */
    private void importExistingNodesAndConnections() {
        // Add existing nodes to the graph
        Set existingNodes = ModelImpl.getInstance().getNodes();
        Iterator iNodes = existingNodes.iterator();
        while (iNodes.hasNext()) {
            addNode((Node) iNodes.next());
        }
        // Add existing connections to the graph
        Set existingConnections = ModelImpl.getInstance().getConnections();
        Iterator iConnections = existingConnections.iterator();
        while (iConnections.hasNext()) {
            Connection connection = (Connection) iConnections.next();
            String type = (String) connection.getAttribute(Attributes.TYPE);
            Node source = connection.getSource();
            Node target = connection.getTarget();
            if (type.equals(JChordAttributes.PREDECESSOR))
                updatePredecessor(source, target);
            else if (type.equals(JChordAttributes.SUCCESSOR))
                updateSuccessor(source, target);
        }
    }

    /**
     * Arrange the nodes in the typical JChord ring layout.
     */
    private void layout() {
        // Sort the nodes by ascending key order
        SortedSet sortedNodes = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                String firstNodeKey = (String) ((Node) o1).getAttribute(JChordAttributes.KEY);
                String secondNodeKey = (String) ((Node) o2).getAttribute(JChordAttributes.KEY);
                return firstNodeKey.compareTo(secondNodeKey);
            }});
        Object[] cells = graph.getGraphLayoutCache().getCells(false, true, false, false);
        for (int j = 0; j < cells.length; j++) {
            Object uo = ((DefaultGraphCell) cells[j]).getUserObject();
            if (uo instanceof NodeComponent)
                sortedNodes.add(((NodeComponent) uo).getNode());
        }
        int count = 0;
        Iterator i = sortedNodes.iterator();
        while (i.hasNext()) {
            // Calculate the position of each node in the ring
            double currentAngle = count * getAngle(sortedNodes.size());
            
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
            Node nextNode = (Node) i.next();
            DefaultGraphCell nextCell = getCellForNode(nextNode);
            GraphConstants.setBounds(nextCell.getAttributes(), new Rectangle(xpos, ypos, CELL_WIDTH, CELL_HEIGHT));
            GraphConstants.setResize(nextCell.getAttributes(), true);
            graph.getGraphLayoutCache().editCell(nextCell, nextCell.getAttributes());
            count++;
        }
    }

    /**
     * Remove a node from the graph.
     * 
     * @param node the node to remove
     */
    private void removeNode(Node node) {
        Object[] cells = graph.getGraphLayoutCache().getCells(false, true, false, false);
        for (int i = 0; i < cells.length; i++) {
            DefaultGraphCell cell = (DefaultGraphCell) cells[i];
            if (cell.getUserObject().equals(node)) {
                graph.getGraphLayoutCache().remove(new Object[] {cell}, true, true);
                layout();
            }
        }
    }

    /**
     * Update a node's predecessor pointer.
     * 
     * @param current the node being updated
     * @param pred the node the predecessor pointer should point to
     */
    private void updatePredecessor(Node current, Node pred) {
        if ((current != null) && (pred != null)) {
            List currentEdges = graph.getGraphLayoutCache().getOutgoingEdges(getCellForNode(current), null, false, true);
            for (Iterator i = currentEdges.iterator(); i.hasNext();) {
                DefaultEdge edgeToCheck = (DefaultEdge) i.next();
                String edgeType = (String) ((JChordEdge) edgeToCheck.getUserObject()).getAttribute(Attributes.TYPE);
                if (edgeType.equals(JChordAttributes.PREDECESSOR)) {
                    graph.getGraphLayoutCache().remove(new Object[] {edgeToCheck}, true, false);
                }
            }
            current.setAttribute(JChordAttributes.PREDECESSOR, pred);
            DefaultGraphCell sourceCell = getCellForNode(current);
            DefaultGraphCell targetCell = getCellForNode(pred);
            JChordEdge edgeData = new JChordEdge();
            edgeData.setAttribute(Attributes.TYPE, JChordAttributes.PREDECESSOR);
            edgeData.setAttribute(Attributes.LABEL, "Pred");
            if (sourceCell != null && targetCell != null)
                drawEdge(sourceCell, targetCell, Color.RED, edgeData);
        }
    }
    
    /**
     * Update a node's successor pointer.
     * 
     * @param current the node being updated
     * @param succ the node the successor pointer should point to
     */
    private void updateSuccessor(Node current, Node succ) {
        if ((current != null) && (succ != null)) {
            List currentEdges = graph.getGraphLayoutCache().getOutgoingEdges(getCellForNode(current), null, false, true);
            for (Iterator i = currentEdges.iterator(); i.hasNext();) {
                DefaultEdge edgeToCheck = (DefaultEdge) i.next();
                String edgeType = (String) ((JChordEdge) edgeToCheck.getUserObject()).getAttribute(Attributes.TYPE);
                if (edgeType.equals(JChordAttributes.SUCCESSOR)) {
                    graph.getGraphLayoutCache().remove(new Object[] {edgeToCheck}, true, false);
                }
            }
            current.setAttribute(JChordAttributes.SUCCESSOR, succ);
            DefaultGraphCell sourceCell = getCellForNode(current);
            DefaultGraphCell targetCell = getCellForNode(succ);
            JChordEdge edgeData = new JChordEdge();
            edgeData.setAttribute(Attributes.TYPE, JChordAttributes.SUCCESSOR);
            edgeData.setAttribute(Attributes.LABEL, "Succ");
            if (sourceCell != null && targetCell != null)
                drawEdge(sourceCell, targetCell, Color.BLUE, edgeData);
        }
    }
}
