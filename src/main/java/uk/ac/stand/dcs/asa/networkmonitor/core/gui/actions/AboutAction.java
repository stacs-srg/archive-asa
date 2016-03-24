/*
 * Created on Jun 17, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions;

import uk.ac.stand.dcs.asa.networkmonitor.core.gui.AboutDialog;
import uk.ac.stand.dcs.asa.networkmonitor.core.gui.NetworkMonitor;

import java.awt.event.ActionEvent;


/**
 * <code>Action</code> responsible for displaying the about dialog.
 * 
 * @author gjh1
 */
public class AboutAction extends NetworkMonitorAbstractAction {
    public AboutAction(NetworkMonitor netmon) {
        super("About...", "/general/About16.gif", netmon);
        putValue(SHORT_DESCRIPTION, "Show the about dialog.");
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        AboutDialog aboutDialog = new AboutDialog(netmon, NetworkMonitor.NAME, NetworkMonitor.VERSION);
        aboutDialog.setVisible(true);
    }
}
