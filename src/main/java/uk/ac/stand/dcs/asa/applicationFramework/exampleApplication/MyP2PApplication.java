/*
 * Created on 10-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.applicationFramework.factories.JChordRRT_P2PApplicationFrameworkFactory;
import uk.ac.stand.dcs.asa.applicationFramework.impl.*;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.ApplicationUpcallHandler;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationComponent;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.PolicyType;
import uk.ac.stand.dcs.rafda.rrt.policy.transmission.TransmissionPolicyManager;

import java.net.InetSocketAddress;

/**
 * @author stuart
 */
public class MyP2PApplication extends AbstractP2PApplication implements P2PApplicationComponent, ApplicationUpcallHandler, MyP2PAppMessageReceiver{

    private static AID myAID = new AIDImpl("B65D904A-82E3-EA98-E8A0-190EBAD65D64");
    
    private MyP2PAppGUI gui;
    
    private DHTComponent dhtc;
    
    private static final String MESSAGE_SERVICE_NAME = "MyP2PAppMessageReciever_SERVICE";

    public MyP2PApplication(){
        super(JChordRRT_P2PApplicationFrameworkFactory.getInstance());
        
        Diagnostic.initialise(myBus);
        Diagnostic.setLevel(Diagnostic.INIT);
        Diagnostic.setLocalErrorReporting(false);
        Error.initialise(myBus);
        Error.disableLocalErrorReporting();
       
        dhtc=new DHTComponent(framework);
        
        gui = new MyP2PAppGUI(this, myBus,dhtc);
        
        try {
            framework.getApplicationRegistry().registerApplicationComponent(this);
            framework.getApplicationRegistry().registerApplicationComponent(dhtc);
        } catch (ApplicationRegistryException e) {
            gui.disableInputComponents();
            Error.exceptionError("An error occurred during application component registry",e); 
        }
        gui.show();
    }

    public AID getAID() {
        return myAID;
    }

    public ApplicationUpcallHandler getApplicationUpcallHandler() {
        return this;
    }
   
    public static void main(String[] args) {
        new MyP2PApplication();
    }

    /**
     * Don't change the routing but reply appropriately where this node is the root for the given key.
     */
    public String applicationNextHop(IKey k, AID a) throws P2PApplicationException {
        try {
            if(framework.inLocalKeyRange(k)){
                return MyP2PApplication.MESSAGE_SERVICE_NAME;
            }else{
                return null;
            }
            
        } catch (P2PNodeException e) {
            throw new P2PApplicationException(P2PStatus.STATE_ACCESS_FAILURE,"failed to determine if this node is the root for the given key");
        }
        
    }
    
    /**
     * Don't change the routing.
     */
    public String applicationNextHop(IKey k, AID a, Message m) throws P2PApplicationException {
        
    	Error.hardError("unimplemented method");
    	return null;
    }

    public void receiveMessage(IKey k, AID a, Message m) {
    	
    	Error.hardError("unimplemented method");
	}
    
    public boolean send(IKey key, String text) {
    	DOLResult result=null;
    	try {
    		result=framework.dol(key,myAID);
    	} catch (P2PNodeException e) {
		    Error.error("Failed to send message. \nFramework reported:\n"+e.getLocalizedMessage());
			return false;
		} 
		if(result==null){
		    Error.errorNoSource("P2P Application Framework returned null during P2P key lookup");
		}
		MyP2PAppMessageReceiver rec = (MyP2PAppMessageReceiver)(result.getApplicationComponent());
		IP2PNode remote = result.getRemoteNode();
		InetSocketAddress remoteAddress = null;
		try {
            remoteAddress = remote.getHostAddress();
        } catch (Exception e1) {
            Error.error("Could not get address of remote node.");
            return false;
        }
		Diagnostic.traceNoSource(">>>>Sending message to "+FormatHostInfo.formatHostName(remoteAddress),Diagnostic.FINAL);
		try {
            rec.receiveMsg(new Message(getLocalAddress(),text));
        } catch (P2PApplicationException e2) {
            Error.error("Failed to send message to "+FormatHostInfo.formatHostName(remoteAddress)+".\nFramework reported:\n"+e2.getLocalizedMessage());
            return false;
        }
        Diagnostic.traceNoSource("<<<<SUCCESS ("+FormatHostInfo.formatHostName(remoteAddress)+")",Diagnostic.FINAL);
		return true;
    	
    }

