package uk.ac.stand.dcs.asa.simulation.util;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SpanTreeGen;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.Output;

import javax.swing.tree.DefaultMutableTreeNode;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Random;

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

public class AllPathsToRandomSample extends P2PSimWrapper {
    
	private static int DEFAULT_NODE_SELECTION_SEED = 1664;
	
	private static String NUMBER_FORMAT = "0.000";
	
	protected String stats;
	protected int sampleSize;
	protected SpanTreeGen stg;
	protected IDistanceCalculator dc;
	//private DefaultMutableTreeNode paths[];
	protected double depthsOfExpectedMeets[];
	protected double averageHopCounts[];
	protected double averagePhysicalDistance[];
	protected int routingStateSize[];
	protected IP2PNode[] sampleNodes;
	//private int popularity[];
	protected boolean showProgress;
	protected DecimalFormat df;
	private Random nodeSelectionRandom;
	
	public AllPathsToRandomSample(P2PSim simulation, boolean showProgress, double sampleFraction, int seed) {
		super(simulation);
		this.showProgress=showProgress;
		dc=new SimulatedPhysicalDistanceModel(node_count);
		stg = simulation.getSpanTreeGen();
		df=new DecimalFormat(NUMBER_FORMAT);
		
		if(sampleFraction<0){
			this.sampleSize=0;
		}else{
			if(sampleFraction>1.0){
				this.sampleSize=node_count;
			}else{
				this.sampleSize=(int)(node_count*sampleFraction);
				if(this.sampleSize<2){
					sampleSize=2;
				}
			}
		}
		stats=null;
		nodeSelectionRandom=new Random(seed);
		depthsOfExpectedMeets=null;
		averageHopCounts=null;
		averagePhysicalDistance=null;
	}
	
	public AllPathsToRandomSample(P2PSim simulation, boolean showProgress, double sampleFraction) {
		this(simulation,showProgress,sampleFraction,DEFAULT_NODE_SELECTION_SEED);
	}
	
	/**
	 * This method will compute all of the lookup paths to a specified node. That is,
	 * the method computes the path to the specified node from each node in the simulation
	 * using a TopDownSpanTreeGen object.
	 * @param nodeIndex
	 * @return DefaultMutableTreeNode representing all lookup paths to the given node
	 */
	public DefaultMutableTreeNode computePathsToNode(int nodeIndex){
		DefaultMutableTreeNode result=null;
		IP2PNode node = nodes[nodeIndex];
		IKey k=null;
		try {
			k = node.getKey();
		} catch (Exception e) {
		    Error.exceptionError("P2PNode call threw exception", e);
			e.printStackTrace();
		}
		result=stg.allPathsToNode(k);
		return result;
	}
	
	protected double computeExpectedMeetDepthForNode(DefaultMutableTreeNode tree){
		return ExpectedMeetDepth.computeExpectedMeetDepth(tree).depth;
	}
	
	protected double computeAverageHopCountForNode(DefaultMutableTreeNode tree){
		int totalHops=0;
		for(Enumeration e=tree.depthFirstEnumeration();e.hasMoreElements();){
			DefaultMutableTreeNode startNode=(DefaultMutableTreeNode)e.nextElement();
			totalHops+=startNode.getUserObjectPath().length-1;
		}
		return ((double)totalHops)/node_count;
	}
	
	private double pathLength(DefaultMutableTreeNode startNode){
		double distance=0.0;
		//note that the path returned by getUserObjectPath() has
		// the target node
		//at index 0 and the source node at index length-1;
		Object[] pathToRoot = startNode.getUserObjectPath();
		int len = pathToRoot.length;
		InetAddress sourceIP = null;
		try {
			sourceIP = ((IP2PNode) pathToRoot[len - 1]).getHostAddress().getAddress();
		} catch (Exception e1) {
		    Error.exceptionError("JChordNode call threw exception", e1);
			e1.printStackTrace();
		}
		if (len > 1) {
			for (int j = len - 2; j >= 0; j--) {
				try {
					distance += dc.distance(
							sourceIP, ((IP2PNode) pathToRoot[j]).getHostAddress().getAddress());
				} catch (Exception e2) {
				    Error.exceptionError("JChordNode call threw exception", e2);
				}
			}
		}
		
		return distance;
	}

