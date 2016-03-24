/*
 * Created on 08-Jun-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;


/**
 * @author stuart
 */
public class P2PNodeException extends Exception {

    private P2PStatus status;
    
    public P2PNodeException(P2PStatus status){
        super();
        this.status=status;     
    }
    
    public P2PNodeException(P2PStatus status, String msg){
        super(msg);
        this.status=status;     
    }
    
    public String getLocalizedMessage(){
        String msg = status.getStatusMsg();
        if(super.getLocalizedMessage()!=null){
            msg+="\n\t"+super.getLocalizedMessage();
        }
        return msg;
    }
    
    /**
     * @return Returns the status.
     */
    public P2PStatus getStatus() {
        return status;
    }
}
