package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPIDGenerator;

import java.util.Iterator;

/**
 * Different implementations of this interface have different semantics.
 * 
 * Stored data is tagged with a policy that controls the operation that can be carried out on that data.
 * 
 *  @author stuart
 */
public interface IDataMap extends IPolicyControl, IStorageStatusQuery {

    /**
     * The policy parameter allows for the specification of controls over the
     * operations that can be carried out on stored data.
     * 
     * The primary store at a node will store the data locally and create replica copies on appropriate nodes.
     * 
     * @return snap-shot of the storage status for the data at the point at which the put call returned
     */
    public IStorageStatus put(IKey k, IData data, IStoragePolicy policy);
    
    public IStorageStatus put(IKey k, IData data);

    public IData get(IKey k);
    
    public IData update(IKey k, IData data);

    public void append(IKey k, IData data);

    public void remove(IKey k);

    public Iterator getAll();
    
    /**
     * Returns the PID generator for this store. This encapsulates the policy on
     * how to generate PIDs for given data.
     * 
     * @return the PID generator
     */
    public IPIDGenerator getPIDGenerator();
}
