/*
 * Created on 03-Dec-2004
 *
 * Test of convergence of JChord nodes.
 */
package uk.ac.stand.dcs.asa.simulation.util;

import uk.ac.stand.dcs.asa.impl.Route;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author mjl
 */
public class BottomUpSpanTreeGen extends P2PSimWrapper {
    private HashMap sofar;
    /**
     * @param simulation
     */
    public BottomUpSpanTreeGen(P2PSim simulation) {
    	super(simulation);
    	sofar = new HashMap();
    }

	public DefaultMutableTreeNode computeAllPathsToRandomNode() {
	    Diagnostic.trace(Diagnostic.RUN);
	    IKey k = SHA1KeyFactory.generateKey(Integer.toString(simulation.getRand().nextInt()));
	    Route rt = simulation.makeRoute( nodes[1],k );    // route from 1 to k.
	    IKey rootKey = null;
        try {
            rootKey = (rt.lastHop()).getKey();
        } catch (Exception e) {
            Error.exceptionError("error getting node representation", e);
        }

	    DefaultMutableTreeNode  tree = new DefaultMutableTreeNode( rt.lastHop() );  // set up a new tree - root is the target of route
	    for( int i = 0 ; i < node_count ; i++ ) {
		    //if(i!=rootIndex)
		    {
		    		rt = simulation.makeRoute( nodes[i],k ); // find a route to k from node i
		    		addRouteToTree( rootKey ,tree,rt );
		    		simulation.showRoute( nodes[i],k,rt );  
		    	}
	    }
	    return tree;
	}
	
	protected void addRouteToTree( IKey target, DefaultMutableTreeNode tree, Route rt ) {
		try {
			if( tree == null || rt == null ) {
				Error.hardError("Empty root/route passed");
			}
			if( !sofar.containsKey(target) ) sofar.put( target, tree.getRoot() );
			rt.addHopAtStart(rt.getStart());			
			DefaultMutableTreeNode tail = null;
			Iterator the_route = rt.iterator();
			while( the_route.hasNext() ) {
				IP2PNode j = (IP2PNode) the_route.next();
				IKey key = j.getKey();
				if( sofar.containsKey(key) ) {
					if( tail != null ) ((DefaultMutableTreeNode) sofar.get(key)).add(tail);
					return;
				}
				DefaultMutableTreeNode t = new DefaultMutableTreeNode(j);
				if( tail != null ) t.add(tail);
				tail = t;
				sofar.put( key, tail );
			}
			Error.hardError("Roots do not match");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
 
    protected DefaultMutableTreeNode addNodeToTree(IP2PNode j, DefaultMutableTreeNode tree){
    	  IKey nextKey = null;
        try {
            nextKey = j.getKey();
        } catch (Exception e) {
            Error.exceptionError("error getting node representation", e);
        }
        // lookup key in current node and see if JChord node is in tree already
          DefaultMutableTreeNode child = findChild( tree,nextKey );
          if( child == null ) { // next node is not in tree so add it as a child of current tree
              child = new DefaultMutableTreeNode( j );
              tree.add( child );
          }
          return child;
    }
    
    /**
     * @param tree a DefaultMutableTreeNode containing a partially build tree
     * @param key - the key for which are searching.
     * @return a child of node tree containing a JChordNode with key key
     */
    protected DefaultMutableTreeNode findChild(DefaultMutableTreeNode tree, IKey key) {
        Enumeration children = tree.children();
        while( children.hasMoreElements() ) {
            DefaultMutableTreeNode next = (DefaultMutableTreeNode) children.nextElement();
            IP2PNode nextNode = (IP2PNode) next.getUserObject();
            try {
                if( key.compareTo( nextNode.getKey() ) == 0 ) {
                    return next;
                }
            } catch (Exception e) { Error.exceptionError("error getting node representation", e); }
        }
        // we haven't found it.
        return null;
    }
}