/*
 * Created on 24-Feb-2005 at 17:47:59.
 */
package uk.ac.stand.dcs.asa.can.impl.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.can.impl.Coord;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class CoordTest extends TestCase {
    private static String ip1 = "10.0.1.1";
    private static int port1 = 34;
    private static IKey k1 = SHA1KeyFactory.generateKey(ip1 + port1);
    
    
    public void testCanCoordInit() {
        System.out.println( "testCanCoordInit started" );
        System.out.println( "Key = " + k1.toString() );
        Coord c = new Coord( 2, 80, k1);					// 2 dimensions of 80 bits
        System.out.println( "coord = " + c.toString() );

        Coord d = new Coord( 4, 40, k1);					// 4 dimensions of 40 bits
        System.out.println( "coord = " + d.toString() );
        
        Coord e = new Coord( 6, 40, k1);					// 6 dimensions of 40 bits
        System.out.println( "coord = " + e.toString() );

        Coord f = new Coord( 20, 8, k1);					// 20 dimensions of 8 bits
        System.out.println( "coord = " + f.toString() );
        
        
        System.out.println( "testCanCoordInit finished" );
    } 
}
