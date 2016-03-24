/*
 * Created on 10-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.interfaces;


/**
 * @author stuart
 */
public interface P2PApplicationComponent {
	
    public AID getAID();
    
    /**
     * This method is called by the framework to obtain the application's
     * upcall handler when an upcall needs to be made. The framework will
     * only call this method on an ApplicationComponent with AID 'x' if 
     * the routing method currently being executed has an AID parameter
     * which equals 'x'.
     */
    public ApplicationUpcallHandler getApplicationUpcallHandler();
}
