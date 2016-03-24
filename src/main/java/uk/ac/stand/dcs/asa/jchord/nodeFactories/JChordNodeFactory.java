/*
 * Created on Jan 18, 2005 at 5:07:00 PM.
 */
package uk.ac.stand.dcs.asa.jchord.nodeFactories;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.INodeFactory;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.jchord.impl.JChordNodeImpl;
import uk.ac.stand.dcs.asa.jchord.interfaces.IFingerTableFactory;

import java.net.InetSocketAddress;

/**
 * @author stuart/al
 */
public class JChordNodeFactory implements INodeFactory {
    
    private IFingerTableFactory finger_table_factory = null;
    
    public JChordNodeFactory(IFingerTableFactory finger_table_factory) {
        this.finger_table_factory = finger_table_factory;
    }
    
    public IP2PNode makeNode(InetSocketAddress hostAddress, IKey key) {
        
        return new JChordNodeImpl(hostAddress,key,finger_table_factory);
    }
}