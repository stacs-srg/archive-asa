/*
 * Created on Jan 25, 2005 at 3:10:59 PM.
 */
package uk.ac.stand.dcs.asa.plaxton.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.plaxton.impl.LeafSet;
import uk.ac.stand.dcs.asa.plaxton.impl.NeighbourhoodSet;
import uk.ac.stand.dcs.asa.plaxton.impl.NextHopResult;
import uk.ac.stand.dcs.asa.plaxton.impl.RoutingTable;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface PlaxtonRemote extends IP2PNode {
    // TODO these should all throw exceptions - like
    // JChordRemote - should be throws Exception..
    public void getStateFrom(PlaxtonRemote pni, int hop);
    public void receiveNewNodePublicity(PlaxtonRemote newNode);
    public NextHopResult nextHop(IKey k);
    public void addNeighbours(PlaxtonRemote[] nearbynodes);
    public void isAlive() throws Exception;
    public LeafSet getLeaves();
    public NeighbourhoodSet getNeighbours();
    public RoutingTable getRoutes();
}
