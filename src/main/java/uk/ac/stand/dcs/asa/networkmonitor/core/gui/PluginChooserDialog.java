/*
 * Created on Jun 20, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * A dialog box used to choose a plugin for loading into the NetworkMonitor application.
 * 
 * @author gjh1
 */
public class PluginChooserDialog extends JDialog implements ActionListener {
    private static PluginChooserDialog dialog;
    private static String selected;
    private JPanel buttonPanel;
    private JButton cancelButton;
    private JPanel listPanel;
    private JScrollPane listScroller;
    private JList pluginList;
    private JButton selectButton;

    private PluginChooserDialog(Frame parent, Set choices) {
        super(parent, "Add plugin", true);
        // Add the plugin list
        TreeSet sorted = new TreeSet();
        sorted.addAll(choices);
        pluginList = new JList(new Vector(sorted));
        pluginList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Add selection listener to enable select button when a selection is made
        pluginList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                selectButton.setEnabled(true);
            }
        });
        // Allow user to double-click a plugin in the list to add it
        pluginList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Detect a double-click
                if (e.getClickCount() == 2) {
                    selectButton.doClick();
                }
            }
        });
        // Add the rest of the components
        listPanel = new JPanel();
        listScroller = new JScrollPane(pluginList);
        listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        listPanel.add(listScroller);
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        cancelButton = new JButton("Cancel");
        selectButton = new JButton("Select");
        cancelButton.addActionListener(this);
        selectButton.addActionListener(this);
        // Disable the select button until a selection is made
        selectButton.setEnabled(false);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(selectButton);
        getContentPane().add(listPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
        pack();
        setResizable(false);
        // Display the dialog on top of the main application window
        setLocationRelativeTo(parent);
    }

    /**
     * Display a dialog which allows the user to choose a plugin to load.
     * 
     * @param source the source of the event which caused this dialog to be displayed
     * @param choices the plugins from which the user can make a selection
     * @return the chosen plugin
     */
    public static String showDialog(Component source, Set choices) {
        dialog = new PluginChooserDialog(JOptionPane.getFrameForComponent(source), choices);
        selected = "";
        dialog.setVisible(true);
        return selected;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectButton) {
            // Only return a value if the user has made a selection
            if (pluginList.getSelectedIndex() > -1) {
                selected = pluginList.getSelectedValue().toString();
            }
        }
        dialog.dispose();
    }
}
