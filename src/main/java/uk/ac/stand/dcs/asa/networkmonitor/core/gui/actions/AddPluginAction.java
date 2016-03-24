/*
 * Created on Jun 17, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions;


import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.core.events.EventBus;
import uk.ac.stand.dcs.asa.networkmonitor.core.gui.NetworkMonitor;
import uk.ac.stand.dcs.asa.networkmonitor.core.gui.PluginChooserDialog;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.output.OutputPlugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.overlay.OverlayPlugin;
import uk.ac.stand.dcs.asa.util.Error;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Set;

/**
 * <code>Action</code> responsible for adding a new plugin instance to the display.
 * 
 * @author gjh1
 */
public class AddPluginAction extends NetworkMonitorAbstractAction {
    public AddPluginAction(NetworkMonitor netmon) {
        super("Add plugin...", "/general/Add16.gif", netmon);
        putValue(SHORT_DESCRIPTION, "Add a new plugin instance.");
    }

    public void actionPerformed(ActionEvent ae) {
        super.actionPerformed(ae);
        // Ask the user to choose a plugin
        Set pluginNames = PluginLoader.getInstance().getVisualPluginNames();
        String selected = PluginChooserDialog.showDialog((Component) ae.getSource(), pluginNames);
        if (!selected.equals("")) {
            try {
                // Load a new instance of the chosen plugin
                Plugin newPlugin = PluginLoader.getInstance().lookupFactory(selected).createNewInstance();
                newPlugin.load();
                // Add it to the registry
                PluginLoader.getInstance().addPluginToRegistry(newPlugin.getSystemName(), newPlugin);
                // Register it with the event bus if necessary
                if (newPlugin instanceof OutputPlugin || newPlugin instanceof OverlayPlugin)
                    EventBus.getInstance().registerConsumer((EventConsumer) newPlugin);
                // Display its user interface
                netmon.addPluginToDisplay(newPlugin);
            }
            catch (Exception e) {
                Error.exceptionError("Error loading plugin.", e);
            }
        }
    }
}
