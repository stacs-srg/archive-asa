/*
 * Created on 06-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.applicationFramework.impl.AIDImpl;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.*;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

public class DHTComponent implements P2PApplicationComponent, DHT{

    public static final AID myAID = new AIDImpl("CF48516C-474E-BB27-7A96-15F1320A2A38");
    
    private P2PApplicationFramework framework;
    private NetworkAwareDHTImpl networkAwareDHT;
    private LocalDHTImpl localDHT;
    
    public LocalDHTImpl getLocalDHT() {
        return localDHT;
    }

    public NetworkAwareDHTImpl getNetworkAwareDHT() {
        return networkAwareDHT;
    }

    public DHTComponent(P2PApplicationFramework framework) {
        this.framework=framework;
        networkAwareDHT = new NetworkAwareDHTImpl(this.framework);
        localDHT = new LocalDHTImpl(framework,networkAwareDHT);
    }
    
    public AID getAID() {
        return myAID;
    }

    public ApplicationUpcallHandler getApplicationUpcallHandler() {
        return localDHT;
    }
    
    public Object get(IKey k) throws P2PApplicationException {
        return localDHT.get(k);
    }

    public boolean put(IKey k, Object o) throws P2PApplicationException {
        return localDHT.put(k,o);
    }
    
    public boolean containsKey(IKey k) throws P2PApplicationException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * This method allows applications to store String objects without
     * regard to the details of how the corresponding keys are generated.
     * The key with which the string object was stored is returned. Null 
     * is returned. If the object could not be added to the DHT.
     *  
     * @param s the string to be stored in the DHT
     * @return the key with which the specified string object was stored.
     * @throws P2PApplicationException
     */
    
    public IKey put(String s) throws P2PApplicationException {
        IKey k = SHA1KeyFactory.generateKey(s);
        boolean success=localDHT.put(k,s);
        if(success)return k;
        else return null;
    }
    
    /**
     * This method allows applications to retrieve objects that are known to 
     * be of type String. An exception is thrown if the DHT contains an object
     * that is stored with Key k and is not of type String.
     */
    public String getString(IKey k) throws P2PApplicationException, ClassCastException {
        return (String)localDHT.get(k);
    }

}
