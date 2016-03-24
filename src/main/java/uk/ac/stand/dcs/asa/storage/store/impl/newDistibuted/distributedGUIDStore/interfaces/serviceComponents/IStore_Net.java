/*
 * Created on 04-Nov-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents;

import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IPolicyControl;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IStorageStatusQuery;



/**
 * @author stuart
 */
public interface IStore_Net extends IStorageStatusQuery, IPolicyControl, IStorePut, IStoreGet {
}
