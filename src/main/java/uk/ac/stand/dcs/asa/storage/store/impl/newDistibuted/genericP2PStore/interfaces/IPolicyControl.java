package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IKey;


/**
 *  @author stuart
 */
public interface IPolicyControl {  
    public IStoragePolicy getStoragePolicy(IKey k);
    
    public void setStoragePolicy(IKey k, IStoragePolicy policy);
   
    public IStoragePolicy getStoragePolicy();
    
    public void setStoragePolicy(IStoragePolicy policy);
}
