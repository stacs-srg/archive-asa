/*
 * Created on 02-Nov-2004
 */
package uk.ac.stand.dcs.asa.jchord.deploy;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.ApplicationRegistry;
import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.jchord.impl.JChordNextHopResult;
import uk.ac.stand.dcs.asa.jchord.impl.JChordNodeImpl;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.jchord.nodeFactories.JChordSingleton;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.*;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.PolicyType;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.TransmissionPolicyManager;
import uk.ac.stand.dcs.rafda.rrt.ui.UI;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Provides a number of static methods for deploying a JChord node using the
 * RRT. In all cases the JChordSingleton factory class is used to instantiate
 * the JChord node.
 * 
 * @author stuart, graham
 */
public class JChordRRTDeployment {

    private static IJChordNode node = null;

    public static final P2PStatus initialisationSuccess = P2PStatus.NODE_RUNNING;

    /**
     * The default port for the JChord node RRT. Different from
     * RemoteRRTRegistry default port to allow admin node and a single default
     * JChord node to coexist.
     */
    private static final int DEFAULT_JCHORD_NODE_PORT = 52526;

    /**
     * Sets the RRT transmission policies, using
     * TransmissionPolicyManager.setClassPolicy and
     * TransmissionPolicyManager.setFieldToBeCached, for event and JChord
     * objects.
     * 
     * @see uk.ac.stand.dcs.rafda.rrt.policy.transmission.TransmissionPolicyManager
     * 
     */
    private static void setRTTTransmissionPolicy() {

        /*
         * Set the transmission policy for each of the JChord event types
         */
        TransmissionPolicyManager.setClassPolicy(Event.class.getName(),
                PolicyType.BY_VALUE, true);
        TransmissionPolicyManager.setClassPolicy(HashMap.class.getName(),
                PolicyType.BY_VALUE, true);

        /*
         * Set the transmission policy for KeyImpl, JChordNextHopResult and
         * SuccessorList objects
         */
        TransmissionPolicyManager.setClassPolicy(JChordNextHopResult.class
                .getName(), PolicyType.BY_VALUE, true);
        TransmissionPolicyManager.setClassPolicy(KeyImpl.class.getName(),
                PolicyType.BY_VALUE, true);

//        RafdaRunTime.registerCustomSerializer(SuccessorList.class,
//                new jchord_impl_SuccessorList());
//        TransmissionPolicyManager.setClassPolicy(SuccessorList.class.getName(),
//                PolicyType.BY_VALUE, true);
        
//        TransmissionPolicyManager.setReturnValuePolicy(JChordNodeImpl.class
//                .getName(), "getSuccessorList", PolicyType.BY_VALUE, true);

        /*
         * Set the transmission policy for the 'node_rep' fields of
         * JChordNodeImpl objects
         */
        String JChordNodeImplName = JChordNodeImpl.class.getName();
        TransmissionPolicyManager.setFieldToBeCached(JChordNodeImplName,
                "hostAddress");
        TransmissionPolicyManager.setFieldToBeCached(JChordNodeImplName, "key");
    }

    /**
     * Deploys a JChord node using default values for the RRT configuration and
     * JChord node deployment. The RRT is configured to bind to port
     * JChordRRTDeployment.DEFAULT_RRT_PORT on the local machines default
     * address, as returned by Network.defaultLocalHostAddress. The JChord node
     * is configured to create a new ring and the local key is generated using
     * the IPPortKeyFactory key factory.
     * 
     * @throws P2PNodeException if the RRT reports an error in deploying the local node's services.
     */
    public static IJChordNode defaultDeployment() throws P2PNodeException {
        return defaultDeployment(null);
    }

