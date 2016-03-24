/*
 * Created on Jun 29, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A graph node which uses a <code>HashMap</code> to store its attributes.
 * 
 * @author gjh1
 */
public class NodeImpl implements Node {
    private Map attributes;

    public NodeImpl() {
        attributes = new HashMap();
    }

    public boolean contains(String key, Object value) {
       if (!attributes.containsKey(key)) return false;
       Object valueFound = attributes.get(key);
       if (valueFound.equals(value)) return true;
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
        String result = "<html>";
        Iterator i = attributes.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry next = (Entry) i.next();
            if (next.getValue() instanceof String) {
                result += (String) next.getKey() + " = " + (String) next.getValue();
                if (i.hasNext())
                    result += "<br>";
            }
        }
        return result + "</html>";
    }
}
