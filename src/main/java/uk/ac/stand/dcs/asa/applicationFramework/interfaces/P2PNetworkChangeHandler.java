/*
 * Created on 07-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.interfaces;

import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;

import java.util.ArrayList;

public interface P2PNetworkChangeHandler {
    /**
     * This method is called when the local framework detects a change in P2P
     * network that increases the range of the key space for which the local 
     * P2P node is the root.
     * 
     * @param currentRange  range of keys that map to this node under the
     *                      P2P network's routing mechanisms.
     * @param diff  an inclusive range (includes upper and lower bounds)
     *              of the new keys for which this node is now the root
     */
    public void increasedLocalKeyRange(KeyRange currentRange, KeyRange diff);
    
    /**
     * This method is called when the local framework detects a change in P2P
     * network that decreases the range of the key space for which the local 
     * P2P node is the root.
     * 
     * @param currentRange  range of keys that map to this node under the
     *                      P2P network's routing mechanisms.
     * @param diff  an inclusive range (includes upper and lower bounds)
     *              of the new keys for which this node is nolonger the root
     */
    public void decreasedLocalKeyRange(KeyRange currentRange, KeyRange diff);
    
    /**
     * 
     * @param replicas
     * @param addedSites an array of ArrayLists of added sites 
     * @param removedSites an array of ArrayLists of removed sites
     */
    public void replicaSetChange(ReplicaInfo[] replicas, ArrayList[] addedSites, ArrayList[] removedSites);
}
