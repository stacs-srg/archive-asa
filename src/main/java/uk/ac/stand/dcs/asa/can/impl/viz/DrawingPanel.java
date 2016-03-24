/*
 * Created on Apr 29, 2005 at 3:15:16 PM.
 */
package uk.ac.stand.dcs.asa.can.impl.viz;

import uk.ac.stand.dcs.asa.can.impl.Coord;
import uk.ac.stand.dcs.asa.can.impl.HyperCube;
import uk.ac.stand.dcs.asa.can.impl.Range;
import uk.ac.stand.dcs.asa.can.interfaces.CanNode;
import uk.ac.stand.dcs.asa.impl.Route;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.Iterator;

/**
 * @author al
 * 
 * JPanel to visualise a CAN network
 */
public class DrawingPanel extends JPanel {

    private static final int borderSize = 10;
    IP2PNode[] nodes;
    private Route route = null;
    private P2PSim sim;
    private CanNode first = null;
    private CanNode second = null;
    private Color myColour = Color.WHITE;

    public DrawingPanel( P2PSim sim ) {
        super();
        this.sim = sim;
        this.setSize(new Dimension(800,800));
        this.setLayout(null); // make an absolute layout
        nodes = null;
    }

    public void addNodes(IP2PNode[] nodes) {
        this.nodes = nodes;
        drawNodes();
    }
    
    public void setFirst( CanNode cn ) {
        first = cn;     
    }
    
    public void setSecond( CanNode cn ) {
        second = cn;
        if( first != null ) {
            Route r = sim.makeRoute(first, second.getKey());
            addRoute( r );
            sim.showRoute(first,second.getKey(),r );
            repaint();
        }
    }
    
    public void addRoute( Route r ) {
        this.route = r;
    }
    
    public void paint( Graphics g ) {
        super.paint(g);
        paintLines(g);
    }

    public void paintLines(Graphics g) {
        if( route != null ) {
            IP2PNode p2pn = route.getStart(); 
            CanNode last = check_is_can( p2pn );
	        Iterator i = route.iterator();
	        while( i.hasNext() ) {
	            CanNode next = check_is_can( i.next() );
	            drawLine( last,next, g );
	            last = next;
	        }
	        CanNode next = check_is_can( route.lastHop() );
	        drawLine( last,next, g );
        }
    }

    private void drawLine(CanNode first, CanNode second, Graphics g) {
        HyperCube first_hc = first.getHyperCube();
        HyperCube second_hc = second.getHyperCube();
        if (first_hc == null || second_hc == null ) {
            Error.hardError("Encountered null hyperCube instance");
        } else {
            Coord centre1 = first_hc.getCentre(); 
            Coord centre2 = second_hc.getCentre(); 
            BigInteger max = first_hc.getMaxValue();
            drawBigLine(centre1.getDimension(0), centre1.getDimension(1), centre2.getDimension(0), centre2.getDimension(1), max, g );
        }
	}


    private CanNode check_is_can(Object o) {
        if( ! ( o instanceof CanNode ) ) {
            Error.hardError("Encountered instance of " + o.getClass().getName() + " when expecting CanNode");  
        }
        return (CanNode) o;
    }

    public void drawNodes() {
        if (nodes != null) {
            int length = nodes.length;
            for (int i = 0; i < length; i++) {
                IP2PNode nextNode = nodes[i];
                if (!(nextNode instanceof CanNode)) {
                    Error.hardError("Encountered instance of "
                            + nextNode.getClass().getName()
                            + " when expecting CanNode");
                }
                CanNode nextCanNode = (CanNode) nextNode;
                drawCanNode( myColour, nextCanNode, i);
            }
        }
    }

	public void drawCanNode(Color colour, CanNode nextCanNode, int index) {
        HyperCube hc = nextCanNode.getHyperCube();
        if (hc == null) {
            Error.hardError("Encountered null hyperCube instance at node ");
        } else {
            Range[] range = hc.getDimensions(); // an array of upper and
                                                // lower bounds in each
                                                // dimension
            // TODO al - for the minute assume 2D
            BigInteger max = hc.getMaxValue();
            BigInteger x1 = range[0].lower;
            BigInteger x2 = range[0].upper;
            BigInteger y1 = range[1].lower;
            BigInteger y2 = range[1].upper;
            drawBigRectangle(x1, x2, y1, y2, max, colour, nextCanNode, index);
        }
	}


    private void drawBigLine(BigInteger x1, BigInteger y1 , BigInteger x2, BigInteger y2, BigInteger max, Graphics g) {
        //Rectangle bounding_rect = getBounds();
        int xd = 800;//bounding_rect.width;
        int yd = 800;//bounding_rect.height;
        BigInteger xdrawsize = new BigInteger( xd + "" );
        BigInteger ydrawsize = new BigInteger( yd + "" );
        
        int xx1 = (x1.multiply(xdrawsize)).divide(max).intValue()- borderSize;
        int yy1 = (y1.multiply(ydrawsize)).divide(max).intValue()- borderSize;
        int xx2 = (x2.multiply(xdrawsize)).divide(max).intValue()- borderSize;
        int yy2 = (y2.multiply(ydrawsize)).divide(max).intValue()- borderSize;
               
        g.setColor(Color.RED);
        g.drawLine( xx1,yy1, xx2,yy2 );
        g.fillOval( xx2-2,yy2-2,4,4 ); //draw a spot
    }

    
    private void drawBigRectangle(BigInteger x1, BigInteger x2, BigInteger y1,
            BigInteger y2, BigInteger max, Color colour, CanNode nextCanNode, int index) {
        Rectangle bounding_rect = getBounds();
        int xd = bounding_rect.width - ( 2 * borderSize );
        int yd = bounding_rect.height - ( 2 * borderSize );
        BigInteger xdrawsize = new BigInteger( xd + "" );
        BigInteger ydrawsize = new BigInteger( yd + "" );
        
        int xx1 = (x1.multiply(xdrawsize)).divide(max).intValue();
        int yy1 = (y1.multiply(ydrawsize)).divide(max).intValue();
        int xwidth = (x2.multiply(xdrawsize)).divide(max).intValue() - xx1;
        int ywidth = (y2.multiply(ydrawsize)).divide(max).intValue() - yy1;
               
        CanNodePanel cnp = new CanNodePanel( nextCanNode, index, borderSize + xx1, borderSize + yy1, xwidth, ywidth,colour );
        this.add( cnp );
		cnp.setLocation(xx1,yy1);
    }   
}