/*
 * Created on 09-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.*;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.util.Error;

import java.util.HashMap;

/**
 * @author stuart
 */
public class ApplicationRegistryImpl implements ApplicationRegistry {
    private HashMap registeredComponents;
    
    public static final String EXCEPTION_MESSAGE_DUPLICATE = "An ApplicationComponent with a matching key has already been registered";
    
    public ApplicationRegistryImpl(){
        this.registeredComponents = new HashMap();
    }
    
    public AID makeNewAID() {
        return null;
    }

    public String forward(IKey k, AID a) throws P2PApplicationException {
        String result=null;
        Object o = registeredComponents.get(a);
    
        if(o!=null){
            if(o instanceof P2PApplicationComponent){
                P2PApplicationComponent com = (P2PApplicationComponent)o;
                ApplicationUpcallHandler auh = com.getApplicationUpcallHandler();
                result=auh.applicationNextHop(k,a);
            }else{
                Error.hardError("registeredComponents HashMap contained an object of unexpected type "+o.getClass().getName());  
            }
        }
        return result;
    }

    public void registerApplicationComponent(P2PApplicationComponent ac) throws ApplicationRegistryException {
        
        if(registeredComponents.containsKey(ac.getAID())){
            throw new ApplicationRegistryException(ac,EXCEPTION_MESSAGE_DUPLICATE);
        }else{
            registeredComponents.put(ac.getAID(),ac);
        }
    }

    public void unregisterApplicationComponent(P2PApplicationComponent ac){
        registeredComponents.remove(ac.getAID());
    }

    public Object[] getRegisteredComponents() {
        return registeredComponents.entrySet().toArray();
    }

    public void update(IP2PNode node, boolean joined) {
        
    	// TODO should this do anything?
    }
}
