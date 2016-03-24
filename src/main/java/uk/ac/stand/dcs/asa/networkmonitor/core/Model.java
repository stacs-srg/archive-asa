/*
 * Created on Jun 29, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core;

import java.util.Set;

/**
 * A model of a directed graph.
 * 
 * @author gjh1
 */
public interface Model {
    /**
     * @param connection the <code>Connection</code> to add
     */
    public void addConnection(Connection connection);

    /**
     * @param node the <code>Node</code> to add
     */
    public void addNode(Node node);

    /**
     * @param connection the <code>Connection</code> to search for
     * @return <code>boolean</code> indicating if the connection exists in the model
     */
    public boolean connectionExists(Connection connection);

    /**
     * Search for a connection with the given source and destination nodes.
     * Only the first matching connection will be returned, if one exists.
     * 
     * @param source the desired connection source
     * @param target the desired connection target
     * @return the first matching node, or null if no match was found
     */
    public Connection getConnectionMatching(Node source, Node target);

    /**
     * @return the set of all the connections in the model
     */
    public Set getConnections();

    /**
     * Search for all connections coming from or going to a particular node.
     * 
     * @param node the node for which connections should be found
     * @return an array of the connections to and from that node
     */
    public Connection[] getConnections(Node node);

    /**
     * Search for all connections involving the given nodes.
     * 
     * @param source the source node of the connections
     * @param target the target node of the connections
     * @return an array of matching connections
     */
    public Connection[] getConnectionsMatching(Node source, Node target);

    /**
     * Search for all incoming connections to a given node.
     * 
     * @param node the node for which connections should be found
     * @return an array of the connections to that node
     */
    public Connection[] getIncomingConnections(Node node);

    /**
     * Search for a node with the given key and value pair.
     * Only returns the first match, if one exists.
     * 
     * @param key the key to search for
     * @param value the value that key should have
     * @return the first matching node, or null if no match was found
     */
    public Node getNodeMatching(String key, Object value);

    /**
     * @return the set of all the nodes in the model
     */
    public Set getNodes();

    /**
     * Search for all nodes with the given key and value pair;
     * 
     * @param key the key to search for
     * @param value the value that key should have
     * @return an array of matching nodes
     */
    public Node[] getNodesMatching(String key, Object value);

    /**
     * Search for all outgoing connections from a given node.
     * 
     * @param node the node for which connections should be found
     * @return an array of connections from that node
     */
    public Connection[] getOutgoingConnections(Node node);

    /**
     * @param node the <code>Node</code> to search for
     * @return <code>boolean</code> indicating if the node exists in the model
     */
    public boolean nodeExists(Node node);

    /**
     * @param connection the <code>Connection</code> to remove
     */
    public void removeConnection(Connection connection);

    /**
     * @param node the <code>Node</code> to remove
     */
    public void removeNode(Node node);

    /**
     * @param node the <code>Node</code> to modify
     * @param key the key to search for
     * @param value the value that key should have
     */
    public void setNodeAttribute(Node node, String key, Object value);
}
