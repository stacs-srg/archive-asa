/*
 * Created on Jun 17, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions;

import uk.ac.stand.dcs.asa.networkmonitor.core.gui.NetworkMonitor;
import uk.ac.stand.dcs.asa.networkmonitor.core.gui.PreferencesDialog;

import java.awt.event.ActionEvent;


/**
 * <code>Action</code> responsible for displaying the preferences dialog.
 * 
 * @author gjh1
 */
public class PrefsAction extends NetworkMonitorAbstractAction {
    public PrefsAction(NetworkMonitor netmon) {
        super("Preferences...", "/general/Preferences16.gif", netmon);
        putValue(SHORT_DESCRIPTION, "Show the preferences dialog.");
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        PreferencesDialog prefsDialog = new PreferencesDialog(netmon, netmon.getPreferences());
        prefsDialog.setVisible(true);
    }   
}
