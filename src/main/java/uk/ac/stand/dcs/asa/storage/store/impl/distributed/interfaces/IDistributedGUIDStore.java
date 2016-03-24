/*
 * Created on 17-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;


/**
 * This interface defines the set of operations avaiable to a remote client of
 * the distributed store. That is, a client that does not itself maintain a 
 * local StoreComponent object and is not part of the P2P network.
 * 
 * @author stuart
 */
public interface IDistributedGUIDStore {
    //  IStore Operations with exceptions
    public IData get( IPID pid ) throws P2PApplicationException, P2PNodeException, StoreGetException;
    public IPID put( IData data ) throws P2PApplicationException, P2PNodeException, StorePutException;
    public long getPIDPutDate( IPID pid ) throws P2PApplicationException, P2PNodeException, StoreGetException;
    // IGUID_PIDMap operations with exceptions
    public IPID getLatestPID( IGUID guid ) throws P2PApplicationException, P2PNodeException, StoreGetException;
    //TODO kill Scott, rename method to "put" and fix RRT to work with duplicate method names.
    //Methods should be distinguished by signature, not name.
    public void put2( IGUID guid, IPID pid ) throws P2PApplicationException, P2PNodeException, StorePutException;
    public long getGUIDPutDate( IGUID guid ) throws P2PApplicationException, P2PNodeException, StoreGetException;
}