	protected double computeAveragePhysicalDistanceForNode(DefaultMutableTreeNode tree) {
		double totalDistance = 0.0;
		for (Enumeration e = tree.depthFirstEnumeration(); e.hasMoreElements();) {
			DefaultMutableTreeNode startNode = (DefaultMutableTreeNode) 
			e.nextElement();
			totalDistance += pathLength(startNode);
		}
		return totalDistance / node_count;
	}
	
//	private void computePopularityForNode(int nodeIndex, DefaultMutableTreeNode tree){
//		Enumeration e=tree.breadthFirstEnumeration();
//		while(e.hasMoreElements()){
//			DefaultMutableTreeNode treenode=(DefaultMutableTreeNode)e.nextElement();
//			P2PNode node=(P2PNode)treenode.getUserObject();
//			try {
//				int index=simulation.key2Index(node.getKey());
//				popularity[index]+=treenode.getChildCount();
//			} catch (Exception e1) {
//				Error.exceptionError("AllPathsToAllNodes::computePopularity", "JChordNode call threw exception", e1);
//				e1.printStackTrace();
//			}
//		}	
//	}
	
	public void computeStats(){
		if(averageHopCounts==null){
			int[] done=new int[sampleSize];
			Arrays.fill(done,-1);
			
			SimProgressWindow progress=null;
			
			if(showProgress){
				progress=new SimProgressWindow("Computing stats for "+sampleSize+" random nodes from "+node_count,0,sampleSize);
			}
			
			sampleNodes=new IP2PNode[sampleSize];
			averageHopCounts=new double[sampleSize];
			averagePhysicalDistance=new double[sampleSize];
			depthsOfExpectedMeets=new double[sampleSize];
			routingStateSize=new int[sampleSize];
			Arrays.fill(averageHopCounts,-1.0);
			Arrays.fill(averagePhysicalDistance,-1.0);
			Arrays.fill(depthsOfExpectedMeets,-1.0);
			Arrays.fill(routingStateSize,0);
			
			for(int i=0;i<sampleSize;i++){
				int index=-1;
				while(index<0){
					int candidate = nodeSelectionRandom.nextInt(node_count);
					if(Arrays.binarySearch(done,candidate)<0){
						index=candidate;
						done[done.length-1]=index;
						Arrays.sort(done);
					}
				}
				sampleNodes[i]=nodes[index];
				DefaultMutableTreeNode tree=computePathsToNode(i);
				averageHopCounts[i]=computeAverageHopCountForNode(tree);
				averagePhysicalDistance[i]=computeAveragePhysicalDistanceForNode(tree);
				depthsOfExpectedMeets[i]=computeExpectedMeetDepthForNode(tree);
				routingStateSize[i]=nodes[i].routingStateSize();
				
				if(progress!=null){
					progress.incrementProgress();
				}
			}
			if(progress!=null){
				progress.dispose();
			}
		}
	}
	
	public int[] computeHopCountsForNode(int nodeNum){
		if(nodeNum>=0 && nodeNum<node_count){
			DefaultMutableTreeNode t=computePathsToNode(nodeNum);
			if(t.getLevel()==0){
				int[] hopCounts = new int[node_count+1];
				Arrays.fill(hopCounts,0);
				Enumeration e=t.breadthFirstEnumeration();
				int nodeCount=0;
				while(e.hasMoreElements()){
					DefaultMutableTreeNode current = (DefaultMutableTreeNode)e.nextElement();
					int level=current.getLevel();
					hopCounts[nodeCount++]+=level;
				}
				return hopCounts;
			}else{
				Error.hardError("The specified DefaultMutableTreeNode t for node "+nodeNum+" is not the absolute root of the tree.");
			}
			
		}
		return null;
	}
	
