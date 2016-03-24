/*
 * Created on 13-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.replicaManagement;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.IDataInfo;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents.IStoreInfoGet;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaDataMap;

public class StoreInfoReplicaServiceObject implements IStoreInfoGet {

    private IReplicaDataMap replicaDataMap;
    
    public StoreInfoReplicaServiceObject(IReplicaDataMap map) {
        replicaDataMap = map;
    }

    public IDataInfo getInfo(IPID pid) throws P2PApplicationException, P2PNodeException, StoreGetException {
        // TODO Auto-generated method stub
        return null;
    }

}