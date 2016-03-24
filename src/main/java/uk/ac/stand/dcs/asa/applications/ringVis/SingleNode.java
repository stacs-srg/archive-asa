/*
 * Created on 03-Nov-2004
 */
package uk.ac.stand.dcs.asa.applications.ringVis;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.NetworkHost;
import uk.ac.stand.dcs.asa.eventModel.P2PHost;
import uk.ac.stand.dcs.asa.eventModel.eventBus.EventBusImpl;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.deploy.JChordRRTDeployment;
import uk.ac.stand.dcs.asa.util.*;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.PolicyType;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.TransmissionPolicyManager;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Wires-up a JChord node to three event adapters that send events to an admin node for ring visualisation. 
 * 
 * @author stuart, graham
 */
public class SingleNode {
	
    static {

		// Set the transmission policy for each of the generic event types.
		TransmissionPolicyManager.setClassPolicy(Event.class.getName(), PolicyType.BY_VALUE, true);
		
		TransmissionPolicyManager.setClassPolicy(NetworkHost.class.getName(), PolicyType.BY_VALUE, true);
		TransmissionPolicyManager.setClassPolicy(P2PHost.class.getName(), PolicyType.BY_VALUE, true);
		TransmissionPolicyManager.setClassPolicy(HashMap.class.getName(), PolicyType.BY_VALUE, true);
		
		//RRTPolicy.SHOW_SOCKET_TRAFFIC = true;
		
		//The following call is duplicated in JChordRRTDeployment.
		//We send keys before the JChordRRTDeployment call is made so we need to 
		//ensure that keys are passed by value here.
		TransmissionPolicyManager.setClassPolicy(KeyImpl.class.getName(), PolicyType.BY_VALUE, true);
		TransmissionPolicyManager.setClassPolicy(BigInteger.class.getName(), PolicyType.BY_VALUE, true);
	}
	
	private static final int max_number_of_args = 4;
	private static final int RETRIES = 20;

	private InetSocketAddress local_address;

	private EventBus bus;
	private InetSocketAddress known_node_address;
	private InetSocketAddress admin_node_address;
	private InetSocketAddress jchordServiceAddress;
	private JChordNetworkEventFactory network_event_factory;
	
	private ErrorEventVisAdapter error_event_adapter;
	private DiagnosticEventVisAdapter diagnostic_event_adapter;
	private JChordEventVisAdapter jchord_event_adapter;
	
	private static void usage() {
		Error.hardErrorNoEvent("Usage: SingleNode [-a<host>:<port>] [-k<host>:<port>] [-s<host>:<port>] [-p<port>]");
	}
	
	//Four command line parameters are accepted -
	//	-a<host>:<port> 	Optional.
	//					Specifies the hostname/ip address for a host running an AdminNode
	//					service and the port that the service should be contacted on. 
	//					This is the node to which the DiagnosticObserver, ErrorObserver 
	//					and NodeImplEventObserver send observed events. If no port is
	//					specified the default AdminNode port will be used as defined by
	//					uk.ac.stand.dcs.asa.jchord.view.JChordViewerGUI. Note: if no host
	//					is specified i.e. "-s", "-s:" or "-s:12345" then the local
	//					loopback address (127.0.01) is used.
	//	-k<host>:<port> 	Optional.
	//					Specifies the hostname/ip address for a host that will be used to
	//					join the JChord ring and the port on which the JChord service should
	//					be contacted. If the '-k' parameter is omitted the node on the 
	//					local host creates a new ring. Note: if no host
	//					is specified i.e. "-k", "-k:" or "-k:12345" then then the local
	//					loopback address (127.0.01) is used.
	//	-s<host>:<port>  Optional.
	//					Specifies the host address and port (associated with a particular interface) for 
	//					the local machine on which the JChord services should be made available.
	//					Currently only one interface is supported. This is due to a restriction 
	//					imposed by the RAFDA system. If no '-s' parameter is specified the 
	//					JChord services are made available on the network interface associated 
	//					with the InetAddress returned by InetAddress.getLocalHost(). Note: if no host
	//					is specified i.e. "-s", "-s:" or "-s:12345" then then the local loopback address
	//					(127.0.0.1) is used.
	//	-p<host> 		Optional.
	//					Specifes the port (on the local machine) on which the JChord services 
	//					should be made available. If no '-p'parameter is specified the 
	//					JChord services will be bound to the default port as defined by
	//					uk.ac.stand.dcs.asa.jchord.daemon.Daemon.CNode_WS_port. Note: a port
	//					specified for the jchord service address with the '-p' option
	//					will override any port specified with the -s option.			

