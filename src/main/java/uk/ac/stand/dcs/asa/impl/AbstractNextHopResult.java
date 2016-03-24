/*
 * Created on 23-Jun-2005
  */
package uk.ac.stand.dcs.asa.impl;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PStatus;
import uk.ac.stand.dcs.asa.interfaces.INextHopResult;
import uk.ac.stand.dcs.asa.jchord.impl.JChordNextHopResult;

/**
 * @author stuart
 */
public abstract class AbstractNextHopResult implements INextHopResult{
    public int code;
    public P2PNodeException error;
    public String appObjectName;
    
    public AbstractNextHopResult(int code) {
	    this(code, null);
	}
	
	public AbstractNextHopResult(int code, String appObjectName) {
		this.code = code;
		this.appObjectName = appObjectName;
		this.error = null;
	}
    
	public AbstractNextHopResult(P2PNodeException error) {
		this.code = JChordNextHopResult.ERROR;
		this.error = error;
		this.appObjectName = null;
	}
	
    /**
     * @return Returns the code.
     */
    public int getCode() {
        return code;
    }
    
	/**
	 * @return Returns the error.
	 */
	public P2PNodeException getError() {
		return error;
	}

	/**
     * @param errorMsg Describes the error that occured.
     */
    public void setError(String errorMsg){
        this.code=ERROR;
        this.error = new P2PNodeException(P2PStatus.APPLICATION_FAILURE,errorMsg);
    }
    
	public String getAppObjectName(){
	    return appObjectName;
	}
	
    /**
     * @param appObjectName The appObjectName to set.
     */
    public void setAppObjectName(String appObjectName, boolean root) {
        if(root){
            this.code=ROOT;
        }else{
          this.code=INTERCEPT;
        }
        this.appObjectName = appObjectName;
    }
        
	/**
	 * @return Returns true if an error has been returned, otherwise false is returned.
	 */
	public boolean isError() {
		return this.code==ERROR;
	}
}
