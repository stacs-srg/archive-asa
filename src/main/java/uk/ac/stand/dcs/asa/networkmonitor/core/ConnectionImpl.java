/*
 * Created on Jun 29, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core;

import java.util.HashMap;
import java.util.Map;

/**
 * A graph connection which uses a <code>HashMap</code> to store its
 * attributes.
 * 
 * @author gjh1
 */
public class ConnectionImpl implements Connection {
    private Map attributes;
    private Node source;
    private Node target;

    public ConnectionImpl() {
        attributes = new HashMap();
    }

    public boolean contains(String key, Object value) {
        if (!attributes.containsKey(key))
            return false;
        Object valueFound = attributes.get(key);
        if (valueFound.equals(value))
            return true;
        return false;
    }

    public Map getAttributes() {
        return attributes;
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public String toString() {
        return source.toString() + " -> " + target.toString();
    }
}
