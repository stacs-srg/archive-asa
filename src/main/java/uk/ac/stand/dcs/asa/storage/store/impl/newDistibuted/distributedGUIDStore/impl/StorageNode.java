/*
 * Created on 16-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl;

import uk.ac.stand.dcs.asa.applicationFramework.impl.AbstractP2PApplication;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFrameworkFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.replicaManagement.GUIDPIDMapReplicaServiceObject;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.replicaManagement.StoreInfoReplicaServiceObject;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.replicaManagement.StoreReplicaServiceObject;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.serviceComponents.GUIDPIDMapServiceComponent;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.serviceComponents.StoreInfoServiceComponent;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.serviceComponents.StoreSerivceComponent;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.factories.FileStoreBasedDataMapFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.factories.ReplicaDataMapFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.factories.ReplicaManagementServiceObjectFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.factories.ReplicatingDataStoreFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.*;

/**
 * This class implements service component instantiation/registration and calls the service
 * deployment and P2P node intialisation methods.
 * 
 * @author stuart
 */
public class StorageNode extends AbstractP2PApplication {
    private StoreSerivceComponent ssc;
    private StoreInfoServiceComponent sisc;
    private GUIDPIDMapServiceComponent gpmsc;

    protected StorageNode(P2PApplicationFrameworkFactory fwFac) {
        super(fwFac);

        /*
         * Instatiate store factories
         */
        // IDataMap store factory
        IDataMapFactory localStoreFac = new FileStoreBasedDataMapFactory();

        // IReplicaDataMap store factory
        IReplicaDataMapFactory replicaStoreFac = new ReplicaDataMapFactory();

        // ReplicatingDataStore store factory
        IReplicatingDataStoreFactory replicatingDataStoreFac = new ReplicatingDataStoreFactory(
                localStoreFac, replicaStoreFac, framework);

        // Instantiate the service components
        StoreSerivceComponent ssc = new StoreSerivceComponent(framework,
                replicatingDataStoreFac);
        GUIDPIDMapServiceComponent gpmsc = new GUIDPIDMapServiceComponent(
                framework, replicatingDataStoreFac);
        StoreInfoServiceComponent sisc = new StoreInfoServiceComponent(
                framework, replicatingDataStoreFac);

        // Instantiate the replica service objects
        StoreReplicaServiceObject srso = new StoreReplicaServiceObject(ssc
                .getStoragePolicyImplementationObject().getReplicaStore());
        GUIDPIDMapReplicaServiceObject gpmrso = new GUIDPIDMapReplicaServiceObject(
                gpmsc.getStoragePolicyImplementationObject().getReplicaStore());
        StoreInfoReplicaServiceObject sirso = new StoreInfoReplicaServiceObject(
                sisc.getStoragePolicyImplementationObject().getReplicaStore());

        // Now instantiate the service object for the IReplicaManagement intrefacves for each of the replica stores
        
        IReplicaManagementServiceObjectFactory rmsoFac = new ReplicaManagementServiceObjectFactory();
        
        IReplicaManagementServiceObject srmso = rmsoFac.makeReplicaManagementServiceObject(ssc
                .getStoragePolicyImplementationObject().getReplicaStore());
        IReplicaManagementServiceObject gpmrmso = rmsoFac.makeReplicaManagementServiceObject(gpmsc
                .getStoragePolicyImplementationObject().getReplicaStore());
        IReplicaManagementServiceObject sirmso = rmsoFac.makeReplicaManagementServiceObject(sisc
                .getStoragePolicyImplementationObject().getReplicaStore());

        
        // Get the IPolicyControl implementations for each IReplicatingDataStore 
        IPolicyControl storePolicy = ssc.getStoragePolicyImplementationObject().getPolicyControl();
        IPolicyControl guidPidMapPolicy = gpmsc.getStoragePolicyImplementationObject().getPolicyControl();
        IPolicyControl storeInfoPolicy = sisc.getStoragePolicyImplementationObject().getPolicyControl();
        
        // Deploy the services
        
        //StorageNodeRRTDeployment.deployServices(ssc,gpmsc,sisc,srso,gpmrso,sirso,srmso,gpmrmso,sirmso,storePolicy,guidPidMapPolicy,storeInfoPolicy);

        
        // try {
//            framework.getApplicationRegistry().registerApplicationComponent(store);
//        } catch (ApplicationRegistryException e) {
//            Error.hardError("An error occurred during application component registry"); 
//        }
//        initialiseP2P(localAddress, localPort, knownNodeAddress, knownNodePort);
//        Network.reportHostAddress("Local Address", this.getLocalAddress());
    }

    protected void reportInitOutput(String prefix, String msg) {
        // TODO Auto-generated method stub
        
    }

    protected void initialiseApplicationServices() {
        // TODO Auto-generated method stub
        
    }

    public void initialiseApp() {
        // TODO Auto-generated method stub
        
    }

}
