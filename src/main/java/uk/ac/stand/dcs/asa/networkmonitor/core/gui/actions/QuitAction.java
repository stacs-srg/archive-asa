/*
 * Created on Jun 17, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions;


import uk.ac.stand.dcs.asa.networkmonitor.core.gui.NetworkMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * <code>Action</code> responsible for terminating the NetworkMonitor application.
 * 
 * @author gjh1
 */
public class QuitAction extends NetworkMonitorAbstractAction {
    public QuitAction(NetworkMonitor netmon) {
        super("Quit", "/general/Stop16.gif", netmon);
        putValue(SHORT_DESCRIPTION, "Quit the application.");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        netmon.quit();
    }
}
