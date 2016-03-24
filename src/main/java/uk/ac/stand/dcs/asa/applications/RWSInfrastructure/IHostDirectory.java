/*
 * Created on 18-Nov-2004 at 11:21:11.
 */
package uk.ac.stand.dcs.asa.applications.RWSInfrastructure;

import uk.ac.stand.dcs.asa.interfaces.IKey;

import java.net.URL;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface IHostDirectory {
	void store(IKey key, URL serviceHost);
	void remove(IKey key, URL serviceHost);
	URL[] findAll(IKey key);
	URL findOne(IKey key);
}

