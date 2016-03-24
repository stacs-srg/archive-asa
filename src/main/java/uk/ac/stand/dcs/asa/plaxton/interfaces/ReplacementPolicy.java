/*
 * Created on Feb 2, 2005 at 9:49:31 AM.
 */
package uk.ac.stand.dcs.asa.plaxton.interfaces;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface ReplacementPolicy {
    public boolean replace( PlaxtonRemote originalNode, PlaxtonRemote newNode );
}
