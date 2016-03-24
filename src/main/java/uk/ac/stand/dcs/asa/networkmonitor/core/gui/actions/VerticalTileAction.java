/*
 * Created on Jul 27, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions;

import uk.ac.stand.dcs.asa.networkmonitor.core.gui.NetworkMonitor;

import java.awt.event.ActionEvent;


/**
 * <code>Action</code> responsible for tiling windows vertically.
 * 
 * @author gjh1
 */
public class VerticalTileAction extends NetworkMonitorAbstractAction {
    public VerticalTileAction(NetworkMonitor netmon) {
        super("Tile Vertically", "/general/AlignJustifyHorizontal16.gif", netmon);
        putValue(SHORT_DESCRIPTION, "Tile the currently open windows vertically.");
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        netmon.tileWindowsVertically();
    }
}
