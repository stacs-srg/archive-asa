/*
 * Created on Jul 29, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output.java2d;


import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.networkmonitor.core.ModelImpl;
import uk.ac.stand.dcs.asa.networkmonitor.core.Node;
import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.JChordAttributes;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.output.GraphButtonsPanel;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.output.OutputPlugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Output plugin which uses the Java 2D API directly to visualise the network.
 * 
 * @author gjh1
 */
public class Java2DOutputPlugin extends OutputPlugin {
    static class Java2DOutputPluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new Java2DOutputPlugin();
        }
    }

    private class DrawingPanel extends JPanel {
        private static final int DEFAULT_RADIUS = 50;
        private static final int NODE_HEIGHT = 80;
        private static final int NODE_LINE_WIDTH = 2;
        private static final int NODE_RADIUS = 10;
        private static final int NODE_WIDTH = 80;
        private Point origin = new Point();
        private double scaleFactor = 1.0;

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
            if (currentAngle > 0 && currentAngle <= 180) return origin.x + length;
            else return origin.x - length;
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
            if (currentAngle > 90 && currentAngle <= 270) return origin.y + length;
            else return origin.y - length;
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
         * @return the radius of the ring
         */
        private int getRadius() {
            int model_cells_size = ModelImpl.getInstance().getNodes().size();
            if (model_cells_size == 0) return DEFAULT_RADIUS;
            else return model_cells_size * DEFAULT_RADIUS;
        }

        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D surface = (Graphics2D) g;
            surface.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int originX = getPreferredSize().width / 2 ;
            int originY = getPreferredSize().height / 2;
            origin.setLocation(originX, originY);
            paintNodes(surface);
        }

        private void paintNodes(Graphics2D surface) {
            surface.setStroke(new BasicStroke((float) (NODE_LINE_WIDTH * scaleFactor)));
            // Sort the nodes by ascending key order
            SortedSet sortedNodes = new TreeSet(new Comparator() {
                public int compare(Object o1, Object o2) {
                    String firstNodeKey = (String) ((Node) o1).getAttribute(JChordAttributes.KEY);
                    String secondNodeKey = (String) ((Node) o2).getAttribute(JChordAttributes.KEY);
                    return firstNodeKey.compareTo(secondNodeKey);
                }});
            sortedNodes.addAll(ModelImpl.getInstance().getNodes());
            int count = 0;
            Iterator i = sortedNodes.iterator();
            while (i.hasNext()) {
                // Calculate the position of each node in the ring
                double currentAngle = count * (360 / sortedNodes.size());

                int xPosition = 0;
                int yPosition = 0;

                if (currentAngle <= 90) {
                    xPosition = getAbsoluteXPos(currentAngle, getOpposite(currentAngle));
                    yPosition = getAbsoluteYPos(currentAngle, getAdjacent(currentAngle));
                }
                else if (currentAngle <= 180) {
                    xPosition = getAbsoluteXPos(currentAngle, getOpposite(180 - currentAngle));
                    yPosition = getAbsoluteYPos(currentAngle, getAdjacent(180 - currentAngle));
                }
                else if (currentAngle <= 270) {
                    xPosition = getAbsoluteXPos(currentAngle, getOpposite(currentAngle - 180));
                    yPosition = getAbsoluteYPos(currentAngle, getAdjacent(currentAngle - 180));
                }
                else if (currentAngle <= 360) {
                    xPosition = getAbsoluteXPos(currentAngle, getOpposite(360 - currentAngle));
                    yPosition = getAbsoluteYPos(currentAngle, getAdjacent(360 - currentAngle));
                }

                i.next();
                int nodeWidth = (int) (NODE_WIDTH * scaleFactor);
                int nodeHeight = (int) (NODE_HEIGHT * scaleFactor);
                int nodeRadius = (int) (NODE_RADIUS * scaleFactor);
                surface.drawRoundRect(xPosition, yPosition, nodeWidth, nodeHeight, nodeRadius, nodeRadius);
                surface.drawString(String.valueOf(count), xPosition, yPosition);
                count++;
            }
        }

        /**
         * Make the drawing appear larger on screen.
         */
        public void zoomIn() {
            scaleFactor *= 2.0;
            int newHeight = getPreferredSize().height * 2;
            int newWidth = getPreferredSize().width * 2;
            setPreferredSize(new Dimension(newWidth, newHeight));
        }

        /**
         * Make the drawing appear smaller on screen.
         */
        public void zoomOut() {
            scaleFactor *= 0.5;
            int newHeight = getPreferredSize().height / 2;
            int newWidth = getPreferredSize().width / 2;
            setPreferredSize(new Dimension(newWidth, newHeight));
        }
    }

    private static final String FRIENDLY_NAME = "Java 2D Output";
    private static final String SYSTEM_NAME = "Java2DOutput";
    private static final String VERSION = "1.0.0.0";

    static {
        hasUserInterface = true;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, Java2DOutputPlugin.class, new Java2DOutputPluginFactory(), hasUserInterface);
    }

    private GraphButtonsPanel buttonPanel;
    private DrawingPanel graph;
    private JScrollPane graphScroller;

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
        pluginPanel = new JPanel();
        pluginPanel.setLayout(new BorderLayout());
        buttonPanel = new GraphButtonsPanel() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == buttonPanel.getZoomInButton())
                    graph.zoomIn();
                else if (e.getSource() == buttonPanel.getZoomOutButton())
                    graph.zoomOut();
                graph.revalidate();
                graph.repaint();
            }};
        pluginPanel.add(buttonPanel, BorderLayout.PAGE_START);
        graph = new DrawingPanel();
        graph.setPreferredSize(new Dimension(640, 480));
        graphScroller = new JScrollPane(graph);
        graphScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        graphScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pluginPanel.add(graphScroller, BorderLayout.CENTER);
    }

    public void receiveEvent(Event event) {
        // TODO Auto-generated method stub
    }
}
