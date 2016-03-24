/*
 * Created on 11-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.impl;

import uk.ac.stand.dcs.asa.storage.persistence.impl.PIDGenerator;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPIDGenerator;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IPIDGeneratorFactory;

public class ContentHashPidGeneratorFactory implements IPIDGeneratorFactory{

    private IPIDGenerator gen;
    
    public ContentHashPidGeneratorFactory(){
        gen=new PIDGenerator();
    }
    
    public IPIDGenerator makePidGenerator() {
        return gen;
    }
    
}
