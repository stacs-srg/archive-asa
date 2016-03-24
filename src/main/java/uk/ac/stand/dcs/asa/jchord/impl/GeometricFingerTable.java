/*
 * Created on 10-Feb-2005
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.interfaces.IFingerTable;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.SegmentArithmetic;

import java.net.InetAddress;
import java.util.Iterator;

/**
 * @author stuart
 */
public class GeometricFingerTable extends AbstractFingerTable implements IFingerTable {
	
	private int searchDistance;
	private boolean constrained;
	private IDistanceCalculator dc;
	
	
	public GeometricFingerTable(IJChordNode localNode, SegmentRangeCalculator src, IDistanceCalculator dc, int searchDistance, boolean constrained) {
		super(localNode,src);
		this.searchDistance=searchDistance;
		this.constrained=constrained;
		this.dc=dc;
	}
	
	public GeometricFingerTable(IJChordNode localNode, SegmentRangeCalculator src) {
		super(localNode,src);
		this.searchDistance=0;
		this.constrained=true;
	}
	
	//Override addFinger from AbstractFingerTable so that we
	//can implement the various flavours of figer table.
	
	protected void addFinger(IJChordRemote finger, int segmentNumber){
		
		if(searchDistance!=0){
			if(!constrained){
				finger=closestNodeUnconstrained(finger, segmentNumber);
			}else{
				finger=closestNodeConstrained(finger, segmentNumber);
			}
		}

		//Now call addFinger in AbstractFingerTable to add the 
		//appropriate finger to the finger table
		//Note that the behaviour of the super class is to 
		//replace any entries withthe same segmetn number and
		//to only add a finger if the the finger table does not
		//nold a finger with the same key.
		super.addFinger(finger,segmentNumber);
	}

	private IJChordRemote closestNodeConstrained(IJChordRemote startNode, int segmentNumber) {
	    
        IJChordRemote closest = startNode;
        IJChordRemote right = startNode;

        IKey startKey = null;
        KeyRange range=src.currentSegmentRange();
        
        try {
            localNode.getSuccessor(); 
        } catch (Exception e) {
            
            // TODO - the only real error case here is the failure of the successor
            // We should probably deal with that case...
            Error.exceptionError("JChordNode call threw exception", e);
            return startNode;
        }

        startKey = startNode.getKey();

        double distance = -1.0;
        try {
            distance = dc.distance(this.localNode.getHostAddress().getAddress(), startNode.getHostAddress().getAddress());
            
        } catch (Exception e) {
            Diagnostic.trace("error getting representation of 'startNode'", Diagnostic.RUN);
            return null;
        }

        for (int i = 0; i < searchDistance; i++) {
            try {
                right = right.getSuccessor();
            }
            catch (Exception e) {
                //stop looking to the right
                Diagnostic.trace("Call to getSuccessor() on 'right' node failed.", Diagnostic.RUN);
                return closest;
            }

            if (right != null) {
                
                IKey rightKey = null;
                InetAddress rightIP = null;
                
                rightKey = right.getKey();
                try {
                    rightIP = right.getHostAddress().getAddress();
                }
                catch (Exception e) {
                    //stop looking to the right
                    Diagnostic.trace("Call to getKey() on 'right' node failed.", Diagnostic.RUN);
                    return closest;
                }

                if (rightKey != null && this.localKey.compareTo(rightKey) != 0
                        && SegmentArithmetic.inClosedSegment(rightKey, startKey, range.getUpperBound())) {
                    
                    double rightDistance = dc.distance(this.hostAddress.getAddress(), rightIP);
                    
                    if (rightDistance < distance) {
                        distance = rightDistance;
                        closest = right;
                    }
                } else return closest;
            }
        }
        return closest;
    }

	private IJChordRemote closestNodeUnconstrained(IJChordRemote startNode, int segmentNumber) {
        IJChordRemote closest = startNode;
        IJChordRemote left = startNode;
        IJChordRemote right = startNode;

        IJChordRemote succ = null;
        IKey succKey = null;
        
        try {
            succ = localNode.getSuccessor();
        }
        catch (Exception e) {
            // TODO - the only real error case here is the failure of the successor
            // we should probably deal with that case...
            Error.exceptionError("JChordNode call threw exception", e);
            
            return startNode;
        }
        succKey = succ.getKey();

        double distance = -1.0;
        try {
            distance = dc.distance(this.hostAddress.getAddress(), startNode.getHostAddress().getAddress());
        }
        catch (Exception e) {
            Diagnostic.trace("error getting representation of 'startNode'", Diagnostic.RUN);
            return null;
        }

        for (int i = 0; i < searchDistance; i++) {
            if (left != null) {
                
                try {
                    left = left.getPredecessor();
                }
                catch (Exception e) {
                    //stop looking to the left
                    Diagnostic.trace("error getting predecessor of 'left' node", Diagnostic.RUN);
                    left = null;
                }

                if (left != null) {
                    
                    IKey leftKey = null;
                    InetAddress leftIP = null;
                    
                    leftKey = left.getKey();
                    try {
                        leftIP = left.getHostAddress().getAddress();
                    }
                    catch (Exception e) {
                        //stop looking to the left
                        Diagnostic.trace("error getting representation of 'left' node", Diagnostic.RUN);
                        left = null;
                    }

                    if (leftKey != null && succKey.compareTo(leftKey) != 0 && this.localKey.compareTo(leftKey) != 0) {
                        
                        double leftDistance = dc.distance(this.hostAddress.getAddress(), leftIP);
                        
                        if (leftDistance < distance) {
                            distance = leftDistance;
                            closest = left;
                        }
                    }
                }
            }

            if (right != null) {
                try {
                    right = right.getSuccessor();
                } catch (Exception e) {
                    //stop looking to the right
                    Diagnostic.trace("error getting successor of 'right' node", Diagnostic.RUN);
                    right = null;
                }

                if (right != null) {
                    
                    IKey rightKey = null;
                    InetAddress rightIP = null;
 
                    rightKey = right.getKey();
                    try {
                        rightIP = right.getHostAddress().getAddress();
                    }
                    catch (Exception e) {
                        //stop looking to the right
                        Diagnostic.trace("error getting represenation of 'right' node", Diagnostic.RUN);
                        right = null;
                    }

                    if (rightKey != null && succKey.compareTo(rightKey) != 0 && this.localKey.compareTo(rightKey) != 0) {
                        
                        double rightDistance = dc.distance(this.hostAddress.getAddress(), rightIP);
                        
                        if (rightDistance < distance) {
                            distance = rightDistance;
                            closest = right;
                        }
                    }
                }
            }

            if (left.getKey().compareTo(right.getKey()) == 0) {
                //we've gone all the way round the ring - stop looking
                
                return closest;
            }
        }
        return closest;
    }
	
	public Iterator reverseIterator() {
		return null;
	}
}
