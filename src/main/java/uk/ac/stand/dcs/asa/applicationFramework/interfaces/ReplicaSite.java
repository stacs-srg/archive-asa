/*
 * Created on 29-Nov-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.interfaces;

import java.net.InetSocketAddress;

/**
 * Intended as a placeholder for a richer interface, whatever that may be.
 * 
 * The interface implemented by the framwork's representation of replica sites.
 * In the first instance this has a single method that returns the network
 * address of a replica site. With this address the appliation can do whatever
 * it needs to do to interact with the replica storeage serivices on that node.
 * 
 * @author stuart
 */
public interface ReplicaSite {
    public InetSocketAddress getAddress();
}
