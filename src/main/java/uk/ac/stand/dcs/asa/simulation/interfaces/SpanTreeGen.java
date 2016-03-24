/*
 * Created on 19-Jan-2005
 */
package uk.ac.stand.dcs.asa.simulation.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IKey;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author stuart
 */
public interface SpanTreeGen {
	public DefaultMutableTreeNode allPathsToNode(IKey k);
	public DefaultMutableTreeNode computeAllPathsToRandomNode();
}
