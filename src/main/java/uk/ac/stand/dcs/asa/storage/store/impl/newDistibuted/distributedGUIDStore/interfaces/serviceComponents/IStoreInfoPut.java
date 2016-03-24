/*
 * Created on 13-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.IDataInfo;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IStoragePolicy;

public interface IStoreInfoPut {

    public void putInfo(IPID pid, IDataInfo[] locations)
            throws P2PApplicationException, P2PNodeException, StorePutException;

    public void putInfo(IPID pid, IDataInfo[] locations, IStoragePolicy policy)
            throws P2PApplicationException, P2PNodeException, StorePutException;

}