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
public class ThreeCanNodes2DTest extends TestCase {

    private static String ip1 = "10.0.1.1";
    private static int port1 = 34;
    private static IKey k1 = SHA1KeyFactory.generateKey(ip1 + port1);
    private static String ip2 = "10.0.1.2";
    private static int port2 = 35;
    private static IKey k2 = SHA1KeyFactory.generateKey(ip2 + port2);
    private static String ip3 = "10.0.1.3";
    private static int port3 = 36;
    private static IKey k3 = SHA1KeyFactory.generateKey(ip3 + port3);
    
    public void testCanNodeImpInit() {
        System.out.println( "testCanNodeImpInit start" );
        CanNodeImpl cni1 = new CanNodeImpl(new InetSocketAddress(ip1,port1),k1,  2, 80);
        cni1.createNetwork();
        System.out.println( "One node:" );
        
        NodeViewer nv1 = new NodeViewer(cni1);
        nv1.showAll();
        
        CanNodeImpl cni2 = new CanNodeImpl(new InetSocketAddress(ip2,port2),k2,  2, 80);
        cni2.join( cni1 );
        System.out.println( "Two nodes:" );

        NodeViewer nv2 = new NodeViewer(cni2);
        nv1.showAll();
        nv2.showAll();  
        
        CanNodeImpl cni3 = new CanNodeImpl(new InetSocketAddress(ip3,port3),k3,  2, 80);
        cni3.join( cni2 ); 
        System.out.println( "Three nodes:" );

        NodeViewer nv3 = new NodeViewer(cni3);
        nv1.showAll();
        nv2.showAll();  
        nv3.showAll();          

        System.out.println( "testCanNodeImpInit finished" );
    }   
    
    
}
