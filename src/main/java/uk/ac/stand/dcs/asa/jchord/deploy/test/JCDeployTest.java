/*
 * Created on Apr 28, 2005 at 10:43:32 AM.
 */
package uk.ac.stand.dcs.asa.jchord.deploy.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.jchord.deploy.NodeLauncher;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.RemoteRRTRegistry;

import java.net.InetSocketAddress;

/**
 * Test class for JChord ring using actual deployment.
 *
 * @author graham
 */
public class JCDeployTest extends TestCase {
    
    /**
     * Hide diagnostics for all tests.
     */
    protected void setUp() throws Exception {
        
        Diagnostic.setLevel(Diagnostic.NONE);
    }
    
    /**
     * Tests the structural properties of a ring with one node.
     */
    public void testRing1Structure() {
        
        try {
        
            NodeLauncher launcher = new NodeLauncher();

            launcher.createAdminNodeProcess();

            launcher.launchLocalNodes(1, 1111, "localhost", false);

            IJChordRemote node1 = (IJChordRemote)(RemoteRRTRegistry.getInstance().getService(new InetSocketAddress("localhost", 1111), IJChordRemote.class));
            
            IJChordRemote node1_succ = node1.getSuccessor();
            
            assertTrue(node1.getKey().equals(node1_succ.getKey()));
            
        }
        catch (Exception e) { fail(e.getMessage()); }
        
        
//        
//        try {
//            // -----------------------------------------------------------
//            // Make a ring with one node.
//
//            JCSim sim = makeSimulation(1, 0, false);
//
//            // -----------------------------------------------------------
//            // Check the number of nodes.
//
//            JChordNode[] nodes = getNodesInKeyOrder(sim);
//
//            assertTrue(nodes.length == 1);
//            assertTrue(sim.getNodeCount() == 1);
//
//            // -----------------------------------------------------------
//            // Check the node's key.
//
//            Key k0 = nodes[0].getKey();
//            assertEquals("8334f9b826e9f5677ea0ce8579cd968d71882f1d", k0.toString());
//
//            // -----------------------------------------------------------
//            // Check the node's address.
//
//            assertEquals("0.0.0.0", nodes[0].getHostAddress().getAddress().getHostAddress());
//            assertEquals(0, nodes[0].getHostAddress().getPort());
//
//            // -----------------------------------------------------------
//            // The ring should be stable.
//
//            assertTrue(sim.ringIsStable());
//
//            // Don't take the nodes' word for it.
//            assertTrue(isRingStable(nodes));
//
//            // -----------------------------------------------------------
//            // Check successor lists.
//
//            assertTrue(areSuccessorListsConsistent(nodes));
//
//            // -----------------------------------------------------------
//            // The finger tables should be empty, because they haven't been set up yet.
//
//            assertTrue(areFingerTablesConsistent(nodes));
//            
//        } catch (Exception e) { fail(); }
    }
    

}