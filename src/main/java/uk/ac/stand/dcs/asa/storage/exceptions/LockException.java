package uk.ac.stand.dcs.asa.storage.exceptions;

/**
 * Indicates an operation could not be performed because the target object was locked.
 */
public class LockException extends Exception {

    public LockException(String message) {
        super(message);
    }
}
