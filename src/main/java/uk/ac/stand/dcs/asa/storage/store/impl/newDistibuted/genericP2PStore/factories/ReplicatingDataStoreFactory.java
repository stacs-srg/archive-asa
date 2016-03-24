/*
 * Created on 17-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.factories;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFramework;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.impl.IReplicatingDataStore;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.impl.ReplicatingDataStore;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IDataMapFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaDataMapFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicatingDataStoreFactory;

public class ReplicatingDataStoreFactory implements IReplicatingDataStoreFactory{
    
    private IDataMapFactory localStoreFac;
    private IReplicaDataMapFactory replicaStoreFac;
    private P2PApplicationFramework framework;
    
    /**
     * @param fac
     * @param fac2
     */
    public ReplicatingDataStoreFactory(IDataMapFactory fac, IReplicaDataMapFactory fac2, P2PApplicationFramework fw) {
        localStoreFac = fac;
        replicaStoreFac = fac2;
        framework=fw;
    }

    /* (non-Javadoc)
     * @see uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.factories.IReplicatingDataStoreFactory#makeDataStore()
     */
    public IReplicatingDataStore makeDataStore() {
        return new ReplicatingDataStore(localStoreFac,replicaStoreFac,framework);
    }

}
