/*
 * Created on 09-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.*;
import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.INextHopResult;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.jchord.deploy.JChordRRTDeployment;
import uk.ac.stand.dcs.asa.jchord.events.JChordEventFactory;
import uk.ac.stand.dcs.asa.jchord.impl.JChordNextHopResult;
import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.Network;
import uk.ac.stand.dcs.asa.util.RemoteRRTRegistry;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.PolicyType;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.TransmissionPolicyManager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * JChordApplicationFrameworkImple implements the P2PApplicationFramework
 * interface and provides a shim over a JChordNode object. An instance of this
 * class contains a instance of a JChordNode which is instantiated using the
 * JChordRRTDeployment::customDeployment methods.
 * 
 * @author stuart
 */
public class JChordRRTApplicationFrameworkImpl implements
        P2PApplicationFramework, EventConsumer {
    
    // TODO this should really be done on a per-application basis, since any AID
    // implementation can be used
    static{
        TransmissionPolicyManager.setClassPolicy(AIDImpl.class.getName(), PolicyType.BY_VALUE, true);
    }
    
    private ApplicationRegistry applicationRegistry;

    private IJChordNode localJChordNode;

    private EventBus bus;

    private InetSocketAddress[] knownNodes;

    private InetSocketAddress localAddress = null;

    private IKey localKey = null;

    private Vector p2pNetworkChangeHandlers;

    public JChordRRTApplicationFrameworkImpl(ApplicationRegistry applicationRegistry) {
        this.applicationRegistry = applicationRegistry;
        p2pNetworkChangeHandlers = new Vector();
    }

    /**
     * @return Returns the applicationRegistry.
     */
    public ApplicationRegistry getApplicationRegistry() {
        return applicationRegistry;
    }

    /**
     * @see uk.ac.stand.dcs.asa.interfaces.IP2PRoutingAPI#lookup(uk.ac.stand.dcs.asa.interfaces.IKey)
     */
    public IP2PNode lookup(IKey k) throws P2PNodeException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see uk.ac.stand.dcs.asa.interfaces.IP2PRoutingAPI#dol(uk.ac.stand.dcs.asa.interfaces.IKey,
     *      uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID)
     */
    public DOLResult dol(IKey k, AID appID) throws P2PNodeException {
        if(k==null){
            throw new P2PNodeException(P2PStatus.LOOKUP_FAILURE,"routing failed because a null Key value specified in a 'dol' call");
        }
        
        JChordNextHopResult routingResult = localJChordNode.dol(k, appID);
        String componentServiceName = null;
        // check the result
        /*
         * The JChordNextHop object returned by the 'dol' call has a 'code'
         * field with value NextHopResult.INTERCEPT or NextHopResult.FINAL.
         */
        if (routingResult.getCode() == INextHopResult.INTERCEPT) {
            /*
             * If the 'code' is NextHopResult.INTERCEPT then the 'result' node
             * intercepted the routing and we contact that node to obtain the
             * appropriate application object.
             */
            componentServiceName = routingResult.getAppObjectName();
        } else {
            /*
             * If the 'code' is NextHopResult.FINAL then the 'result' node is
             * the root node for the given key. A call to 'nextHop_dol_resolve'
             * on the root node will yield a JChordNextHop object containing the
             * information neccessary to then retrieve the appropriate
             * application object from the root node.
             */
            if (routingResult.getCode() == INextHopResult.FINAL) {
                routingResult = routingResult.getResult().dol_resolve(
                        k, appID);
                /*
                 * The 'code' field of the JChordNextHopResult object returned
                 * by the 'nextHop_dol_resolve' call should be NextHop.ROOT.
                 */
                if (routingResult.getCode() == INextHopResult.ROOT) {
                    componentServiceName = routingResult.getAppObjectName();
                } else {
                    throw new P2PNodeException(P2PStatus.LOOKUP_FAILURE,
                            "routing resulted in a root node that was not the root of the specified key.");
                }
            } else {
                throw new P2PNodeException(P2PStatus.LOOKUP_FAILURE,
                        "routing failed due to unexpected value returned from remote node.");
            }
        }
        Object applicationObject = getApplicationObject(routingResult.getResult(),
                componentServiceName);
        return new DOLResult(applicationObject, routingResult.getResult(),
                routingResult.getCode() == INextHopResult.INTERCEPT ? false : true);
    }

    public Object getApplicationObject(IP2PNode remoteNode,
            String componentServiceName) throws P2PNodeException {
        InetSocketAddress hostAddress = null;
        try {
            hostAddress = remoteNode.getHostAddress();
        } catch (Exception e) {
            throw new P2PNodeException(P2PStatus.STATE_ACCESS_FAILURE,
                    "could not retrieve host address from JChord node proxy object.");
        }
        Object applicationObject = null;
        try {
            applicationObject = RafdaRunTime.getObjectByName(hostAddress
                    .getAddress().getHostAddress(), hostAddress.getPort(),
                    componentServiceName);
        } catch (Exception e1) {
            throw new P2PNodeException(P2PStatus.APPLICATION_FAILURE,
                    "could not get application object from remote node.");
        }
        return applicationObject;
    }

    /**
     * @see uk.ac.stand.dcs.asa.interfaces.IP2PRoutingAPI#dol_noIntercept(uk.ac.stand.dcs.asa.interfaces.IKey,
     *      uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID)
     */
    public DOLResult dol_noIntercept(IKey k, AID appID) throws P2PNodeException {
        IJChordRemote routingResult = localJChordNode.jnl(k);
        JChordNextHopResult dolResult=routingResult.dol_resolve(k,appID);
        String componentServiceName = null;

        if (dolResult.getCode() == INextHopResult.ROOT) {
            componentServiceName = dolResult.getAppObjectName();
        } else {
            throw new P2PNodeException(P2PStatus.LOOKUP_FAILURE,
                    "routing resulted in a root node that was not the root of the specified key.");
        }
        
        Object applicationObject = getApplicationObject(dolResult.getResult(),
                componentServiceName);
        return new DOLResult(applicationObject, dolResult.getResult(),
                true);
    }

    public void sendMsg(IKey k, AID appID, Message msg) throws P2PNodeException {
        
    	Error.hardError("unimplemented method");

    }

    public void sendMsg_noIntercept(IKey k, AID appID, Message msg)
            throws P2PNodeException {
        // TODO Auto-generated method stub

    }

    /**
     * If localPort is equal to Network.UNDEFINED_PORT then the framework uses
     * its default port value as defined by RemoteRRTRegistry.DEFAULT_RRT_PORT.
     * If localAddress is null the framework will use the
     * Network.defaultLocalHostAddress method to decide the local address for
     * the P2P node.
     * 
     * @param localAddress
     * @param localPort
     * @param bus
     * @throws P2PNodeException
     */
    public void initialiseP2PNode(String localAddress, int localPort, EventBus bus) throws P2PNodeException {
        initialiseP2PNode(localAddress,localPort,null,0,bus);
    }
    
    /**
     * If localPort or knownNodePort are equal to Network.UNDEFINED_PORT then
     * the framework uses its default port value as defined by
     * RemoteRRTRegistry.DEFAULT_RRT_PORT. If knownNodeAddress is null a new P2P
     * network will be created. If localAddress is null the framework will use
     * the Network.defaultLocalHostAddress method to decide the local address
     * for the P2P node.
     * 
     * @param localAddress
     * @param localPort
     * @param knownNodeAddress
     * @param knownNodePort
     * @param bus
     * @throws P2PNodeException
     */
    public void initialiseP2PNode(String localAddress, int localPort, String knownNodeAddress, int knownNodePort, EventBus bus) throws P2PNodeException {
        InetSocketAddress local=null;
        InetSocketAddress[] known= new InetSocketAddress[]{null};
        
        int thePort = localPort==Network.UNDEFINED_PORT?RemoteRRTRegistry.DEFAULT_RRT_PORT:localPort;
        if(localAddress==null){
            try {
                local = Network.defaultLocalHostAddress(thePort);
            } catch (IllegalArgumentException e) {
                throw new P2PNodeException(P2PStatus.INSTANTIATION_FAILURE,
                        "illegal port ("+Integer.toString(thePort)+") was specified for local node");
            } catch (UnknownHostException e) {
                throw new P2PNodeException(P2PStatus.INSTANTIATION_FAILURE,
                        "could not configure local address using default values");
            }
        }else{
            try {
                InetAddress addr = InetAddress.getByName(localAddress);
                local= new InetSocketAddress(addr,thePort);
            }catch (IllegalArgumentException e) {
                throw new P2PNodeException(P2PStatus.INSTANTIATION_FAILURE,
                        "illegal host name ("+ localAddress +") or port ("+ 
                        Integer.toString(thePort) +") was specified for local node");
            } catch (UnknownHostException e) {
                throw new P2PNodeException(P2PStatus.INSTANTIATION_FAILURE,
                        "illegal host name ("+ localAddress +") was specified for local node");
            }
        }
        
        if(knownNodeAddress!=null){
            int theKnownNodePort = knownNodePort==Network.UNDEFINED_PORT?RemoteRRTRegistry.DEFAULT_RRT_PORT:knownNodePort;
            try {
                InetAddress addr = InetAddress.getByName(knownNodeAddress);
                known[0] = new InetSocketAddress(addr,theKnownNodePort);
            }catch (IllegalArgumentException e) {
                throw new P2PNodeException(P2PStatus.INSTANTIATION_FAILURE,
                        "illegal host name ("+ knownNodeAddress +") or port ("+ Integer.toString(theKnownNodePort) +") was specified for known node");
            } catch (UnknownHostException e) {
                throw new P2PNodeException(P2PStatus.INSTANTIATION_FAILURE,
                        "illegal host name ("+ knownNodeAddress +") was specified for known node");
            }
        }
        initialiseP2PNode(local,known,bus);
    }
    
