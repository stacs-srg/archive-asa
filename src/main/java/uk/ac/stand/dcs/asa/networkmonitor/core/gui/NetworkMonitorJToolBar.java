/*
 * Created on Jun 17, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui;


import uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions.NetworkMonitorAbstractAction;

import javax.swing.*;

/**
 * The toolbar for the NetworkMonitor application.
 * 
 * @author gjh1
 */
public class NetworkMonitorJToolBar extends JToolBar {
    public NetworkMonitorJToolBar(NetworkMonitor netmon) {
        // Construct the toolbar
        add(makeButton(netmon.prefsAction));
        add(makeButton(netmon.quitAction));
        addSeparator();
        add(makeButton(netmon.addPluginAction));
        add(makeButton(netmon.removePluginAction));
        add(makeButton(netmon.horizontalTileAction));
        add(makeButton(netmon.verticalTileAction));
        addSeparator();
        add(makeButton(netmon.aboutAction));
    }
    
    public static JButton makeButton(NetworkMonitorAbstractAction action) {
        JButton button = new JButton();
        button.setAction(action);
        button.setText("");
        return button;
    }
}
