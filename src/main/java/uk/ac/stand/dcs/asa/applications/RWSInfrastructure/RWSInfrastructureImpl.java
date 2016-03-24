/*
 * Created on 18-Nov-2004 at 11:16:25.
 */
package uk.ac.stand.dcs.asa.applications.RWSInfrastructure;

import java.net.URI;
import java.net.URL;

/**
 * @author al
 */
public class RWSInfrastructureImpl implements IResilientWSInfrastructure {
    
    // TODO implement RWSInfrastructureImpl

    public RWSInfrastructureImpl() {
        super();
    }

    public URL lookupService(URI id) {
//    	Key k = H(id);
//    	URL sdLocation = JChord.lookup(k);
//    	IServiceDirectory sd = (IServiceDirectory)
//    		RRT.getServiceByName(sdLocation,
//    				"ServiceDirectory");
//    	return sd.findOne(k);
        return null;
    }

    public void publishService(URI id, URL location) {
//    	Key k = H(id);
//    	URL sdLocation = JChord.lookup(k);
//    	IServiceDirectory sd = (IServiceDirectory)
//    		RRT.getServiceByName(sdLocation,
//    				"ServiceDirectory");
//    	sd.store(k, location);
    }

    public void addServiceHost(Certificate[] certs, URL service_host) {
    	
    	// To be added.
    }

    public void deploy(Bundle impl, URI service_id, DeploymentSpecification spec) {
//    	Certificate c = b.getCertificate();
//    	Key k_cert = H(c);
//    	URL hdLocation = JChord.lookup(k_cert);
//    	IHostDirectory hd = (IHostDirectory)
//    			RRT.getServiceByName(hdLocation,
//    			"HostDirectory");
//    	URL[] host_urls = hd.findAll(k_cert);
//    	//Choose hosts based on
//    	//DeploymentSpecification parameter.
//    	URL[] chosen_urls = 
//    			chooseHosts(spec, host_urls);
//    	for ( URL i : chosen_urls) {
//    		Cingal.fire(bundle, i);
//    		RWS_Infrastructure.publishService(u,i);
//    	}
    }

    public URL lookupManager(URI service_id) {
        return null;
    }

    public void registerManager(URL am, URI service_id) {

    	// To be added.
    }
}
