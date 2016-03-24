/*
 * Created on 19-Sep-2004
 */
package uk.ac.stand.dcs.asa.plaxton.impl;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.plaxton.interfaces.PlaxtonRemote;

/**
 * @author stuart
 */
public class NextHopResult {
    
	public static final int FINAL = 0;
	public static final int NEXT_HOP = 1;
	public static final int ERROR = 2;
	
	public int code;
	public PlaxtonRemote result;
	public Event error;
	
	public NextHopResult() {/* Needed for RRT */}
	
	/**
	 * @param code
	 * @param result
	 */
	public NextHopResult(int code, PlaxtonRemote result) {
		this.code = code;
		this.result = result;
		this.error = null;
	}
	
	public NextHopResult(Event error) {
		this.code = NextHopResult.ERROR;
		this.result = null;
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
	public PlaxtonRemote getResult() {
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
		return error == null;
	}
}
