/*
 * Created on Jul 4, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core;

import java.util.Map;

/**
 * An object containing key/value pairs which can be read and set.
 * 
 * @author gjh1
 */
public interface AttributeContainer {
    /**
     * Determine if this node contains the specified key, value pair.
     * 
     * @param key the key to search for
     * @param value the value that key should have
     * @return <code>boolean</code> indicating if a match was found
     */
    public boolean contains(String key, Object value);

    /**
     * Return a <code>Map</code> containing all of the node's attributes.
     * 
     * @return all the attributes for this node
     */
    public Map getAttributes();

    /**
     * @param key the attribute to search for
     * @return the value of that attribute, or null if not found
     */
    public Object getAttribute(String key);

    /**
     * @param key the attribute to change
     * @param value the new value for the attribute
     */
    public void setAttribute(String key, Object value);
}
