/*
 * Created on 24-Dec-2004
 */
package uk.ac.stand.dcs.asa.simulation.util;

import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
/**
 * @author stuart
 */
public class P2PSimWrapper {
	public P2PSim simulation;
	public IP2PNode nodes[];
	public int node_count;
	
	public P2PSimWrapper(P2PSim simulation){
		this.simulation=simulation;
		nodes=simulation.getNodes();
		node_count=nodes.length;
	}
	
}
