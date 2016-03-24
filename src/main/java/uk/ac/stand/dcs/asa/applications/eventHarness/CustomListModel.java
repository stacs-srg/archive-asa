/*
 * Created on 10-Aug-2004
 */
package uk.ac.stand.dcs.asa.applications.eventHarness;

import javax.swing.*;
import java.util.Vector;

/**
 * @author stuart
 */
public class CustomListModel extends AbstractListModel{
	private Vector v;

	public CustomListModel(Vector v) {
		this.v = v;
	}

	public void addElement(Object o) {
		v.addElement(o);
		 fireContentsChanged(this, getSize()-1, getSize()-1);
	}
	
	public void addElementAtStart(Object o){
		v.add(0,o);
		fireContentsChanged(this, 0, 0);
	}

	public Object remove(int index) {
		return v.remove(index);
		
	}

	public Object getElementAt(int index) {
		return v.get(index);
	}

	public int getSize() {
		return v.size();
	}
}
