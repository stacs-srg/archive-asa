/*
 * Created on Apr 27, 2005 at 1:23:48 PM.
 */
package uk.ac.stand.dcs.asa.util.test;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.eventModel.eventBus.EventBusImpl;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.util.Diagnostic;

/**
 * Test class for Diagnostic.
 *
 * @author graham
 */
public class DiagnosticTest extends TestCase {

    public void testDiagnostic() {
        
        EventBus bus = new EventBusImpl();
        
        DiagnosticTestAdapter adapter = new DiagnosticTestAdapter("test");
        
        bus.register(adapter);
        
        Diagnostic.initialise(bus);
        
        Diagnostic.setLevel(Diagnostic.RUN);
        Diagnostic.setLocalErrorReporting(false);
        
        // Shouldn't get a message with diagnostic level lower than global level.
        Diagnostic.trace(Diagnostic.FULL);
        assertNull(adapter.event);

        // Should get a message with diagnostic level same as global level.
        Diagnostic.trace("test1", Diagnostic.RUN);
        assertEquals("uk.ac.stand.dcs.asa.util.test.DiagnosticTest::testDiagnostic : test1", adapter.event.get("msg"));
        
        // Should get a message with diagnostic level higher than global level.
        Diagnostic.trace("test2", Diagnostic.NONE);
        assertEquals("uk.ac.stand.dcs.asa.util.test.DiagnosticTest::testDiagnostic : test2", adapter.event.get("msg"));
    }
}
