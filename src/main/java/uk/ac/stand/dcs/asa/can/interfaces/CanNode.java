/*
 * Created on Jan 10, 2005 at 5:02:24 PM.
 */
package uk.ac.stand.dcs.asa.can.interfaces;

import uk.ac.stand.dcs.asa.can.impl.Coord;
import uk.ac.stand.dcs.asa.can.impl.HyperCube;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface CanNode extends CanRemote {
    public abstract void join(CanRemote knownNode);
    public abstract void setFailed(boolean failed);
    public abstract HyperCube getHyperCube();		// TODO replace with interface
    public abstract Coord getCentre();				// TODO replace with interface
    public abstract void createNetwork();
}
