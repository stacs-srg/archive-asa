/*
 * Created on Jun 30, 2005 at 4:48:44 PM.
 */
package uk.ac.stand.dcs.asa.storage.util;

import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IAttribute;

public class Attribute implements IAttribute {

    private String name;
    private String value;

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
