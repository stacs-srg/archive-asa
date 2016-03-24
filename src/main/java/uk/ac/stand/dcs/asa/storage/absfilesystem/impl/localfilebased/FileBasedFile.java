/**
 * Created on Sep 9, 2005 at 12:36:03 PM.
 */
package uk.ac.stand.dcs.asa.storage.absfilesystem.impl.localfilebased;

import uk.ac.stand.dcs.asa.storage.absfilesystem.impl.storebased.StoreBasedFileSystem;
import uk.ac.stand.dcs.asa.storage.absfilesystem.interfaces.IDirectory;
import uk.ac.stand.dcs.asa.storage.absfilesystem.interfaces.IFile;
import uk.ac.stand.dcs.asa.storage.exceptions.PersistenceException;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IAttributes;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.util.Attributes;
import uk.ac.stand.dcs.asa.storage.webdav.impl.MIME;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.KeyImpl;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * File implementation using real local file system.
 * 
 * @author graham
 */
public class FileBasedFile extends FileBasedFileSystemObject implements IFile {

	/**
     * Used to create a new file.
     */
    public FileBasedFile(IDirectory logical_parent, String name, IData data) throws PersistenceException {
    	
        super(logical_parent, name, data);
        
        if (!(logical_parent instanceof FileBasedDirectory)) Error.hardError("parent of file-based file isn't file-based");
        
        real_file = new File(((FileBasedDirectory)logical_parent).getRealFile(), name);
        
        try {
			guid = (KeyImpl)SHA1KeyFactory.generateKey(real_file.getCanonicalPath());    // generate GUID based on file path
		} catch (IOException e) {
			throw new PersistenceException("can't obtain file path for backing file");
		} 
        
        persist();
    }

	public long getCreationTime() {

		return 0;  // Can't obtain from file itself, so would have to maintain separate persistent data structure.
	}

	public long getModificationTime() {
		
		return real_file.lastModified();
	}

	public void persist() throws PersistenceException {
		
		if (real_file.exists()) {
			if (!real_file.isFile()) throw new PersistenceException("backing file isn't a file");
		}
		else {
			try {
				if (!real_file.createNewFile()) throw new PersistenceException("couldn't create file");
			}
			catch (IOException e) { throw new PersistenceException("couldn't create file"); }
		}
		
		// Write the data to the file.
        byte[] bytes = state.getState();
        
        try {
	        FileOutputStream output_stream = new FileOutputStream(real_file);
			output_stream.write(bytes);
			output_stream.close();
    	}
    	catch (IOException e) { throw new PersistenceException("couldn't write data to file: "+ e.getMessage()); }
	}

	public IAttributes getAttributes() {
		
		String content_type = MIME.getContentTypeFromFileName(name);

        IAttributes attributes = new Attributes( StoreBasedFileSystem.ISFILE + Attributes.EQUALS + "true" + Attributes.SEPARATOR +
                StoreBasedFileSystem.CONTENT + Attributes.EQUALS + content_type + Attributes.SEPARATOR );
        
        return attributes;
	}

	public void setAttributes(IAttributes atts) {
		Error.hardError("unimplemented method");
	}

}
