/*
 * Created on 03-Dec-2004
 *
 * Test of convergence of JChord nodes.
 */
package uk.ac.stand.dcs.asa.simulation.util;


import uk.ac.stand.dcs.asa.impl.StatsStruct;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/**
 * @author mjl
 */
public class ExpectedMeetDepth {

    public static StatsStruct nd0( DefaultMutableTreeNode t ) {
        Enumeration kids = t.children();
        if( t.getChildCount() == 0 ) return new StatsStruct( 1, 0 );
        int num = 1;
        double depth = 0;
        while( kids.hasMoreElements() ) {
        		StatsStruct k = nd0((DefaultMutableTreeNode) kids.nextElement());
        		num += k.size;
//        		depth += k.size * (k.size - 1) * (1 + k.depth);
        		depth += k.depth;
        }
//        return new StatsStruct( num, depth / num / (num -1) );
        return new StatsStruct( num, depth + num * (num -1) );
    }

	public static StatsStruct computeExpectedMeetDepth( DefaultMutableTreeNode t )
	{
		StatsStruct nd = nd0(t);
		return new StatsStruct( nd.size, nd.depth / nd.size / (nd.size - 1) - 1 );
	}

}
