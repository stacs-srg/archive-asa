/*
 * Created on 04-Dec-2004 at 16:34:25.
 */
package uk.ac.stand.dcs.asa.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.Comparator;


/**
 * @author al, stuart
 *
 * A Comparitor class for comparing P2PNode objects via their keys
 */
public class NodeComparator implements Comparator {

    public int compare(Object arg0, Object arg1) {
        if( arg1 == null ) {
            return 1;
        }
        if( arg0 == null ) {
            return -1;
        }
        IKey k0 = null;
        IKey k1 = null;
        try {
            k0 = ((IP2PNode)(arg0)).getKey();
            k1 = ((IP2PNode)(arg1)).getKey();
        } catch (Exception e1) {
            Error.exceptionError( "Exception getting key for NodeComparison", e1 );
            // don't need to do anything here - next lines takes care of it.
        }
        if( k1 == null ) {
            return 1;
        }
        if( k0 == null ) {
            return -1;
        }
        return (k0.compareTo(k1));
    }

}
