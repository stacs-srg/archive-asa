/*
 * Created on 24-Jun-2005
  */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author stuart
 */
public class UndockedWindow extends JFrame {
    protected  final int number;
    protected final JTabbedPane home;
    protected final String title;
    protected final ImageIcon icon;
    protected final JComponent component;
    /**
     * 
     */
    public UndockedWindow(JComponent component, int number, JTabbedPane home, String title, ImageIcon icon){
        super();
        this.component=component;
        this.number=number;
        this.home=home;
        this.title=title;
        this.icon=icon; 
        initialise();
    }
    
    private void initialise(){
        setIconImage(icon.getImage());
        setTitle(title+" [close window to dock panel]");
        setSize(new Dimension(900, 700));

        addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent arg0) {
                
                // No action
            }

            public void windowClosing(WindowEvent arg0) {
                if(icon!=null){
                    home.addTab(title,icon,component);
                }else{
                    home.addTab(title,component);
                }
                dispose();
            }

            public void windowClosed(WindowEvent arg0) {
                
                // No action
            }

            public void windowIconified(WindowEvent arg0) {
                
                // No action
            }

            public void windowDeiconified(WindowEvent arg0) {
                
                // No action
            }

            public void windowActivated(WindowEvent arg0) {
                
                // No action
            }

            public void windowDeactivated(WindowEvent arg0) {
                
                // No action
            }
        });
        getContentPane().add(component,BorderLayout.CENTER);
        show();
    }
    
}
