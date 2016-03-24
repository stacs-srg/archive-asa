/*
 * Created on Dec 9, 2004 at 9:40:47 PM.
 */
package uk.ac.stand.dcs.asa.applications.store;

/**
 * @author graham
 *
 * Exception indicating storage problem.
 */
public class StorageException extends Exception {

    public StorageException() {
        super();
    }

    public StorageException(String arg0) {
        super(arg0);
    }

    public StorageException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public StorageException(Throwable arg0) {
        super(arg0);
    }
}
