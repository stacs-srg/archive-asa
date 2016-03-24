package uk.ac.stand.dcs.asa.can.impl.viz;

import uk.ac.stand.dcs.asa.can.impl.NodeViewer;
import uk.ac.stand.dcs.asa.can.interfaces.CanNode;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CanNodePanel extends javax.swing.JPanel {

	private static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
	private static final Color DEFAULT_BACKGROUND_COLOR = Color.GREEN;
	private static final int BORDER_WIDTH = 1;
	public static final Font font = new Font( "Arial", 9, 9 );
	
	private Color borderColor = DEFAULT_BORDER_COLOR;
	private Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private CanNode myNode;
    private int index;
	
	public CanNodePanel( CanNode cn, int index, int x, int y, int xwidth, int ywidth, Color colour ) {
		super();
		this.myNode = cn;
		this.index = index;
		this.setLocation(x,y);
		this.setPreferredSize(new Dimension(xwidth,ywidth));
		this.setSize(new Dimension(xwidth,ywidth));
		initListener();
		setBackgroundColor(colour);
		this.setVisible(true);
	}
	
	private void initListener() {
	    this.setLayout(null);
		try {
			this.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
				    if( evt.isControlDown() ) {
				        rootMouseSetEnd();
				    } else if( evt.isShiftDown() ) {
				        rootMouseSetStart();
				    }
				    else {
				        rootMouseClicked(evt);
				    }
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setBorderColor(Color borderColor){
		this.borderColor = borderColor;
	}
	
	public void setBackgroundColor(Color backgroundColor){
		this.backgroundColor = backgroundColor;
	}	
	
	private void rootMouseClicked(MouseEvent evt) {
		NodeViewer nv = new NodeViewer(myNode);
		nv.showAll();
	}
	
    private void rootMouseSetStart() {
        ((DrawingPanel) this.getParent()).setFirst(myNode);    
    }

    private void rootMouseSetEnd() {
        ((DrawingPanel) this.getParent()).setSecond(myNode);
    }
    
	public void paint(Graphics g){
		super.paint(g);
		
		int width = this.getWidth();//size.width;
		int height = this.getHeight();//size.height;
		
		// Draw the background
		g.setColor(backgroundColor);
		g.fillRect(BORDER_WIDTH,BORDER_WIDTH,width-(2*BORDER_WIDTH),height-(2*BORDER_WIDTH));
		
		// Draw the border
		g.setColor(borderColor);
		if(BORDER_WIDTH == 1)
			g.drawRect(0,0,width,height);
		
		else{
			int max = Math.max(width,height);
			for(int i=0; i<BORDER_WIDTH && i<max/2; i++){
				g.drawRect(i,i,width-(i*2),height-(i*2));
			}
		}
		if( this.getHeight() > 9 && this.getWidth() > 12 ) { // only label if enough room
		    g.setFont( font );
		    g.drawString( index + "", 2, this.getHeight() - 5 );
		}	
	}
}
