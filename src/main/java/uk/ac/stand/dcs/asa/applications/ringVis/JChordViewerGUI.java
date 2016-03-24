/*
 * Created on 26-Jan-2005
 *
 */
package uk.ac.stand.dcs.asa.applications.ringVis;

import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.Network;
import uk.ac.stand.dcs.asa.util.RemoteRRTRegistry;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Create an AdminNode, deploy it, and create a GUI to show system information
 */
public class JChordViewerGUI {
    
	public static final String ADMIN_NODE_NETWORK_EVENT_SERVICE_NAME = "AdminNodeNetworkEventService";
	public static final int MAX_COMMAND_LINE_ARGS = 2;
	
	private InetSocketAddress adminNodeServiceAddress = null;
	
	private static void usage() throws Exception {
	    
		Error.error("Usage: [-s<host>:port] [-p<host>");
		throw new Exception("invalid usage");
	}

	// Two command line parameters are accepted 
	//	-s<host>:port 		Optional.
	//					Specifies the host address and port (associated with a particular interface) for 
	//					the local machine on which the AdminNode service should be made available.
	//					Currently only one interface is supported. This is due to a restriction 
	//					imposed by the Rafda system. If no '-s' parameter is specified the 
	//					AdminNode service is made available on the network interface associated 
	//					with the InetAddress returned by InetAddress.getLocalHost(). Note: if no host
	//					is specified i.e. "-s", "-s:" or "-s:12345" then then the local loopback address
	//					(127.0.0.1) is used.
	//	-p<host> 			Optional.
	//					Specifies the port (on the local machine) on which the AdminNode service
	//					should be made available. If no '-p'parameter is specified the 
	//					AdminNode service will be bound to the default port as defined by
	//					uk.ac.stand.dcs.asa.jchord.view.JChordViewerGUI. Note: a port
	//					specified for the jchord service address with the '-p' option
	//					will override any port specified with the -s option.			

	private void processCLA(String[] args) throws Exception {
	    
		int numParameters = args.length;

		String serviceAddressParameter = null;
		String portParameter = null;
		if (numParameters > MAX_COMMAND_LINE_ARGS) usage();

		for (int i = 0; i < numParameters; i++) {
		    
			String p = args[i];
			
			if (p.regionMatches(0, "-s", 0, 2) && serviceAddressParameter == null) serviceAddressParameter = p.substring(2);
			else {
				if (p.regionMatches(0, "-p", 0, 2) && portParameter == null) portParameter = p.substring(2);
				else usage();
			}
		}
		
		if (serviceAddressParameter != null) {
		    
			adminNodeServiceAddress = Network.processHostPortParameter(serviceAddressParameter, RemoteRRTRegistry.DEFAULT_RRT_PORT);
			
			if (portParameter == null) {
				// We have the final definition for the jchord service address so produce some diagnostic output
			    Network.reportHostAddress("JChord Service Address", adminNodeServiceAddress);
			}
		}
		
		if (portParameter != null) {

            int port = Integer.parseInt(portParameter);

            if (adminNodeServiceAddress != null) adminNodeServiceAddress = new InetSocketAddress(adminNodeServiceAddress.getAddress(), port);
            else adminNodeServiceAddress = Network.defaultLocalHostAddress(port);

            Network.reportHostAddress("JChord Service Interface", adminNodeServiceAddress);
        }
	}
	
	public JChordViewerGUI(String[] args) {
	    
	    InetSocketAddress localAddress=null;
	    
	    try {
	        processCLA(args);
	    } catch (Exception e) { /* Error will have already been reported. */ }
	    
	    try {
	        if (adminNodeServiceAddress == null)
	            localAddress = Network.defaultLocalHostAddress(RemoteRRTRegistry.DEFAULT_RRT_PORT);
	        else
	            localAddress = adminNodeServiceAddress;
	        
	        RafdaRunTime.setPort(localAddress.getPort());
	        RafdaRunTime.setHost(localAddress.getAddress());
	    }
	    catch (UnknownHostException e) { Error.exceptionError("error getting local node", e); }
        
		RafdaRunTime.setUserInterface(null);
		
	    RafdaRunTime.startConnectionListener();
	    
	    AdminNode admin_node = new AdminNode(new AdminGUI(localAddress));
	    
	    try {
	        RafdaRunTime.deploy(EventConsumer.class, admin_node, ADMIN_NODE_NETWORK_EVENT_SERVICE_NAME);
	    }
	    catch (Exception e) { Error.exceptionError("error deploying admin node as web service", e); }
	}
	
	public static void main(String[] args) {
	    
		Diagnostic.setLocalErrorReporting(true);
		Diagnostic.setLevel(Diagnostic.RUN);
		
		Error.enableLocalErrorReporting();
		
		// Create new instance of this class but don't need to do anything with it here.
		new JChordViewerGUI(args);
	}
}