//    public void initialiseP2PNode(InetAddress localAddress, int localPort, EventBus bus) throws P2PNodeException {
//        initialiseP2PNode(localAddress,localPort,null,0,bus);
//    }

    private void initialiseP2PNode(InetSocketAddress localAddress,
            InetSocketAddress[] knownNodes, EventBus bus)
            throws P2PNodeException {
        
        InetSocketAddress configLocalAddress=localAddress;;
        if(configLocalAddress==null){
            //use some default value
            try {
                configLocalAddress=Network.defaultLocalHostAddress(getDefaultPort());
            } catch (UnknownHostException e) {
                throw new P2PNodeException(P2PStatus.LOCAL_HOST_INIT_FAILURE,"A failure occurred while computing the default local address");
            }
        }
        this.localAddress=configLocalAddress;
        // Dirty Hack Alert!
        // This allows us to know the key before the node
        // configuration is finished. Useful for sending events
        // for visualisation and the like.
        this.localKey=SHA1KeyFactory.generateKey(configLocalAddress);
        
        this.knownNodes = knownNodes;
        this.bus = bus;
        this.bus.register(this);
        InetSocketAddress known = this.knownNodes!=null?this.knownNodes[0]:null;
        
        localJChordNode = JChordRRTDeployment.customDeployment(configLocalAddress,
                known, this.bus, true, applicationRegistry);
        try {
            this.localAddress = localJChordNode.getHostAddress();
        } catch (Exception e) {
            throw new P2PNodeException(
                    P2PStatus.GENERAL_NODE_DEPLOYMENT_FAILURE,
                    "could not obtain local host address from P2P node.");
        }   
    }

