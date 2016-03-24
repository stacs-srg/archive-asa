/*
 * Created on Jun 16, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui;


import uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions.NetworkMonitorAbstractAction;

import javax.swing.*;

/**
 * The menu bar for the main NetworkMonitor application.
 * 
 * @author gjh1
 */
public class NetworkMonitorJMenuBar extends JMenuBar {
    private JMenu menu = null;
    
    public NetworkMonitorJMenuBar(NetworkMonitor netmon) {
        // Construct the control menu
        menu = new JMenu("Control");
        menu.add(makeMenuItem(netmon.prefsAction));
        menu.addSeparator();
        menu.add(makeMenuItem(netmon.quitAction));
        add(menu);
        // Construct the plugins menu
        menu = new JMenu("Plugins");
        menu.add(makeMenuItem(netmon.addPluginAction));
        menu.add(makeMenuItem(netmon.removePluginAction));
        menu.add(makeMenuItem(netmon.horizontalTileAction));
        menu.add(makeMenuItem(netmon.verticalTileAction));
        add(menu);
        // Force the help menu over to the right of the menu bar
        add(Box.createHorizontalGlue());
        // Construct the help menu
        menu = new JMenu("Help");
        menu.add(makeMenuItem(netmon.aboutAction));
        add(menu);
    }
    
    public static JMenuItem makeMenuItem(NetworkMonitorAbstractAction action) {
        JMenuItem menuItem = new JMenuItem();
        menuItem.setAction(action);
        menuItem.setIcon(null);
        return menuItem;
    }
}
