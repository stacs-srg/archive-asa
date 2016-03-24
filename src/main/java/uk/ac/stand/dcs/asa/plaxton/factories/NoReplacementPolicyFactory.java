/*
 * Created on Feb 2, 2005 at 2:14:20 PM.
 */
package uk.ac.stand.dcs.asa.plaxton.factories;

import uk.ac.stand.dcs.asa.plaxton.impl.NoReplacementPolicy;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonNode;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicy;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicyFactory;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class NoReplacementPolicyFactory implements ReplacementPolicyFactory {

    public ReplacementPolicy makeReplacementPolicy( PlaxtonNode node ) {
        return new NoReplacementPolicy();
    }
}
