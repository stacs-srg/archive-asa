/*
 * Created on 12-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.replicaManagement;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents.IGUIDPIDMapGet;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaDataMap;

import java.util.Iterator;

public class GUIDPIDMapReplicaServiceObject implements IGUIDPIDMapGet {

    private IReplicaDataMap replicaDataMap;
    
    public GUIDPIDMapReplicaServiceObject(IReplicaDataMap map) {
        replicaDataMap = map;
    }

    public IPID getLatestPID(IGUID guid) throws P2PApplicationException, P2PNodeException, StoreGetException {
        // TODO Auto-generated method stub
        return null;
    }

    public long getGUIDPutDate(IGUID guid) throws P2PApplicationException, P2PNodeException, StoreGetException {
        // TODO Auto-generated method stub
        return 0;
    }

    public Iterator iterator(IGUID guid) throws P2PApplicationException, P2PNodeException, StoreGetException {
        // TODO Auto-generated method stub
        return null;
    }

    
    

}
