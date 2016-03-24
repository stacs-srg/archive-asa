/*
 * Created on 18-Nov-2004 at 11:09:02.
 */
package uk.ac.stand.dcs.asa.applications.RWSInfrastructure;

import java.net.URI;
import java.net.URL;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface IResilientWSInfrastructure {
	URL lookupService(URI service_id);
	void publishService(URI service_id, URL location);
	void addServiceHost(Certificate[] certs,URL service_host);
	void deploy(Bundle impl, URI service_id, DeploymentSpecification spec);
	URL lookupManager(URI service_id);
	void registerManager(URL am, URI service_id);
}
