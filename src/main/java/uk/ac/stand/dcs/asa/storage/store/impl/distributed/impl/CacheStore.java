/*
 * Created on 09-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.interfaces.ILocalGUIDStore;
import uk.ac.stand.dcs.asa.util.Error;

public class CacheStore implements ILocalGUIDStore{

    public void put3(IGUID guid, IPID[] pid) throws P2PApplicationException {
    	Error.hardError("unimplemented method");
        
    }

    public IData get(IPID pid) throws P2PApplicationException {
    	Error.hardError("unimplemented method");
        return null;
    }

    public IPID put(IData data) throws P2PApplicationException {
    	Error.hardError("unimplemented method");
        return null;
    }

    public long getPIDPutDate(IPID pid) throws P2PApplicationException {
    	Error.hardError("unimplemented method");
        return 0;
    }

    public IPID getLatestPID(IGUID guid) throws P2PApplicationException {
    	Error.hardError("unimplemented method");
        return null;
    }

    public void put2(IGUID guid, IPID pid) throws P2PApplicationException {
    	Error.hardError("unimplemented method");   
    }

    public long getGUIDPutDate(IGUID guid) throws P2PApplicationException {
    	Error.hardError("unimplemented method");
        return 0;
    }



}
