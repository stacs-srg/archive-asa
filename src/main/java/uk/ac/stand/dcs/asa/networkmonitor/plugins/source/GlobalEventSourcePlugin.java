/*
 * Created on Jun 15, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.source;


import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.P2PHost;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.networkmonitor.core.*;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.JChordAttributes;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;
import uk.ac.stand.dcs.asa.util.Network;
import uk.ac.stand.dcs.asa.util.RemoteRRTRegistry;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Event source plugin which receives events from Rafda.
 * 
 * @author gjh1
 */
public class GlobalEventSourcePlugin extends EventSourcePlugin implements EventConsumer {
    static class GlobalEventSourcePluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new GlobalEventSourcePlugin();
        }
    }

    private static final String ADMIN_NODE_NETWORK_EVENT_SERVICE_NAME = "AdminNodeNetworkEventService";
    private static final String FRIENDLY_NAME = "Global Event Source";
    private static final String SYSTEM_NAME = "GlobalES";
    private static final String VERSION = "1.0.0.0";

    static {
        hasUserInterface = false;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, GlobalEventSourcePlugin.class, new GlobalEventSourcePluginFactory(), hasUserInterface);
    }
    
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
        return true;
    }

    public void load() {
        super.load();
        JLabel caption = new JLabel("This plugin receives events from the Rafda admin node service.");
        pluginPanel = new JPanel();
        pluginPanel.add(caption);
        InetSocketAddress localAddress;

        try {
            localAddress = Network.defaultLocalHostAddress(RemoteRRTRegistry.DEFAULT_RRT_PORT);
            RafdaRunTime.setPort(localAddress.getPort());
            RafdaRunTime.setHost(localAddress.getAddress());
            RafdaRunTime.deploy(EventConsumer.class, this, ADMIN_NODE_NETWORK_EVENT_SERVICE_NAME);
        }
        catch (UnknownHostException e) {
            Error.exceptionError("Error getting local node.", e);
        }
        catch (Exception e) {
            Error.exceptionError("Error deploying admin node as web service.", e);
        }
    }

    public void receiveEvent(Event externalEvent) {
        String externalEventType = externalEvent.getType();

        if (externalEventType.equals("PredecessorRepEvent") || externalEventType.equals("SuccessorRepEvent")) {
            P2PHost reportingHost = (P2PHost) externalEvent.get("source");
            String reportingHostName = FormatHostInfo.formatHostName(reportingHost);
            String reportingHostKey = reportingHost.key.toString();
            String predHostname = null;
            String succHostname = null;
            Node nodeAffected = ModelImpl.getInstance().getNodeMatching(Attributes.HOST, reportingHostName);
            
            if (nodeAffected == null) {
                // Create a new node
                nodeAffected = new NodeImpl();
                nodeAffected.setAttribute(Attributes.HOST, reportingHostName);
                nodeAffected.setAttribute(JChordAttributes.KEY, reportingHostKey);
                // Add it to the model
                ModelImpl.getInstance().addNode(nodeAffected);
            }
            
            Connection conn = new ConnectionImpl();
            conn.setSource(nodeAffected);
            
            if (externalEventType.equals("PredecessorRepEvent")) {
                InetSocketAddress predAddr = (InetSocketAddress) externalEvent.get("pred");
                if (predAddr != null) {
                    predHostname = FormatHostInfo.formatHostName(predAddr);
                    // Remove the current predecessor connection
                    Connection[] currentOutgoing = ModelImpl.getInstance().getOutgoingConnections(nodeAffected);
                    for (int i = 0; i < currentOutgoing.length; i++) {
                        if (currentOutgoing[i].getAttribute(Attributes.TYPE).equals(JChordAttributes.PREDECESSOR))
                            ModelImpl.getInstance().removeConnection(currentOutgoing[i]);
                    }
                    // Create a new predecessor connection
                    ModelImpl.getInstance().setNodeAttribute(nodeAffected, JChordAttributes.PREDECESSOR, predHostname);
                    conn.setTarget(ModelImpl.getInstance().getNodeMatching(Attributes.HOST, predHostname));
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
                    Connection[] currentOutgoing = ModelImpl.getInstance().getOutgoingConnections(nodeAffected);
                    for (int i = 0; i < currentOutgoing.length; i++) {
                        if (currentOutgoing[i].getAttribute(Attributes.TYPE).equals(JChordAttributes.SUCCESSOR))
                            ModelImpl.getInstance().removeConnection(currentOutgoing[i]);
                    }
                    // Create a new successor connection
                    ModelImpl.getInstance().setNodeAttribute(nodeAffected, JChordAttributes.SUCCESSOR, succHostname);
                    conn.setTarget(ModelImpl.getInstance().getNodeMatching(Attributes.HOST, succHostname));
                    conn.setAttribute(Attributes.TYPE, JChordAttributes.SUCCESSOR);
                    conn.setAttribute(Attributes.LABEL, "Succ");
                    ModelImpl.getInstance().addConnection(conn);
                }
            }
            
            // Don't generate an event on the bus - the model will do this for us
            return;
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
            // Don't generate an event on the bus - the model will do this for us
            return;
        }

        sendEventToBus(externalEvent);
    }

    public void unload() {
        // Terminate the RRT to prevent an error when this plugin is loaded for a second time
        RafdaRunTime.undeploy(this);
        RafdaRunTime.stop();
        super.unload();
    }
}
