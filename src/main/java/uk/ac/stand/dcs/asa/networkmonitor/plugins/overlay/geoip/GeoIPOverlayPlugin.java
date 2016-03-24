/*
 * Created on Jul 4, 2005
 * 
 * This product includes GeoIP data created by MaxMind,
 * available from http://maxmind.com/
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.overlay.geoip;


import com.maxmind.geoip.LookupService;
import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.networkmonitor.core.Attributes;
import uk.ac.stand.dcs.asa.networkmonitor.core.Node;
import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.overlay.OverlayPlugin;
import uk.ac.stand.dcs.asa.util.Error;

import java.io.IOException;

/**
 * Overlay which uses the MaxMind GeoIP database to map IP addresses to countries.
 * 
 * @author gjh1
 */
public class GeoIPOverlayPlugin extends OverlayPlugin {
    static class GeoIPOverlayPluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new GeoIPOverlayPlugin();
        }
    }

    private static final String DATABASE = "gjh1/networkMonitor/plugins/overlay/geoip/GeoIP.dat";
    private static final String FRIENDLY_NAME = "GeoIP Overlay";
    private static final String SYSTEM_NAME = "GeoIPOverlay";
    private static final String VERSION = "1.0.0.0";

    static {
        hasUserInterface = false;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, GeoIPOverlayPlugin.class, new GeoIPOverlayPluginFactory(), hasUserInterface);
    }

    private LookupService ls;

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
        return event.getType().equals("ADD_NODE");
    }

    public void load() {
        super.load();
        try {
            ls = new LookupService(DATABASE, LookupService.GEOIP_MEMORY_CACHE);
        }
        catch (IOException e) {
            Error.exceptionError("The GeoIP database could not be loaded.", e);
        }
    }

    public void receiveEvent(Event event) {
        if (event.getType().equals("ADD_NODE") && ls != null) {
            Node addedNode = (Node) event.get(Attributes.NODE);
            String host = (String) addedNode.getAttribute(Attributes.HOST);
            String[] parts = host.split(":");
            if (parts.length > 0) {
                String ip = parts[0];
                String countryName = ls.getCountry(ip).getName();
                Event overlayEvent = new Event("OVERLAY");
                overlayEvent.put(Attributes.OVERLAY, SYSTEM_NAME);
                overlayEvent.put(Attributes.NODE, addedNode);
                overlayEvent.put("COUNTRY_NAME", countryName);
                sendEventToBus(overlayEvent);
            }
        }
    }

    public void unload() {
        if (ls != null) {
            ls.close();
            ls = null;
        }
        super.unload();
    }
}