    /**
     * As JChordRTTDeployment.defaultDeployment() but allows the caller to
     * sepcify the address of a JChord node in an existing ring that will be
     * used by the local node to join that ring.
     * 
     * @param known_node a node in an existing ring that will be joined by the local node
     * @throws P2PNodeException if the known node is unavailable or if the RRT reports an error in deploying the local node's services.
     */
    public static IJChordNode defaultDeployment(InetSocketAddress known_node) throws P2PNodeException {

        InetSocketAddress local_address = null;
        try {
            local_address = Network.defaultLocalHostAddress(DEFAULT_JCHORD_NODE_PORT);
        } catch (UnknownHostException e) {
            throw new P2PNodeException(
                    P2PStatus.GENERAL_NODE_DEPLOYMENT_FAILURE,
                    "could not obtain default local host address");
        }
        IKey node_key = SHA1KeyFactory.generateKey(local_address);

        return customDeployment(local_address, node_key, known_node, null,
                RemoteRRTRegistry.DEFAULT_RETRY,
                RemoteRRTRegistry.DEFAULT_DELAY, true, null, null);
    }

    /**
     * Deploys a JChord node and allows the caller to specify parameter values
     * for the RRT configuration and JChord node deployment. The JChord node is
     * configured to create a new ring.
     * 
     * @param local_node the address and port and key for the local JChord node
     * @param local_node_key the key for the local JChord node
     * @param bus a bus object through which the JChord node will publish events
     * @param configRRT
     *            indicates whether or not the customDeployment call should
     *            configure the local RRT (setPort, setHost and
     *            startConnectionListener)
     * @throws P2PNodeException if the RRT reports an error in deploying the local node's services.
     */
    public static IJChordNode customDeployment(InetSocketAddress local_node,
            IKey local_node_key, EventBus bus, boolean configRRT)
            throws P2PNodeException {

        return customDeployment(local_node, local_node_key, null, bus,
                RemoteRRTRegistry.DEFAULT_RETRY,
                RemoteRRTRegistry.DEFAULT_DELAY, configRRT, null, null);
    }

    /**
     * Deploys a JChord node and allows the caller to specify parameter values
     * for the RRT configuration and JChord node deployment. The JChord node is
     * configured to join an existing ring.
     * 
     * @param local_node
     *            the address and port and key for the local JChord node
     * @param local_node_key
     *            the key for the local JChord node
     * @param known_node
     *            a node in an existing ring that will be joined by the local
     *            node
     * @param bus
     *            a bus object through which the JChord node will publish events
     * @param configRRT
     *            indicates whether or not the customDeployment call should
     *            configure the local RRT (setPort, setHost and
     *            startConnectionListener)
     * @throws P2PNodeException
     *             if the known node is unavailable or if the RRT reports an
     *             error in deploying the local node's services.
     */
    public static IJChordNode customDeployment(InetSocketAddress local_node,
            IKey local_node_key, InetSocketAddress known_node, EventBus bus,
            boolean configRRT) throws P2PNodeException {
        return customDeployment(local_node, local_node_key, known_node, bus,
                RemoteRRTRegistry.DEFAULT_RETRY,
                RemoteRRTRegistry.DEFAULT_DELAY, configRRT, null, null);
    }

    /**
     * Deploys a JChord node and allows the caller to specify parameter values
     * for the RRT configuration and JChord node deployment. The node's key is
     * generated automatically using KeyFactory.generateKey(local_node). The
     * JChord node is configured to join an existing ring.
     * 
     * @param local_node
     *            the address and port and key for the local JChord node
     * @param known_node
     *            a node in an existing ring that will be joined by the local
     *            node
     * @param bus
     *            a bus object through which the JChord node will publish events
     * @param configRRT
     *            indicates whether or not the customDeployment call should
     *            configure the local RRT (setPort, setHost and
     *            startConnectionListener)
     * @throws P2PNodeException
     *             if the known node is unavailable or if the RRT reports an
     *             error in deploying the local node's services.
     * 
     * @see uk.ac.stand.dcs.asa.util.SHA1KeyFactory#generateKey(InetSocketAddress)
     */
    public static IJChordNode customDeployment(InetSocketAddress local_node,
            InetSocketAddress known_node, EventBus bus, boolean configRRT,
            ApplicationRegistry appRegistry) throws P2PNodeException {
        IKey node_key = SHA1KeyFactory.generateKey(local_node);
        return customDeployment(local_node, node_key, known_node, bus,
                RemoteRRTRegistry.DEFAULT_RETRY,
                RemoteRRTRegistry.DEFAULT_DELAY, configRRT, null, appRegistry);
    }

