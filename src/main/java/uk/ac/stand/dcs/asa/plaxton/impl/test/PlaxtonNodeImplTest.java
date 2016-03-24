/*
 * Created on 12-Jan-2005 at 19:15:13.
 */
package uk.ac.stand.dcs.asa.plaxton.impl.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.plaxton.factories.NoReplacementPolicyFactory;
import uk.ac.stand.dcs.asa.plaxton.impl.PlaxtonNodeImpl;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicyFactory;
import uk.ac.stand.dcs.asa.simulation.util.SimulatedPhysicalDistanceModel;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

import java.net.InetSocketAddress;


/**
 * @author al
 */
public class PlaxtonNodeImplTest extends TestCase {

    private static String ip1 = "10.0.1.1";
    private static int port1 = 34;
    private static IKey k1 = SHA1KeyFactory.generateKey(ip1 + port1);
    
    public void testPlaxtonNodeImpInit() {
        IDistanceCalculator dc = new SimulatedPhysicalDistanceModel(1);
        ReplacementPolicyFactory rpf = new NoReplacementPolicyFactory();
        
        new PlaxtonNodeImpl(new InetSocketAddress(ip1, port1),k1, dc,rpf);
        System.out.println( "testPlaxtonNodeImpInit finished" );
    }   
}
