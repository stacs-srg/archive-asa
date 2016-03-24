/*
 * Created on 05-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.deploy;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.storage.absfilesystem.impl.general.StringData;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl.StoreComponent;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.interfaces.ILocalGUIDStore;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.interfaces.IReplicaGUIDStore;
import uk.ac.stand.dcs.asa.storage.store.impl.localfilebased.ByteData;
import uk.ac.stand.dcs.asa.storage.store.impl.localfilebased.FileData;
import uk.ac.stand.dcs.asa.storage.webdav.impl.InputStreamData;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.PolicyType;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.TransmissionPolicyManager;

public class StoreComponentRRTDeployment {

    public static final String LocalGUIDStore_SERVICE = "LocalGUIDStore_SERVICE";
    public static final String ReplicaGUIDStore_SERVICE = "ReplicaGUIDStore_SERVICE";
//    public static final String DistributedGUIDStore_SERVICE = "DistributedGUIDStore_SERVICE";
    
    public static void deployStorageServices(StoreComponent sc) throws P2PApplicationException{
        TransmissionPolicyManager.setClassPolicy(FileData.class.getName(), PolicyType.BY_VALUE, true);
        TransmissionPolicyManager.setClassPolicy(ByteData.class.getName(), PolicyType.BY_VALUE, true);
        TransmissionPolicyManager.setClassPolicy(InputStreamData.class.getName(), PolicyType.BY_VALUE, true);
        TransmissionPolicyManager.setClassPolicy(StringData.class.getName(), PolicyType.BY_VALUE, true);
        
//        try {
//            RafdaRunTime.deploy(DirstibutedGUIDStore.class,sc,StoreComponentRRTDeployment.DistributedGUIDStore_SERVICE);
//        } catch (Exception e) {
//            throw new P2PApplicationException(P2PStatus.SERVICE_DEPLOYMENT_FAILURE,"Failed to deploy IGUIDStore interface for distributed storage implementation");
//        }
        try {
            RafdaRunTime.deploy(ILocalGUIDStore.class,sc.getLocalStoreNetWrapper(),StoreComponentRRTDeployment.LocalGUIDStore_SERVICE);
        } catch (Exception e) {
            throw new P2PApplicationException(P2PStatus.SERVICE_DEPLOYMENT_FAILURE,"Failed to deploy IGUIDStore interface for local store implementation");
        }
        try {
            RafdaRunTime.deploy(IReplicaGUIDStore.class,sc.getReplicaStoreNetWrapper(),StoreComponentRRTDeployment.ReplicaGUIDStore_SERVICE);
        } catch (Exception e) {
            throw new P2PApplicationException(P2PStatus.SERVICE_DEPLOYMENT_FAILURE,"Failed to deploy IGUIDStore interface for replica store implementation");
        }
    }

    /**
     * IData implmentations need to contain all of the application-level state
     * if they are to be meaningfully passed accross the network.
     * 
     * This method originally looked for FileData objects which do not contain
     * the application- level state and converted them to ByteData object which
     * do. However, InputStreamData objects cause RRT errore when transmitted so
     * I am converting them to ByteData too.
     * 
     * If 'data' is a FileData or InputStreamData object then a ByteData object
     * is instantiated (with data.getState()) and the ByteData object is
     * returned. Otherwise the method checks that 'data' is either an instance
     * of ByteData or StringData before returning the 'data' object. If 'data'
     * is not an instance of either ByteData, StringData then a hard error is
     * generated.
     */
    public static IData packageData(IData data){
        IData result = data;
        if(data!=null){
            if(data instanceof FileData){
                result= new ByteData(data.getState());
            }else{
                if(data instanceof InputStreamData){
                    result= new ByteData(data.getState());
                }else{
                    if(!(data instanceof ByteData)&&!(data instanceof StringData)){
                        Error.hardError("the store cannot be sure that the specified IData implementation can be passed across the network");
                    }
                }
            }
        }
        return result;
    }
    
}
