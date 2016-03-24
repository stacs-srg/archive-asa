/**
 * Created on Aug 18, 2005 at 9:13:30 AM.
 */
package uk.ac.stand.dcs.asa.storage.exceptions;

/**
 * Indicates a problem with persistence.
 *
 * @author graham
 */
public class PersistenceException extends Exception {

	public PersistenceException(String msg) {
		super(msg);
	}
	public PersistenceException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
