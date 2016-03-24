package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents;

import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IPolicyControl;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IStorageStatusQuery;



/*
 * Created on 01-Nov-2005
 */

/**
 * @author stuart
 */
public interface IStoreInfo_Net extends IStorageStatusQuery, IPolicyControl, IStoreInfoPut, IStoreInfoGet {
}



