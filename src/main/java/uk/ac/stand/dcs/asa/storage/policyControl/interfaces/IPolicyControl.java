/*
 * Created on 18-Nov-2005 at 12:10:01.
 */
package uk.ac.stand.dcs.asa.storage.policyControl.interfaces;

/**
 * @author al
 *
 * NEW_ARCHITECTURE_AL
 * An interface to allow policy to be changed within a storage application
 */
public interface IPolicyControl {
    
    /**
     * @return the default storage policy implemented with this object
     */
    public abstract IStoragePolicy defaultPolicy();
 
    /**
     * sets the storage policy to be that provided by @param policy
     * If @param overwrite_current is true, only policies
     * specified in @param policy will be in place when method returns.
     * If @param overwrite_current is false, the policies specified in @param policy will be added
     * to those in place before the code, overwriting as appropriate.
     */
    public abstract void setPolicy( IStoragePolicy policy, boolean overwrite_current );
    
    /**
     * @return the storage policies that are currently in force.
     */
    public abstract IStoragePolicy getPolicy();
}
