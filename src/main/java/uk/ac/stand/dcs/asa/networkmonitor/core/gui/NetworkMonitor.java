/*
 * Created on Jun 15, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui;


import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.core.Preferences;
import uk.ac.stand.dcs.asa.networkmonitor.core.gui.actions.*;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

/**
 * The NetworkMonitor application. It allows the user to view the state of a peer-to-peer network.
 * 
 * @author gjh1
 */
public final class NetworkMonitor extends JFrame {
    public static final String NAME = "NetworkMonitor";
    public static final String VERSION = "1.0.0.0";
    private static Diagnostic diagnosticLevel;
    AboutAction aboutAction;
    AddPluginAction addPluginAction;
    HorizontalTileAction horizontalTileAction;
    PrefsAction prefsAction;
    QuitAction quitAction;
    RemovePluginAction removePluginAction;
    VerticalTileAction verticalTileAction;
    private JScrollPane desktopScroller;
    private NetworkMonitorJMenuBar menuBar;
    private JDesktopPane pluginDesktop;
    private InternalFrameListener pluginListener;
    private Preferences prefs;
    private NetworkMonitorJToolBar toolBar;

    private NetworkMonitor() {
        super(NAME);
        prefs = new Preferences();
        prefs.loadPreferences();
        String pluginPath = "gjh1" + File.separator + "networkMonitor" + File.separator + "plugins";
        // Determine which plugins are available for loading
        PluginLoader.getInstance().scanPathForPlugins(pluginPath);
        // Load any requested non-visual plugins at startup
        PluginLoader.getInstance().loadNonVisualPlugins(prefs);
        pluginListener = new InternalFrameListener() {
            public void internalFrameActivated(InternalFrameEvent e) {/* no action */}

            public void internalFrameClosed(InternalFrameEvent e) {/* no action */}
            
            public void internalFrameClosing(InternalFrameEvent e) {
                pluginDesktop.setSelectedFrame(e.getInternalFrame());
                removePluginAction.removePlugin();
            }

            public void internalFrameDeactivated(InternalFrameEvent e) {/* no action */}

            public void internalFrameDeiconified(InternalFrameEvent e) {/* no action */}

            public void internalFrameIconified(InternalFrameEvent e) {/* no action */}

            public void internalFrameOpened(InternalFrameEvent e) {/* no action */}
        };

        setSize(1024, 768);
        // Use our own event handler when the window is closed
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {/* no action */}

            public void windowClosed(WindowEvent e) {/* no action */}

            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                quit();
            }

            public void windowDeactivated(WindowEvent e) {/* no action */}

            public void windowDeiconified(WindowEvent e) {/* no action */}

            public void windowIconified(WindowEvent e) {/* no action */}

