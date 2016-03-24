/**
 * Created on Sep 9, 2005 at 12:36:03 PM.
 */
package uk.ac.stand.dcs.asa.storage.absfilesystem.impl.localfilebased;

import uk.ac.stand.dcs.asa.storage.absfilesystem.interfaces.IDirectory;
import uk.ac.stand.dcs.asa.storage.persistence.impl.AttributedStatefulObject;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.util.UriUtil;
import uk.ac.stand.dcs.asa.util.Error;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Stateful object that uses the file system as its persistence mechanism.
 * 
 * @author al, graham
 */
public abstract class FileBasedFileSystemObject extends AttributedStatefulObject implements IAttributedStatefulObject {
	
	protected String name;
	protected IDirectory logical_parent;
	protected File real_file;

    public FileBasedFileSystemObject() {
        super(null);
        this.name = "";
    }
 
    public FileBasedFileSystemObject(IDirectory logical_parent, String name) {
        super(null);
        this.name = name;
        this.logical_parent = logical_parent;
    }
    
    public FileBasedFileSystemObject(IDirectory logical_parent, String name, IData data) {
        super(data, null);
        this.name = name;
    }

	protected File getRealFile() {
		return real_file;
	}

	public URI getURI() {

		try {
			return new URI(UriUtil.uriEncode(real_file.getCanonicalPath()));
		}
		catch (URISyntaxException e) {
			Error.hardExceptionError("uri syntax error", e);
			return null;
		}
		catch (IOException e) {
			Error.hardExceptionError("I/O error", e);
			return null;
		}
	}
}
