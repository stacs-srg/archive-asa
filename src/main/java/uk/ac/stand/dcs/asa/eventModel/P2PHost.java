/*
 * Created on Apr 20, 2005 at 9:27:14 AM.
 */
package uk.ac.stand.dcs.asa.eventModel;

import uk.ac.stand.dcs.asa.interfaces.IKey;

import java.net.InetSocketAddress;

/**
 * @author al, stuart
 *
 * Class extends IPHost to add Key functionality.
 */

public class P2PHost extends IPHost {

    public IKey key;
    
	/**
	 * Default constructor for use by the RRT when deserializing.
	 */
	public P2PHost() { /* Required for RRT transmission. */ }
	
    public P2PHost(InetSocketAddress inet_sock_addr, IKey key) {
    	
        super(inet_sock_addr);
        this.key = key;
    }

    public IKey getKey() {
        return key;
    }
}
