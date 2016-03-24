/*
 * Created on Feb 2, 2005 at 10:27:52 AM.
 */
package uk.ac.stand.dcs.asa.plaxton.impl;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonNode;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonRemote;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicy;
import uk.ac.stand.dcs.asa.util.Error;

import java.net.InetAddress;

/**
 * @author al
 */
public class ClosestReplacementPolicy implements ReplacementPolicy {

    private IDistanceCalculator dc;
    private InetAddress nodeAddress;
    
    public ClosestReplacementPolicy( PlaxtonNode node, IDistanceCalculator dc ) {

        try {
            nodeAddress = node.getHostAddress().getAddress();
        } catch (Exception e) {
            Error.exceptionError("Cannot obtain inet address of owner",e);
        }
        this.dc = dc;
    }
    
    public boolean replace(PlaxtonRemote originalNode, PlaxtonRemote newNode) {
        try {
            InetAddress inoriginal = originalNode.getHostAddress().getAddress();
            InetAddress innewnode = newNode.getHostAddress().getAddress();
            return dc.distance(nodeAddress, inoriginal) > dc.distance(
                    nodeAddress, innewnode);
        } catch (Exception e) {
            	Error.exceptionError("Cannot obtain inet address of nodes",e);
            	return false;
        }
    }
}
