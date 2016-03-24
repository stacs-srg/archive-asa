/*
 * Created on Jul 4, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output.jgraph;


import uk.ac.stand.dcs.asa.networkmonitor.core.AttributeContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Data structure which allows attributes to be stored for edges in the graph.
 * 
 * @author gjh1
 */
public class JChordEdge implements AttributeContainer {
    private Map attributes;

    public JChordEdge() {
        attributes = new HashMap();
        attributes.put("LABEL", "");
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

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String toString() {
        return (String) attributes.get("LABEL");
    }
}
