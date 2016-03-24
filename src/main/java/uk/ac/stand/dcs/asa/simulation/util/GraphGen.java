/*
 * Created on 03-Dec-2004
 *
 * Test of convergence of JChord nodes.
 */
package uk.ac.stand.dcs.asa.simulation.util;

import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/**
 * @author al
 */
public class GraphGen{

	private P2PSim simulation;
    /**
     * @param simulation
     */
    public GraphGen(P2PSim simulation) {
        this.simulation=simulation;
    }

    /**
     * generates a dot file for GraphViz tool
     * @param tree - the tree to visualise.
     */
    public void show_dot_file(DefaultMutableTreeNode tree, String label) {
        //get the index for the root node
        IP2PNode jcn = (IP2PNode)tree.getUserObject();
        int root = 0;
        try {
            root = simulation.key2Index(jcn.getKey());
        } catch (Exception e) {
            Error.exceptionError("JChordNode.getKey threw exception", e);
        }
        System.out.println( "digraph \"\" {" ); // the first thing in a dot file
        System.out.println( "node[style=filled,width=.125,height=.375,fontsize=9];");
        System.out.println( "graph[overlap=false,fontname=\"Helvetica-Oblique\",fontsize=12,label=\""+label+"\"];");
        show_paths( tree );
        show_ranking(root,5);
        show_colouringRGB(tree,tree.getDepth(),0x811480,0xFE0906,0xFDE800);
        System.out.println( "}" ); // the last thing in a dot file
    }

    // TODO obsolete code?
//    private void show_colouringHSV(DefaultMutableTreeNode tree) {
//        //a [color="0.348 0.839 0.839"];
//        Enumeration e = tree.depthFirstEnumeration();
//        while(e.hasMoreElements()){
//            DefaultMutableTreeNode current = (DefaultMutableTreeNode) e.nextElement();
//            P2PNode jcn=(P2PNode) current.getUserObject();
//            double level=current.getLevel();
//            int index = 0;
//            try {
//                index = simulation.key2Index(jcn.getKey());
//            } catch (Exception e1) {
//                Error.exceptionError("GraphGen::show_colouringHSV", "JChordNode.getKey threw exception", e1);
//            }
//            double colourVal= 1;
//            if(level!=0){
//                colourVal=0.999/level;
//            }
//            System.out.println(index + " [color=\"" + colourVal +" 1.000 1.000\"];");
//        }
//    }
	
    private void show_colouringRGB(DefaultMutableTreeNode tree, int depth, int colourFar, int colourNear, int rootColour) {
        int colourBase = colourFar > colourNear ? colourNear : colourFar;
        int colourRange = Math.abs(colourFar-colourNear);
        //int colourRange = colourFar > colourNear ? colourFar-colourNear : colourNear-colourFar;
        int colourInc=colourRange/depth;
        
        Enumeration e = tree.depthFirstEnumeration();
        while(e.hasMoreElements()){
            DefaultMutableTreeNode current = (DefaultMutableTreeNode) e.nextElement();
            IP2PNode jcn=(IP2PNode) current.getUserObject();
            int level=current.getLevel();
            int index = 0;
            try {
                index = simulation.key2Index(jcn.getKey());
            } catch (Exception e1) {
                Error.exceptionError("error getting node representation", e1);
            }
            int colourVal;
            if(e.hasMoreElements()){
                if(colourInc!=0){
                    colourVal=colourBase+(level*colourInc);
                }else{
                    colourVal=colourBase;
                }
            }else{
                colourVal=rootColour;
            }
            String str = Integer.toHexString( colourVal  & 0xffffff);
            String colourStr = "#" + "000000".substring( str.length() ) + str.toUpperCase();
            
            System.out.println(index + " [color=\"" + colourStr  +"\"];");
        }
    }

	/**
     * 
     */
    private void show_ranking(int root, int distanceInc) {
    	int length = simulation.getNodes().length;
    	int count=length/2;
    	System.out.print("{rank=same; ");
    	for(int i=0;i<=count;i++){
			
    		if(i%distanceInc==1){
				System.out.print("{rank=same; ");
			}
    		
    		int leftIndex=root-i;
			if(leftIndex<0){
				leftIndex+=length;
			}

			int rightIndex=root+i;
			if(rightIndex>=length){
				rightIndex-=length;
			}
			
			System.out.print(leftIndex + " ");
			if(rightIndex!=leftIndex){
				System.out.print(rightIndex + " ");
			}
			
			if(i%distanceInc==0){
				System.out.print(";}\n");
			}
		}
    }

    /**
     * generates nodes for dot file for GraphViz tool
     * @param tree - the subtree to visualise.
     */
    private void show_paths(DefaultMutableTreeNode tree) {
        try {
            if (tree != null) {
            	IP2PNode nextNode = (IP2PNode) tree.getUserObject();
                int rootrep = simulation.key2Index(nextNode.getKey());
                Enumeration children = tree.children();
                while (children.hasMoreElements()) {
                    DefaultMutableTreeNode next = (DefaultMutableTreeNode) children.nextElement();
                    IP2PNode childNode = (IP2PNode) next.getUserObject();
                    int childrep = simulation.key2Index(childNode.getKey());
                    System.out.println(childrep + " -> " + rootrep);
                    show_paths(next);
                }
            }
        } catch (Exception e) {
            Error.exceptionError("error getting node representation", e);
        }
    }
}
