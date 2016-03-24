/*
 * Created on 03-Nov-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces;

import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents.IStorePut;

/**
 * @author stuart
 */
public interface IDataInfo {
    public IStorePut getStore();
    // get meta data about store location, uptime, blah
}
