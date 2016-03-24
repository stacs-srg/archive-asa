/*
 * Created on 21-Jul-2004
 */
package uk.ac.stand.dcs.asa.applications.ringVis;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.P2PHost;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;

import java.net.InetSocketAddress;

/**
 * Accepts JChord events and passes them on to the appropriate part of the ring visualisation tool.
 * 
 * @author stuart, graham
 */
public class AdminNode implements EventConsumer {

    private AdminGUI admin_GUI;

    public AdminNode(AdminGUI admin_GUI) {
        this.admin_GUI = admin_GUI;
    }
    
    public boolean interested(Event event) {
    	
    	return true;
    }
    
    /**
     * Receives an event sent to the ring visualisation tool.
     * 
     * @param event the event
     */
    public synchronized void receiveEvent(Event event) {
    	
		if (event.getType().equals("ErrorEvent")) {
		    
			String errorString = null;

			P2PHost event_source = (P2PHost)event.get("source");
			errorString = "[" + FormatHostInfo.formatHostName(event_source) + "] ";
			
			errorString += event.get("msg");
			
			admin_GUI.addError(errorString);

		}
		else if (event.getType().equals("DiagnosticEvent")) {
		    
			String diagnosticString = null;

			P2PHost event_source = (P2PHost)event.get("source");
						
			diagnosticString = "[" + FormatHostInfo.formatHostName(event_source) + "] ";	

			diagnosticString += event.get("msg");
			
			admin_GUI.addDiagnostics(diagnosticString);
		}
		else {

			P2PHost event_source = (P2PHost)event.get("source");
			String nodeChangeString;

		    if (event_source == null) nodeChangeString = "[null host] ";
		    else {
		        InetSocketAddress addr = event_source.getInet_sock_addr();
		        if (addr == null) nodeChangeString = "[null address] ";
		        else nodeChangeString = "[" + FormatHostInfo.formatHostName(addr) + "] ";
		    }

			nodeChangeString += event.getType();
			admin_GUI.addChange(nodeChangeString);
			
			// At this point we have extracted a JChordNode object for the event source.
			JChordNodeCell nodeCell = admin_GUI.getCellDirectory().lookup(FormatHostInfo.formatHostName(event_source));
			
			if (nodeCell == null) {
			    
				Diagnostic.trace("Creating node cell for node " + FormatHostInfo.formatHostName(event_source) + "[" + event_source.getKey() +"]", Diagnostic.INIT);
				
				nodeCell = new JChordNodeCell(event_source, admin_GUI);
				admin_GUI.layoutCells();
			}
			
			if (event.getType().equals("SuccessorRepEvent")) {

			    InetSocketAddress succAddr = (InetSocketAddress)event.get("succ");
			    
			    if (succAddr != null) Diagnostic.trace("received SucessorEvent from " + event.get("source") + "successor is "+ succAddr.getAddress().getHostName(), Diagnostic.INIT);
			    else Error.error("received SucessorRepEvent but successor was null");
			    
			    nodeCell.changeToSuccessor(succAddr, (IKey)event.get("succ_key"));
			}
			else if (event.getType().equals("PredecessorRepEvent")) {
			    
			    InetSocketAddress predAddr = (InetSocketAddress)event.get("pred");
			    
			    if (predAddr != null) nodeCell.changeToPredecessor(predAddr, (IKey)event.get("pred_key"));
			    else nodeCell.changeToPredecessor(null,null);
			}				
			else if (event.getType().equals("NodeFailureNotificationRepEvent")) {
			    
			    InetSocketAddress failed = (InetSocketAddress)event.get("failed");
			    
			    Diagnostic.trace("received notification from " + event.get("source") + " that node " + failed + " may have failed", Diagnostic.RUN);
			    
				JChordNodeCell failed_cell = admin_GUI.getCellDirectory().lookup(FormatHostInfo.formatHostName(failed));
			    
			    if (failed_cell != null) failed_cell.mayHaveFailed();
			}
			else Error.error("unexpected event type: " + event.getType());
		}
	}
}