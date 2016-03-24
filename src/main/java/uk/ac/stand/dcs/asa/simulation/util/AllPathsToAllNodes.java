package uk.ac.stand.dcs.asa.simulation.util;

import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/**
 * This class takes a JCSim object and computes a number of statistics related
 * to the routing trees. For each node in the simulation 3 statistics are
 * computed.
 * <ul>
 * <li>The average physical distance of all routes to the node from each of the
 * nodes in the simulation. That is the total distance for all paths to the node
 * divided by the number of paths to the node (which is just the number of
 * nodes).</li>
 * <li>The average number of routing hops of all routes to the node from each
 * of the nodes in the simulation.</li>
 * <li>The depth of the expected meet points for all routes to a node.</li>
 * </ul>
 * 
 * @author stuart
 */

public class AllPathsToAllNodes extends AllPathsToRandomSample {
	private int popularity[];

	public AllPathsToAllNodes(P2PSim simulation, boolean showProgress) {
		super(simulation,showProgress,1.0);
		popularity=null;
	}
	
	private void updatePopularityStats(DefaultMutableTreeNode tree){
		Enumeration e=tree.breadthFirstEnumeration();
		while(e.hasMoreElements()){
			DefaultMutableTreeNode treenode=(DefaultMutableTreeNode)e.nextElement();
			IP2PNode node=(IP2PNode)treenode.getUserObject();
			try {
				int index=simulation.key2Index(node.getKey());
				popularity[index]+=treenode.getChildCount();
			} catch (Exception e1) {
				Error.exceptionError("JChordNode call threw exception", e1);
				e1.printStackTrace();
			}
		}
	}

	/**
	 * @return Returns the popularity.
	 */
	public int[] getPopularityData() {
		computeStats();
		return popularity;
	}
	
	public void computeStats(){
		if(averageHopCounts==null){
			SimProgressWindow progress=null;
			if(showProgress){
				progress=new SimProgressWindow("Computing stats for "+node_count+" nodes",0,node_count);
			}
			averageHopCounts=new double[node_count];
			averagePhysicalDistance=new double[node_count];
			depthsOfExpectedMeets=new double[node_count];
			popularity=new int[node_count];
			for(int i=0;i<node_count;i++){
				DefaultMutableTreeNode tree=computePathsToNode(i);
				averageHopCounts[i]=computeAverageHopCountForNode(tree);
				averagePhysicalDistance[i]=computeAveragePhysicalDistanceForNode(tree);
				depthsOfExpectedMeets[i]=computeExpectedMeetDepthForNode(tree);
				updatePopularityStats(tree);
				if(progress!=null){
					progress.incrementProgress();
				}
			}
			if(progress!=null){
				progress.dispose();
			}
		}
	}
	
	public String getStats(){
		if(stats==null){
			computeStats();
			stats=simulation.toString();
			stats+="\n\nColumns:\tN.I. (Node Index)\n";
			stats+="\t\tA.P.D. (Average Physical Distance)\n";
			stats+="\t\tA.H.C. (Average Hop Count)\n";
			stats+="\t\tE.M.D. (Expected Meet Depth)\n";
			stats+="\t\tPop. (Popularity)\n";
			stats+="\nN.I.\tA.P.D.\tA.H.C.\tE.M.D.\tPop.\tKey\n";
			for(int i=0;i<averagePhysicalDistance.length;i++){
				try {
					stats+=i+"\t"+df.format(averagePhysicalDistance[i])
							+"\t"+df.format(averageHopCounts[i])
							+"\t"+df.format(depthsOfExpectedMeets[i])
							+"\t"+df.format(popularity[i])
							+"\t"+nodes[i].getKey().bigIntegerRepresentation().toString(16)
							+"\n";
				} catch (Exception e) {
				    Error.exceptionError("JChordNode call threw exception", e);
					e.printStackTrace();
				}
			}
		}
		return stats;
	}	
}
