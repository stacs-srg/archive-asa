package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IKey;


/**
 *  @author stuart
 */
public interface IStorageStatusQuery {
    public IStorageStatus getStorageStatus(IKey k);
}
