/*
 * Created on Jan 20, 2005 at 3:37:32 PM.
 */
package uk.ac.stand.dcs.asa.experiments;

import uk.ac.stand.dcs.asa.impl.StatsStruct;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.simulation.util.BottomUpSpanTreeGen;
import uk.ac.stand.dcs.asa.simulation.util.ExpectedMeetDepth;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author al
 * 
 * This class displays the depth of an expected meet point for a
 * spanning tree of a randomly selected node in a simulation.
 * 
 * Command line parameters are:
 * 1. Class name of the simulation factory
 * 
 */
public class ShowExpectedMeetDepths {
    
    public static void main(String[] args) {

        if (args.length < 1) Error.hardError("Usage: SimulationFactoryName");
        
        String FactoryName = args[0];
        Diagnostic.setLevel(Diagnostic.FINAL);

        try {
            for (int i = 1; i <= 10; i++) {
                int numNodes = 100 * i;

                SimulationFactory sf = ((SimulationFactory) Class.forName(FactoryName).newInstance());
                P2PSim sim = sf.makeSimulation(numNodes);

                BottomUpSpanTreeGen bustg = new BottomUpSpanTreeGen(sim);
                DefaultMutableTreeNode tree = bustg.computeAllPathsToRandomNode();
                StatsStruct s = ExpectedMeetDepth.computeExpectedMeetDepth(tree);
                System.out.println("Nodes: " + 100 * i + " Expected depth on " + s.size + " nodes = " + s.depth);
                //TreeViewGUI tvg = new TreeViewGUI( tree );
            }

        } catch (Exception e) {
            Error.exceptionError("Cannot instantiate simulation factory", e);
        }
    }
}
