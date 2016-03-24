/*
 * Created on 09-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.test;

import uk.ac.stand.dcs.asa.applicationFramework.factories.JChordRRT_P2PApplicationFrameworkFactory;
import uk.ac.stand.dcs.asa.applicationFramework.impl.AbstractP2PApplication;
import uk.ac.stand.dcs.asa.applicationFramework.impl.ApplicationRegistryException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFrameworkFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.deploy.StoreComponentRRTDeployment;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl.StoreComponent;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

public class StoreTestApplication extends AbstractP2PApplication {
    
    private StoreTestGUI gui;
    private StoreComponent store;
    
    public StoreTestApplication(P2PApplicationFrameworkFactory fwFac) {
        super(fwFac);
        
        Diagnostic.initialise(myBus);
        Diagnostic.setLevel(Diagnostic.INIT);
        Diagnostic.setLocalErrorReporting(false);
        Error.initialise(myBus);
        Error.disableLocalErrorReporting();
        
        store=new StoreComponent(framework);
        gui=new StoreTestGUI(store, this, this.myBus);
        try {
            framework.getApplicationRegistry().registerApplicationComponent(store);
        } catch (ApplicationRegistryException e) {
            gui.disableInputComponents();
            Error.exceptionError("An error occurred during application component registry",e); 
        }
        gui.show();
    }

    protected void reportInitOutput(String prefix, String msg) {
        gui.networkInitOutput(prefix+"\n"+msg);
    }

    protected void initialiseApplicationServices() {
        Diagnostic.trace("Deploying storage services",Diagnostic.INIT);
        try {
            StoreComponentRRTDeployment.deployStorageServices(store);
        } catch (P2PApplicationException e) {
            Error.error("Failed to deploy storage services\n"+e.getLocalizedMessage());
        }
    }

    public void initialiseApp() {
        gui.initialiseApp();
        
    }

    public static void main(String[] args) {
        new StoreTestApplication(JChordRRT_P2PApplicationFrameworkFactory.getInstance());
    }
}
