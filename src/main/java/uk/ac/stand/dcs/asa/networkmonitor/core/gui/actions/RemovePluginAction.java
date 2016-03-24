/*
 * Created on Jun 17, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions;


import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.core.gui.NetworkMonitor;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.util.Error;

import java.awt.event.ActionEvent;

/**
 * <code>Action</code> responsible for removing the current plugin instance.
 * 
 * @author gjh1
 */
public class RemovePluginAction extends NetworkMonitorAbstractAction {
    public RemovePluginAction(NetworkMonitor netmon) {
        super("Remove plugin", "/general/Delete16.gif", netmon);
        putValue(SHORT_DESCRIPTION, "Remove the current plugin instance.");
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        removePlugin();
    }

    public void removePlugin() {
        Plugin plugin = netmon.getSelectedPlugin();
        if (plugin != null) {
            // Remove its user interface
            netmon.removePluginFromDisplay();
            try {
                // Remove it from the registry
                PluginLoader.getInstance().removePluginFromRegistry(plugin.getSystemName(), plugin);
            }
            catch (Exception ex) { Error.exceptionError("error removing plugin", ex);
            }
            // Unload the plugin instance
            plugin.unload();
        }
    }
}
