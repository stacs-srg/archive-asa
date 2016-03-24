/*
 * Created on 29 March 2005 10 past 12
 */
package uk.ac.stand.dcs.asa.can.impl;

import uk.ac.stand.dcs.asa.can.interfaces.CanRemote;
import uk.ac.stand.dcs.asa.eventModel.Event;

/**
 * @author stuart
 */
public class NextHopResult {
    
	public static final int FINAL = 0;
	public static final int NEXT_HOP = 1;
	public static final int ERROR = 2;
	
	public int code;
	public CanRemote result;
	public Event error;
	
	public NextHopResult() {
		// Needed for RRT transmission.
	}
	
	/**
	 * @param code
	 * @param result
	 */
	public NextHopResult(int code, CanRemote result) {
		this.code = code;
		this.result = result;
		error = null;
	}
	
	public NextHopResult(Event error) {
		code = NextHopResult.ERROR;
		result = null;
		this.error = error;
	}
	
	/**
	 * @return Returns the code.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return Returns the result.
	 */
	public CanRemote getResult() {
		return result;
	}
	
	/**
	 * @return Returns the error.
	 */
	public Event getError() {
		return error;
	}
	
	/**
	 * @return Returns true if an error has been returned, otherwise false is returned.
	 */
	public boolean isError() {
		return error != null;
	}
}
