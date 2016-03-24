/*
 * Created on 18-Nov-2005 at 12:07:22.
 */
package uk.ac.stand.dcs.asa.storage.policyControl.impl;

import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.policyControl.interfaces.IPolicyControl;
import uk.ac.stand.dcs.asa.storage.policyControl.interfaces.IStoragePolicy;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IGUIDStore;

import java.util.Iterator;
import java.util.Set;

/**
 * @author al
 *
 * NEW_ARCHITECTURE_AL
 * This is a policy control shim that multiplexes and controls
 * use of some stores that have been registered with it
 */
public class LocalPolicyShim implements IGUIDStore, IPolicyControl {
    // Known policies
    private static final String RETURN_POLICY = "return_policy";              // values - after_local, after_local_and remote, after_all
    private static final String REPLICATION_FACTOR = "replication_factor";    // values - 0,1,2..n
    private static final String USE_LOCAL_CACHE = "use_local_cache";          // value true, false  

    // Policy values
    private static final String return_after_local = "after_local";
    private static final String return_after_all = "after_all";
    private static final String return_after_local_and_remote = "after_local_and remote";
    // default Policy values
    private static final String default_return_policy = return_after_local;
    private static final String default_use_local_cache = "true";
    private static final String default_replication_factor = "3";
    
    // Local state
        
    private IStoragePolicy defaultPolicy;
    private IStoragePolicy currentPolicy;
    
    private IGUIDStore localCache;
    private IGUIDStore netStore;
    
    // Constructor
    
    public LocalPolicyShim( IStoragePolicy suppliedPolicy, IGUIDStore localCache, IGUIDStore netStore ) {
        this.defaultPolicy = initialiseDefaultPolicy();
        if( suppliedPolicy != null ) {
            this.currentPolicy = suppliedPolicy;
        }
        this.localCache = localCache;
        this.netStore = netStore;
    }
    
    ////////////////////////////////////// IGUIDStore interface ///////////////////////////////////// 

    public IData get(IPID pid) throws StoreGetException {
        if( localCache != null ) {
            IData localData = localCache.get(pid);
            if( localData != null ) {
                return localData;
            }
        }
        if( netStore != null ) {
            IData localData = netStore.get(pid);
            if( localData != null ) {
                return localData;
            }
        }
        return null;       // didnt find it.
    }

    public IPID put(IData data) throws StorePutException {
        // TODO Auto-generated method stub
        if( currentPolicy.containsKey(RETURN_POLICY) ) {
            
        }
        return null;
    }

    public long getPIDPutDate(IPID pid) throws StoreGetException {
        // TODO Auto-generated method stub
        return 0;
    }   

    public IPID getLatestPID(IGUID guid) throws StoreGetException {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator getAllPIDs(IGUID guid) throws StoreGetException {
        // TODO Auto-generated method stub
        return null;
    }

    public void put(IGUID guid, IPID pid) throws StorePutException {
        // TODO Auto-generated method stub
        
    }

    public long getGUIDPutDate(IGUID guid) throws StoreGetException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    ////////////////////////////////////// IPolicyControl interface ///////////////////////////////////// 

    public IStoragePolicy defaultPolicy() {
        return defaultPolicy;
    }

    public void setPolicy(IStoragePolicy newPolicy, boolean overwrite_current) {
        if( overwrite_current ) { // if an overwrite is called for just nuke the old one
            currentPolicy = newPolicy;
            enforcePolicy( newPolicy );
        } else {            // otherwise must be more careful
            Set currentKeys = currentPolicy.keySet();
            Set newKeys = newPolicy.keySet();
            Iterator iter = newKeys.iterator();         // why no intersection and union operators in Java Sets :(
            while( iter.hasNext() ) {                   // this finds intersection
                String newKey = (String) iter.next();
                if( currentKeys.contains(newKey) ) {    // already in currentPolicy - check if changed
                    // potential overwrite
                    // check if different
                    Object curr = currentPolicy.get(newKey);
                    Object neww = newPolicy.get(newKey);
                    if( ! curr.equals(neww) ) {         // attempts to find the policies that have changed
                        currentPolicy.put(newKey,neww);
                        enforcePolicy(newKey,neww);
                    }
                } else {                        // this is a totally new policy
                    Object neww = newPolicy.get(newKey);
                    currentPolicy.put(newKey,neww);
                    enforcePolicy(newKey,neww);  
                }
            }
        }
        
    }

    public IStoragePolicy getPolicy() {
        return currentPolicy;
    } 
    
    ////////////////////////////////////// Private IPolicyControl methods ///////////////////////////////////// 
    
    private void enforcePolicy(IStoragePolicy newPolicy) {
        Set newKeys = newPolicy.keySet();
        Iterator iter = newKeys.iterator();
        while( iter.hasNext() ) {
            String newKey = (String) iter.next();
            Object neww = newPolicy.get(newKey);
            enforcePolicy( newKey,neww );
        }
    }
    

    private void enforcePolicy(String newKey, Object neww) {
        // TODO implement method   
    }
    
    
    private IStoragePolicy initialiseDefaultPolicy() {
        IStoragePolicy policy = new StoragePolicy();
        policy.put(RETURN_POLICY,default_return_policy);
        policy.put(REPLICATION_FACTOR,default_replication_factor);
        policy.put(USE_LOCAL_CACHE,default_use_local_cache);
        return policy;
    }   
}
