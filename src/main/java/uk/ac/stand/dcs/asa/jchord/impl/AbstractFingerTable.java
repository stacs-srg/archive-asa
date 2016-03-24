/*
 * Created on Dec 9, 2004 at 10:04:47 AM.
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.interfaces.IFingerTable;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;
import uk.ac.stand.dcs.asa.util.SegmentArithmetic;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public abstract class AbstractFingerTable implements IFingerTable {

    protected IJChordNode localNode;
    protected InetSocketAddress hostAddress;
    protected SegmentRangeCalculator src;
    protected TreeSet store;
    protected IKey localKey;
    
    public AbstractFingerTable(IJChordNode localNode, SegmentRangeCalculator src){
    	this.localNode=localNode;
		this.localKey = localNode.getKey();
    	try {
			this.hostAddress=this.localNode.getHostAddress();
		} catch (Exception e) {
			Error.exceptionError("Could not get local host address from local node",e);
		}
    	this.src=src;
    	this.store=new TreeSet(new NodeSegmentStructComparator(localNode));
    }

    /**@return The size of the AbstractFingerTable */
    public int size() {
        return store.size();
    }

    public Iterator iterator() {
        return store.iterator();
    }

    protected void addFinger(IJChordRemote finger, int segmentNumber){
    	//Create a NodeSegmentStruct to put in the finger table
   
    	NodeSegmentStruct nss=new NodeSegmentStruct(finger,segmentNumber);
    	
    	//Default policy - remove any existing entry whose segment number is equal to 
    	//src.getCurrentSegment()
    	NodeSegmentStruct entry;
    	do{
    		entry=getEntryBySegmentNumber(segmentNumber);
    		if(entry!=null){
    			store.remove(entry);
    		}
    	}while(entry!=null);
    		
    	//The TreeSet will deal with duplicate entries so just add the object.
    	//Note - NodesegmentStructComparator deems two entries to be the same
    	//iff the node's keys are the same and the segment numbers are the same.
    	store.add(nss);
    }
    
	public void printStore() {
		for(Iterator i=store.iterator();i.hasNext();){
			NodeSegmentStruct nss = (NodeSegmentStruct)i.next();
			System.out.println(nss.fingerKey+" "+nss.fingerAddress.getAddress().getHostAddress());
		}
	}

	private void fixFinger(IKey target_key, int segmentNumber){
		if (target_key != null) {
			IJChordRemote finger=null;
			try {
				finger = localNode.findSuccessor(target_key);
			} catch (Exception e) {
				Error.exceptionError("localNode.getSuccessor() threw exception", e);
			}
			try {
				if ( finger != null && finger != localNode && finger != localNode.getSuccessor()){
					addFinger(finger,segmentNumber);
				}
			} catch (Exception e) {
				Error.exceptionError("localNode.getSuccessor() threw exception", e);
			}
		}
	}
    
    /**
     * Fixes the next finger in the finger table.
     */
    public void fixNextFinger() {    	
    	IKey target_key = src.nextSegmentLowerBound();    
    	fixFinger(target_key, src.getCurrentSegment());
	}
    
	public void fixAllFingers() {

		int numberOfSegments = src.numberOfSegments();
		
		for(int i=0;i<numberOfSegments;i++){
			IKey target_key = src.nextSegmentLowerBound();
			fixFinger(target_key, src.getCurrentSegment());
		} 
	}

	private NodeSegmentStruct getEntryBySegmentNumber(int segmentNumber) {
		for (Iterator i = store.iterator(); i.hasNext();){
			NodeSegmentStruct nss = (NodeSegmentStruct)i.next();
			if (nss.segmentNumber==segmentNumber){		
				return nss;
			}
		}
		return null;
	}
	
	private NodeSegmentStruct getEntryByKey(IKey k){
		for (Iterator i = store.iterator(); i.hasNext();){
			NodeSegmentStruct nss=(NodeSegmentStruct)i.next();
			if (nss.fingerKey.compareTo(k) == 0){
				return nss;
			}
		}
		return null;
	}
	
	/**
	 * This method searches the finger table for the closest preceding node for
	 * the target key k. If no preceding node is found, this node's successor
	 * should be returned. In this case we are just passing the lookup one node
	 * further round the ring. Note that in 'find_successor' we check that k
	 * does lie in the range (localNode,localNode.successor] before calling this
	 * method.
	 */
	public synchronized IJChordRemote closestPrecedingNode(IKey k) {
		
		//Note the the TreeSet store contains NodeSegmentStruct objects
		//which have a natural order from largest to smallest key, as defined
		//by NodeSegmentStructComparator. Thus the TreeSet's iterator runs from
		//largest to smallest key - which is nice.
		Iterator it = store.iterator();
		
		while (it.hasNext()) {
			NodeSegmentStruct nss = (NodeSegmentStruct) it.next();
			IJChordRemote next=nss.finger;
			
			if (next != null && SegmentArithmetic.inOpenSegment(nss.fingerKey, localKey, k)) {    
				if (next == localNode) Error.hardErrorNoEvent("finger table contains local node");
				return next;
			}
		}
		
        try {
            IJChordRemote successor = localNode.getSuccessor();
			if (successor != localNode){
				return successor;
			}
			else{
				return null; //closest preceding node is not allowed to return this node - this could cause infinite loops. 
			}

        } catch (Exception e) {
		    Error.exceptionError("error getting successor of local node", e);
		    return null;
		}
	}
	
	/**
     * The finger table implementation cannot contain this node or this node's 
     * successor. 
     * 
     * @param suggestedNode suggests a JChordNode that might be added to the finger table
     */
    public void exists(IJChordRemote suggestedNode) {
        
        if (suggestedNode == null) {
            
            Error.error("suggested node was null");
            return;
        }
        
        if (suggestedNode == localNode) {
            
            Error.error("suggested node was local node");
            return;
        }
        
        try {
            if (suggestedNode == localNode.getSuccessor()) {
                
                Error.error("suggested node was local node's successor");
                return;
            }
        } catch (Exception e) {
            Error.exceptionError("error getting successor of local node", e);
            return;
        }
        
        // I have a node... in which segment does this node lie?
        

        int segmentNumber=src.calculateSegmentNumber(suggestedNode.getKey());
        //Ignore suggestions for nodes which lie outwith the set of defined segments
        if(segmentNumber!=-1){
        	addFinger(suggestedNode,segmentNumber);
        }
    }

    public void dead(IJChordRemote suggestedNode) {        
         NodeSegmentStruct entry=null;
         do {
         	entry=getEntryByKey(suggestedNode.getKey());
         	if(entry!=null){
         		if (localNode.getFailureSuspector().hasFailed(suggestedNode)) {
         			store.remove(entry);
         			try {
         				Diagnostic.trace(">>>>>>>>>>>>>>>>>>>>>>>>>>>removing node " + FormatHostInfo.formatHostName(suggestedNode) + " from finger table", Diagnostic.NONE);
         			}catch(Exception e){
         				Error.exceptionError("error while gathering information for creation of node representation", e); 
         			}
         		}	
         	}
         } while(entry!=null);
    }
    
    /* (non-Javadoc)
     * @see uk.ac.stand.dcs.asa.jchord.interfaces.FingerTable#owned(uk.ac.stand.dcs.asa.interfaces.Key, uk.ac.stand.dcs.asa.jchord.interfaces.JChordRemote)
     */
    public void owned(IKey k, IJChordRemote ownerNode) {
        Error.hardError("unimplemented method called"); 
    }

    /**
     * @return a string representing the contents of the finger table
     */
    public String toString() {
        String fingerString = "";
        Iterator i=store.iterator();
        while(i.hasNext()){
            try {
                IJChordRemote j = ((NodeSegmentStruct)i.next()).finger;
                if (j != null) {
                    fingerString += "\nNode At Position :\t" + i + "\nIP Address :\t"
                            + j.getHostAddress().getAddress().getHostAddress() + "\nKey :\t"
                            + j.getKey() + "\n";
                } else {
                    fingerString += "\nNode At Position :\t" + i + "\nnull";
                }

            } catch (Exception e) { Error.exceptionError("error getting representation of finger", e); }
        }

        return fingerString;
    }

    /**
     * @return a string representing the contents of the finger table omitting duplicate keys
     */
    public String toString_compact() {
        String fingerString = "";
        String substr = "\nNode At Position :\t" + 0;

        Iterator i=store.iterator();
        IJChordRemote finger;
        if(!i.hasNext()){
        	return "Finger table is empty";
        }
        finger=((NodeSegmentStruct)i.next()).finger;
        try {
            if (store.size() > 0) {
                InetAddress currentIpAddress = null;
                InetAddress prevIpAddress = null;
                IKey currentKey = null;
                IKey prevKey = null;
                
                prevIpAddress = finger.getHostAddress().getAddress();
                prevKey = finger.getKey();
               
                int entryCounter=1;
                
                while(i.hasNext()){
                	IJChordRemote jcr = ((NodeSegmentStruct)i.next()).finger;
                    if (jcr == null) {
                        currentKey = null;
                        currentIpAddress = null;
                    } else {
                        currentKey = jcr.getKey();
                        currentIpAddress = jcr.getHostAddress().getAddress();
                    }
                    if (prevKey != currentKey
                            && (currentKey == null && prevKey != null || currentKey != null && prevKey == null ||
                                    currentKey.compareTo(prevKey) != 0)) {
                        substr += "\nIP Address :\t" + prevIpAddress.getHostAddress() + "\nKey :\t" + prevKey + "\n";
                        fingerString += substr;
                        substr = "\nNode At Position :\t" + entryCounter++;
                        prevIpAddress = currentIpAddress;
                        prevKey = currentKey;
                    } else {
                        substr += "," + entryCounter++;
                    }
                }
                substr += "\nIP Address :\t" + prevIpAddress.getHostAddress() + "\nKey :\t" + prevKey + "\n";
                fingerString += substr;
            }
        } catch (Exception e) { Error.exceptionError("error getting representation of finger", e); }
        
        return fingerString;
    }
    
    public int getNumberOfEntries(){
    	return store.size();
    }
}