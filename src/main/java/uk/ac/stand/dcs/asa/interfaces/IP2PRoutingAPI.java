/*
 * Created on 10-May-2005
 */
package uk.ac.stand.dcs.asa.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.DOLResult;
import uk.ac.stand.dcs.asa.applicationFramework.impl.Message;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID;

/**
 * This interface defines the P2P routing methods that are exposed to the application-level components
 * by the P2PApplicationFramework implmentations.
 * 
 * @author stuart
 */
public interface IP2PRoutingAPI {
	
    /**
     * Returns the root of key k as defined by the overlay network.
     * This corresponds to the lookup operation in Chord.
     */
    public IP2PNode lookup(IKey k) throws P2PNodeException;
    
    // Decentralised object location methods.
    
    /**
     * Returns a DOLResult object for an application-level component, with AID appID,
     * that has responsibility for Key k. The first application-level component that is
     * interested in the key, along the route to the root of k.
     */  
    public DOLResult dol(IKey k, AID appID) throws P2PNodeException;
    
    /**
     * Returns a DOLResult object for application level component, with AID appID,
     * at the root node for Key k.
     */
    public DOLResult dol_noIntercept(IKey k, AID appID) throws P2PNodeException;
    
    // Message sending methods - asynchronous or synchronous?
    
    /**
     * Delivers the message msg to an application-level component, with AID appID,
     * that has responsibility for Key k. The first application-level component that is
     * interested in the key, along the route to the root of k.
     */    
    public void sendMsg(IKey k, AID appID, Message msg) throws P2PNodeException;
    
    /**
     * Delivers the message msg to the application-level component, with AID appID,
     * at the root node for key k.
     */    
    public void sendMsg_noIntercept(IKey k, AID appID, Message msg) throws P2PNodeException;
    
    // Additional methods for discussion.
    
    //Synchronous message sending methods
//    /**
//     * Delivers the message msg to an application-level component, with AID appID,
//     * that has responsibility for Key k, and blocks waiting for a response in the 
//     * from of a SendMsgResult object. The first application-level component that
//     * is interested in the key, along the route to the root of k.
//     */    
//    public SendMsgResult sendMsg_blocking(Key k, AID appID, Message msg) throws P2PNodeException;

    
//    /**
//     * Delivers the message msg to the application-level component, with AID appID,
//     * at the root node for key k, and blocks waiting for a response in the 
//     * from of a SendMsgResult object. The first application-level component that
//     */    
//    public SendMsgResult sendMsg_noIntercept_blocking(Key k, AID appID, Message msg) throws P2PNodeException;
    
}
