/*
 * Created on 05-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl;

import uk.ac.stand.dcs.asa.applicationFramework.impl.AIDImpl;
import uk.ac.stand.dcs.asa.applicationFramework.impl.Message;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.*;
import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPIDGenerator;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;
import uk.ac.stand.dcs.asa.storage.store.factories.ManagedLocalFileBasedStoreFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.deploy.StoreComponentRRTDeployment;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IGUIDStore;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IManagedGUIDStore;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.Iterator;

/**
 * This class implments the local point-of-presence for the distributed GUID
 * store and integrates with the local P2PApplicationFramework object.
 * 
 * @author stuart
 */

public class StoreComponent implements P2PApplicationComponent, IGUIDStore,
        ApplicationUpcallHandler {

    private P2PApplicationFramework framework;

    private IManagedGUIDStore localStore;

    private IManagedGUIDStore replicaStore;

    private LocalStoreNetworkWrapper localStoreNetWrapper;

    private ReplicaStoreNetworkWrapper replicaStoreNetWrapper;

    private NetworkStore networkStore;

    private IPIDGenerator pidgen;

    public static final AID appID = new AIDImpl(
            "F05E5CFE-9777-3470-4D73-CD1C5D90255E");

    /**
     * @param framework
     */
    public StoreComponent(P2PApplicationFramework framework) {
        super();
        this.framework = framework;

        // Date today;
        // String output;
        // SimpleDateFormat formatter;

        // formatter = new SimpleDateFormat("hh-mm-ss-SSS_dd-MM-yy");
        // today = new Date();
        // output = formatter.format(today);

        // localStore = (IManagedGUIDStore)(new
        // ManagedLocalFileBasedStoreFactory(output+"_LocalStore").makeStore());
        // replicaStore = (IManagedGUIDStore)(new
        // ManagedLocalFileBasedStoreFactory(output+"_ReplicaStore").makeStore());
        localStore = (IManagedGUIDStore) (new ManagedLocalFileBasedStoreFactory(
                "LocalStore").makeStore());
        replicaStore = (IManagedGUIDStore) (new ManagedLocalFileBasedStoreFactory(
                "ReplicaStore").makeStore());
        localStoreNetWrapper = new LocalStoreNetworkWrapper(localStore, this);
        replicaStoreNetWrapper = new ReplicaStoreNetworkWrapper(replicaStore);
        networkStore = new NetworkStore(framework, this);
        pidgen = localStoreNetWrapper.getPIDGenerator();

        framework.addP2PNetworkChangeHandler(localStoreNetWrapper);
    }

    public LocalStoreNetworkWrapper getLocalStoreNetWrapper() {
        return localStoreNetWrapper;
    }

    public ReplicaStoreNetworkWrapper getReplicaStoreNetWrapper() {
        return replicaStoreNetWrapper;
    }

    // P2PApplicationComponent method implementations
    public ApplicationUpcallHandler getApplicationUpcallHandler() {
        return this;
    }

    public AID getAID() {
        return appID;
    }

    // IGUIDStore method implementations
    public IData get(IPID pid) throws StoreGetException {
        try {
            if (isLocalKey(pid)) {
                return localStore.get(pid);
            } else {
                return networkStore.get(pid);
            }
        } catch (P2PApplicationException e) {
            throw new StoreGetException(e.getMessage());
        } catch (P2PNodeException e) {
            throw new StoreGetException(e.getMessage());
        }
    }

    public IPID put(IData data) throws StorePutException {
        IPID pid = pidgen.dataToPID(data);
        IPID storePid = null;
        try {
            if (isLocalKey(pid)) {
                storePid = localStore.put(data);
            } else {
                storePid = networkStore.put(pid, data);
            }
        } catch (P2PApplicationException e) {
            throw new StorePutException(e.getMessage());
        } catch (P2PNodeException e) {
            throw new StorePutException(e.getMessage());
        }
        return storePid;
    }

    public long getPIDPutDate(IPID pid) throws StoreGetException {
        try {
            if (isLocalKey(pid)) {
                return localStore.getPIDPutDate(pid);
            } else {
                return networkStore.getPIDPutDate(pid);
            }
        } catch (P2PApplicationException e) {
            throw new StoreGetException(e.getMessage());
        } catch (P2PNodeException e) {
            throw new StoreGetException(e.getMessage());
        }
    }

    public IPID getLatestPID(IGUID guid) throws StoreGetException {
        try {
            if (isLocalKey(guid)) {
                return localStore.getLatestPID(guid);
            } else {
                return networkStore.getLatestPID(guid);
            }
        } catch (P2PApplicationException e) {
            throw new StoreGetException(e.getMessage());
        } catch (P2PNodeException e) {
            throw new StoreGetException(e.getMessage());
        }
    }

    public void put(IGUID guid, IPID pid) throws StorePutException {
        try {
            if (isLocalKey(guid)) {

                localStore.put(guid, pid);

            } else {
                networkStore.put(guid, pid);
            }
        } catch (P2PApplicationException e) {
            throw new StorePutException(e.getMessage());
        } catch (P2PNodeException e) {
            throw new StorePutException(e.getMessage());
        }
    }

    public long getGUIDPutDate(IGUID guid) throws StoreGetException {
        try {
            if (isLocalKey(guid)) {
                return localStore.getGUIDPutDate(guid);
            } else {
                return networkStore.getGUIDPutDate(guid);
            }
        } catch (P2PApplicationException e) {
            throw new StoreGetException(e.getMessage());
        } catch (P2PNodeException e) {
            throw new StoreGetException(e.getMessage());
        }
    }

    public Iterator getAllPIDs(IGUID guid) throws StoreGetException {
        Error.hardError("unimplemented method");
        return null;
    }
    
    // ApplicationUpcallHandler method implementations
    public String applicationNextHop(IKey k, AID a)
            throws P2PApplicationException {
        boolean inLocalKeyRange = false;
        try {
            inLocalKeyRange = framework.inLocalKeyRange(k);
        } catch (P2PNodeException e) {
            throw new P2PApplicationException(P2PStatus.STATE_ACCESS_FAILURE, e
                    .getLocalizedMessage());
        }

        /*
         * The current implementation does not do caching or replication so only
         * return the service name for this node's LocalStore if this node is
         * the root for the specified key.
         */
        if (inLocalKeyRange) {
            return StoreComponentRRTDeployment.LocalGUIDStore_SERVICE;
        } else {
            return null;
        }
    }

    public String applicationNextHop(IKey k, AID a, Message m)
            throws P2PApplicationException {
        Error.hardError("unimplemented method");
        return null;
    }

    public void receiveMessage(IKey k, AID a, Message m)
            throws P2PApplicationException {
        Error.hardError("unimplemented method");
    }

    public boolean isLocalKey(IKey k) throws P2PNodeException {
        return framework.inLocalKeyRange(k);
    }
}
