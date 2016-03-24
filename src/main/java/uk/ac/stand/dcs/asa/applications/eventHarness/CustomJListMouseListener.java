/*
 * Created on 10-Aug-2004
 */
package uk.ac.stand.dcs.asa.applications.eventHarness;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author stuart
 */
public class CustomJListMouseListener implements MouseListener {

	/**
	 * 
	 * @uml.property name="text"
	 * @uml.associationEnd 
	 * @uml.property name="text" multiplicity="(1 1)"
	 */
	private JTextField text;

	/**
	 * 
	 * @uml.property name="list"
	 * @uml.associationEnd 
	 * @uml.property name="list" multiplicity="(1 1)"
	 */
	private JList list;

	/**
	 * 
	 * @uml.property name="lm"
	 * @uml.associationEnd 
	 * @uml.property name="lm" multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private ListModel lm;

	public CustomJListMouseListener(JTextField text, JList list){
		this.text=text;
		this.list=list;
		lm=list.getModel();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {

		if (e.getClickCount() == 2) {
			int index = list.locationToIndex(e.getPoint());
			if(index>=0&&index<lm.getSize()){
				String msg = (String) lm.getElementAt(index);
				list.ensureIndexIsVisible(index);
				text.setText(msg);
				list.clearSelection();
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
        
        // No action
	}

	public void mouseExited(MouseEvent arg0) {
        
        // No action
	}

	public void mousePressed(MouseEvent arg0) {
        
        // No action
	}

	public void mouseReleased(MouseEvent arg0) {
        
        // No action
	}
}
