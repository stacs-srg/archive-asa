/*
 * Created on 10-Feb-2005
 */
package uk.ac.stand.dcs.asa.jchord.simulation;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.jchord.impl.AbstractFingerTable;
import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;
import uk.ac.stand.dcs.asa.jchord.impl.SegmentRangeCalculator;
import uk.ac.stand.dcs.asa.jchord.interfaces.IFingerTable;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordNode;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.SegmentArithmetic;

import java.util.Iterator;

/**
 * @author stuart
 */
public class GlobalViewGeometricFingerTable extends AbstractFingerTable implements IFingerTable {
	
	private IDistanceCalculator dc;
	private P2PSimulationProxy psp;
	
	public GlobalViewGeometricFingerTable(IJChordNode localNode, SegmentRangeCalculator src, IDistanceCalculator dc, P2PSimulationProxy psp) {
		super(localNode,src);
		this.dc=dc;
		this.psp=psp;
	}
		
	//Override addFinger from AbstractFingerTable so that we
	//can implement the various flavours of finger tables.
	
	protected void addFinger(IJChordRemote finger, int segmentNumber){
		
		// TODO original - check:
		// IJChordRemote cfinger=closestNode();
		// if(finger==null){cfinger=finger;}
		// super.addFinger(finger,segmentNumber);
		
		 IJChordRemote cfinger = closestNode();
		 if (cfinger == null) cfinger = finger;
		 super.addFinger(cfinger,segmentNumber);
	}
	
	private IJChordRemote closestNode() {
        KeyRange range=src.currentSegmentRange();
		IJChordRemote closest = null;
        double distance = 0.0;
        P2PSim theSim = psp.getSim();
        IP2PNode nodes[] = theSim.getNodes();
        int node_count = theSim.getNodeCount();

        //[k+2^1, k+2^(i+1))
        for (int i = 0; i < node_count; i++) {
            IKey k = null;
            try {
                k = nodes[i].getKey();
                if (this.localKey.compareTo(k) != 0 && localNode.getSuccessor().getKey().compareTo(k) != 0) {
                    if (SegmentArithmetic.inClosedSegment(k, range.getLowerBound(), range.getUpperBound())) {
                        if (closest == null) {
                            closest = (IJChordRemote) nodes[i];
                            distance = dc.distance(this.hostAddress.getAddress(), nodes[i].getHostAddress().getAddress());
                        } else {
                            double newDistance = dc.distance(this.hostAddress.getAddress(), nodes[i].getHostAddress().getAddress());
                            if (newDistance < distance) {
                                closest = (IJChordRemote) nodes[i];
                                distance = newDistance;
                            } else {
                                if (newDistance == distance) {
                                    //remember the one which is farthest away
                                    // in key-space
                                    if (!SegmentArithmetic.inClosedSegment(nodes[i].getKey(), this.localKey, closest.getKey())) {
                                        closest = (IJChordRemote) nodes[i];
                                        distance = newDistance;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Error.exceptionError("JChordNode call threw exception", e);
                e.printStackTrace();
            }
        }
        return closest;
    }
	
	public Iterator reverseIterator() {
		return null;
	}
}
