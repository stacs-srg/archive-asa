/*
 * Created on 18-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces;

import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.impl.IReplicatingDataStore;

public interface IReplicatingDataStoreFactory {

    public IReplicatingDataStore makeDataStore();

}