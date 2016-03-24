/*
 * Created on 04-Nov-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;

/**
 * @author stuart
 */
public interface IReplicaManagement {
    public boolean put(IKey k, IData data, IStoragePolicy policy);
    
}
