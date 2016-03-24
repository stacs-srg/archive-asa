/*
 * Created on 07-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.applicationFramework.impl.Message;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.*;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalDHTImpl implements DHT, ApplicationUpcallHandler, P2PNetworkChangeHandler{
    private HashMap localData;
    private P2PApplicationFramework framework;
    private NetworkAwareDHTImpl networkAwareDHT;

    public LocalDHTImpl(P2PApplicationFramework framework, NetworkAwareDHTImpl net) {
        localData = new HashMap();
        this.framework=framework;
        this.networkAwareDHT=net;
        this.framework.addP2PNetworkChangeHandler(this);
    }

    public Object get(IKey k) throws P2PApplicationException { 
        Object result=null;
        
        boolean inLocalKeyRange=false;
        try {
            inLocalKeyRange=framework.inLocalKeyRange(k);
        } catch (P2PNodeException e) {
            throw new P2PApplicationException(P2PStatus.STATE_ACCESS_FAILURE,
                    "failed to determine if specified key was in local key range.");
        }
        
        if(inLocalKeyRange){
            result=localData.get(k);
        }else{
            result=networkAwareDHT.get(k);
        }
        
        return result;
    }

    public boolean put(IKey k, Object o) throws P2PApplicationException {
        boolean result=false;
        
        boolean inLocalKeyRange=false;
        try {
            inLocalKeyRange=framework.inLocalKeyRange(k);
        } catch (P2PNodeException e) {
            throw new P2PApplicationException(P2PStatus.STATE_ACCESS_FAILURE,
                    "failed to determine if specified key was in local key range.");
        }
        
        if(inLocalKeyRange){
            if(localData.containsKey(k)){
                result=false;
            }else{
                result=true;
                localData.put(k,o);
            }
        }else{
            result=networkAwareDHT.put(k,o);
        }
        
        return result;
    }

    public boolean containsKey(IKey k) throws P2PApplicationException {
       return localData.containsKey(k)||networkAwareDHT.containsKey(k);
    }

    // obsolete?
//    private boolean objectContains(IKey k){
//        return localData.containsKey(k);
//    }
    
    // obsolete?
//    private List removeKeysInRange(KeyRange range){
//        Vector values=new Vector();
//        for(Iterator i=localData.keySet().iterator();i.hasNext();){
//            IKey key=(IKey)i.next();
//            if(range.inRange(key)){
//                Object value=localData.remove(key);
//                KeyValuePair kvp=new KeyValuePair(key,value);
//                values.add(kvp);
//            }
//        }
//        Collections.sort(values);
//        return values;
//    }
    
    public String applicationNextHop(IKey k, AID a) throws P2PApplicationException {
        boolean inLocalKeyRange=false;
        try {
            inLocalKeyRange=framework.inLocalKeyRange(k);
        } catch (P2PNodeException e) {
            throw new P2PApplicationException(P2PStatus.STATE_ACCESS_FAILURE,e.getLocalizedMessage());
        }
        /*
         * Here we want to know if this is the root node for the given key of if
         * this DHT node holds the given key. Don't use
         * 'networkAwareDHT.containsKey(k)' because that does a P2P lookup for
         * the given key.
         */ 
        if(inLocalKeyRange || networkAwareDHT.objectContains(k)){
            return DHTComponentRRTDeployment.localDHT_SERVICE;
        }else{
           return null;
        }
    }

    public String applicationNextHop(IKey k, AID a, Message m) throws P2PApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    public void receiveMessage(IKey k, AID a, Message m) throws P2PApplicationException {
        // TODO Auto-generated method stub
        
    }

    public void increasedLocalKeyRange(KeyRange currentRange, KeyRange diff) {
        /*
         * This means that the node is now responsible for some additional range of keys.
         * But since we cannot predict extant keys in diff there is nothing this node 
         * can do.
         */
        System.out.println("Local key range increased");
    }

    public void decreasedLocalKeyRange(KeyRange currentRange, KeyRange diff){
        
        /*
         * This means that the node is nolonger responsible for some range of keys.
         * Store any data in the range diff at its new root node.
         */
        
        //TODO deleteme
        System.out.println("Local key range decreased");
        //
        
//        List values=removeKeysInRange(diff);
//        for(Iterator i=values.iterator();i.hasNext();){
//            KeyValuePair kvp=(KeyValuePair)i.next();
//            try {
//                boolean inLocalKeyRange=false;
//                try {
//                    inLocalKeyRange=framework.inLocalKeyRange(kvp.getKey());
//                } catch (P2PNodeException e) {
//                    System.out.println(e.getLocalizedMessage());
//                    e.printStackTrace();
//                }
//                if(!inLocalKeyRange){
//                    System.out.println("NOT ROOT>>> data with key "+kvp.getKey()+" removed from local store.");
//                }else{
//                    System.out.println("STILL ROOT!!!>>>data with key "+kvp.getKey()+" removed from local store.");
//                }
//                boolean success=networkAwareDHT.put(kvp.getKey(),kvp.getValue());
//            } catch (P2PApplicationException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
        
        
    }

    public void replicaSetChange(ReplicaInfo[] replicas, ArrayList[] addedSites, ArrayList[] removedSites) {
        // TODO Auto-generated method stub
        
    }

}
