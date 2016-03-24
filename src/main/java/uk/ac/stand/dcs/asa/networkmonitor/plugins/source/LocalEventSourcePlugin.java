/*
 * Created on Jun 15, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.source;


import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.networkmonitor.core.*;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.JChordAttributes;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;

import java.net.InetSocketAddress;

/**
 * Event source plugin which receives events from Rafda.
 * 
 * @author gjh1
 */
public class LocalEventSourcePlugin extends EventSourcePlugin implements EventConsumer {
    static class LocalEventSourcePluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new LocalEventSourcePlugin();
        }
    }

    private static final String FRIENDLY_NAME = "Local Event Source";
    private static final String SYSTEM_NAME = "LocalES";
    private static final String VERSION = "1.0.0.0";

    static {
        hasUserInterface = false;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, LocalEventSourcePlugin.class, new LocalEventSourcePluginFactory(), hasUserInterface);
    }

    private Node localNode;
    
    public String getFriendlyName() {
        return FRIENDLY_NAME;
    }

    public String getSystemName() {
        return SYSTEM_NAME;
    }
    
    public String getVersion() {
        return VERSION;
    }
    
    public boolean interested(Event event) {
        String eventType = event.getType();
        return (eventType.equals("PredecessorRepEvent") ||
                eventType.equals("SuccessorRepEvent") ||
                eventType.equals("NodeFailureNotificationRepEvent"));
    }

    public void load() {
        super.load();
        localNode = new NodeImpl();
        localNode.setAttribute(Attributes.HOST, "127.0.0.1:52525");
        ModelImpl.getInstance().addNode(localNode);
    }

    public void receiveEvent(Event externalEvent) {
        String externalEventType = externalEvent.getType();

        if (externalEventType.equals("PredecessorRepEvent") || externalEventType.equals("SuccessorRepEvent")) {
            String predHostname = null;
            String succHostname = null;
            Connection conn = new ConnectionImpl();
            conn.setSource(localNode);

            if (externalEventType.equals("PredecessorRepEvent")) {
                InetSocketAddress predAddr = (InetSocketAddress) externalEvent.get("pred");
                if (predAddr != null) {
                    predHostname = FormatHostInfo.formatHostName(predAddr);
                    // Remove the current predecessor connection
                    Connection[] currentOutgoing = ModelImpl.getInstance().getOutgoingConnections(localNode);
                    for (int i = 0; i < currentOutgoing.length; i++) {
                        if (currentOutgoing[i].getAttribute(Attributes.TYPE).equals(JChordAttributes.PREDECESSOR))
                            ModelImpl.getInstance().removeConnection(currentOutgoing[i]);
                    }
                    // Create a new predecessor connection
                    ModelImpl.getInstance().setNodeAttribute(localNode, JChordAttributes.PREDECESSOR, predHostname);
                    Node targetNode = ModelImpl.getInstance().getNodeMatching(Attributes.HOST, predHostname);
                    if (targetNode == null) {
                        targetNode = new NodeImpl();
                        targetNode.setAttribute(Attributes.HOST, predHostname);
                        targetNode.setAttribute(JChordAttributes.KEY, externalEvent.get("pred_key"));
                        ModelImpl.getInstance().addNode(targetNode);
                    }
                    conn.setTarget(targetNode);
                    conn.setAttribute(Attributes.TYPE, JChordAttributes.PREDECESSOR);
                    conn.setAttribute(Attributes.LABEL, "Pred");
                    ModelImpl.getInstance().addConnection(conn);
                }
            }
            
            else if (externalEventType.equals("SuccessorRepEvent")) {
                InetSocketAddress succAddr = (InetSocketAddress) externalEvent.get("succ");
                if (succAddr != null) {
                    succHostname = FormatHostInfo.formatHostName(succAddr);
                    // Remove the current successor connection
                    Connection[] currentOutgoing = ModelImpl.getInstance().getOutgoingConnections(localNode);
                    for (int i = 0; i < currentOutgoing.length; i++) {
                        if (currentOutgoing[i].getAttribute(Attributes.TYPE).equals(JChordAttributes.SUCCESSOR))
                            ModelImpl.getInstance().removeConnection(currentOutgoing[i]);
                    }
                    // Create a new successor connection
                    ModelImpl.getInstance().setNodeAttribute(localNode, JChordAttributes.SUCCESSOR, succHostname);
                    Node targetNode = ModelImpl.getInstance().getNodeMatching(Attributes.HOST, succHostname);
                    if (targetNode == null) {
                        targetNode = new NodeImpl();
                        targetNode.setAttribute(Attributes.HOST, succHostname);
                        targetNode.setAttribute(JChordAttributes.KEY, externalEvent.get("succ_key"));
                        ModelImpl.getInstance().addNode(targetNode);
                    }
                    conn.setTarget(targetNode);
                    conn.setAttribute(Attributes.TYPE, JChordAttributes.SUCCESSOR);
                    conn.setAttribute(Attributes.LABEL, "Succ");
                    ModelImpl.getInstance().addConnection(conn);
                }
            }
            
        }
        
        else if (externalEventType.equals("NodeFailureNotificationRepEvent")) {
            String hostname = FormatHostInfo.formatHostName((InetSocketAddress) externalEvent.get("failed"));
            Model model = ModelImpl.getInstance();
            Node failedNode = model.getNodeMatching(Attributes.HOST, hostname);
            if (failedNode != null) {
                // Remove the failed node and its connections
                Connection[] failedNodeConnections = model.getConnections(failedNode);
                for (int i = 0; i < failedNodeConnections.length; i++)
                    model.removeConnection(failedNodeConnections[i]);
                ModelImpl.getInstance().removeNode(failedNode);
            }
        }
    }
}
