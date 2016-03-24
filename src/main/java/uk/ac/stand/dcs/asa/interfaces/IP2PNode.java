/*
 * Created on Jan 18, 2005 at 3:42:43 PM.
 */
package uk.ac.stand.dcs.asa.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;

import java.net.InetSocketAddress;



/**
 * @author al, graham, stuart
 */
public interface IP2PNode {
	
	public InetSocketAddress getHostAddress() throws Exception;
    public boolean hasFailed();
    public int routingStateSize();
    public IKey getKey();
    public IP2PNode lookup(IKey k) throws P2PNodeException;
}
