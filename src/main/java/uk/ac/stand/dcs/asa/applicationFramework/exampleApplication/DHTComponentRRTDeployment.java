/*
 * Created on 07-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.PolicyType;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.TransmissionPolicyManager;

/**
 * This assumes a configured RRT.
 * 
 * @author stuart
 */

public class DHTComponentRRTDeployment {
    
    public static final String localDHT_SERVICE = "DHT_SERVICE";
    public static final String networkAwareDHT_SERVICE = "NetworkAwareDHT_SERVICE";

    static{
        TransmissionPolicyManager.setClassPolicy(KeyValuePair.class.getName(),PolicyType.BY_VALUE,false);
    }
    
    public static void deployServices(DHTComponent dhtc)throws P2PApplicationException{
        DHT localDHT = dhtc.getLocalDHT();
        DHTMaintenance networkAwareDHT = dhtc.getNetworkAwareDHT();
        
        try {
            RafdaRunTime.deploy(DHT.class,localDHT,DHTComponentRRTDeployment.localDHT_SERVICE);
        } catch (Exception e) {
            throw new P2PApplicationException(P2PStatus.SERVICE_DEPLOYMENT_FAILURE,"failed to deploy DHT interface");
        }
        try {
            RafdaRunTime.deploy(DHTMaintenance.class,networkAwareDHT,DHTComponentRRTDeployment.networkAwareDHT_SERVICE);
        } catch (Exception e) {
            throw new P2PApplicationException(P2PStatus.SERVICE_DEPLOYMENT_FAILURE,"failed to deploy DHTMaintenance interface");
        }
    }
}
