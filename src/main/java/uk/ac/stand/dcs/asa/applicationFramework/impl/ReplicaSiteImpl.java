/*
 * Created on 29-Nov-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.ReplicaSite;

import java.net.InetSocketAddress;

public class ReplicaSiteImpl implements ReplicaSite {
    
    private InetSocketAddress address;
 
    public ReplicaSiteImpl(InetSocketAddress address) {
        super();
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
