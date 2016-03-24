/*
 * Created on 18-Nov-2004 at 11:13:36.
 */
package uk.ac.stand.dcs.asa.applications.RWSInfrastructure;

import uk.ac.stand.dcs.asa.interfaces.IKey;

import java.net.URL;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface IServiceDirectory {
	void store(IKey key, URL location);
	void remove(IKey key, URL location);
	URL[] findAll(IKey key);
	URL findOne(IKey key);


}
