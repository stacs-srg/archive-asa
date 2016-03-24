/*
 * Created on 21-Jun-2005
  */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.impl.AbstractNextHopResult;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;

/**
 * 
 * @author stuart
 */
public class JChordNextHopResult extends AbstractNextHopResult{
  
	public IJChordRemote result;
	
	public JChordNextHopResult(int code, IJChordRemote result) {
	    this(code,result,null);
	}
	
	public JChordNextHopResult(int code, IJChordRemote result, String appObjectName) {
		super(code,appObjectName);
		this.result = result;
	}
	
	public JChordNextHopResult(P2PNodeException error) {
		super(error);
		this.result = null;
	}
	
	/**
	 * @return Returns the result.
	 */
	public IJChordRemote getResult() {
		return result;
	}
	

}
