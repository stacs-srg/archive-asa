package uk.ac.stand.dcs.asa.storage.exceptions;

/**
 * Indicates an operation could not be performed because the wrong lock was presented.
 */
public class LockUseException extends Exception {

    public LockUseException(String message) {
        super(message);
    }
}
