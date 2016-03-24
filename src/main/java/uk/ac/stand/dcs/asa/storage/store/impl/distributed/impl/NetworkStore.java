/*
 * Created on 09-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl;

import uk.ac.stand.dcs.asa.applicationFramework.impl.DOLResult;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFramework;
import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.deploy.StoreComponentRRTDeployment;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.interfaces.ILocalGUIDStore;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.Iterator;

/**
 * Used to carry out store related operations on keys (PIDs and GUIDs) for which
 * the local node is not the root. This class does not implement either the
 * IGUIDStore or the NetworGUIDStore interface because it is not meant to
 * represent some deeply meaningful abstraction. It's just network lookup code
 * that would clutter-up the StoreComponent implementation.
 * 
 * Extensions to this class might implement caching.
 * 
 * @author stuart
 */

public class NetworkStore{
    P2PApplicationFramework framework;
    StoreComponent storeComponent;
    
    /**
     * @param framework
     */
    public NetworkStore(P2PApplicationFramework framework, StoreComponent storeComponent) {
        super();
        this.framework = framework;
        this.storeComponent = storeComponent;
    }

    private ILocalGUIDStore getRemoteStore(IKey k) throws P2PNodeException{
        ILocalGUIDStore remote = null;
        DOLResult result = null;
        result = framework.dol(k,storeComponent.getAID());
        remote=(ILocalGUIDStore)result.getApplicationComponent();
        return remote;
    }

    public IData get(IPID pid) throws P2PNodeException, StoreGetException {
            ILocalGUIDStore remote = getRemoteStore(pid);
            IData data=remote.get(pid);
            return data;    
    }

    public IPID put(IPID pid, IData data) throws P2PNodeException, StorePutException {
     
        ILocalGUIDStore remote = getRemoteStore(pid);
        IPID storePid = remote.put(StoreComponentRRTDeployment.packageData(data));
        return storePid;
    }

    public long getPIDPutDate(IPID pid) throws P2PNodeException, StoreGetException {
        ILocalGUIDStore remote = getRemoteStore(pid);
        long date = remote.getPIDPutDate(pid);
        return date;
    }

    public IPID getLatestPID(IGUID guid) throws P2PNodeException, StoreGetException {
        ILocalGUIDStore remote = getRemoteStore(guid);
        IPID pid = remote.getLatestPID(guid);
        return pid;
    }

    public Iterator getAllPIDs(IGUID guid) throws P2PApplicationException {
    	Error.hardError("unimplemented method");
    	throw new P2PApplicationException(new P2PNodeException(P2PStatus.APPLICATION_FAILURE));   // To keep Eclipse happy - will never execute.
    }

    public void put(IGUID guid, IPID pid) throws P2PNodeException, StorePutException {
        ILocalGUIDStore remote = getRemoteStore(guid);
        remote.put2(guid,pid);
    }

    public long getGUIDPutDate(IGUID guid) throws P2PNodeException, StoreGetException {
        ILocalGUIDStore remote = getRemoteStore(guid);
        long date = remote.getGUIDPutDate(guid);
        return date;
    }    
}
