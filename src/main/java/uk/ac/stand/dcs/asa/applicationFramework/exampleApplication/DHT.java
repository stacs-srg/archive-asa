/*
 * Created on 07-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IKey;

public interface DHT{
    public Object get(IKey k) throws P2PApplicationException;
    public boolean put(IKey k, Object o) throws P2PApplicationException;
    public boolean containsKey(IKey k) throws P2PApplicationException;
}
