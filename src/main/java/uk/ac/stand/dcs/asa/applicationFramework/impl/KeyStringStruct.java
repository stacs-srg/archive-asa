/*
 * Created on 18-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;

public class KeyStringStruct{
    private String string;
    private IKey key;
    
    /**
     * @param string
     * @param key
     */
    public KeyStringStruct(String string, IKey key) {
        super();
        this.string = string;
        this.key = key;
    }
    
    /**
     * @return Returns the key.
     */
    public IKey getKey() {
        return key;
    }
    /**
     * @return Returns the string.
     */
    public String getString() {
        return string;
    }
    
    public String toString(){
        return string;
    }
}
