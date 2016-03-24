/*
 * Created on 12-Jan-2005 at 19:15:13.
 */
package uk.ac.stand.dcs.asa.plaxton.impl.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.plaxton.factories.NoReplacementPolicyFactory;
import uk.ac.stand.dcs.asa.plaxton.impl.NodeViewer;
import uk.ac.stand.dcs.asa.plaxton.impl.PlaxtonNodeImpl;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicyFactory;
import uk.ac.stand.dcs.asa.simulation.util.SimulatedPhysicalDistanceModel;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

import java.net.InetSocketAddress;

/**
 * @author al
 */
public class PlaxtonThreeNodeImplTest extends TestCase {

    private static String ip1 = "10.0.1.1";
    private static int port1 = 34;
    private static IKey k1 = SHA1KeyFactory.generateKey(ip1 + port1);
    private static String ip2 = "10.0.1.2";
    private static int port2 = 35;
    private static IKey k2 = SHA1KeyFactory.generateKey(ip2 + port2);
    private static String ip3 = "10.0.1.3";
    private static int port3 = 36;
    private static IKey k3 = SHA1KeyFactory.generateKey(ip3 + port3);
    
    public void testThreePlaxtonNodes() {
        IDistanceCalculator dc = new SimulatedPhysicalDistanceModel(3);
        ReplacementPolicyFactory rpf = new NoReplacementPolicyFactory();
        PlaxtonNodeImpl pni1 = new PlaxtonNodeImpl(new InetSocketAddress(ip1, port1),k1, dc,rpf);
        PlaxtonNodeImpl pni2 = new PlaxtonNodeImpl(new InetSocketAddress(ip2, port2),k2, dc,rpf);
        pni2.join(pni1);
        PlaxtonNodeImpl pni3 = new PlaxtonNodeImpl(new InetSocketAddress(ip3, port3),k3, dc,rpf);
        pni3.join(pni2);
        System.out.println( "_________STATE____________" );
        NodeViewer ni1 = new NodeViewer(pni1);
        ni1.showAll();
        NodeViewer ni2 = new NodeViewer(pni2);
        ni2.showAll(); 
        NodeViewer ni3 = new NodeViewer(pni3);
        ni3.showAll(); 
        System.out.println( "testThreePlaxtonNodes finished" );
    }    
}
