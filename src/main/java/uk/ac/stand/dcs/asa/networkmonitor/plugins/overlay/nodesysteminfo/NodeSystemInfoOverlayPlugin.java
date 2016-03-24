/*
 * Created on Aug 2, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.overlay.nodesysteminfo;

import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.networkmonitor.core.Attributes;
import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.overlay.OverlayPlugin;

/**
 * Overlay which displays technical information about a node.
 * 
 * @author gjh1
 */
public class NodeSystemInfoOverlayPlugin extends OverlayPlugin {
    static class NodeSystemInfoOverlayPluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new NodeSystemInfoOverlayPlugin();
        }
    }

    private static final String FRIENDLY_NAME = "Node Information Overlay";
    private static final String SYSTEM_NAME = "NodeSystemInfoOverlay";
    private static final String VERSION = "1.0.0.0";

    static {
        hasUserInterface = false;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, NodeSystemInfoOverlayPlugin.class, new NodeSystemInfoOverlayPluginFactory(), hasUserInterface);
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

    public boolean interested(Event event) {
        return event.getType().equals("NodeSystemInfo");
    }

    public void receiveEvent(Event event) {
        if (event.getType().equals("NodeSystemInfo")) {
            Event overlayEvent = new Event("OVERLAY");
            overlayEvent.put(Attributes.OVERLAY, SYSTEM_NAME);
            overlayEvent.putAll(event);
            sendEventToBus(overlayEvent);
        }
    }
}
