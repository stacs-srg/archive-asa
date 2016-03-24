/*
 * Created on Jan 25, 2005 at 3:10:59 PM.
 */
package uk.ac.stand.dcs.asa.can.interfaces;

import uk.ac.stand.dcs.asa.can.impl.HyperCube;
import uk.ac.stand.dcs.asa.can.impl.NextHopResult;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface CanRemote extends IP2PNode {
    // TODO these should all throw exceptions - like
    // JChordRemote - should be throws Exception..
    public NextHopResult nextHop(IKey k);
    public void isAlive() throws Exception;
    /**
     * @param impl
     */
    public HyperCube requestSplit(CanRemote impl);
    public abstract int getDimensions();
}
