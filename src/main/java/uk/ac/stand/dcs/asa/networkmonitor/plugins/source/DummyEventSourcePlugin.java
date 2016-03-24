/*
 * Created on Jun 15, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.source;


import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple event source plugin used for testing.
 * 
 * @author gjh1
 */
public class DummyEventSourcePlugin extends EventSourcePlugin {
    static class DummyEventSourcePluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new DummyEventSourcePlugin();
        }
    }

    private static final String FRIENDLY_NAME = "Dummy Event Source";
    private static final String SYSTEM_NAME = "DummyES";
    private static final String VERSION = "1.0.0.0";

    static {
        hasUserInterface = false;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, DummyEventSourcePlugin.class, new DummyEventSourcePluginFactory(), hasUserInterface);
    }

    private int count = 0;
    private Timer eventGenerator;
    
    public String getFriendlyName() {
        return FRIENDLY_NAME;
    }
    
    public String getSystemName() {
        return SYSTEM_NAME;
    }

    public String getVersion() {
        return VERSION;
    }
    
    public void load() {
        super.load();
        JLabel caption = new JLabel("This plugin generates a fake event every 2 seconds.");
        pluginPanel = new JPanel();
        pluginPanel.add(caption);
        // Generate an event every 2 seconds
        eventGenerator = new Timer();
        eventGenerator.schedule(new TimerTask() {
            public void run() {
                Event event;
                event = new Event("DiagnosticEvent");
                event.put("msg", "Dummy DiagnosticEvent " + count);
                sendEventToBus(event);
                event = new Event("ErrorEvent");
                event.put("msg", "Dummy ErrorEvent " + count);
                sendEventToBus(event);
                count++;
            }}, 0, 2000);
    }
    
    public void unload() {
        eventGenerator.cancel();
        super.unload();
    }
}
