/*
 * Created on 19-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.interfaces.IP2PNode;

/**
 * @author stuart
 */
public class DOLResult {
    private Object applicationComponent;
	private IP2PNode remoteNode;
    private boolean isRoot;
    
    public boolean isRoot() {
        return isRoot;
    }

    /**
     * @param applicationComponent
     * @param remoteNode
     */
    public DOLResult(Object applicationComponent, IP2PNode remoteNode, boolean isRoot) {
        this.applicationComponent = applicationComponent;
        this.remoteNode = remoteNode;
        this.isRoot=isRoot;
    }
    
    /**
     * @return Returns the applicationComponent.
     */
    public Object getApplicationComponent() {
        return applicationComponent;
    }
    /**
     * @return Returns the remoteNode.
     */
    public IP2PNode getRemoteNode() {
        return remoteNode;
    }
}
