/*
 * Created on Dec 9, 2004 at 10:33:57 PM.
 */
package uk.ac.stand.dcs.asa.applications.store.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.applications.store.DataStoreImpl;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.jchord.impl.JChordNodeImpl;
import uk.ac.stand.dcs.asa.storage.absfilesystem.impl.general.StringData;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.InvalidKeyException;
import uk.ac.stand.dcs.asa.util.KeyImpl;
import uk.ac.stand.dcs.asa.util.KeyNotFoundException;

import java.math.BigInteger;


/**
 * @author graham
 * 
 * Test class for DataStoreImpl.
 */
public class DataStoreImplTest extends TestCase {

    private static JChordNodeImpl jcn;
    private static IKey node_key1, node_key2, data_key1, data_key2, data_key3, data_key4;
    private static IData d1, d2, d3;
    private static DataStoreImpl ds;
    
    static {
        
        // Lowest level of diagnostics.
        Diagnostic.setLevel(Diagnostic.NONE);   
        
        // Test keys.
        node_key1 = new KeyImpl(new BigInteger("12345"));
        node_key2 = new KeyImpl(new BigInteger("373728"));
        data_key1 = new KeyImpl(new BigInteger("72263"));
        data_key2 = new KeyImpl(new BigInteger("173"));
        data_key3 = new KeyImpl(new BigInteger("432782387327872"));
        data_key4 = new KeyImpl(new BigInteger("93271"));
        
        // Test node with set predecessor.
        jcn = new JChordNodeImpl(node_key2);
        jcn.setPredecessor(new JChordNodeImpl(node_key1));

        d1 = new StringData("data1");
        d2 = new StringData("data2");
        d3 = new StringData("data3");
        
        ds = new DataStoreImpl(jcn);
    }

    public void testStore() {

        // Check initial state.

        assertEquals(ds.numberOfPrimaryCopies(), 0);
        assertEquals(ds.numberOfReplicaCopies(), 0);

        // Should succeed - key in range between successor and node.
        try {
            ds.store(data_key1, d1);
            
        } catch (Exception e) { fail(e.getMessage()); }

        // Should fail - key lower than predecessor.
        try {
            ds.store(data_key2, d2);
            
            fail();
            
        } catch (InvalidKeyException e) {
            // Correct behaviour, do nothing.
        } catch (Exception e) { fail(e.getMessage()); }
        
        // Should fail - key higher than node.
        try {
            ds.store(data_key3, d2);
            
            fail();
            
        } catch (InvalidKeyException e) {
            // Correct behaviour, do nothing.
        } catch (Exception e) { fail(e.getMessage()); }
        
        // Should fail - equal to predecessor.
        try {
            ds.store(node_key1, d2);
            
            fail();
            
        } catch (InvalidKeyException e) {
            // Correct behaviour, do nothing.
        } catch (Exception e) { fail(e.getMessage()); }
        
        // Should succeed - key equal to node.
        try {
            ds.store(node_key2, d2);
            
        } catch (Exception e) { fail(e.getMessage()); }
        
        // Should succeed - overwriting existing key.
        try {
            ds.store(data_key1, d2);
            
        } catch (Exception e) { fail(e.getMessage()); }
        
        // Check final state.
        try {
            assertEquals(ds.lookup(data_key1), d2);
            assertEquals(ds.lookup(node_key2), d2);
            assertEquals(ds.numberOfPrimaryCopies(), 2);
            assertEquals(ds.numberOfReplicaCopies(), 0);
        }
        catch (KeyNotFoundException e) { fail(e.getMessage()); }
    }

    public void testLookup() {
        
        // Check initial state.
        try {
            IData d = ds.lookup(data_key1);
            
            assertEquals(d, d2);
            assertEquals(ds.lookup(node_key2), d2);
            assertEquals(ds.numberOfPrimaryCopies(), 2);
            assertEquals(ds.numberOfReplicaCopies(), 0);
            
        } catch (Exception e) { fail(e.getMessage()); }

	    // Should succeed - looking up previously stored key.
        try {
            assertEquals(ds.lookup(data_key1), d2);
            
        } catch (Exception e) { fail(e.getMessage()); }
        
        // Should fail - looking up not previously stored key.
        try {
            ds.lookup(data_key4);
            
            fail();
            
        } catch (KeyNotFoundException e) {
            // Correct behaviour, do nothing.
        } catch (Exception e) { fail(e.getMessage()); }

        // Should succeed - updating.
        try {
            ds.store(data_key1, d3);
            assertEquals(ds.lookup(data_key1), d3);
            
        } catch (Exception e) { fail(e.getMessage()); }        
        
        // Check final state.
        try {
            assertEquals(ds.lookup(data_key1), d3);
            assertEquals(ds.lookup(node_key2), d2);
            assertEquals(ds.numberOfPrimaryCopies(), 2);
            assertEquals(ds.numberOfReplicaCopies(), 0);
            
        } catch (Exception e) { fail(e.getMessage()); }
    }

    public void testDelete() { 
        
        // Check initial state.
        try {
            assertEquals(ds.lookup(data_key1), d3);
            assertEquals(ds.lookup(node_key2), d2);
            assertEquals(ds.numberOfPrimaryCopies(), 2);
            assertEquals(ds.numberOfReplicaCopies(), 0);
            
        } catch (Exception e) { fail(e.getMessage()); }

        // Should succeed - deleting previously stored key.
        try {
            ds.delete(data_key1);
            
        } catch (Exception e) { fail(e.getMessage()); }
        
        // Should fail - looking up previously deleted key.
        try {
            ds.lookup(data_key1);
            
            fail();
            
        } catch (KeyNotFoundException e) {
            // Correct behaviour, do nothing.
        } catch (Exception e) { fail(e.getMessage()); }

        // Should fail - deleting not previously stored key.
        try {
            ds.delete(data_key4);
            
            fail();
            
        } catch (KeyNotFoundException e) {
            // Correct behaviour, do nothing.
        } catch (Exception e) { fail(e.getMessage()); }
        
        // Check final state.
        try {
            assertEquals(ds.lookup(node_key2), d2);
            assertEquals(ds.numberOfPrimaryCopies(), 1);
            assertEquals(ds.numberOfReplicaCopies(), 0);
            
        } catch (Exception e) { fail(e.getMessage()); }
    }
}