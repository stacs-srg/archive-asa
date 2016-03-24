/*
 * Created on 05-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PNetworkChangeHandler;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.ReplicaInfo;
import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPIDGenerator;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.deploy.StoreComponentRRTDeployment;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.interfaces.ILocalGUIDStore;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IManagedGUIDStore;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.ArrayList;

/**
 * This class implements the service object for network interface to the local
 * store. An instance of this class is exposed to remote hosts inorder for those
 * hosts to access keys for which the local node is the root.
 * 
 * @author stuart
 */

public class LocalStoreNetworkWrapper implements ILocalGUIDStore,
        P2PNetworkChangeHandler {

    IManagedGUIDStore store;

    StoreComponent storeComponent;

    /**
     * @param store
     */
    public LocalStoreNetworkWrapper(IManagedGUIDStore store,
            StoreComponent storeComponent) {
        super();
        this.store = store;
        this.storeComponent = storeComponent;
    }

    public IPIDGenerator getPIDGenerator() {
        return store.getPIDGenerator();
    }

    // IGUIDStore method implementations
    public IData get(IPID pid) throws P2PApplicationException, P2PNodeException, StoreGetException {
        if (storeComponent.isLocalKey(pid)) {
            return StoreComponentRRTDeployment.packageData(store.get(pid));            
        } else {
            throw new P2PApplicationException(P2PStatus.APPLICATION_FAILURE,
                    "This is not the root node for the specified PID");
        }
    }

    public IPID put(IData data) throws P2PApplicationException, P2PNodeException, StorePutException{
        IPID pid = store.getPIDGenerator().dataToPID(data);
        IPID storePid = null;
        if (storeComponent.isLocalKey(pid)) {
            storePid = store.put(data);
        } else {
            throw new P2PApplicationException(P2PStatus.APPLICATION_FAILURE,
                    "This is not the root node for the PID generated from the specified data");
        }
        return storePid;
    }

    public long getPIDPutDate(IPID pid) throws P2PApplicationException, P2PNodeException, StoreGetException{
        if (storeComponent.isLocalKey(pid)) {
            
                return store.getPIDPutDate(pid);
            
        } else {
            throw new P2PApplicationException(P2PStatus.APPLICATION_FAILURE,
                    "This is not the root node for the specified PID");
        }
    }

    public IPID getLatestPID(IGUID guid) throws P2PApplicationException , P2PNodeException, StoreGetException{
        if (storeComponent.isLocalKey(guid)) {
           
                return store.getLatestPID(guid);
           
        } else {
            throw new P2PApplicationException(P2PStatus.APPLICATION_FAILURE,
                    "This is not the root node for the specified GUID");
        }
    }

    public void put2(IGUID guid, IPID pid) throws P2PApplicationException, P2PNodeException, StorePutException{
        if (storeComponent.isLocalKey(guid)) {
                store.put(guid, pid);
            
        } else {
            throw new P2PApplicationException(P2PStatus.APPLICATION_FAILURE,
                    "This is not the root node for the specified GUID");
        }
    }

    public void put3(IGUID guid, IPID[] pid) throws P2PApplicationException {
        Error.hardError("unimplemented method");
    }

    public long getGUIDPutDate(IGUID guid) throws P2PApplicationException, P2PNodeException, StoreGetException{
        if (storeComponent.isLocalKey(guid)) {
                return store.getGUIDPutDate(guid);
           
        } else {
            throw new P2PApplicationException(P2PStatus.APPLICATION_FAILURE,
                    "This is not the root node for the specified GUID");
        }

    }

    // P2PNetworkChangeHandler method implementations
    public void increasedLocalKeyRange(KeyRange currentRange, KeyRange diff) {
        Diagnostic.trace("\nLocal key range increased\ncurrent range is ["
                + currentRange.getLowerBound() + ","
                + currentRange.getUpperBound()
                + "]\n range of keys added to local key range is ["
                + diff.getLowerBound() + "," + diff.getUpperBound() + "]",
                Diagnostic.FINAL);
    }

    public void decreasedLocalKeyRange(KeyRange currentRange, KeyRange diff) {
        Diagnostic.trace("\nLocal key range decreased\ncurrent range is ["
                + currentRange.getLowerBound() + ","
                + currentRange.getUpperBound()
                + "]\n range of keys removed from local key range is ["
                + diff.getLowerBound() + "," + diff.getUpperBound() + "]",
                Diagnostic.FINAL);
    }

    public void replicaSetChange(ReplicaInfo[] replicas, ArrayList[] addedSites, ArrayList[] removedSites) {
        // TODO Auto-generated method stub
        
    }

}
