/**
 * Created on Sep 8, 2005 at 9:40:28 PM.
 */
package uk.ac.stand.dcs.asa.storage.absfilesystem.util;

import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.absfilesystem.impl.general.StringData;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IGUIDStore;
import uk.ac.stand.dcs.asa.util.GUIDFactory;

/**
 * 
 *
 * @author graham
 */
public class RootGUIDStorage {

    public static IGUID getRootGUID(IGUIDStore store, IGUID root_GUID_index) throws StoreGetException{
	
    	IPID PID_of_root_GUID = store.getLatestPID(root_GUID_index);
    	
    	if (PID_of_root_GUID == null) return null;
    	else {
	    	IData root_GUID_data = store.get(PID_of_root_GUID);
	    	IGUID root_GUID = GUIDFactory.recreateGUID(new String(root_GUID_data.getState()));
	    	
	    	return root_GUID;
    	}
    }
    
    public static void setRootGUID(IGUIDStore store, IGUID root_GUID_index, IGUID root_GUID) throws StorePutException {
	
    	IData root_GUID_data = new StringData(root_GUID.toString());
    	IPID PID_of_root_GUID = store.put(root_GUID_data);
    	store.put(root_GUID_index, PID_of_root_GUID);
    }
}
