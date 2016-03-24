/*
 * Created on Jan 1, 2005 at 5:10:36 PM.
 */
package uk.ac.stand.dcs.asa.simulation;

/**
 * @author graham
 */
public class SimulatedFailureException extends Exception {

    public SimulatedFailureException() {
        super();
    }

    public SimulatedFailureException(String arg0) {
        super(arg0);
    }

    public SimulatedFailureException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SimulatedFailureException(Throwable arg0) {
        super(arg0);
    }
}
