/*
 * Created on Jun 14, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output;


import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;

import javax.swing.*;

/**
 * Simple output plugin which renders all received events as text.
 * 
 * @author gjh1
 */
public class JChordTextOutputPlugin extends TextOutputPlugin {
    static class JChordTextOutputPluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new JChordTextOutputPlugin();
        }
    }

    private static final String FRIENDLY_NAME = "JChord Text Output";
    private static final String SYSTEM_NAME = "JChordTextOutput";
    private static final String VERSION = "1.0.0.0";

    private JScrollPane jchordScroller;
    private JTextArea jchordText;

    static {
        hasUserInterface = true;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, JChordTextOutputPlugin.class, new JChordTextOutputPluginFactory(), hasUserInterface);
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
        jchordText = new JTextArea();
        jchordScroller = new JScrollPane(jchordText);
        jchordScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jchordScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabs.add("JChord Events", jchordScroller);
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
        else {
            jchordText.append(event.toString() + "\n");
        }
        // FIXME Fix automatic scrolling
        diagnosticText.setCaretPosition(diagnosticText.getDocument().getLength());
        errorText.setCaretPosition(errorText.getDocument().getLength());
        jchordText.setCaretPosition(jchordText.getDocument().getLength());
    }
}
