/**
 * Created on Aug 10, 2005 at 3:01:15 PM.
 */
package uk.ac.stand.dcs.asa.storage.store.exceptions;

/**
 * Exception indicating a problem with the integrity of a store.
 *
 * @author graham
 */
public class StoreIntegrityException extends Exception {

	public StoreIntegrityException(String message) {

		super(message);
	}

}
