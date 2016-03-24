/*
 * Created on Jan 28, 2005 at 2:53:45 PM.
 */
package uk.ac.stand.dcs.asa.util;

import uk.ac.stand.dcs.asa.interfaces.IFailureSuspector;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;

import java.net.InetSocketAddress;

/**
 * Failure suspector that tries to ping the suspected node.
 *
 * @author graham
 */
public class TimeoutFailureSuspector implements IFailureSuspector {

    public boolean hasFailed(IP2PNode node) {
        
        // TODO implement check based on ping timeout
        return true;
    }

    public boolean hasFailed(InetSocketAddress rep) {


        // TODO implement check based on ping timeout
        return true;
    }
}
