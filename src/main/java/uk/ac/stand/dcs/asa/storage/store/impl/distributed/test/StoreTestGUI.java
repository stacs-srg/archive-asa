/*
 * Created on 10-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.test;

import uk.ac.stand.dcs.asa.applicationFramework.impl.AbstractP2PAppGUI;
import uk.ac.stand.dcs.asa.applicationFramework.impl.AbstractP2PApplication;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl.StoreComponent;

import javax.swing.*;
import java.awt.*;

public class StoreTestGUI extends AbstractP2PAppGUI{

    StoreUserInterface storeUI;
    
    public StoreTestGUI(StoreComponent store, AbstractP2PApplication app, EventBus bus){
        this(store, app,bus,"/Pictures/net_icon.gif","Store Test Application",900,700);
    }
    
    public StoreTestGUI(StoreComponent store, AbstractP2PApplication app, EventBus bus, String icon_path, String window_title, int window_width, int window_height) throws HeadlessException {
        super(app, bus, icon_path, window_title, window_width, window_height);
        storeUI=new StoreUserInterface(store);
        
        if (imgURL != null) {
            mainTabbedPanel.addTab("Store", new ImageIcon(imgURL), storeUI);
        } else {
            mainTabbedPanel.addTab("Store", storeUI);
        }
    }
    
    public void initialiseApp() {
        super.initialiseApp();
        storeUI.enableControls();
    }
   

}
