/*
 * Created on 09-Aug-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFramework;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFrameworkFactory;
import uk.ac.stand.dcs.asa.eventModel.eventBus.EventBusImpl;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.Network;

import java.net.InetSocketAddress;


/**
 * Some implemantation code to help setup the local framework object and initialise the 
 * underlying P2P node.
 * 
 * @author stuart
 */
public abstract class AbstractP2PApplication {
    
    protected P2PApplicationFramework framework;
    protected InetSocketAddress[] knownNodeSocketAddresses;
    protected EventBus myBus;
    protected P2PApplicationFrameworkFactory fwFac;

    protected AbstractP2PApplication(P2PApplicationFrameworkFactory fwFac){
        this.fwFac=fwFac;
        myBus=new EventBusImpl();
        framework=fwFac.makeP2PApplicationFramework();
    }
    
    private void initialiseP2Psubsystem(String localNetAddress, int localPort, String knownNetAddress, int knownPort) throws P2PNodeException{
        this.framework.initialiseP2PNode(localNetAddress, localPort, knownNetAddress, knownPort, this.myBus);
        initialiseApplicationServices();
    }
    
    private static String CREATE_FAILURE_PREFIX = "Error Creating P2P Network";
    private static String CREATE_SUCCESS_PREFIX = "Created P2P Network";
    
    public boolean initialiseP2P(String localAddress_IP_hostname, String localAddress_port) {
        String prefix;
        String msg="";
        boolean result=false;
       
        int port = Network.UNDEFINED_PORT;
        if(localAddress_port!=null&&localAddress_port.length()!=0){
            try{
                port = Integer.parseInt(localAddress_port);
            } catch (NumberFormatException e){
                Error.error("Invalid local port number ("+localAddress_port+")");
                return result;
            }
        }
        try {
            this.initialiseP2Psubsystem(localAddress_IP_hostname, port, null, Network.UNDEFINED_PORT);
            prefix = CREATE_SUCCESS_PREFIX;
            result = true;
        } catch (P2PNodeException e) {
            // report initialisation error
            prefix = CREATE_FAILURE_PREFIX;
            msg = e.getLocalizedMessage() + "\n";
        }
        
        msg+="\nLocal address = ";
        if(localAddress_IP_hostname==null||localAddress_IP_hostname.length()==0){
            msg+="<unspecified>";
        }else{
            msg+=localAddress_IP_hostname;
        }
        msg+=":";
        if(localAddress_port==null||localAddress_port.length()==0){
            msg+="<unspecified>";
        }else{
            msg+=localAddress_port;
        }
        msg+="\n";
        reportInitOutput(prefix,msg);
        return result;
}
    
    private static String JOIN_FAILURE_PREFIX = "Error Joining P2P Network";
    private static String JOIN_SUCCESS_PREFIX = "Joined P2P Network";
    
    public boolean initialiseP2P(String localAddress_IP_hostname, String localAddress_port, String knownAddress_IP_hostname, String knownAddress_port) {
        String prefix="";
        String msg="";
        boolean result=false;
        int lport = Network.UNDEFINED_PORT;
        int kport = Network.UNDEFINED_PORT;
        
        if(localAddress_port!=null&&localAddress_port.length()!=0){
            try{
               lport=Integer.parseInt(localAddress_port);
            } catch (NumberFormatException e){
                Error.error("Invalid local port number ("+localAddress_port+")");
                return result;
            }
        }
        
        if(knownAddress_port!=null&&knownAddress_port.length()!=0){
            try{
                kport=Integer.parseInt(knownAddress_port);
            } catch (NumberFormatException e){
                Error.error("Invalid local port number ("+knownAddress_port+")");
                return result;
            }
        }      
            try {
                this.initialiseP2Psubsystem(localAddress_IP_hostname, lport, knownAddress_IP_hostname, kport);
                prefix = JOIN_SUCCESS_PREFIX;
                result = true;                
            } catch (P2PNodeException e) {
                //report initialisation error
                prefix = JOIN_FAILURE_PREFIX;
                msg=e.getLocalizedMessage();
            }
        
        msg+="\nLocal address = ";
        if(localAddress_IP_hostname==null||localAddress_IP_hostname.length()==0){
            msg+="<unspecified>";
        }else{
            msg+=localAddress_IP_hostname;
        }
        msg+=":";
        if(localAddress_port==null||localAddress_port.length()==0){
            msg+="<unspecified>";
        }else{
            msg+=localAddress_port;
        }
        msg+="\nKnown node address = ";
        if(knownAddress_IP_hostname==null||knownAddress_IP_hostname.length()==0){
            msg+="<unspecified>";
        }else{
            msg+=knownAddress_IP_hostname;
        }
        msg+=":";
        if(knownAddress_port==null||knownAddress_port.length()==0){
            msg+="<unspecified>";
        }else{
            msg+=knownAddress_port;
        }
        msg+="\n";
        reportInitOutput(prefix,msg);
        return result;
    }
    
//    /**
//     * Returns an InetSocketAddress
//     * @param localNetAddress_IP_hostname
//     * @param localNetAddress_port
//     * @return
//     */
//    protected InetSocketAddress makeNetAddress(String localNetAddress_IP_hostname, int localNetAddress_port) throws IllegalArgumentException{
//        //decide the port number
//        int port=localNetAddress_port==Network.UNDEFINED_PORT?framework.getDefaultAddress().getPort():localNetAddress_port;
//        
//        if(localNetAddress_IP_hostname==null){
//            return new InetSocketAddress(framework.getDefaultAddress().getAddress(),port);
//        }else{
//            return new InetSocketAddress(localNetAddress_IP_hostname,port);
//        }
//    }
    
    public IKey getLocalKey() {
        return framework.getLocalKey();
    }

    public InetSocketAddress getLocalAddress() {
        return framework.getLocalAddress();
        
    }  

    /**
     * Called by initialiseP2P after initialisation the local P2P node in the
     * framework. This method allows the framework to report any errors that
     * occured or the status of the local P2P node following configuration.
     * 
     * @param prefix
     * @param msg
     */
    protected abstract void reportInitOutput(String prefix, String msg);
    
    /**
     * Called by initialiseP2P after successful initialisation the local P2P
     * framework. Allows the application to delay deployment of network services
     * until after the initialisation of the local P2P node in the framework.
     */
    protected abstract void initialiseApplicationServices();
    
    /**
     * Called by initialiseP2P after successful initialisation the local P2P
     * framework. Allows the application to make configuration changes (to a gui
     * for instance) following successful initialisation of the local P2P node
     * in the framework.
     */
    public abstract void initialiseApp();
    
}
