/*
 * Created on Jun 15, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output;


import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;

import javax.swing.*;

/**
 * Simple output plugin used for testing.
 * 
 * @author gjh1
 */
public class DummyOutputPlugin extends OutputPlugin {
    static class DummyOutputPluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new DummyOutputPlugin();
        }
    }

    private static final String FRIENDLY_NAME = "Dummy Output";
    private static final String SYSTEM_NAME = "DummyOutput";
    private static final String VERSION = "1.0.0.0";
    
    static {
        hasUserInterface = true;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, DummyOutputPlugin.class, new DummyOutputPluginFactory(), hasUserInterface);
    }

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
        JLabel caption = new JLabel("This plugin does absolutely nothing.");
        pluginPanel = new JPanel();
        pluginPanel.add(caption);
    }

    public void receiveEvent(Event event) {
        // Handle new event from the event bus
    }
}
