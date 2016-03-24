/*
 * Created on Feb 2, 2005 at 2:14:20 PM.
 */
package uk.ac.stand.dcs.asa.plaxton.factories;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.plaxton.impl.ClosestReplacementPolicy;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonNode;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicy;
import uk.ac.stand.dcs.asa.plaxton.interfaces.ReplacementPolicyFactory;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class ClosestReplacementPolicyFactory implements ReplacementPolicyFactory {

    private IDistanceCalculator dc;

    public ClosestReplacementPolicyFactory( IDistanceCalculator dc ) {
        this.dc = dc;
    }
    public ReplacementPolicy makeReplacementPolicy( PlaxtonNode node ) {
        return new ClosestReplacementPolicy( node,dc );
    }
}
