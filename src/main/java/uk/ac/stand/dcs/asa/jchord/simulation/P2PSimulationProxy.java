/*
 * Created on Jan 19, 2005 at 4:22:40 PM.
 */
package uk.ac.stand.dcs.asa.jchord.simulation;

import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.util.Error;

/**
 * @author al
 */
public class P2PSimulationProxy {

    private P2PSim sim;

    public P2PSimulationProxy() {
        this.sim = null;
    }

    public P2PSim getSim() {
        if (sim == null) {
            Error.hardError("P2PSim sim is null.");
        }
        return sim;
    }

    public void setSim(P2PSim sim) {
        if (this.sim == null) {
            this.sim = sim;
        }
    }
}
