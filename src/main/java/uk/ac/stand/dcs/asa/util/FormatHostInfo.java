/*
 * Created on Apr 20, 2005 at 11:33:00 AM.
 */
package uk.ac.stand.dcs.asa.util;

import uk.ac.stand.dcs.asa.eventModel.P2PHost;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;

import java.net.InetSocketAddress;

/**
 * @author al, stuart, graham
 *
 * Methods to provide consistent formatting of host information.
 */
public class FormatHostInfo {

    public static String formatHostName(InetSocketAddress hostAddress) {
        if(hostAddress!=null){
            int port=hostAddress.getPort();
            String host=hostAddress.getAddress().getHostAddress();
            return formatHostName(host,port);
        }else{
            return null;
        }
    }
    
    public static String formatHostName(P2PHost host) {
        return formatHostName(host.getInet_sock_addr());
    }
    
    public static String formatHostName(IP2PNode node) {
        try {
            return formatHostName(node.getHostAddress());
        } catch (Exception e) {
            return "unknown host";
        }
    }

    public static String formatHostName(String host, int port) {
        return host + ":" + port;
    }
}
