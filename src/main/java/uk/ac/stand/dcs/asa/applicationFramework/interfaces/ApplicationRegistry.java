/*
 * Created on 10-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.ApplicationRegistryException;
import uk.ac.stand.dcs.asa.interfaces.IKey;

/**
 * An implementation of this interface allows the user application to register an application 
 * component to recieve appropriate routing upcalls.
 * 
 * @author stuart
 */
public interface ApplicationRegistry {
	
    void registerApplicationComponent(P2PApplicationComponent ac) throws ApplicationRegistryException;
    void unregisterApplicationComponent(P2PApplicationComponent ac);
    Object[] getRegisteredComponents();

    public String forward(IKey k, AID a) throws P2PApplicationException;
}
