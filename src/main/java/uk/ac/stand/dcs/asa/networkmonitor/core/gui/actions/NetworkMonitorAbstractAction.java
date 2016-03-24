/*
 * Created on Jun 17, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions;


import uk.ac.stand.dcs.asa.networkmonitor.core.gui.NetworkMonitor;
import uk.ac.stand.dcs.asa.util.Diagnostic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * Base class for <code>Action</code> objects used by the NetworkMonitor application.
 * 
 * @author gjh1
 */
public abstract class NetworkMonitorAbstractAction extends AbstractAction {
    protected static final String IMAGE_DIR = "/gjh1/networkMonitor/core/images/toolbarButtonGraphics";
    protected ImageIcon icon;
    protected URL imageURL;
    protected NetworkMonitor netmon;

    public NetworkMonitorAbstractAction(String name, String iconPath, NetworkMonitor netmon) {
        super(name);
        this.netmon = netmon;
        imageURL = this.getClass().getResource(IMAGE_DIR + iconPath);
        if (imageURL != null) {
            icon = new ImageIcon(imageURL);
            putValue(SMALL_ICON, icon);
        }
    }

    public void actionPerformed(ActionEvent e) {
        // Default behaviour is to display diagnostic information
        Diagnostic.trace(e.getActionCommand(), Diagnostic.FULL);
    }
}
