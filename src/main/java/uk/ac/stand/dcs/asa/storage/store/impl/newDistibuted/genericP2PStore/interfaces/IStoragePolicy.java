/*
 * Created on 03-Nov-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces;

/**
 * @author stuart
 */
public interface IStoragePolicy {
    /*
     * Stu - the choice of these two options as exemplars is puzzling me. The
     * service defines the scope of policy for the data, for instance the IStore
     * service does not allow update or append but the IStoragePolicy interface
     * defined here would indicatre to a service client that data is mutable. I
     * am not sure what the scope of these policies would be. The IDataMap
     * implementation is generic and necessarily support update but the
     * higher-level services are more restrictive.
     */
    public boolean canUpdate();
    public boolean canAppend();
    
    /*
     * Resilience Specification
     * ------------------------
     * Instructional:
     * 	How many copies to make before returning from a put.
     * Goals:
     * 	Where the copies are held - disk, memory - type of medium, geographic location of medium
     * 	How many copies to try and keep.
     */
}
