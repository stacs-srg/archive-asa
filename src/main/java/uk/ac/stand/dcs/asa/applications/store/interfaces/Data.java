/*
 * Created on 25-Oct-2004
 */
package uk.ac.stand.dcs.asa.applications.store.interfaces;

/**
 * Type of data that can be stored in a DataStore.
 * 
 * @author stuart, graham
 */
public interface Data {
    
	/**
	 * Returns the raw data.
	 * 
	 * @return the raw data as bytes
	 */
	public byte[] getData();
	
	/**
	 * Returns the size of the raw data.
	 * 
	 * @return the size of the raw data in bytes
	 */
	public int size();
}
