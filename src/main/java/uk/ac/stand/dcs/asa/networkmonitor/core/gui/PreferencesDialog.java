/*
 * Created on Jun 16, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui;


import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.core.Preferences;
import uk.ac.stand.dcs.asa.util.Diagnostic;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * The user interface for configuring application preferences.
 * 
 * @author gjh1
 */
public class PreferencesDialog extends JDialog implements ActionListener {
    private class NonVisualPluginsTab extends JPanel implements PreferenceTab {
        private Boolean[] loadPlugin;
        private String[] pluginNames;
        private JTable pluginsTable;
        private JScrollPane tableScroller;

        public NonVisualPluginsTab() {
            super(new BorderLayout());
            pluginNames = new String[PluginLoader.getInstance().getNonVisualPluginNames().size()];
            PluginLoader.getInstance().getNonVisualPluginNames().toArray(pluginNames);
            loadPlugin = new Boolean[pluginNames.length];
            pluginsTable = new JTable(new NonVisualPluginsTableModel(this));
            pluginsTable.setColumnSelectionAllowed(false);
            pluginsTable.setRowSelectionAllowed(false);
            pluginsTable.setCellSelectionEnabled(false);
            for (int i = 0; i < pluginsTable.getColumnCount(); i++)
                pluginsTable.getColumnModel().getColumn(i).setResizable(false);
            tableScroller = new JScrollPane(pluginsTable);
            tableScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            add(tableScroller, BorderLayout.CENTER);
        }

        public Boolean[] getLoadPlugins() {
            return loadPlugin;
        }

        public String[] getPluginNames() {
            return pluginNames;
        }

        public void loadPreferences(Preferences prefs) {
            for (int i = 0; i < pluginNames.length; i++)
                loadPlugin[i] = new Boolean(prefs.getPreference(Preferences.LOAD_NON_VISUAL_PLUGINS_PREFIX + pluginNames[i], "false"));
        }

        public void savePreferences(Preferences prefs) {
            for (int i = 0; i < pluginNames.length; i++)
                prefs.setPreference(Preferences.LOAD_NON_VISUAL_PLUGINS_PREFIX + pluginNames[i], loadPlugin[i].toString());
        }
    }

    private class NonVisualPluginsTableModel extends AbstractTableModel {
        private NonVisualPluginsTab dataSource;
 
        public NonVisualPluginsTableModel(NonVisualPluginsTab dataSource) {
            super();
            this.dataSource = dataSource;
        }

        public Class getColumnClass(int columnIndex) {
            if (columnIndex == 0) return String.class;
            if (columnIndex == 1) return Boolean.class;
            return Object.class;
        }

        public int getColumnCount() {
            return 2;
        }

        public String getColumnName(int column) {
            if (column == 0) return "Plugin";
            if (column == 1) return "Enable";
            return "";
        }

        public int getRowCount() {
            return dataSource.getPluginNames().length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0)
                return dataSource.getPluginNames()[rowIndex];
            if (columnIndex == 1)
                return dataSource.getLoadPlugins()[rowIndex];
            return null;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) return false;
            if (columnIndex == 1) return true;
            return false;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 1)
                dataSource.getLoadPlugins()[rowIndex] = (Boolean) aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    private JPanel buttonPanel;
    private JButton cancelButton;
    private NonVisualPluginsTab nonVisualPluginsTab;
    private JButton okButton;
    private Preferences prefs;
    private JPanel tabPanel;
    private JTabbedPane tabs;

    public PreferencesDialog(Frame parent, Preferences prefs) {
        super(parent, "Preferences", true);
        this.prefs = prefs;
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {/* no action */}

            public void windowClosed(WindowEvent e) {/* no action */}

            public void windowClosing(WindowEvent e) {
                closeDialog();
            }

            public void windowDeactivated(WindowEvent e) {/* no action */}

            public void windowDeiconified(WindowEvent e) {/* no action */}

            public void windowIconified(WindowEvent e) {/* no action */}

            public void windowOpened(WindowEvent e) {/* no action */}
        });
        buildGUI();
        setLocationRelativeTo(parent);
        nonVisualPluginsTab.loadPreferences(prefs);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            nonVisualPluginsTab.savePreferences(prefs);
            prefs.savePreferences();
        }
        closeDialog();
    }

    private void buildGUI() {
        getContentPane().setLayout(new BorderLayout());
        // Create the tabs
        tabPanel = new JPanel();
        tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.PAGE_AXIS));
        tabs = new JTabbedPane();
        nonVisualPluginsTab = new NonVisualPluginsTab();
        tabs.addTab("Non-Visual Plugins", nonVisualPluginsTab);
        tabPanel.add(tabs);
        tabPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Create the buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        buttonPanel.add(okButton);
        getContentPane().add(tabPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
        // Size the window
        setSize(640, 480);
        setResizable(false);
    }

    private void closeDialog() {
        Diagnostic.trace(Diagnostic.FULL);
        dispose();
    }
}
