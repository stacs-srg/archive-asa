/*
 * Created on Dec 6, 2004 at 9:38:51 PM.
 */
package uk.ac.stand.dcs.asa.util.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.util.Assert;

/**
 * @author graham
 *
 * Test class for util.Assert
 */
public class AssertTest extends TestCase {

    public void testAssertion() {
        
        Assert.assertion(true, "asserting true");
    }
}
