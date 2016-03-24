/**
 * Created on Aug 15, 2005 at 11:35:07 AM.
 */
package uk.ac.stand.dcs.asa.storage.webdav.entrypoints;

import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.storage.absfilesystem.exceptions.FileSystemCreationException;
import uk.ac.stand.dcs.asa.storage.absfilesystem.factories.StoreBasedFileSystemFactory;
import uk.ac.stand.dcs.asa.storage.absfilesystem.interfaces.IFileSystem;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.factories.JChordRRT_Distributed_IGUIDStoreFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl.StorageNodeApplication;
import uk.ac.stand.dcs.asa.storage.store.interfaces.IGUIDStore;
import uk.ac.stand.dcs.asa.util.*;
import uk.ac.stand.dcs.asa.util.Error;

import java.io.IOException;

/**
 * Launcher that brings together policies for how to create store and file system for a
 * WebDAV server.
 *
 * @author graham
 */
public class PoicyAwareWebDAVServer_Al {

    private static final String appName = StorageNodeApplication.class.getName();

    private static void usage() {
        Error.hardErrorExplicitLocalReport("Usage: "+appName+" -r<store root guid> [-w<port>] [-d<store root directory>] [-n<store name>] [-D] [-k<host>:<port>] [-s<host>:<port>] [-p<port>]");
    }
    
	/**
     * Creates a store and file system, and runs a WebDAV server over it.
     * 
     * Usage: java WebDAV_DistributedStore_Launcher -r<store root guid> [-w<port>] [-d<store root directory>] [-n<store name>] [-D] [-k<host>:<port>] [-s<host>:<port>] [-p<port>]
     * 
     * @param args optional command line arguments	
     */	
    public static void main(String[] args) {
		
        String root_GUID_string = CommandLineArgs.getArg(args, "-r");
        
        // Can't continue if no root GUID string supplied.
        if (root_GUID_string != null) {
        	
            // ********************************************************************************

        	// Read diagnostic level from the console if not already specified in command line argument.
    		Diagnostic.setLevel(Diagnostic.FULL);

    		if (CommandLineArgs.getArg(args, "-D") == null) {
        		
    			Output.getSingleton().print("Enter D<return> for diagnostics, anything else for no diagnostics: ");
    			String input = CommandLineInput.readLine();

    	        if (! input.equalsIgnoreCase("D")) Diagnostic.setLevel(Diagnostic.NONE);
    		}
    		
            // ********************************************************************************

    		// Get port number.
    	    int port = 0;
    	    String port_string = CommandLineArgs.getArg(args, "-w");	    // Get port argument.
       	    if (port_string != null){
                try {
                    port = Integer.parseInt(port_string);   // Try to extract port number.
                } catch (NumberFormatException e) {
                    Error.errorExplicitLocalReport("invalid port specification for WebDAV service ("+port_string+")");
                    usage();
                }
            }
	    		    
            // ********************************************************************************

            String localAddress=null;
            String localPort=null;
            String knownNodeAddress=null;
            String knownNodePort=null;
            
            String knownNodeParameter = CommandLineArgs.getArg(args,"-k");
            String P2PServiceAddressParameter = CommandLineArgs.getArg(args,"-s");
            String portParameter = CommandLineArgs.getArg(args,"-p");
            
            if (knownNodeParameter != null){
                knownNodeAddress = Network.extractHostName(knownNodeParameter);
                knownNodePort = Network.extractPortNumber(knownNodeParameter);
            }
            
            if (P2PServiceAddressParameter != null){
                localAddress = Network.extractHostName(P2PServiceAddressParameter);
                localPort = Network.extractPortNumber(P2PServiceAddressParameter);
            }
            
            if (portParameter != null) {
                if(localPort==null){
                    localPort = portParameter;
                }else{
                    //ignore -p parameter
                }
            }
            
 			try {
				IGUID root_GUID = GUIDFactory.recreateGUID(root_GUID_string);

				// Initialise the store.
				IGUIDStore store = new JChordRRT_Distributed_IGUIDStoreFactory(knownNodeAddress,knownNodePort,localAddress,localPort).makeStore();
	        
		        // Initialise a file system using the store.
		        IFileSystem file_system = new StoreBasedFileSystemFactory(store, root_GUID).makeFileSystem();
	
		        // Run the WebDAV server.
		        WebDAVServer server;
		        
		        if (port == 0) server = new WebDAVServer(file_system);
		        else           server = new WebDAVServer(file_system, port);
		        
		        server.run();
			}
			catch (FileSystemCreationException e) { Error.exceptionError("couldn't create file system", e); }
			catch (IOException e)                 { Error.exceptionError("socket error", e); }
        }else{
            usage();
        }
	}
}
