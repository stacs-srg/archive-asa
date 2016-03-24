/*
 * Created on 13-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.replicaManagement;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents.IStoreGet;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaDataMap;

public class StoreReplicaServiceObject implements IStoreGet {

    private IReplicaDataMap replicaDataMap;
    
    public StoreReplicaServiceObject(IReplicaDataMap map) {
        replicaDataMap = map;
    }

    public IData get(IPID pid) throws P2PApplicationException, P2PNodeException, StoreGetException {
        // TODO Auto-generated method stub
        return null;
    }

    public long getPIDPutDate(IPID pid) throws P2PApplicationException, P2PNodeException, StoreGetException {
        // TODO Auto-generated method stub
        return 0;
    }

   
}
