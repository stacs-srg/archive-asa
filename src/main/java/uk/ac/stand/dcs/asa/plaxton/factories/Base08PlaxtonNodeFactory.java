/*
 * Created on Jan 18, 2005 at 5:07:00 PM.
 */
package uk.ac.stand.dcs.asa.plaxton.factories;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.INodeFactory;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.plaxton.impl.PlaxtonNodeImpl;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicyFactory;

import java.net.InetSocketAddress;


/**
 * Builds a PlaxtonImpl using a radix of 8
 * @author stuart/al
 */
public class Base08PlaxtonNodeFactory implements INodeFactory {
    
    private IDistanceCalculator dc;
    private ReplacementPolicyFactory rpf;
    
    public Base08PlaxtonNodeFactory(IDistanceCalculator dc, ReplacementPolicyFactory rpf) {
        this.dc = dc;
        this.rpf = rpf;
    }
    
    public IP2PNode makeNode(InetSocketAddress hostAddress, IKey key){
        return new PlaxtonNodeImpl(hostAddress,key,dc,rpf,8) ;
    }
}
