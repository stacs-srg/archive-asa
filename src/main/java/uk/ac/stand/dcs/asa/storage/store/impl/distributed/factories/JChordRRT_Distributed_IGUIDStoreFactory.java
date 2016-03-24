/*
 * Created on 08-Sep-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.factories;

import uk.ac.stand.dcs.asa.storage.store.interfaces.IGUIDStore;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IGUIDStoreFactory;

public class JChordRRT_Distributed_IGUIDStoreFactory implements IGUIDStoreFactory {
	
    private String localAddress;
    private String localPort;
    private String knownNodeAddress;
    private String knownNodePort;

    public JChordRRT_Distributed_IGUIDStoreFactory(String knownNodeAddress, String knownNodePort, String localAddress, String localPort) {
        this.knownNodeAddress = knownNodeAddress;
        this.knownNodePort = knownNodePort;
        this.localAddress = localAddress;
        this.localPort = localAddress;
    }
    
    public IGUIDStore makeStore(){
        return JChordRRT_StorageNodeApplicationFactory.makeStorageNode(localAddress,localPort,knownNodeAddress, knownNodePort).getStore();
    }
}
