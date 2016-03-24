/*
 * Created on Feb 2, 2005 at 12:52:58 PM.
 */
package uk.ac.stand.dcs.asa.util;

import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.rafda.rrt.RafdaRunTime;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry that provides binding service to potentially remote objects using RRT.
 *
 * @author graham
 */
public class RemoteRRTRegistry implements RemoteRegistry {
    
    /******************** Constants *********************/
    
	/**
	 * The default port for binding the RRT.
	 */
	public static final int DEFAULT_RRT_PORT = 52525;
	
	/**
	 * The number of times to try to contact the remote site, if necessary.
	 */
	public static final int DEFAULT_RETRY = 10;
	
	/**
	 * The delay in milliseconds between attempts to contact the remote site, if necessary.
	 */
	public static final int DEFAULT_DELAY = 2000;

    /******************** Static Fields *********************/
    
    /**
     * Singleton instance.
     */
    private static RemoteRRTRegistry instance = null;
    
    /******************** Private Fields ********************/
	
    /**
     * Maps deployed interfaces to corresponding service names.
     */
    private Map interface_to_service_name_map;
    
    /**
     * Maps remote node representations to remote references.
     */
    private Map rep_to_node_map;
    
    /********************* Constructors *********************/
	
    /**
     * Private constructor.
     */
    private RemoteRRTRegistry() {
        
        interface_to_service_name_map = new HashMap();
        rep_to_node_map = new HashMap();
        
        // Add default mappings for JChordRemote and JChordLookup interfaces.
        createMapping(IJChordRemote.class, interface_to_service_name_map);
        createMapping(IP2PNode.class, interface_to_service_name_map);
    }
    
    /******************* Public Methods *********************/

    /**
     * Returns singleton instance.
     * 
     * @return the singleton instance
     */
    public static RemoteRRTRegistry getInstance() {
        
        if (instance == null) instance = new RemoteRRTRegistry();
        return instance;
    }
    
    /**
     * Gets the service at the given node exporting the given interface. A default service name is generated from
     * the interface type. A different variant taking an explicit service name should be used if there are multiple
     * services exporting the same interface.
     * 
     * @param address the representation of the remote RRT node
     * @param interface_type the interface exported by the remote service
     * @see #getService(java.net.InetSocketAddress, java.lang.Class, java.lang.String)
     * 
     */
    public Object getService(InetSocketAddress address, Class interface_type) throws Exception {
        
        return getService(address, interface_type, DEFAULT_RETRY, DEFAULT_DELAY);
    }

    public Object getService(InetSocketAddress address, Class interface_type, String service_name) throws Exception {
        
        return getService(address, interface_type, service_name, DEFAULT_RETRY, DEFAULT_DELAY);
    }

    public Object getService(InetSocketAddress address, Class interface_type, int retry, int delay) throws Exception {
        
        String service_name = getServiceName(interface_type);
        if (service_name.equals("")) throw ExceptionFactory.makeLabelledException(new Exception("no service name for class: " + interface_type.getName()));
        
        return getService(address, interface_type, service_name, retry, delay);
    }
	
    public Object getService(InetSocketAddress address, Class interface_type, String service_name, int retry, int delay) throws Exception {
        
        if (address == null ) throw ExceptionFactory.makeLabelledException(new Exception("node address was null"));
        
        String service_handle = makeServiceHandle(address, interface_type);
        
        Object locally_cached_reference = rep_to_node_map.get(service_handle);
        
        // Check whether the local reference is an instance of the specified interface (returns false if the reference is null).
        if (interface_type.isInstance(locally_cached_reference)) return locally_cached_reference;
        else {
            
           // No locally cached reference found.
            Object remote_reference = null;
            
           while (retry >= 0) {
               
                try {
                    remote_reference = RafdaRunTime.getObjectByName(address.getHostName(), address.getPort(), service_name);

                    retry = -1;
                    
                } catch (Exception e) {
                    
                    Error.errorNoEvent("failed to contact remote node, retrying: " + retry +" left: " + FormatHostInfo.formatHostName(address) + "/" + service_name);
                    retry--;
                    
                    if (retry < 0) Error.exceptionError("failed to contact remote node - giving up!", e);
                    else Thread.sleep(delay);
                }
            }
            
            if (remote_reference == null) throw ExceptionFactory.makeLabelledException(new Exception("remote reference retrieved was null"));
            
            if (!interface_type.isInstance(remote_reference)) throw ExceptionFactory.makeLabelledException(new Exception("remote reference retrieved should have type: " + interface_type.getName() + " but has type: " + remote_reference.getClass().getName()));
            
            rep_to_node_map.put(service_handle, remote_reference);
            
            return remote_reference;
        }
    }
	
    /**
     * Adds a mapping from an interface type to an associated service deployment name.
     * 
     * @param interface_type
     * @param service_name
     */
    public void addClassToServiceNameMapping(Class interface_type, String service_name) {
        
        interface_to_service_name_map.put(interface_type, service_name);
    }
    
    /**
     * Gets the service deployment name associated with a given interface type.
     * 
     * @param interface_type an interface type
     * @return the associated service name
     */
    public String getServiceName(Class interface_type) {
        
        Object name = interface_to_service_name_map.get(interface_type);
        if (name == null || !(name instanceof String)) return ""; else return (String)name;
    }

    /******************* Private Methods ********************/

    private void createMapping(Class interface_type, Map class_to_service_name_map) {
        
        addClassToServiceNameMapping(interface_type, makeServiceName(interface_type.getName()));
    }

    private String makeServiceName(String class_name) {
        
        return class_name;
    }
    
    private String makeServiceHandle(InetSocketAddress address, Class interface_type) {
        
        return FormatHostInfo.formatHostName(address) + "::" + interface_type.getName();
    }
}
