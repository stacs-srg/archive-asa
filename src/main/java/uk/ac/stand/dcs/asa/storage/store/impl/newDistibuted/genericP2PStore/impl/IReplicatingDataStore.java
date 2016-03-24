/*
 * Created on 18-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.impl;


import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IDataMap;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IPolicyControl;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaDataMap;

public interface IReplicatingDataStore {
    IDataMap getLocalStore();
    IReplicaDataMap getReplicaStore();
    IPolicyControl getPolicyControl();
}