            public void windowOpened(WindowEvent e) {/* no action */}
        });

        // Actions used by both the menu items and toolbar buttons
        prefsAction = new PrefsAction(this);
        quitAction = new QuitAction(this);
        addPluginAction = new AddPluginAction(this);
        removePluginAction = new RemovePluginAction(this);
        horizontalTileAction = new HorizontalTileAction(this);
        verticalTileAction = new VerticalTileAction(this);
        aboutAction = new AboutAction(this);
        // Disable removing and tiling until some plugins are loaded
        removePluginAction.setEnabled(false);
        horizontalTileAction.setEnabled(false);
        verticalTileAction.setEnabled(false);

        // Construct the GUI
        menuBar = new NetworkMonitorJMenuBar(this);
        setJMenuBar(menuBar);
        toolBar = new NetworkMonitorJToolBar(this);
        getContentPane().add(toolBar, BorderLayout.PAGE_START);
        pluginDesktop = new JDesktopPane();
        desktopScroller = new JScrollPane(pluginDesktop);
        desktopScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        desktopScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(desktopScroller, BorderLayout.CENTER);
        // Display the main window in the centre of the screen
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        Error.enableLocalErrorReporting();
        Diagnostic.setLocalErrorReporting(true);
        Diagnostic.setLevel(diagnosticLevel);
        Diagnostic.traceNoSource("[Core] " + NAME + " " + VERSION + " loading...", Diagnostic.RUNALL);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        new NetworkMonitor();
    }

    /**
     * Determine which options the user wishes to be set, if any.
     */
    private static void parseCommandLine(String[] args) {
        // Set a default diagnostic level
        diagnosticLevel = Diagnostic.RUNALL;
        if (args.length > 0) {
            try {
                int diagnostic_level = Integer.parseInt(args[0]);
                switch (diagnostic_level) {
                    case 0:
                        diagnosticLevel = Diagnostic.FULL;
                        break;
                    case 1:
                        diagnosticLevel = Diagnostic.INIT;
                        break;
                    case 2:
                        diagnosticLevel = Diagnostic.RUN;
                        break;
                    case 3:
                        diagnosticLevel = Diagnostic.RUNALL;
                        break;
                    case 4:
                        diagnosticLevel = Diagnostic.RESULT;
                        break;
                    case 5:
                        diagnosticLevel = Diagnostic.FINAL;
                        break;
                    case 6:
                        diagnosticLevel = Diagnostic.NONE;
                        break;
                    default:
                        Error.errorNoEvent("Not a valid diagnostic level: " + diagnostic_level);
                        break;
                }
            }
            catch (NumberFormatException e) {
                Error.exceptionErrorNoEvent("Not a valid diagnostic level: " + args[0], e);
            }
        }
    }

    /**
     * Add the given plugin to the user interface.
     * 
     * @param plugin the <code>Plugin</code> instance to add
     */
    public void addPluginToDisplay(Plugin plugin) {
        JInternalFrame pluginFrame = new JInternalFrame(plugin.getSystemName(), true, true, true);
        pluginFrame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        pluginFrame.addInternalFrameListener(pluginListener);
        pluginFrame.setContentPane(plugin.getPluginPanel());
        pluginFrame.pack();
        pluginDesktop.add(pluginFrame);
        pluginFrame.show();
        // There is now at least one plugin added, so enable removing and tiling
        removePluginAction.setEnabled(true);
        horizontalTileAction.setEnabled(true);
        verticalTileAction.setEnabled(true);
    }

    /**
     * @return the object responsible for handling application preferences
     */
    public Preferences getPreferences() {
        return prefs;
    }

    /**
     * @return the plugin currently selected in the user interface
     */
    public Plugin getSelectedPlugin() {
        return PluginLoader.getInstance().lookupPluginFromPanel(getSelectedPanel());
    }

    /**
     * Perform a graceful shutdown of the application.
     */
    public void quit() {
        PluginLoader.getInstance().unloadAllPlugins();
        System.exit(0);
    }

    /**
     * Remove the currently selected plugin from the user interface.
     */
    public void removePluginFromDisplay() {
        JInternalFrame current = pluginDesktop.getSelectedFrame();
        if (current != null) {
            current.dispose();
            JInternalFrame[] plugins = pluginDesktop.getAllFrames();
            if (plugins.length > 0)
                pluginDesktop.setSelectedFrame(plugins[plugins.length - 1]);
            else {
                // This is the last plugin to be removed, so disable removing and tiling
                removePluginAction.setEnabled(false);
                horizontalTileAction.setEnabled(false);
                verticalTileAction.setEnabled(false);
            }
        }
    }

    /**
     * Tile the currently open plugin windows horizontally.
     */
    public void tileWindowsHorizontally() {
        Dimension desktopSize = pluginDesktop.getSize();
        int numberOfWindows = pluginDesktop.getComponentCount();
        int windowWidth = desktopSize.width / numberOfWindows;
        int windowHeight = desktopSize.height;
        for (int i = 0; i < numberOfWindows; i++)
            pluginDesktop.getAllFrames()[i].setBounds(i * windowWidth, 0, windowWidth, windowHeight);
    }

    /**
     * Tile the currently open plugin windows vertically.
     */
    public void tileWindowsVertically() {
        Dimension desktopSize = pluginDesktop.getSize();
        int numberOfWindows = pluginDesktop.getComponentCount();
        int windowWidth = desktopSize.width;
        int windowHeight = desktopSize.height / numberOfWindows;
        for (int i = 0; i < numberOfWindows; i++)
            pluginDesktop.getAllFrames()[i].setBounds(0, i * windowHeight, windowWidth, windowHeight);
    }

    /**
     * @return the <code>JPanel</code> currently selected in the user interface
     */
    private JPanel getSelectedPanel() {
        return (JPanel) pluginDesktop.getSelectedFrame().getContentPane();
    }
}
