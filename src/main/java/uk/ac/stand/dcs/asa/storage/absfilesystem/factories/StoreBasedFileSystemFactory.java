/*
 * Created on May 30, 2005 at 1:28:34 PM.
 */
package uk.ac.stand.dcs.asa.storage.absfilesystem.factories;

import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.storage.absfilesystem.exceptions.FileSystemCreationException;
import uk.ac.stand.dcs.asa.storage.absfilesystem.impl.storebased.StoreBasedFileSystem;
import uk.ac.stand.dcs.asa.storage.absfilesystem.interfaces.IFileSystem;
import uk.ac.stand.dcs.asa.storage.absfilesystem.interfaces.IFileSystemFactory;
import uk.ac.stand.dcs.asa.storage.exceptions.PersistenceException;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreIntegrityException;
import uk.ac.stand.dcs.asa.storage.store.factories.LocalFileBasedStoreFactory;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IGUIDStore;

/**
 * Factory providing methods to create a new file system using a given store.
 *  
 * @author al, graham
 */
public class StoreBasedFileSystemFactory implements IFileSystemFactory {
	
	private IGUIDStore store;
	private IGUID root_GUID;
    
    /**
     * Creates a file system factory using the default store.
     */
    public StoreBasedFileSystemFactory(IGUID root_GUID) {
    	
    	this(null, root_GUID);
    }
    
    /**
     * Creates a file system factory using the specified store.
     */
   public StoreBasedFileSystemFactory(IGUIDStore store, IGUID root_GUID) {
	   
	   this.store = store;
	   this.root_GUID = root_GUID;
     }
   
   public IFileSystem makeFileSystem() throws FileSystemCreationException {
	   
	   IGUIDStore store = this.store;
	   if (store == null) store = new LocalFileBasedStoreFactory().makeStore();
	   
	   try {
		   return new StoreBasedFileSystem(store, root_GUID);
	   }
	   catch (StoreIntegrityException e) { throw new FileSystemCreationException(e.getMessage()); }
	   catch (PersistenceException e)    { throw new FileSystemCreationException(e.getMessage()); }
   }
}
