/*
 * Created on 12-Jan-2005 at 19:15:13.
 */
package uk.ac.stand.dcs.asa.can.impl.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.can.impl.CanNodeImpl;
import uk.ac.stand.dcs.asa.can.impl.NodeViewer;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

import java.net.InetSocketAddress;


/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class OneCanNode4DTest extends TestCase {

    private static String ip1 = "10.0.1.1";
    private static int port1 = 34;
    private static IKey k1 = SHA1KeyFactory.generateKey(ip1 + port1);
    
    
    public void testCanNodeImpInit() {
        CanNodeImpl cni = new CanNodeImpl(new InetSocketAddress(ip1,port1),k1,  4, 40);
        cni.createNetwork();
        NodeViewer nv = new NodeViewer(cni);
        nv.showAll();
        System.out.println( "testCanNodeImpInit finished" );
    }   
}
