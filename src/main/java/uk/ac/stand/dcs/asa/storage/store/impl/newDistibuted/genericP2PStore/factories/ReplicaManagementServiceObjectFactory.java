/*
 * Created on 13-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.factories;

import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.impl.DefaultReplicaManagementServiceObject;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaDataMap;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaManagementServiceObject;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaManagementServiceObjectFactory;

public class ReplicaManagementServiceObjectFactory implements IReplicaManagementServiceObjectFactory{
    
    public IReplicaManagementServiceObject makeReplicaManagementServiceObject(IReplicaDataMap replicaDataMap) {
        // TODO Auto-generated method stub
        return new DefaultReplicaManagementServiceObject(replicaDataMap);
    }

}
