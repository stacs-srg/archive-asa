/*
 * Created on 11-Jan-2005
 */
package uk.ac.stand.dcs.asa.simulation.util;

import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.util.Error;

import java.net.InetAddress;

/**
 * @author stuart
 */
public class SimulatedPhysicalDistanceModel implements IDistanceCalculator {

	private int node_count;
	
	public SimulatedPhysicalDistanceModel(int node_count){
		this.node_count=node_count;
	}
	
	private static int byteArray2Int(byte[] b) {
	    
	    int pos = 0;
        return (b[pos] << 24) + ((b[pos + 1] & 0xff) << 16) + ((b[pos + 2] & 0xff) << 8) + (b[pos + 3] & 0xff);
    }
	
	private static int ip2Location(InetAddress ip) {

         return byteArray2Int(ip.getAddress());
    }
	
	/**
	 * Calculates the distance between two nodes.
	 * 
	 * @param ip1 the IP address for the first node which we are calculating distance between
	 * @param ip2 the IP address for the second node which we are calculating distance between
	 * @return the normalised (wrt number of nodes) distance between the two nodes in real space
	 * or a negative value if the distance between the two specified node cannot be calculated.
	 */
	public double distance(InetAddress ip1, InetAddress ip2) {
	    
        int loc1 = ip2Location(ip1);
        int loc2 = ip2Location(ip2);
        
        if (loc1 >= 0 && loc2 >= 0 && loc1 < node_count && loc2 < node_count) {
            if (loc1 > loc2) { // swap into natural order
                int temp = loc1;
                loc1 = loc2;
                loc2 = temp;
            }
            // calculate the minimum distance around the (circular) array in either direction
            return (double)(Math.min(loc2 - loc1, loc1 + node_count - loc2)) / node_count;
        }
        return -1.0;
    }

	public static void main(String args[]) {
	    
	    try {
            InetAddress ip1 = InetAddress.getByName("0.0.1.1");
            InetAddress ip2 = InetAddress.getByName("0.0.0.17");

            System.out.println(ip1 + " is location " + ip2Location(ip1));

            SimulatedPhysicalDistanceModel s = new SimulatedPhysicalDistanceModel(600);

            System.out.println("distance between " + ip1.getHostAddress() + " and " + ip2.getHostAddress() + " is " + s.distance(ip1, ip2));
            System.out.println("distance between " + ip2.getHostAddress() + " and " + ip1.getHostAddress() + " is " + s.distance(ip2, ip1));
        }
	    catch (Exception e) { Error.exceptionError("error getting addresses", e); }
	}
}
