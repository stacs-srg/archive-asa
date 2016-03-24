/*
 * Created on Feb 2, 2005 at 2:20:06 PM.
 */
package uk.ac.stand.dcs.asa.plaxton.interfaces;


/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface ReplacementPolicyFactory {
    public abstract ReplacementPolicy makeReplacementPolicy(PlaxtonNode node);
}