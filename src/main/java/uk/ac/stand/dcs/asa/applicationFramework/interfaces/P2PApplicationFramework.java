/*
 * Created on 09-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.interfaces.IP2PRoutingAPI;

import java.net.InetSocketAddress;


/**
 * @author stuart
 */
public interface P2PApplicationFramework extends IP2PRoutingAPI, P2PRoutingStateQueryAPI{
    public static final P2PStatus P2P_INIT_SUCCESS = P2PStatus.NODE_RUNNING;
    /**
     * @return Returns the applicationRegistry.
     */
    public ApplicationRegistry getApplicationRegistry();
    public void initialiseP2PNode(String localAddress, int localPort, EventBus bus) throws P2PNodeException;
    public void initialiseP2PNode(String localAddress, int localPort, String knownNodeAddress, int knownNodePort, EventBus bus) throws P2PNodeException;
    public IP2PNode getLocalNode();
    public InetSocketAddress getLocalAddress();
    public IKey getLocalKey();
    public boolean inLocalKeyRange(IKey k) throws P2PNodeException;
    public boolean addP2PNetworkChangeHandler(P2PNetworkChangeHandler handler);
    public int getDefaultPort(); 
}