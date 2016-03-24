package uk.ac.stand.dcs.asa.storage.persistence.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IPID;

/**
 * Abstraction of PID generation policy.
 *
 * @author graham
 */
public interface IPIDGenerator {

	/**
	 * Generates a PID for the given data.
	 * 
	 * @param data the data
	 * @return a PID for the data
	 */
	IPID dataToPID(IData data);
}