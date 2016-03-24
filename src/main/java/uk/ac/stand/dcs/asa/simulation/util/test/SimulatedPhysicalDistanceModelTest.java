/*
 * Created on Feb 1, 2005 at 1:52:57 PM.
 */
package uk.ac.stand.dcs.asa.simulation.util.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.simulation.util.SimulatedPhysicalDistanceModel;

import java.net.InetAddress;

/**
 * Test class for SimulatedPhysicalDistanceModel.
 *
 * @author graham
 */
public class SimulatedPhysicalDistanceModelTest extends TestCase {

    public void testDistance() {
        
	    try {
            InetAddress ip1 = InetAddress.getByName("0.0.1.1");
            InetAddress ip2 = InetAddress.getByName("0.0.0.17");

            SimulatedPhysicalDistanceModel s = new SimulatedPhysicalDistanceModel(600);

            assertEquals(0.4, s.distance(ip1, ip2), 0.0);
            assertEquals(0.4, s.distance(ip2, ip1), 0.0);
        }
	    catch (Exception e) { fail(); }
    }
}
