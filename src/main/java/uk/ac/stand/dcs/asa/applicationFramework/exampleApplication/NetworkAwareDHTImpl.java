/*
 * Created on 07-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.applicationFramework.impl.DOLResult;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFramework;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;

import java.util.HashMap;

public class NetworkAwareDHTImpl implements DHTMaintenance, DHT{
    private P2PApplicationFramework framework;
    private HashMap cachedData;
    private HashMap replicatedData;
    
    public NetworkAwareDHTImpl(P2PApplicationFramework framework){
        cachedData=new HashMap();
        replicatedData=new HashMap();
        this.framework=framework;
    }

    public Object get(IKey k) throws P2PApplicationException {
        Object result=null;
        if(replicatedData.containsKey(k)){
            result=replicatedData.get(k);
        }else{
            if(cachedData.containsKey(k)){
                result=cachedData.get(k);
            }else{
                DOLResult dr;
                try {
                    dr=framework.dol(k,DHTComponent.myAID);
                } catch (P2PNodeException e) {
                    throw new P2PApplicationException(P2PStatus.LOOKUP_FAILURE);
                }
                DHT targetDHT=(DHT)dr.getApplicationComponent();
                result=targetDHT.get(k);
            }
        }
        return result;
    }

    public boolean put(IKey k, Object o) throws P2PApplicationException {
        boolean result=false;
        
        if(objectContains(k)){
            result=false;
        }else{
            DOLResult dr;
            try {
                dr = framework.dol(k, DHTComponent.myAID);
            } catch (P2PNodeException e) {
                throw new P2PApplicationException(P2PStatus.LOOKUP_FAILURE);
            }
            /*
             * If the dol call gets intercepted then the key must exist
             * and thus we should return false;
             */
            if(dr.isRoot()){
                DHT targetDHT = (DHT)dr.getApplicationComponent();
                result=targetDHT.put(k,o);
            }
        }
        
        return result;
    }

    public boolean containsKey(IKey k) throws P2PApplicationException {
        boolean result=replicatedData.containsKey(k)||cachedData.containsKey(k);
        if(!result){
            //TODO look on the network for the given key...
            
        }
        
        return result;
    }
    
    public boolean objectContains(IKey k){
        return replicatedData.containsKey(k)||cachedData.containsKey(k);
    }

    public boolean storeReplica(IKey k, Object o){
        boolean result=false;
        if(!replicatedData.containsKey(k)){
            replicatedData.put(k,o);
            result=true;
        }
        return result;
    }
    
    public boolean storeCached(IKey k, Object o){
        boolean result=false;
        if(!cachedData.containsKey(k)){
            cachedData.put(k,o);
            result=true;
        }
        return result;
    }
    
    public KeyValuePair[] getDataInRange(KeyRange kr) throws P2PApplicationException  {
        // TODO Auto-generated method stub
        return null;
    }    
}
