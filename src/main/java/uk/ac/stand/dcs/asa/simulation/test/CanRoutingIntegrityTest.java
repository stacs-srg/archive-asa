/*
 * Created on Dec 20, 2004 at 10:14:33 PM.
 */
package uk.ac.stand.dcs.asa.simulation.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.impl.Route;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.simulation.simulationFactories.CanSimulationFactoryDimension2;
import uk.ac.stand.dcs.asa.simulation.util.SimProgressWindow;
import uk.ac.stand.dcs.asa.util.Diagnostic;

/**
 * @author al
 */
public class CanRoutingIntegrityTest extends TestCase {
	
	public static String CanFactoryName = CanSimulationFactoryDimension2.class.getName();
	public static int num_nodes = 200;	// 40 works OK
	private static boolean showProgress = false;
	
	public void testRoutes() throws Exception {
		SimulationFactory sf = null;
		
		SimProgressWindow progress=null;
		if(showProgress){
			progress=new SimProgressWindow("Computing stats for "+num_nodes+" nodes",0,num_nodes);
		}
		
		Diagnostic.setLevel(Diagnostic.RUN);
		sf = ((SimulationFactory)Class.forName( CanFactoryName ).newInstance());
		
		P2PSim sim = sf.makeSimulation(num_nodes); 
		
		IP2PNode[] nodes = sim.getNodes();
		
		for( int i = 0; i < nodes.length; i++ ) {
			IP2PNode target = nodes[i];
			IKey target_key = target.getKey();
			
			if(progress!=null){
				progress.incrementProgress();
			}
			
			for( int j = 0; j < nodes.length; j++ ) {
			
			// Now and try to route to the target from each of the others.
			
				IP2PNode source = nodes[j];
				System.out.println( "Routing from\t" + source + " to:\tt " + target ); 
				Route rt = sim.makeRoute(source,target_key );	// route from nodes[j] to target_key

				// all being well this should find the target node.
				if( rt.lastHop() != target ) {
					System.out.println("Final hop in route does not match specified target. Routing from:\n\t " 
							+ source + "\nto:\n\t " + target + "\nFinal hop in route is:\n\t" + rt.lastHop());
					sim.showRoute( source,target_key,rt );
					fail();
				}
			}
		}
	}	
	
	// TODO obsolete?
//	private void displayAll(P2PSim sim) {
//		IP2PNode[] nodes = sim.getNodes();
//		for( int j = 0; j < nodes.length; j++ ) {
//			sim.showNode(j);
//		}
//	}
}