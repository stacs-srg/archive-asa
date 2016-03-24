/*
 * Created on 19-Jan-2005
 *
 */
package uk.ac.stand.dcs.asa.experiments;

import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.simulation.util.AllPathsToAllNodes;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.Output;
import uk.ac.stand.dcs.asa.util.StatsCalculator;

/**
 * @author stuart
 * 
 * This class displays the distribution of path lengths in physical distance and
 * hop counts for a specified node.
 * 
 * Command line parameters are: 1. Class name of the simulation factory 2.
 * Number of nodes in the simulation 3. Node number to be examined.
 */
public class ShowPathLengthDistributionForNode {

    public static void main(String[] args) {
        
        if (args.length < 3) Error.hardError("Usage: SimulationFactoryName nodecount nodenumber");
        
        String FactoryName = args[0];
        String node_count_string = args[1];
        int num_nodes = 0;
        try {
            num_nodes = Integer.parseInt(node_count_string);
        } catch (NumberFormatException e) {
            Error.hardError("Illegal parameter: " + node_count_string + "must be an integer");
        }
        
        String node_number_string = args[2];
        int nodeNumber = 0;
        try {
            nodeNumber = Integer.parseInt(node_number_string);
        } catch (NumberFormatException e) {
            Error.hardError("Illegal parameter: " + node_number_string + "must be an integer");
        }
        
        if (nodeNumber < 0 || nodeNumber >= num_nodes) {
            Error.hardError("Illegal parameter: " + node_number_string + "must be > 0 and < num_nodes");
        }
        
        Diagnostic.setLevel(Diagnostic.FINAL);

        SimulationFactory sf = null;
        try {
            sf = ((SimulationFactory) Class.forName(FactoryName).newInstance());
        } catch (Exception e) {
            Error.exceptionError("Cannot instantiate simulation factory", e);
        }
        
        P2PSim sim = sf.makeSimulation(num_nodes);

        AllPathsToAllNodes aptan = new AllPathsToAllNodes(sim, true);

        aptan.computePathsToNode(nodeNumber);

        Output ef = new Output();
        ef.outputExperimentalSetup(aptan.node_count, sf);
        aptan.showPathLengthDistribution(nodeNumber, 20, 0.1, ef);
        aptan.showHopCountDistributionForNode(nodeNumber, ef);

        double[] pathDistances = aptan.computePathLengthsForNode(nodeNumber);
        int[] hopCounts = aptan.computeHopCountsForNode(nodeNumber);
        double meanpd = StatsCalculator.mean(pathDistances);
        double meanhc = StatsCalculator.mean(hopCounts);

        ef.printlnSeparated("Mean path distance", meanpd);
        ef.printlnSeparated("Mean hop count", meanhc);
        ef.printlnSeparated("Average distance per hop", meanpd / meanhc);
    }
}