	public double[] computePathLengthsForNode(int nodeNum){
		if(nodeNum>=0 && nodeNum<node_count){
			DefaultMutableTreeNode t=computePathsToNode(nodeNum);
			if(t.getLevel()==0){
				double[] pathLengths= new double[node_count+1];
				Arrays.fill(pathLengths,0.0);
				Enumeration e=t.breadthFirstEnumeration();
				int nodeCount=0;
				while(e.hasMoreElements()){
					DefaultMutableTreeNode current = (DefaultMutableTreeNode)e.nextElement();
					pathLengths[nodeCount++]+=pathLength(current);
				}
				System.out.println( "*******" + nodeCount );
				return pathLengths;
			}else{
				Error.hardError("The specified DefaultMutableTreeNode t for node "+nodeNum+" is not the absolute root of the tree.");
			}
		}
		return null;
	}
	
	/**
	 * Prints a table in two tab-separated columns detailing
	 * the distribution of path lengths (in number of hops)
	 * for a specified spanning-tree. 
	 */
	public void showHopCountDistributionForNode(int nodeNumber, Output ef){
		if(nodeNumber>=0 && nodeNumber<node_count){
			int[] computedHopCounts = computeHopCountsForNode(nodeNumber);
			DefaultMutableTreeNode t=computePathsToNode(nodeNumber);
			int depth=t.getDepth();
			int hopCountBuckets[]=new int[depth+1];
			Arrays.fill(hopCountBuckets,0);
			for(int i=0;i<computedHopCounts.length;i++){
				hopCountBuckets[computedHopCounts[i]]++;
			}
		
			ef.printlnSeparated("Hop count distribution for node",nodeNumber);	
			ef.printlnSeparated("Path Length in Hops","Number of Paths");
			for(int i=0;i<hopCountBuckets.length;i++){
				ef.printlnSeparated(Integer.toString(i),hopCountBuckets[i]);		
			}
//			double average = StatsCalculator.mean(computedHopCounts);
//			ef.outputNameValue("Average number of hops",average);	
		}else{
			Error.error("illegal node number");
		}
	}
	
	/**
	 * Prints a table in two tab-separated columns detailing
	 * the distribution of path lengths (in real distance)
	 * for a specified spanning-tree. 
	 */
	public void showPathLengthDistribution(int nodeNumber, int partitions, double interval, Output ef){
		
		if(nodeNumber>=0 && nodeNumber<node_count){
			double[] computedPathLengths=computePathLengthsForNode(nodeNumber);
			int buckets[]=new int[partitions+1];
			Arrays.fill(buckets,0);
			for(int i=0;i<node_count;i++){
				double div=computedPathLengths[i]/interval;
				double rem=computedPathLengths[i]%interval;
				int index=rem>0.0?(int)div:(int)div-1;
				if(index<0)index=0;
				if(index<partitions){
					buckets[index]++;
				}else{
					buckets[partitions]++;
				}
			}
			DecimalFormat df = new DecimalFormat("#.#");
			ef.printlnSeparated("Hop count distribution for node",nodeNumber);	
			ef.printlnSeparated("Path Distance Range","Number of Paths");
			ef.printlnSeparated("X<="+df.format(interval),buckets[0]);
			for(int i=1;i<partitions;i++){
				ef.printlnSeparated(df.format(interval*i)+"<X<="+df.format(interval*(i+1)),buckets[i]);
			}
			ef.printlnSeparated("X>"+df.format(interval*partitions),buckets[partitions]);
			
//			double averagePathDistance=StatsCalculator.mean(computedPathLengths);
//			ef.outputSeparatedPair("Mean path distance",averagePathDistance);
		}else{
			Error.error("illegal node number");
		}
	}
	
	/**
	 * @return Returns the averageHopCounts.
	 */
	public IP2PNode[] getSampleNodeData() {
		computeStats();
		return sampleNodes;
	}
	
	/**
	 * @return Returns the averageHopCounts.
	 */
	public double[] getAverageHopCountData() {
		computeStats();
		return averageHopCounts;
	}
	
	/**
	 * @return Returns the averagePysicalDistance.
	 */
	public double[] getAveragePysicalDistanceData() {
		computeStats();
		return averagePhysicalDistance;
	}
	
	/**
	 * @return Returns the depthsOfExpectedMeets.
	 */
	public double[] getDepthOfExpectedMeetsData() {
		computeStats();
		return depthsOfExpectedMeets;
	}
	/**
	 * @return Returns the routingStateSize.
	 */
	public int[] getRoutingStateSizeData() {
		computeStats();
		return routingStateSize;
	}
	
}
