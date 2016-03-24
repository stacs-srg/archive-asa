/*
 * Created on 11-Feb-2005
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.util.Error;

import java.net.InetSocketAddress;

/**
 * @author stuart
 */
public class NodeSegmentStruct {
    
	public IJChordRemote finger;
	public InetSocketAddress fingerAddress; 
	public int segmentNumber;
    public IKey fingerKey;
	
	public NodeSegmentStruct(IJChordRemote finger, int segmentNumber) {

		this.finger = finger;
		fingerKey = finger.getKey();
		
		try {
			fingerAddress = finger.getHostAddress();
		} catch (Exception e) {
			Error.exceptionError("Could not get address from finger",e);
		}
		this.segmentNumber = segmentNumber;
	}
	
	public boolean equals(Object o) {
        NodeSegmentStruct nss = (NodeSegmentStruct) o;
        return (fingerKey.compareTo(nss.fingerKey) == 0);
    }
}
