/*
 * Created on Feb 2, 2005 at 10:27:52 AM.
 */
package uk.ac.stand.dcs.asa.plaxton.impl;

import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonRemote;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicy;

/**
 * @author al
 */
public class NoReplacementPolicy implements ReplacementPolicy {
    
    public boolean replace(PlaxtonRemote originalNode, PlaxtonRemote newNode) {
        return false;
    }
}
