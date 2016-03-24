/*
 * Created on 07-Sep-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.factories;

import uk.ac.stand.dcs.asa.applicationFramework.factories.JChordRRT_P2PApplicationFrameworkFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl.StorageNodeApplication;

public class JChordRRT_StorageNodeApplicationFactory {
    public static StorageNodeApplication makeStorageNode(String localAddress, String localPort, String knownNodeAddress, String knownNodePort){
        return new StorageNodeApplication(JChordRRT_P2PApplicationFrameworkFactory.getInstance(),
                localAddress,localPort,knownNodeAddress,knownNodePort);
    }
}
