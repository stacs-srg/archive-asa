/*
 * Created on 11-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.persistence.impl;

import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPIDGenerator;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

public class PIDGenerator implements IPIDGenerator {
    public IPID dataToPID(IData data) {    
        IPID pid = (IPID) SHA1KeyFactory.generateKey(data.getState());
        return pid;
    }
}
