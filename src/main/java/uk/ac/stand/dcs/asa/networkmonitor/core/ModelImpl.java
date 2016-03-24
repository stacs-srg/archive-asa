/*
 * Created on Jun 29, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core;


import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.networkmonitor.core.events.EventBus;
import uk.ac.stand.dcs.asa.networkmonitor.core.events.EventProducer;

import java.util.*;

/**
 * Singleton responsible for modelling the network being monitored.
 * 
 * @author gjh1
 */
public class ModelImpl implements Model, EventProducer {
    private static ModelImpl uniqueInstance = new ModelImpl();
    private Set connections;
    private Set nodes;

    private ModelImpl() {
        nodes = new HashSet();
        connections = new HashSet();
    }

    /**
     * @return the unique instance of this singleton class
     */
    public static Model getInstance() {
        return uniqueInstance;
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
        Event addConnectionEvent = new Event("ADD_CONNECTION");
        addConnectionEvent.put(Attributes.CONNECTION, connection);
        sendEventToBus(addConnectionEvent);
    }

    public void addNode(Node node) {
        nodes.add(node);
        Event addNodeEvent = new Event("ADD_NODE");
        addNodeEvent.put(Attributes.NODE, node);
        sendEventToBus(addNodeEvent);
    }

    public boolean connectionExists(Connection connection) {
        return connections.contains(connection);
    }

    public Connection getConnectionMatching(Node source, Node target) {
        Connection[] matchingConnections = getConnectionsMatching(source, target);
        if (matchingConnections.length > 0)
            return matchingConnections[0];
        else
            return null;
    }

    public Set getConnections() {
        return connections;
    }

    public Connection[] getConnections(Node node) {
        List resultList = new ArrayList();
        Iterator i = connections.iterator();
        while (i.hasNext()) {
            Connection nextConnection = (Connection) i.next();
            if (nextConnection.getSource() == node || nextConnection.getTarget() == node)
                resultList.add(nextConnection);
        }
        return (Connection[]) resultList.toArray(new Connection[0]);
    }

    public Connection[] getConnectionsMatching(Node source, Node target) {
        List resultList = new ArrayList();
        Iterator i = connections.iterator();
        while (i.hasNext()) {
            Connection nextConnection = (Connection) i.next();
            if (nextConnection.getSource() == source && nextConnection.getTarget() == target)
                resultList.add(nextConnection);
        }
        return (Connection[]) resultList.toArray(new Connection[0]);
    }

    public Connection[] getIncomingConnections(Node node) {
        List resultList = new ArrayList();
        Iterator i = connections.iterator();
        while (i.hasNext()) {
            Connection nextConnection = (Connection) i.next();
            if (nextConnection.getTarget() == node)
                resultList.add(nextConnection);
        }
        return (Connection[]) resultList.toArray(new Connection[0]);
    }

    public Node getNodeMatching(String key, Object value) {
        Node[] matchingNodes = getNodesMatching(key, value);
        if (matchingNodes.length > 0)
            return matchingNodes[0];
        else
            return null;
    }

    public Set getNodes() {
        return nodes;
    }

    public Node[] getNodesMatching(String key, Object value) {
        ArrayList resultList = new ArrayList();
        Iterator i = nodes.iterator();
        while (i.hasNext()) {
            Node nextNode = (Node) i.next();
            if (nextNode.contains(key, value))
                resultList.add(nextNode);
        }
        return (Node[]) resultList.toArray(new Node[0]);
    }

    public Connection[] getOutgoingConnections(Node node) {
        List resultList = new ArrayList();
        Iterator i = connections.iterator();
        while (i.hasNext()) {
            Connection nextConnection = (Connection) i.next();
            if (nextConnection.getSource() == node)
                resultList.add(nextConnection);
        }
        return (Connection[]) resultList.toArray(new Connection[0]);
    }

    public boolean nodeExists(Node node) {
        return nodes.contains(node);
    }

    public void removeConnection(Connection connection) {
        connections.remove(connection);
        Event removeConnectionEvent = new Event("REMOVE_CONNECTION");
        removeConnectionEvent.put(Attributes.CONNECTION, connection);
        sendEventToBus(removeConnectionEvent);
    }

    public void removeNode(Node node) {
        nodes.remove(node);
        Event removeNodeEvent = new Event("REMOVE_NODE");
        removeNodeEvent.put(Attributes.NODE, node);
        sendEventToBus(removeNodeEvent);
    }

    public void sendEventToBus(Event event) {
        EventBus.getInstance().receiveEvent(event);
    }

    public void setNodeAttribute(Node node, String key, Object value) {
        if (nodeExists(node)) {
            node.setAttribute(key, value);
            Event nodeChangedEvent = new Event("NODE_CHANGED");
            nodeChangedEvent.put(Attributes.NODE, node);
            sendEventToBus(nodeChangedEvent);
        }
    }
}
