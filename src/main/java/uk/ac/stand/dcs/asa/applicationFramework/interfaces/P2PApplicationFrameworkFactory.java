/*
 * Created on 10-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.interfaces;


/**
 * @author stuart
 */
public interface P2PApplicationFrameworkFactory {

    /**
     * Instantiate a P2PApplicationFrameworkFactory 
     */
    public P2PApplicationFramework makeP2PApplicationFramework();
//	public P2PApplicationFramework makeP2PApplicationFramework(InetSocketAddress localAddress);
//	public P2PApplicationFramework makeP2PApplicationFramework(InetSocketAddress localAddress, InetSocketAddress[] knownNodes);
//	public P2PApplicationFramework makeP2PApplicationFramework(InetSocketAddress localAddress, InetSocketAddress[] knownNodes,
//	        EventBus bus);
}
