/*
 * Created on 11-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.impl.serviceComponents;

import uk.ac.stand.dcs.asa.applicationFramework.impl.AIDImpl;
import uk.ac.stand.dcs.asa.applicationFramework.impl.Message;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFramework;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents.IStore_Net;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicatingDataStoreFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IStoragePolicy;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IStorageStatus;

public class StoreSerivceComponent extends AbstractStorageComponent implements IStore_Net {

    public static final AID StoreSerivceComponent_AID = new AIDImpl(
            "60032F70-E0DA-C0DE-584F-5FCC93A86294");

    public StoreSerivceComponent(P2PApplicationFramework framework, IReplicatingDataStoreFactory rdsFac) {
        super(framework,rdsFac,StoreSerivceComponent_AID);
    }
    
    public IData get(IPID pid) throws P2PApplicationException, P2PNodeException, StoreGetException {
        // TODO Auto-generated method stub
        return null;
    }

    public void put(IPID pid, IData data) throws P2PApplicationException, P2PNodeException, StorePutException {
        // TODO Auto-generated method stub
        
    }

    public void put(IPID pid, IData data, IStoragePolicy policy) throws P2PApplicationException, P2PNodeException, StorePutException {
        // TODO Auto-generated method stub
        
    }

    public long getPIDPutDate(IPID pid) throws P2PApplicationException, P2PNodeException, StoreGetException {
        // TODO Auto-generated method stub
        return 0;
    }

    public IStorageStatus getStorageStatus(IKey k) {
        // TODO Auto-generated method stub
        return null;
    }

    public IStoragePolicy getStoragePolicy(IKey k) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setStoragePolicy(IKey k, IStoragePolicy policy) {
        // TODO Auto-generated method stub
        
    }

    public IStoragePolicy getStoragePolicy() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setStoragePolicy(IStoragePolicy policy) {
        // TODO Auto-generated method stub
        
    }

    public String applicationNextHop(IKey k, AID a) throws P2PApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    public String applicationNextHop(IKey k, AID a, Message m) throws P2PApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    public void receiveMessage(IKey k, AID a, Message m) throws P2PApplicationException {
        // TODO Auto-generated method stub
        
    }

}
