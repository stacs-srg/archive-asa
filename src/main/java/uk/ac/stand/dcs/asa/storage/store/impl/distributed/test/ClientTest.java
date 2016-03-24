/*
 * Created on 18-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.test;

import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.deploy.StoreComponentRRTDeployment;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.interfaces.ILocalGUIDStore;
import uk.ac.stand.dcs.asa.util.KeyImpl;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;
import uk.ac.stand.dcs.rafda.rrt.infrastructure.RRTPolicy;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.PolicyType;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.TransmissionPolicyManager;

public class ClientTest {
    public static void main(String[] args) {
        RRTPolicy.SHOW_SOCKET_TRAFFIC=true;
        TransmissionPolicyManager.setClassPolicy(KeyImpl.class.getName(), PolicyType.BY_VALUE, true);
        String magicPid = "d530c7935925a29a99cb2734370e728e1527240c";
        IPID p = new KeyImpl(magicPid);
        System.out.println("The magic PID is: "+ p);
        try {
            ILocalGUIDStore ngs = (ILocalGUIDStore) RafdaRunTime.getObjectByName("127.0.0.1",1112,StoreComponentRRTDeployment.LocalGUIDStore_SERVICE);
            IData data = ngs.get(p);
            String s=new String(data.getState());
            System.out.println("retrieved: "+s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
