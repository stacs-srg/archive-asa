/*
 * Created on Dec 9, 2004 at 9:02:23 AM.
 */
package uk.ac.stand.dcs.asa.applications.store;

/**
 * @author graham
 *
 * Exception indicating incorrect initialisation.
 */
public class InitialisationException extends Exception {

    /**
     * 
     */
    public InitialisationException() {
        super();
    }

    /**
     * @param arg0
     */
    public InitialisationException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public InitialisationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public InitialisationException(Throwable arg0) {
        super(arg0);
    }

}
