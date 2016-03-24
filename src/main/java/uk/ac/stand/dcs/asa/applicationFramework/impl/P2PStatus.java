/*
 * Created on 06-Jun-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

/**
 * @author stuart
 */
public class P2PStatus {
    private String statusMsg;
    
    //instantiation failures
    public static final P2PStatus INSTANTIATION_FAILURE=new P2PStatus("Failed to instantiate P2P node.");
    public static final P2PStatus NODE_NOT_INSTANTIATED = new P2PStatus("P2P Node has not been instantiated.");
    
    //initialisation failures
    public static final P2PStatus UNDISCLOSED_NODE_INIT_FAILURE=new P2PStatus("P2P Node could not be initialised for undisclosed reasons.");
    public static final P2PStatus LOCAL_HOST_INIT_FAILURE=new P2PStatus("Could not configure P2P node with specified hostname or IP address.");
    public static final P2PStatus LOCAL_PORT_INIT_FAILURE=new P2PStatus("Could not configure P2P node with specified port.");
 
    //network join failures
    public static final P2PStatus KNOWN_NODE_FAILURE=new P2PStatus("No P2P node could be found at specified known node address.");
    public static final P2PStatus NODE_JOIN_FAILURE=new P2PStatus("P2P node could not join P2P network using specified known node address.");
    
    //status vales for instantiated nodes
    public static final P2PStatus NO_ROUTING_STATE=new P2PStatus("P2P node has been initialised but has no routing state.");
    public static final P2PStatus NODE_CREATED_NETWORK=new P2PStatus("P2P node has created a new P2P network.");
    public static final P2PStatus NODE_JOINED_NETWORK=new P2PStatus("P2P node has joined an existing P2P network.");
    public static final P2PStatus NODE_RUNNING=new P2PStatus("P2P node is running.");
    public static final P2PStatus NODE_FAILED=new P2PStatus("P2P node has failed.");

    //service deployment failures
    public static final P2PStatus GENERAL_NODE_DEPLOYMENT_FAILURE=new P2PStatus("Failed to instantiate and deploy P2P node.");
    public static final P2PStatus SERVICE_DEPLOYMENT_FAILURE=new P2PStatus("Failed to deploy P2P node's network services.");

    //lookup failures
	public static final P2PStatus LOOKUP_FAILURE = new P2PStatus("A failure occured during execution of P2P key lookup");
    
	//local node failures
	public static final P2PStatus STATE_ACCESS_FAILURE = new P2PStatus("A failure occured while acessing the local P2P node's local state.");   
	
	//application level failure
	public static final P2PStatus APPLICATION_FAILURE = new P2PStatus("A failure occured in an application level object.");

    private P2PStatus(String statusMsg){
        this.statusMsg=statusMsg;
    }
    
    public String getStatusMsg() {
        return statusMsg;
    }
}
