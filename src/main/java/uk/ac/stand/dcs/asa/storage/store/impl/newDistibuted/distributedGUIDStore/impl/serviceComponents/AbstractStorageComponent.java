/*
 * Created on 12-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.serviceComponents;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.ApplicationUpcallHandler;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationComponent;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFramework;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPIDGenerator;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.impl.IReplicatingDataStore;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IDataMap;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicatingDataStoreFactory;

public abstract class AbstractStorageComponent implements
        ApplicationUpcallHandler, P2PApplicationComponent {
    
    private P2PApplicationFramework framework;
    private IPIDGenerator pidgen;
    private IReplicatingDataStore storagePolicyImplementationObject;
    private IDataMap dataStore;
    private final AID appID;

    public AbstractStorageComponent(P2PApplicationFramework framework, IReplicatingDataStoreFactory rdsFac, AID appID) {
        this.framework = framework;
        this.appID=appID;
        storagePolicyImplementationObject=rdsFac.makeDataStore();
        this.dataStore=storagePolicyImplementationObject.getLocalStore();
        pidgen=dataStore.getPIDGenerator();
    }

    public P2PApplicationFramework getFramework() {
        return framework;
    }

    public IPIDGenerator getPidgen() {
        return pidgen;
    }

    public AID getAID() {
        return appID;
    }
    
    public IDataMap getDataStore() {
        return dataStore;
    }

    public IReplicatingDataStore getStoragePolicyImplementationObject(){
        return storagePolicyImplementationObject;
    }
    
    public ApplicationUpcallHandler getApplicationUpcallHandler() {
        return this;
    }
    
}
