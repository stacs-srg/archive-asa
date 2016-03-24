/*
 * Created on Jun 14, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output;


import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Simple output plugin which renders all received events as text.
 * 
 * @author gjh1
 */
public class TextOutputPlugin extends OutputPlugin {
    static class TextOutputPluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new TextOutputPlugin();
        }
    }

    private static final String FRIENDLY_NAME = "Text Output";
    private static final String SYSTEM_NAME = "TextOutput";
    private static final String VERSION = "1.0.0.0";

    protected JScrollPane diagnosticScroller;
    protected JTextArea diagnosticText;
    protected JScrollPane errorScroller;
    protected JTextArea errorText;
    protected JTabbedPane tabs;

    static {
        hasUserInterface = true;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, TextOutputPlugin.class, new TextOutputPluginFactory(), hasUserInterface);
    }

    public String getFriendlyName() {
        return FRIENDLY_NAME;
    }

    public String getSystemName() {
        return SYSTEM_NAME;
    }

    public String getVersion() {
        return VERSION;
    }

    public void load() {
        super.load();
        pluginPanel = new JPanel();
        pluginPanel.setLayout(new BorderLayout());
        pluginPanel.setPreferredSize(new Dimension(640, 240));
        tabs = new JTabbedPane(JTabbedPane.BOTTOM);
        diagnosticText = new JTextArea();
        errorText = new JTextArea();
        diagnosticText.setEditable(false);
        errorText.setEditable(false);
        diagnosticScroller = new JScrollPane(diagnosticText);
        diagnosticScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        diagnosticScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        errorScroller = new JScrollPane(errorText);
        errorScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        errorScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabs.add("Diagnostic Events", diagnosticScroller);
        tabs.add("Error Events", errorScroller);
        pluginPanel.add(tabs, BorderLayout.CENTER);
    }

    public void receiveEvent(Event event) {
        String eventType = event.getType();
        if (eventType.equals("DiagnosticEvent")) {
            // A diagnostic event has been received
            diagnosticText.append(event.get("msg") + "\n");
        }
        else if (eventType.equals("ErrorEvent")) {
            // An error event has been received
            errorText.append(event.get("msg") + "\n");
        }
        // FIXME Fix automatic scrolling
        diagnosticText.setCaretPosition(diagnosticText.getDocument().getLength());
        errorText.setCaretPosition(errorText.getDocument().getLength());
    }
}
