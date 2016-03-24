/*
 * Created on Jun 27, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core;

/**
 * A connection between two nodes in a directed graph.
 * 
 * @author gjh1
 */
public interface Connection extends AttributeContainer {
    /**
     * @return the target node
     */
    public Node getTarget();

    /**
     * @return the source node
     */
    public Node getSource();

    /**
     * @param target the new target of this connection
     */
    public void setTarget(Node target);

    /**
     * @param source the new source of this connection
     */
    public void setSource(Node source);
}
