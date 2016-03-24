/*
 * Created on Aug 3, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output.jgraph;

import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

/**
 * Window used to display extra information about a node.
 * 
 * @author gjh1
 */
public class NodeInfoWindow extends JFrame implements ItemListener {
    private class GeneralInfoPane extends JPanel {
        private JLabel title;

        public GeneralInfoPane() {
            super();
            title = new JLabel("General");
            add(title);
        }
    }

    private JComboBox cardChooser;
    private JPanel cardPane;
    private JPanel comboBoxPane;

    public NodeInfoWindow(String host) {
        super("Info for " + host);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Set overlayPluginNames = PluginLoader.getInstance().getOverlayPluginNames();
        cardChooser = new JComboBox(overlayPluginNames.toArray());
        cardChooser.addItemListener(this);
        cardChooser.insertItemAt("General", 0);
        cardChooser.setSelectedIndex(0);
        comboBoxPane = new JPanel();
        comboBoxPane.add(cardChooser);
        getContentPane().add(comboBoxPane, BorderLayout.PAGE_START);
        cardPane = new JPanel(new CardLayout());
        cardPane.add(new GeneralInfoPane(), "General");
        getContentPane().add(cardPane, BorderLayout.CENTER);
        pack();
    }

    public void itemStateChanged(ItemEvent e) {
        CardLayout cl = (CardLayout) cardPane.getLayout();
        cl.show(cardPane, (String) e.getItem());
    }
}
