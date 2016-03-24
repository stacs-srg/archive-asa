/*
 * Created on 19-Aug-2005
 */
package uk.ac.stand.dcs.asa.util;

import uk.ac.stand.dcs.asa.interfaces.IGUID;

public class GUIDFactory {
    
    public static IGUID generateRandomGUID(){
        return (KeyImpl)SHA1KeyFactory.generateRandomKey();
    }
    
    public static IGUID recreateGUID(String s){
        return (KeyImpl)SHA1KeyFactory.recreateKey(s);
    }
    
}
