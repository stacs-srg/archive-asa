/*
 * Created on 13-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;

import java.util.Iterator;

public interface IGUIDPIDMapGet {

    public IPID getLatestPID(IGUID guid) throws P2PApplicationException,
            P2PNodeException, StoreGetException;

    public long getGUIDPutDate(IGUID guid) throws P2PApplicationException,
            P2PNodeException, StoreGetException;

    public Iterator iterator(IGUID guid) throws P2PApplicationException,
            P2PNodeException, StoreGetException;

}