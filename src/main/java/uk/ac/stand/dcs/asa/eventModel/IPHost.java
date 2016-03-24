package uk.ac.stand.dcs.asa.eventModel;

import uk.ac.stand.dcs.asa.util.FormatHostInfo;

import java.net.InetSocketAddress;

public class IPHost extends NetworkHost {
    
	public InetSocketAddress inet_socket_address;
	
    public String string_rep = null;

	/**
	 * Default constructor for use by the RRT when deserializing.
	 */
	public IPHost() { /* Required for RRT transmission. */ }
	
	public IPHost(InetSocketAddress inet_socket_address) {
	    this.inet_socket_address = inet_socket_address;
	}
	
    /**
     * @return the address and port of the host
     */
    public InetSocketAddress getInet_sock_addr() {
    	    	
        return inet_socket_address;
    }

    public String toString() {
        
        if (string_rep == null) string_rep = FormatHostInfo.formatHostName(inet_socket_address);
        return string_rep;
    }
}
