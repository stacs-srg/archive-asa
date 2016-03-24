/*
 * Created on Jul 27, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions;

import uk.ac.stand.dcs.asa.networkmonitor.core.gui.NetworkMonitor;

import java.awt.event.ActionEvent;


/**
 * <code>Action</code> responsible for tiling windows horizontally.
 * 
 * @author gjh1
 */
public class HorizontalTileAction extends NetworkMonitorAbstractAction {
    public HorizontalTileAction(NetworkMonitor netmon) {
        super("Tile Horizontally", "/general/AlignJustifyVertical16.gif", netmon);
        putValue(SHORT_DESCRIPTION, "Tile the currently open windows horizontally.");
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        netmon.tileWindowsHorizontally();
    }
}
