/*
 * Created on 09-Aug-2004
 */
package uk.ac.stand.dcs.asa.applications.eventHarness;

import uk.ac.stand.dcs.asa.eventModel.eventBus.EventBusImpl;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.eventModel.eventBus.consumers.DiagnosticEventStringWriter;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.Network;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author stuart
 */
public class EventViewer {
    
	private static InetAddress serviceInterfaceAddress;
	private static int maxNumCLA = 1;
	
	public static final int EventDeliveryServices_port = 6666;
	public static final String ErrorEventServiceName="ErrorEventService"; 
	public static final String DiagnosticEventServiceName="DiagnosticEventService";
	public static final String JChordEventServiceName="JChordEventService";
	public static final String EventServiceName="EventService";

	/**
	 * 
	 * @uml.property name="eventHandler"
	 * @uml.associationEnd 
	 * @uml.property name="eventHandler" multiplicity="(1 1)"
	 */
	public NetworkEventHandler eventHandler;

	public EventViewer() {
		this.eventHandler = new NetworkEventHandler();
	}
	
	private static void usage() {
		Error.hardError("Usage: JChord.EventViewer [-s<host>]");
	}

	//JChord.EventViewer [-s<host>]
	//One command line parameter is accepted 
	//	-s<host> :	Optional.
	//					Specifies the host address (associated with a particular interface) for 
	//					the local machine on which the EventModel services should be made available.
	//					Currently only one interface is supported. This is due to a restriction 
	//					imposed by thr Rafda system. If no '-s' parameter is specifed the 
	//					EventModel services are made available on the network interface associated 
	//					with the InetAddress returned by InetAddress.getLocalHost(). 
	//		
	
	private static void processCLA(String[] args) {
		int numParameters = args.length;

		String serviceInterfaceParameter = null;

		if (numParameters>EventViewer.maxNumCLA){
			EventViewer.usage();
		}

		for (int i = 0; i < numParameters; i++) {
			String p = args[i];
			
			if (p.regionMatches(0, "-s", 0, 2) && serviceInterfaceParameter == null) {
				serviceInterfaceParameter = p.substring(2);
			} else {
				Error.error("Unrecognised or duplicate parameter: \"" + p + "\"");
				EventViewer.usage();
			}
		}
		
		if (serviceInterfaceParameter != null) {
			try {
				serviceInterfaceAddress = InetAddress.getByName(serviceInterfaceParameter);
				//now check that this is a valid address for the local machine
				
				if (Network.isValidAddress(serviceInterfaceAddress)){
					Diagnostic.trace("\tService Interface Hostname: "
									+ serviceInterfaceAddress.getHostName()
									+ "\tService Interface IP Address: "
									+ serviceInterfaceAddress.getHostAddress(),
							Diagnostic.INIT);
				}else{
					Error.hardError(" host specified for service interface address (" + 
							serviceInterfaceParameter + 
							") was not valid on local host");
				}
			} catch (UnknownHostException e) {
				Error.hardError(" error resolving address for service interface address (" + 
						serviceInterfaceParameter + ")");
			}
		}
	}
	
	public static void main(String[] args) {

		EventBus bus = new EventBusImpl();
		Diagnostic.initialise(bus);
		Error.enableLocalErrorReporting();

		bus.register(new DiagnosticEventStringWriter());
		
		processCLA(args);

		RafdaRunTime.setPort(EventViewer.EventDeliveryServices_port);
		if(serviceInterfaceAddress!=null){
			RafdaRunTime.setHost(serviceInterfaceAddress);
		}
		//RafdaRunTime.useTextualUserInterface();
		RafdaRunTime.startConnectionListener();
		
		EventViewer ev = new EventViewer();

		try {
/*			RafdaRunTime.deploy(ErrorNetworkEvent_WS.class,
					ev.eventHandler);
			RafdaRunTime.mapNameToObject(ev.eventHandler,
					ErrorEventServiceName);
			RafdaRunTime.deploy(DiagnosticNetworkEvent_WS.class,
					ev.eventHandler);
			RafdaRunTime.mapNameToObject(ev.eventHandler,
					DiagnosticEventServiceName);
			RafdaRunTime.deploy(JChordNetworkEvent_WS.class,
					ev.eventHandler);
			RafdaRunTime.mapNameToObject(ev.eventHandler,
					JChordEventServiceName);*/
			RafdaRunTime.deploy(NetworkEvent_WS.class,
					ev.eventHandler, EventServiceName);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
