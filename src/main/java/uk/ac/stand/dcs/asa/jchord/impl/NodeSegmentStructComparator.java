/*
 * Created on 11-Feb-2005
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;

import java.util.Comparator;


/**
 * @author stuart
 */
public class NodeSegmentStructComparator implements Comparator {
	
	private IJChordNode localNode;

	public NodeSegmentStructComparator(IJChordNode localNode){
		this.localNode=localNode;
	}
	
	public int compare(Object arg0, Object arg1) {
		if(arg0==null)return -1;
		if(arg1==null)return 1;
		
		NodeSegmentStruct kss0 = (NodeSegmentStruct)arg0;
		NodeSegmentStruct kss1 = (NodeSegmentStruct)arg1;
		IKey k0 = kss0.fingerKey;
		IKey k1 = kss1.fingerKey;
		if(kss0.equals(kss1)){
				return 0;
		}else{		
			if(localNode.getKey().ringDistanceTo(k0).compareTo(localNode.getKey().ringDistanceTo(k1))<0){
				return 1;
			}else{
				return -1;
			}
		}	
	}

}