	public void processCLA(String[] args) {
		int numParameters = args.length;

		String adminParameter = null;
		String knownNodeParameter = null;
		String jServiceAddressParameter = null;
		String portParameter = null;
		
		if (numParameters > max_number_of_args) usage();

		for (int i = 0; i < numParameters; i++) {
			String p = args[i];
			if (p.regionMatches(0, "-a", 0, 2) && adminParameter == null) {
				adminParameter = p.substring(2);
			} else {
				if (p.regionMatches(0, "-k", 0, 2)
						&& knownNodeParameter == null) {
					knownNodeParameter = p.substring(2);
				} else {
					if (p.regionMatches(0, "-p", 0, 2) && portParameter == null) {
						portParameter = p.substring(2);
					} else {
						if (p.regionMatches(0, "-s", 0, 2) && jServiceAddressParameter == null) {
							jServiceAddressParameter = p.substring(2);
						} else {
							Error.error("Unrecognised or duplicate parameter: \"" + p + "\"");
							usage();
						}
					}
				}
			}
		}
		
		if (adminParameter != null)
            try {
                admin_node_address = Network.processHostPortParameter(adminParameter, RemoteRRTRegistry.DEFAULT_RRT_PORT);
            } catch (UnknownHostException e) {
                Error.exceptionError("Invalid admin node address", e);
            }
		
		if (knownNodeParameter != null)
            try {
                known_node_address = Network.processHostPortParameter(knownNodeParameter, RemoteRRTRegistry.DEFAULT_RRT_PORT);
            } catch (UnknownHostException e) {
                Error.exceptionError("Invalid known node address", e);
            }
		
		if (jServiceAddressParameter != null)
            try {
                jchordServiceAddress = Network.processHostPortParameter(jServiceAddressParameter, RemoteRRTRegistry.DEFAULT_RRT_PORT);
            } catch (UnknownHostException e) {
                Error.exceptionError("Invalid service address", e);
            }
		
		if (portParameter != null) {
		    
            int port = Integer.parseInt(portParameter);
            
			if (jchordServiceAddress != null)
				jchordServiceAddress = new InetSocketAddress(jchordServiceAddress.getAddress(), port);
            else
                try {
                    jchordServiceAddress = Network.defaultLocalHostAddress(port);
                } catch (UnknownHostException e) {
                    Error.exceptionError("Invalid local host address", e);
                }
		}
	}
	
	public void setBus(EventBus bus){
		if (this.bus == null) this.bus = bus;
	}
	
	public void setAdapters(ErrorEventVisAdapter e, DiagnosticEventVisAdapter d, JChordEventVisAdapter j) {
	    
		if (e != null) {
		    
			bus.register(e);
			error_event_adapter = e;
		}
		
		if (d != null) {
		    
			bus.register(d);
			diagnostic_event_adapter = d;
		}
		
		if (j != null) {
		    
			bus.register(j);
			jchord_event_adapter = j;
		}
	}
	
	public void startJChordNode() {
	    
		if (local_address != null) {
		    
		    IKey local_key = SHA1KeyFactory.generateKey(local_address);
		    
			P2PHost local_node_rep = new P2PHost(local_address, local_key);
		    
			network_event_factory = JChordNetworkEventFactory.initialiseJNEFactory(local_node_rep);
			
			if (admin_node_address != null) {

				error_event_adapter.setAdminNode(     admin_node_address, JChordViewerGUI.ADMIN_NODE_NETWORK_EVENT_SERVICE_NAME, network_event_factory, RETRIES, RemoteRRTRegistry.DEFAULT_DELAY);
				diagnostic_event_adapter.setAdminNode(admin_node_address, JChordViewerGUI.ADMIN_NODE_NETWORK_EVENT_SERVICE_NAME, network_event_factory, RETRIES, RemoteRRTRegistry.DEFAULT_DELAY);
				jchord_event_adapter.setAdminNode(    admin_node_address, JChordViewerGUI.ADMIN_NODE_NETWORK_EVENT_SERVICE_NAME, network_event_factory, RETRIES, RemoteRRTRegistry.DEFAULT_DELAY);

				Network.reportHostAddress("Admin Node Address", admin_node_address);
			}
			
			Network.reportHostAddress("Local Address", local_address);
			
			if (known_node_address != null) Network.reportHostAddress("Known Node Address", known_node_address);
			
			try {
			    if (known_node_address != null) JChordRRTDeployment.customDeployment(local_address, local_key, known_node_address, bus, RETRIES, RemoteRRTRegistry.DEFAULT_DELAY, true, null, null);
				else JChordRRTDeployment.customDeployment(local_address, local_key, null, bus, RETRIES, RemoteRRTRegistry.DEFAULT_DELAY, true, null, null);
			    
			}
			catch (Exception e) { Error.exceptionError("JChord node deployment failed", e); }
		}
	}
	
	public SingleNode(String args[]) {
	    
		processCLA(args);

		if (jchordServiceAddress == null)
            try {
                local_address = Network.defaultLocalHostAddress(RemoteRRTRegistry.DEFAULT_RRT_PORT);
            }
		   catch (UnknownHostException e) { Error.exceptionError("could not get local host", e); }
		   
        else local_address = jchordServiceAddress;
	}

	public static void main(String[] args) {
	    
		EventBus bus = new EventBusImpl();
		Error.enableLocalErrorReporting(); //enable by default but... 
	    
		Error.initialise(bus);
		Diagnostic.initialise(bus);

		SingleNode node = new SingleNode(args);
		Diagnostic.setLevel(Diagnostic.INIT);
		
		node.setBus(bus);
		
		if (node.admin_node_address != null) node.setAdapters(new ErrorEventVisAdapter("ErrorEvent adapter"), new DiagnosticEventVisAdapter("DiagnosticEvent adapter"), new JChordEventVisAdapter("JChordRepEvent adapter"));
		
		node.startJChordNode();
	}
}
