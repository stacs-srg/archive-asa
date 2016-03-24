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
 * This class displays the mean path distance, and average hop count (both with std deviation) and
 * average hop distance for all paths to all nodes in a simulation.
 * This averages over all of the spanning trees in the simulation unlike ShowStatsForAllNodes
 * which shows the avarage values for each of the spanning trees.
 * 
 * Command line parameters are:
 * 1. Class name of the simulation factory
 * 2. Number of nodes in the simulation
 */
public class ShowAverageStatsForAllNodes {

	public static void main(String[] args) {
	    if( args.length <2 ) {
	        Error.hardError("Usage: SimulationFactoryName nodecount");
	    }
		    String FactoryName = args[0];
		    String node_count_string = args[1];
		    int num_nodes = 0;
		    try {
		        num_nodes = Integer.parseInt(node_count_string);
		    }
		    catch ( NumberFormatException e) {
		        Error.hardError("Illegal parameter: " + node_count_string + "must be an integer" );
	        }
			Diagnostic.setLevel(Diagnostic.NONE);

			Output ef = new Output();
			SimulationFactory sf=null;
			  
            try {
                sf = ((SimulationFactory)Class.forName( FactoryName ).newInstance());
               
            } catch (Exception e) {
                Error.exceptionError("Cannot instatiate simulation factory", e);
            }  
            if(sf!=null){
            	ef.printlnSeparated("Number of Nodes","Mean path distance","(stdev)","Mean hop count","stdev","Average hop length");
            	//for(int i=1000;i<=100000;i+=1000){
	            //	P2PSim sim = sf.makeSimulation(i);
            		P2PSim sim = sf.makeSimulation(num_nodes);
            		AllPathsToAllNodes aptan = new AllPathsToAllNodes(sim,true);
	                
	                //aptan.showStats();
	                //TreeViewGUI tvg = new TreeViewGUI(aptan.getPaths(), null,aptan.getStats() );
	                double[] apd = aptan.getAveragePysicalDistanceData();
	                double[] ahc = aptan.getAverageHopCountData();
	                
	                double pathDistanceMean=StatsCalculator.mean(apd);
	                double pathDistanceStdev=StatsCalculator.standardDeviation(apd);
	                double hopCountMean=StatsCalculator.mean(ahc);
	                double hopCountStdev=StatsCalculator.standardDeviation(ahc);
	                double averageHopLength=pathDistanceMean/hopCountMean;
	                
	                //ef.outputExperimentalSetup(aptan.node_count,sf);
	                ef.printlnSeparated(sim.getNodeCount(),pathDistanceMean,pathDistanceStdev,hopCountMean,hopCountStdev,averageHopLength);
	             
            	//}
            }
		
	}
}
