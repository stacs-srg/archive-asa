/*
 * Created on 07-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;

public interface DHTMaintenance{
    public boolean storeReplica(IKey k, Object o) throws P2PApplicationException;
    public boolean storeCached(IKey k, Object o) throws P2PApplicationException;
    public KeyValuePair[] getDataInRange(KeyRange kr) throws P2PApplicationException;
    
//    /** To be called by the new root node for the given range of keys.
//     * The idea here is that while the local node may be aware that 
//     * a new node has joined ther ring and taken over part of the local
//     * node's key space, the local node will treat data as local until
//     * it recieves confirmation (through a call to this method) that the
//     * new node holds the data.
//     * @throws P2PApplicationException 
//     */
//    public void rootForRange(P2PNode newRootNode, KeyRange kr) throws P2PApplicationException;
}
