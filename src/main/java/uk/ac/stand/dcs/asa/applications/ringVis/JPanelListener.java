/*
 * Created on 03-Aug-2004
 */
package uk.ac.stand.dcs.asa.applications.ringVis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * @author sja7
 * Creates a JPanel object that has a image set as its background.  It 
 * listens for component events such as the user moving or resizing the component.  
 * On such an event, the component is repainted.
 */
public class JPanelListener extends JPanel implements ComponentListener{

	private Image background;
	
	/**
	 * @param background_image The image to be shown on this instantiation of JPanelListener
	 */
	public JPanelListener(Image background_image){
		addComponentListener(this);
		this.background = background_image;
	}

	/**
	 * Paints an Image (specified on instantiation) onto a specified graphic.
	 * @param g a graphic of a specified component
	 */
	public void paintComponent(Graphics g) {
		try {
			g.drawImage(background, 0, 0, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * METHODS REQUIRED TO IMPLEMENT COMPONENTLISTENER
	 */	
	public void componentHidden(ComponentEvent e) {
		this.repaint();
	}

	public void componentMoved(ComponentEvent e) {
		this.invalidate();
		paintComponent( this.getGraphics() );
		this.validate();
		this.repaint();
		
		
	}
	
	public void componentResized(ComponentEvent e) {
		this.repaint();
	}

	public void componentShown(ComponentEvent e) {
		this.repaint();
	}

}