    protected void initialiseApplicationServices(){
        TransmissionPolicyManager.setClassPolicy(Message.class.getName(), PolicyType.BY_VALUE, true);
        try {
            //deploy application services
            RafdaRunTime.deploy(MyP2PAppMessageReceiver.class,this,MyP2PApplication.MESSAGE_SERVICE_NAME);
        } catch (Exception e) {
            gui.disableInputComponents();
            Error.exceptionError("An error occurred while deploying messaging services",e); 
        }
        
        try {
            DHTComponentRRTDeployment.deployServices(dhtc);
        } catch (P2PApplicationException e) {
            gui.disableInputComponents();
            Error.exceptionError("An error occurred while deploying DHTComponent network services",e); 
        }
    }
    
//    private void initialiseP2Psubsystem(InetSocketAddress localNetAddress, InetSocketAddress knownNetAddress) throws P2PNodeException{
//        this.knownNodeSocketAddresses=new InetSocketAddress[] {knownNetAddress};
//        framework.initialiseP2PNode(localNetAddress,knownNodeSocketAddresses,this.myBus);
//        initialiseApplicationServices();
//    }
    
    
//    private static String CREATE_FAILURE_PREFIX = "Error Creating P2P Network";
//    private static String CREATE_SUCCESS_PREFIX = "Created P2P Network";
//    private static String INVALID_PORT = "Specified port value is not valid";
//    
//    public boolean initialiseP2P(String localAddress_IP_hostname, String localAddress_port) {
//        InetSocketAddress localAddress = makeNetAddress(localAddress_IP_hostname,localAddress_port);
//        String prefix;
//        String msg="";
//        boolean result=false;
//        
//        
//        if(localAddress==null){
//            //report initialisation error
//            prefix = CREATE_FAILURE_PREFIX;
//            msg=INVALID_PORT;
//        }else{
//            try {
//                this.initialiseP2Psubsystem(localAddress,null);
//                prefix = CREATE_SUCCESS_PREFIX;
//    	        result = true; 
//            } catch (P2PNodeException e) {
//                //report initialisation error
//	            prefix = CREATE_FAILURE_PREFIX;
//	            msg=e.getLocalizedMessage()+"\n";
//            }
//        }
//        msg+="\nLocal address = ";
//        if(localAddress_IP_hostname.length()==0){
//            msg+="<unspecified>";
//        }else{
//            msg+=localAddress_IP_hostname;
//        }
//        msg+=":";
//        if(localAddress_port.length()==0){
//            msg+="<unspecified>";
//        }else{
//            msg+=localAddress_port;
//        }
//        msg+="\n";
//        reportInitOutput(prefix,msg);
//        return result;
//    }

    
   
//    public boolean initialiseP2P(String localAddress_IP_hostname, String localAddress_port, String knownAddress_IP_hostname, String knownAddress_port) {
//        InetSocketAddress localAddress = makeNetAddress(localAddress_IP_hostname,localAddress_port);
//        InetSocketAddress knownAddress = makeNetAddress(knownAddress_IP_hostname,knownAddress_port);
//        
//        String prefix="";
//        String msg="";
//        boolean result=false;
//        
//        if(localAddress==null||knownAddress==null){
//            //report initialisation error
//            prefix = JOIN_FAILURE_PREFIX;
//            msg=INVALID_PORT;
//        }else{
//            try {
//                this.initialiseP2Psubsystem(localAddress,knownAddress);
//                prefix = JOIN_SUCCESS_PREFIX;
//                result = true;                
//            } catch (P2PNodeException e) {
//                //report initialisation error
//	            prefix = JOIN_FAILURE_PREFIX;
//	            msg=e.getLocalizedMessage();
//            }
//        }
//        msg+="\nLocal address = ";
//        if(localAddress_IP_hostname.length()==0){
//            msg+="<unspecified>";
//        }else{
//            msg+=localAddress_IP_hostname;
//        }
//        msg+=":";
//        if(localAddress_port.length()==0){
//            msg+="<unspecified>";
//        }else{
//            msg+=localAddress_port;
//        }
//        msg+="\nKnown node address = ";
//        if(knownAddress_IP_hostname.length()==0){
//            msg+="<unspecified>";
//        }else{
//            msg+=knownAddress_IP_hostname;
//        }
//        msg+=":";
//        if(knownAddress_port.length()==0){
//            msg+="<unspecified>";
//        }else{
//            msg+=knownAddress_port;
//        }
//        msg+="\n";
//        reportInitOutput(prefix,msg);
//        return result;
//    }
    
    protected void reportInitOutput(String prefix, String msg){
        gui.networkInitOutput(prefix+"\n"+msg);
    } 
    
    /**
     * @see uk.ac.stand.dcs.asa.applicationFramework.exampleApplication.MyP2PAppMessageReceiver#receiveMsg(uk.ac.stand.dcs.asa.applicationFramework.impl.Message)
     */
    public void receiveMsg(Message m){
        gui.displayMessage(m);
    }

    public void initialiseApp() {
        gui.initialiseApp();
    }
}
