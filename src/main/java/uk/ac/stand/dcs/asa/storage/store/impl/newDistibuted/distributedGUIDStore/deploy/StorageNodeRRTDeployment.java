/*
 * Created on 05-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.deploy;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.storage.absfilesystem.impl.general.StringData;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.impl.localfilebased.ByteData;
import uk.ac.stand.dcs.asa.storage.store.impl.localfilebased.FileData;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.distributedGUIDStore.interfaces.serviceComponents.*;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IPolicyControl;
import uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces.IReplicaManagement;
import uk.ac.stand.dcs.asa.storage.webdav.impl.InputStreamData;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.PolicyType;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.TransmissionPolicyManager;

public class StorageNodeRRTDeployment {

    public static final String Store_SERVICE = "Store_SERVICE";
    public static final String StoreInfo_SERVICE = "StoreInfo_SERVICE";
    public static final String GUIDPIDMap_SERVICE = "GUIDPIDMap_SERVICE";
    
    public static final String StoreReplicaAccess_SERVICE = "StoreReplicaAccess_SERVICE";
    public static final String StoreInfoReplicaAccess_SERVICE = "StoreInfoReplicaAccess_SERVICE";
    public static final String GUIDPIDMapReplicaAccess_SERVICE = "GUIDPIDMapReplicaAccess_SERVICE";
    
    public static final String StoreReplicaManagement_SERVICE = "StoreReplicaManagement_SERVICE";
    public static final String StoreInfoReplicaManagement_SERVICE = "StoreInfoReplicaManagement_SERVICE";
    public static final String GUIDPIDMapReplicaManagement_SERVICE = "GUIDPIDMapReplicaManagement_SERVICE";
    
    public static final String StorePolicyControl_SERVICE = "StorePolicyControl_SERVICE";
    public static final String StoreInfoPolicyControl_SERVICE = "StoreInfoPolicyControl_SERVICE";
    public static final String GUIDPIDMapPolicyControl_SERVICE = "GUIDPIDMapPolicyControl_SERVICE";
    
    
    
    private static void deployService(Class type, Object object, String name,
            String serviceObjectDescription) throws P2PApplicationException {
        try {
            RafdaRunTime.deploy(type, object, name);
        } catch (Exception e) {
            throw new P2PApplicationException(
                    P2PStatus.SERVICE_DEPLOYMENT_FAILURE, "Failed to deploy"
                            + type.getName() + "interface for service object: "
                            + serviceObjectDescription);
        }
    }
    
    public static void deployServices(IStore_Net s, IGUIDPIDMap_Net gpm, IStoreInfo_Net si,
            IStoreGet sra, IGUIDPIDMapGet gpmra, IStoreInfoGet sira,
            IReplicaManagement srm, IReplicaManagement gpmrm, IReplicaManagement sirm,
            IPolicyControl spc, IPolicyControl gpmpc, IPolicyControl sipc) throws P2PApplicationException{
        TransmissionPolicyManager.setClassPolicy(FileData.class.getName(), PolicyType.BY_VALUE, true);
        TransmissionPolicyManager.setClassPolicy(ByteData.class.getName(), PolicyType.BY_VALUE, true);
        TransmissionPolicyManager.setClassPolicy(InputStreamData.class.getName(), PolicyType.BY_VALUE, true);
        TransmissionPolicyManager.setClassPolicy(StringData.class.getName(), PolicyType.BY_VALUE, true);
        
        deployService(IStore_Net.class,s,Store_SERVICE,"Store service component");
        deployService(IGUIDPIDMap_Net.class,gpm,GUIDPIDMap_SERVICE,"GUIDPIDMap service component");
        deployService(IStoreInfo_Net.class,s,StoreInfo_SERVICE,"StoreInfo service component");
        
        deployService(IStoreGet.class,sra,StoreReplicaAccess_SERVICE,"Store replica access service object");
        deployService(IGUIDPIDMapGet.class,gpmra,GUIDPIDMapReplicaAccess_SERVICE,"GUIDPIDMap replica access service object");
        deployService(IStoreInfoGet.class,sira,StoreInfoReplicaAccess_SERVICE,"StoreInfo replica access service object");
        
        deployService(IReplicaManagement.class,srm,StoreReplicaManagement_SERVICE,"Store replica management service object");
        deployService(IReplicaManagement.class,gpmrm,GUIDPIDMapReplicaManagement_SERVICE,"GUIDPIDMap replica management service object");
        deployService(IReplicaManagement.class,sirm,StoreInfoReplicaManagement_SERVICE,"StoreInfo replica management service object");
        
        deployService(IPolicyControl.class,spc,StorePolicyControl_SERVICE,"Store policy control service object");
        deployService(IPolicyControl.class,gpmpc,GUIDPIDMapPolicyControl_SERVICE,"GUIDPIDMap policy control service object");
        deployService(IPolicyControl.class,sipc,StoreInfoPolicyControl_SERVICE,"StoreInfo policy control  service object"); 
    }

    /**
     * IData implementations need to contain all of the application-level state
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
    