//    public void initialiseP2PNode(InetSocketAddress localAddress, EventBus bus)
//            throws P2PNodeException {
//        initialiseP2PNode(localAddress, null, bus);
//
//    }
//
//    public void initialiseP2PNode(InetSocketAddress localAddress,
//            InetSocketAddress[] knownNodes) throws P2PNodeException {
//        initialiseP2PNode(localAddress, knownNodes);
//    }
//
//    public void initialiseP2PNode(InetSocketAddress localAddress)
//            throws P2PNodeException {
//        initialiseP2PNode(localAddress, null, null);
//    }

    public IP2PNode getLocalNode() {
        return localJChordNode;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public IKey getLocalKey() {
        return localKey;
    }

    /**
     * @see uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFramework#inLocalKeyRange(uk.ac.stand.dcs.asa.interfaces.IKey)
     */
    public boolean inLocalKeyRange(IKey k) throws P2PNodeException {
        if (localJChordNode == null) {
            // the p2p node has not been initialiased
            throw new P2PNodeException(P2PStatus.STATE_ACCESS_FAILURE,
                    "The framework's P2P node has not been initialised.");
        }else{
            return localJChordNode.inLocalKeyRange(k);
        }
    }

    public boolean addP2PNetworkChangeHandler(P2PNetworkChangeHandler handler) {
        if (!p2pNetworkChangeHandlers.contains(handler)) {
            p2pNetworkChangeHandlers.add(handler);
            return true;
        } else {
            return false;
        }
    }

    public boolean interested(Event event) {
        return event.getType().compareTo(JChordEventFactory.PRED_REP_EVENT) == 0
                || event.getType().compareTo(JChordEventFactory.SUCC_REP_EVENT) == 0 ? true
                : false;
    }

    public void receiveEvent(Event event) {
        executeP2PNetworkChangeHandlers(event);
    }

    private IKey pred_key = null;

    private void executeP2PNetworkChangeHandlers(Event event) {
        if (event.getType().equals(JChordEventFactory.PRED_REP_EVENT)) {
            InetSocketAddress eventPred = (InetSocketAddress) event.get("pred");
            IKey eventPredKey = (IKey) event.get("pred_key");
            
            if(eventPred!=null){
                boolean decreased=pred_key==null?false:keyRangeDecreased(pred_key,eventPredKey);
                KeyRange range=KeyRange.makeClosedRangeFromHalfOpen_includeUpper(eventPredKey,localKey);
                KeyRange diff=calculateDiff(pred_key,eventPredKey);
                if(diff!=null){
                    for (Iterator i = p2pNetworkChangeHandlers.iterator(); i.hasNext();) {
                        P2PNetworkChangeHandler handler = (P2PNetworkChangeHandler) i.next();   
                        if(decreased){
                            handler.decreasedLocalKeyRange(range,diff);
                        }else{
                            handler.increasedLocalKeyRange(range,diff);
                        }
                    }
                    pred_key = eventPredKey;
                }
            }
        } else {
            if (event.getType().equals(JChordEventFactory.SUCCESSORLIST_CHANGE_EVENT)) {
                for (Iterator i = p2pNetworkChangeHandlers.iterator(); i.hasNext();) {
                    P2PNetworkChangeHandler handler = (P2PNetworkChangeHandler) i.next();
                    KeyRange range = (KeyRange)event.get(JChordEventFactory.SUCCESSORLIST_CHANGE_KEY_RANGE);
                    ArrayList new_list = (ArrayList)event.get(JChordEventFactory.SUCCESSORLIST_CHANGE_NEW_LIST);
                    ArrayList added = (ArrayList)event.get(JChordEventFactory.SUCCESSORLIST_CHANGE_ADDED);
                    ArrayList removed= (ArrayList)event.get(JChordEventFactory.SUCCESSORLIST_CHANGE_REMOVED);
                    
                    /*
                     * We require some mapping from IJChordRemote objects, which
                     * the SuccessorList knows about, to ReplicaSite objects
                     * that the application can understand and work with to
                     * replicate data.
                     */
                    
                    ArrayList sites=arrayList_IJChordRemote_to_ReplicaSite(new_list);
                    ReplicaInfo ri=new ReplicaInfoImpl(range,sites);
                    ReplicaInfo[] replicas=new ReplicaInfo[]{ri};
                    
                    
                    ArrayList[] addedSites=new ArrayList[]{arrayList_IJChordRemote_to_ReplicaSite(added)};
                    ArrayList[] removedSites=new ArrayList[]{arrayList_IJChordRemote_to_ReplicaSite(removed)};
                                        
                    handler.replicaSetChange(replicas,addedSites,removedSites);
                }
            }/*else{
                if (event.getType().compareTo(JChordEventFactory.SUCC_REP_EVENT) == 0) {
                    
                }
            }*/
        }
    }

    private ArrayList arrayList_IJChordRemote_to_ReplicaSite(ArrayList jchordremote){
        if(jchordremote!=null){
            Iterator i=jchordremote.iterator();
            ArrayList sites=new ArrayList();
            while (i.hasNext()) {
                IJChordRemote jcnode = (IJChordRemote) i.next();
                InetSocketAddress address=null;
                try {
                    address = jcnode.getHostAddress();
                } catch (Exception e) {
                    Error.hardError("could not get host address from IJChordRemote object");
                }
                sites.add(new ReplicaSiteImpl(address));
            }
            return sites;
        }else{
            return null;
        }
    }
    
    private boolean keyRangeDecreased(IKey predKey, IKey newKey) {
        return newKey.ringDistanceTo(localKey).compareTo(predKey.ringDistanceTo(localKey))>0;
    }

    private KeyRange calculateDiff(IKey predKey, IKey newKey) {
        if(newKey!=null){
            if (predKey == null) {
                /*
                 * This case is treated as an increase in the key range. If the
                 * predecessor is null then the local key range for the local
                 * node is undefined. This will only ever happen once since the
                 * framework effectively ignores the case where prdecessor is set
                 * to null.
                 */
                return KeyRange.makeClosedRangeFromHalfOpen_includeUpper(newKey,
                        localKey);
            }else{
                if(predKey.compareTo(newKey)!=0){ //make sure they are different
                    return keyRangeDecreased(predKey, newKey) ? 
                            KeyRange.makeClosedRangeFromHalfOpen_includeUpper(predKey, newKey) : 
                                KeyRange.makeClosedRangeFromHalfOpen_includeUpper(newKey,predKey);
                }else{
                    return null;
                }
            }
        }else{
            return null;
        }
    }

    public int getDefaultPort() {
        return RemoteRRTRegistry.DEFAULT_RRT_PORT;
    }

}
