/*
 * Created on 05-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.interfaces.IReplicaGUIDStore;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IManagedGUIDStore;
import uk.ac.stand.dcs.asa.util.Error;

/**
 * This class implements the service object for the network interface to the
 * replica store on this node. An instance of this class is exposed to remote
 * hosts in-order for those hosts to store replica data on this node.
 * 
 * @author stuart
 */
public class ReplicaStoreNetworkWrapper implements IReplicaGUIDStore{

    IManagedGUIDStore store;
    
    /**
     * @param store
     */
    public ReplicaStoreNetworkWrapper(IManagedGUIDStore store) {
        super();
        this.store = store;
    }

    //IGUIDStore method implementations
    public IData get(IPID pid) {
    	Error.hardError("unimplemented method");
        return null;
    }

    public IPID put(IData data) throws P2PApplicationException{
    	Error.hardError("unimplemented method");
        return null;
    }

    public long getPIDPutDate(IPID pid) throws P2PApplicationException{
    	Error.hardError("unimplemented method");
        return 0;
    }

    public IPID getLatestPID(IGUID guid) throws P2PApplicationException{
    	Error.hardError("unimplemented method");
        return null;
    }
    
    public void put2(IGUID guid, IPID pid) throws P2PApplicationException{
    	Error.hardError("unimplemented method");
        
    }

    public void put3(IGUID guid, IPID[] pid) throws P2PApplicationException {
    	Error.hardError("unimplemented method");
    }
    
    public long getGUIDPutDate(IGUID guid) {
    	Error.hardError("unimplemented method");
        return 0;
    }

}
