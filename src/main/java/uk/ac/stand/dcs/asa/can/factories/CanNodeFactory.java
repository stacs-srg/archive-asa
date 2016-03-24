/*
 * Created on March 29, 2005 at 5:07:00 PM.
 */
package uk.ac.stand.dcs.asa.can.factories;

import uk.ac.stand.dcs.asa.can.impl.CanNodeImpl;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.INodeFactory;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;

import java.net.InetSocketAddress;


/**
 * Builds a CanNodeImpl
 * @author stuart/al
 */
public class CanNodeFactory implements INodeFactory {
    
    private int dimensions;
    private int bitsPerDimension;
    
    public CanNodeFactory(int dimensions, int bitsPerDimension ) {
        this.dimensions = dimensions;
        this.bitsPerDimension = bitsPerDimension;
    }
    
    public IP2PNode makeNode(InetSocketAddress hostAddress, IKey key){
        return new CanNodeImpl(hostAddress,key,dimensions,bitsPerDimension ) ;
    }
}
