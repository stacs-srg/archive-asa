/*
 * Created on 12-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID;

/**
 * @author stuart
 */
public class AIDImpl implements AID {

    private String GUID_string;
    
    public AIDImpl(String id){
        this.GUID_string=id;
    }
    
    public String toString(){
        return GUID_string;
    }
    
    public boolean equals(Object o){
        if(o instanceof AIDImpl)
            return GUID_string.compareTo(o.toString())==0;
        else
            return false;
    }
    
    public int hashCode(){
        return GUID_string.hashCode();
    }
}