    /**
     * Deploys a JChord node and allows the caller to specify parameter values
     * for the RRT configuration and JChord node deployment. The JChord node is
     * configured to join an existing ring.
     * 
     * @param local_node
     *            the address and port and key for the local JChord node
     * @param local_node_key
     *            the key for the local JChord node
     * @param known_node
     *            a node in an existing ring that will be joined by the local
     *            node
     * @param bus
     *            a bus object through which the JChord node will publish events
     * @param retry
     *            the number of retries to be used in contacting the known node
     * @param delay
     *            the delay in ms between attempts to contact the known node
     * @param configRRT
     *            indicates whether or not the customDeployment call should
     *            configure the local RRT (setPort, setHost and
     *            startConnectionListener)
     * @param ui
     *            RRT user interface object
     * @throws P2PNodeException
     *             if the known node is unavailable or if the RRT reports an
     *             error in deploying the local node's services.
     */
    public static IJChordNode customDeployment(InetSocketAddress local_node,
            IKey local_node_key, InetSocketAddress known_node, EventBus bus,
            int retry, int delay, boolean configRRT, UI ui,
            ApplicationRegistry appRegistry) throws P2PNodeException {
        if (node == null) {
            RafdaRunTime.setUserInterface(ui);

            int port = local_node.getPort();
            InetAddress address = local_node.getAddress();
            if (configRRT) {
                RafdaRunTime.setPort(port);
                RafdaRunTime.setHost(address);
                RafdaRunTime.startConnectionListener();
            }

            setRTTTransmissionPolicy();
            IJChordRemote known_node_remote = null;

            RemoteRegistry registry = RemoteRRTRegistry.getInstance();

            if (known_node != null) {
                try {
                    known_node_remote = (IJChordRemote) registry.getService(
                            known_node, IJChordRemote.class, retry, delay);
                } catch (Exception e) {
                    throw new P2PNodeException(P2PStatus.KNOWN_NODE_FAILURE);
                }

            }

            JChordSingleton.initialise(local_node, local_node_key,
                    known_node_remote, bus, appRegistry);
            node = JChordSingleton.getInstance();

            try {
                RafdaRunTime.deploy(IJChordRemote.class, JChordSingleton
                        .getInstance(), registry
                        .getServiceName(IJChordRemote.class));
            } catch (Exception e) {
                throw new P2PNodeException(
                        P2PStatus.SERVICE_DEPLOYMENT_FAILURE,
                        "could not deploy \"" + IJChordRemote.class.getName()
                                + "\" interface");
            }

            try {
                RafdaRunTime
                        .deploy(IP2PNode.class, JChordSingleton.getInstance(),
                                registry.getServiceName(IP2PNode.class));
            } catch (Exception e) {
                throw new P2PNodeException(
                        P2PStatus.SERVICE_DEPLOYMENT_FAILURE,
                        "could not deploy \"" + IP2PNode.class.getName()
                                + "\" interface");

            }
            Thread myThread = new Thread(JChordSingleton.getInstance());
            myThread.start();
        } else {
            throw new P2PNodeException(
                    P2PStatus.GENERAL_NODE_DEPLOYMENT_FAILURE,
                    "The JChord node has already been deployed");
        }
        return node;
    }

    /**
     * @return Returns the node.
     */
    public static IJChordNode getNode() {
        if (node == null) {
            Error.hardError("JChord node has not been deployed");
        }
        return node;
    }
}