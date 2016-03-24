/*
 * Created on 07-Sep-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.servers;

import uk.ac.stand.dcs.asa.storage.store.impl.distributed.factories.JChordRRT_StorageNodeApplicationFactory;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl.StorageNodeApplication;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.Network;

public class StorageNodeProcess {
    private static final int max_number_of_args = 3;
    private static final String appName = StorageNodeApplication.class.getName();
    
    private static String localAddress=null;
    private static String localPort=null;
    private static String knownNodeAddress=null;
    private static String knownNodePort=null;
   
    private static void usage() {
        Error.hardErrorExplicitLocalReport("Usage: "+appName+" [-k<host>:<port>] [-s<host>:<port>] [-p<port>]");
    }
    
    private static void processCLA(String[] args) {
        int numParameters = args.length;

        String knownNodeParameter = null;
        String jServiceAddressParameter = null;
        String portParameter = null;
        
        if (numParameters > max_number_of_args) usage();

        for (int i = 0; i < numParameters; i++) {
            String p = args[i];
            
                if (p.regionMatches(0, "-k", 0, 2)
                        && knownNodeParameter == null) {
                    knownNodeParameter = p.substring(2);
                } else {
                    if (p.regionMatches(0, "-p", 0, 2) && portParameter == null) {
                        portParameter = p.substring(2);
                    } else {
                        if (p.regionMatches(0, "-s", 0, 2) && jServiceAddressParameter == null) {
                            jServiceAddressParameter = p.substring(2);
                        } else {
                            Error.errorExplicitLocalReport("Unrecognised or duplicate parameter: \"" + p + "\"");
                            usage();
                        }
                    }
                }
            
        }
        
        if (knownNodeParameter != null){
            knownNodeAddress = Network.extractHostName(knownNodeParameter);
            knownNodePort = Network.extractPortNumber(knownNodeParameter);
        }
        
        if (jServiceAddressParameter != null){
            localAddress = Network.extractHostName(jServiceAddressParameter);
            localPort = Network.extractPortNumber(jServiceAddressParameter);
        }
        
        if (portParameter != null) {
            if(localPort==null){
                localPort = portParameter;
            }else{
                //ignore -p parameter
            }
        }
        
    }
        
    public static void main(String[] args) {
        Error.enableLocalErrorReporting(); // enable by default but...
        Diagnostic.setLocalErrorReporting(true);
        Diagnostic.setLevel(Diagnostic.INIT);
        processCLA(args);
        JChordRRT_StorageNodeApplicationFactory.makeStorageNode(localAddress,localPort,knownNodeAddress,knownNodePort);
    }
}
