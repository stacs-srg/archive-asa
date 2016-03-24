/*
 * Created on 13-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaDataMap;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaManagementServiceObject;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IStoragePolicy;

public class DefaultReplicaManagementServiceObject implements IReplicaManagementServiceObject {

    private IReplicaDataMap replicaDataMap;

    public DefaultReplicaManagementServiceObject(IReplicaDataMap replicaDataMap) {
        this.replicaDataMap=replicaDataMap;
    }

    public boolean put(IKey k, IData data, IStoragePolicy policy) {
        // TODO Auto-generated method stub
        return false;
    }

    public IReplicaDataMap getReplicaDataMap() {
        // TODO Auto-generated method stub
        return null;
    }

}
