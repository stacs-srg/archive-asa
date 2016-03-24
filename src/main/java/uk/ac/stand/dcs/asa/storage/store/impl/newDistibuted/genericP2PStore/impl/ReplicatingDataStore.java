/*
 * Created on 17-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.impl;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFramework;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PNetworkChangeHandler;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.ReplicaInfo;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPIDGenerator;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.*;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Policy implmentation for data storage. This class is responsible for the resilient storage and retrieval of data. Data is stored locally in an IDataMap implmentation responsible for 
 */
public class ReplicatingDataStore implements IReplicatingDataStore, IPolicyControl, IDataMap, P2PNetworkChangeHandler {

    private P2PApplicationFramework framework;
    private IDataMap localStore;
    private IReplicaDataMap replicaStore;

    public ReplicatingDataStore(IDataMapFactory localStoreFac, IReplicaDataMapFactory replicaStoreFac, P2PApplicationFramework framework){
        localStore=localStoreFac.makeDataMap();
        this.replicaStore=replicaStoreFac.makeReplicaDataMap();
        this.framework=framework;
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

    public IStorageStatus put(IKey k, IData data, IStoragePolicy policy) {
        // TODO Auto-generated method stub
        return null;
    }

    public IStorageStatus put(IKey k, IData data) {
        // TODO Auto-generated method stub
        return null;
    }

    public IData get(IKey k) {
        // TODO Auto-generated method stub
        return null;
    }

    public IData update(IKey k, IData data) {
        // TODO Auto-generated method stub
        return null;
    }

    public void append(IKey k, IData data) {
        // TODO Auto-generated method stub
        
    }

    public void remove(IKey k) {
        // TODO Auto-generated method stub
        
    }

    public Iterator getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    public IPIDGenerator getPIDGenerator() {
        // TODO Auto-generated method stub
        return null;
    }

    public IStorageStatus getStorageStatus(IKey k) {
        // TODO Auto-generated method stub
        return null;
    }

    public void increasedLocalKeyRange(KeyRange currentRange, KeyRange diff) {
        // TODO Auto-generated method stub
        
    }

    public void decreasedLocalKeyRange(KeyRange currentRange, KeyRange diff) {
        // TODO Auto-generated method stub
        
    }

    public void replicaSetChange(ReplicaInfo[] replicas, ArrayList[] addedSites, ArrayList[] removedSites) {
        // TODO Auto-generated method stub
        
    }

    public IDataMap getLocalStore() {
        return this;
    }

    public IReplicaDataMap getReplicaStore() {
        return replicaStore;
    }

    public IPolicyControl getPolicyControl() {
        return this;
    }
    
}
