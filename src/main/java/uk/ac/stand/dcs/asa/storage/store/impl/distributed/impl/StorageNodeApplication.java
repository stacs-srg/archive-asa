/*
 * Created on 07-Sep-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl;

import uk.ac.stand.dcs.asa.applicationFramework.impl.AbstractP2PApplication;
import uk.ac.stand.dcs.asa.applicationFramework.impl.ApplicationRegistryException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFrameworkFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.deploy.StoreComponentRRTDeployment;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.Network;

public class StorageNodeApplication extends AbstractP2PApplication {

    private StoreComponent store;
    
    public StorageNodeApplication(P2PApplicationFrameworkFactory fwFac, String localAddress, String localPort, String knownNodeAddress, String knownNodePort) {
        super(fwFac);
        
        store=new StoreComponent(this.framework);
        try {
            framework.getApplicationRegistry().registerApplicationComponent(store);
        } catch (ApplicationRegistryException e) {
            Error.hardError("An error occurred during application component registry"); 
        }
        initialiseP2P(localAddress, localPort, knownNodeAddress, knownNodePort);
        Network.reportHostAddress("Local Address", this.getLocalAddress());
    }
    
    protected void reportInitOutput(String prefix, String msg) {
        // TODO Auto-generated method stub
    }

    protected void initialiseApplicationServices() {
        try {
            StoreComponentRRTDeployment.deployStorageServices(store);
        } catch (P2PApplicationException e) {
            Error.hardError("Failed to deploy storage services\n"+e.getLocalizedMessage());
        }
    }

    public void initialiseApp() {
        Diagnostic.trace("Storage node initialised!");
    }

    public StoreComponent getStore() {
        return store;
    }
    
    
    
    

}
