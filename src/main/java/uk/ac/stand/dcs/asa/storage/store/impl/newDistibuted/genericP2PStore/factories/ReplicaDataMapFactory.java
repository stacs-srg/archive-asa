/*
 * Created on 13-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.factories;

import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.impl.DefaultReplicaDataMap;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaDataMap;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaDataMapFactory;

public class ReplicaDataMapFactory implements IReplicaDataMapFactory {

    public IReplicaDataMap makeReplicaDataMap() {
        return new DefaultReplicaDataMap();
    }

}
