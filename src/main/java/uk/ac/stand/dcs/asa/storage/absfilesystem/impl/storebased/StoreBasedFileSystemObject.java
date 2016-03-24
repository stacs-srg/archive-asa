/*
 * Created on Jun 16, 2005 at 9:03:03 AM.
 */
package uk.ac.stand.dcs.asa.storage.absfilesystem.impl.storebased;

import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.exceptions.AccessFailureException;
import uk.ac.stand.dcs.asa.storage.exceptions.PersistenceException;
import uk.ac.stand.dcs.asa.storage.persistence.impl.AttributedStatefulObject;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IAttributes;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IGUIDStore;

/**
 * Attributed stateful object that uses a store as its persistence mechanism.
 * 
 * @author al, graham
 */
public abstract class StoreBasedFileSystemObject extends AttributedStatefulObject implements IAttributedStatefulObject {

    protected IGUIDStore store;
    
    public StoreBasedFileSystemObject(IGUIDStore store, IAttributes atts) {
        super(atts);
        this.store = store;
    }
 
    public StoreBasedFileSystemObject(IGUIDStore store, IGUID guid, IAttributes atts) {
        super(guid, atts);
        this.store = store;
    }

    public StoreBasedFileSystemObject(IGUIDStore store, IData data, IAttributes atts) {
        super(data, atts);
        this.store = store;
    }
    
    public StoreBasedFileSystemObject(IGUIDStore store, IData data, IPID pid, IGUID guid, IAttributes atts ) {
        super(data, pid, guid, atts);
        this.store = store;
    }
    
    public void persist() throws PersistenceException {

        try {
            pid = store.put(reify());
            store.put(guid, pid);
		}
        catch (StorePutException e) { throw new PersistenceException(e.getMessage()); }	
	}
    
    public long getCreationTime() throws AccessFailureException{
        try {
            return store.getGUIDPutDate(guid);
        } catch (StoreGetException e) {
            throw new AccessFailureException("could not retrieve creation time");
        }
    }

    public long getModificationTime() throws AccessFailureException {
        try {
            return store.getPIDPutDate(pid);
        } catch (StoreGetException e) {
            throw new AccessFailureException("could not retrieve modification time");
        }
    }
}
