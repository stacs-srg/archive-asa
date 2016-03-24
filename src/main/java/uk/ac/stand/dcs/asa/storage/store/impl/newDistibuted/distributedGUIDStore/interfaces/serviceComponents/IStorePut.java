/*
 * Created on 13-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IStoragePolicy;

public interface IStorePut {

    public void put(IPID pid, IData data) throws P2PApplicationException,
            P2PNodeException, StorePutException;

    public void put(IPID pid, IData data, IStoragePolicy policy)
            throws P2PApplicationException, P2PNodeException, StorePutException;

}