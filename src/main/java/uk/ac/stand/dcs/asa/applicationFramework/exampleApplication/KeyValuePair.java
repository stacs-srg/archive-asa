/*
 * Created on 07-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.interfaces.IKey;



public class KeyValuePair {
    private IKey key;
    private Object value;

    public KeyValuePair(IKey key, Object value){
        this.key=key;
        this.value=value;
    }

    public IKey getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
    

}
