/*
 * Created on 10-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.factories;

import uk.ac.stand.dcs.asa.applicationFramework.impl.ApplicationRegistryImpl;
import uk.ac.stand.dcs.asa.applicationFramework.impl.JChordRRTApplicationFrameworkImpl;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.ApplicationRegistry;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFramework;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFrameworkFactory;

/**
 * @author stuart
 */
public class JChordRRT_P2PApplicationFrameworkFactory implements P2PApplicationFrameworkFactory {

    private static P2PApplicationFramework appFW;
    private static P2PApplicationFrameworkFactory instance=null;; 
    
    private JChordRRT_P2PApplicationFrameworkFactory(){
        appFW=null;
    }
    
    public static P2PApplicationFrameworkFactory getInstance(){
        if(instance==null){
            instance=new JChordRRT_P2PApplicationFrameworkFactory();
        }
        return instance;
    }
    
    /**
     * Create a JChordRRTApplicationFrameworkImpl P2PApplicationFramework. 
     * 	
     * @see uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationFrameworkFactory#makeP2PApplicationFramework()
     * @see uk.ac.stand.dcs.asa.applicationFramework.impl.JChordRRTApplicationFrameworkImpl
     */
    
//    public P2PApplicationFramework makeP2PApplicationFramework() {
//        return this.makeP2PApplicationFramework(null, null);
//    }
//
//    public P2PApplicationFramework makeP2PApplicationFramework(InetSocketAddress localAddress) {
//        return this.makeP2PApplicationFramework(localAddress, null);
//    }

    public P2PApplicationFramework makeP2PApplicationFramework() {
        if(appFW==null){
            ApplicationRegistry ar = new ApplicationRegistryImpl();
            appFW=new JChordRRTApplicationFrameworkImpl(ar);
        }
        return appFW;
    }

}
