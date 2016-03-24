/*
 * Created on 13-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;

public interface IStoreGet {

    public IData get(IPID pid) throws P2PApplicationException,
            P2PNodeException, StoreGetException;

    public long getPIDPutDate(IPID pid) throws P2PApplicationException,
            P2PNodeException, StoreGetException;

}