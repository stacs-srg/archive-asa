/*
 * Created on 19-Aug-2005
 */
package uk.ac.stand.dcs.asa.util;

import uk.ac.stand.dcs.asa.interfaces.IPID;

public class PIDFactory {
    
    public static IPID generateRandomPID() {
    	
        return (KeyImpl)SHA1KeyFactory.generateRandomKey();
    }
}
