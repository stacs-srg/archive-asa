/*
 * Created on 25-May-2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output;


import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.networkmonitor.core.PluginLoader;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;

import javax.swing.*;
import java.awt.*;
import java.net.InetSocketAddress;

/**
 * @author stuart
 * @author gjh1
 */
public class RingDisplayOutputPlugin extends OutputPlugin {
    static class RingDisplayOutputPluginFactory extends PluginFactory {
        public Plugin createNewInstance() {
            return new RingDisplayOutputPlugin();
        }
    }

    private class DisplayPanel extends JPanel {
        public void paint(Graphics g) {
            super.paint(g);
            if (localAddress != null) {
                setBackground(Color.WHITE);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Font exFont = new Font("Verdana", Font.PLAIN, 10);
                g2d.setFont(exFont);

                g2d.setColor(Color.black);
                g2d.drawString(FormatHostInfo.formatHostName(localAddress), 300, 375);
                g2d.drawString(localKey.toString(16), 300, 390);

                // Ellipse2D.Double localNode = new Ellipse2D.Double(300, 400, 100, 100);
                Stroke s = g2d.getStroke();

                Color color = g2d.getColor();
                g2d.setColor(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 180));
                g2d.fillOval(300, 400, 100, 100);

                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(color);
                g2d.drawOval(300, 400, 100, 100);

                // Ellipse2D.Double successor = new Ellipse2D.Double(550, 425, 50, 50);
                if (successor != null) {
                    g2d.setStroke(new BasicStroke(3));
                    g2d.setColor(new Color(0.36f, 0.25f, 0.78f, 0.75f));
                    g2d.fillOval(550, 425, 50, 50);
                    g2d.setColor(color);
                    g2d.drawOval(550, 425, 50, 50);
                    g2d.setStroke(s);
                    g2d.drawLine(400, 450, 550, 450);
                    g2d.drawString(FormatHostInfo.formatHostName(successor), 550, 525);
                    g2d.drawString(successor_key.toString(16), 550, 540);
                }

                if (predecessor != null) {
                    // Ellipse2D.Double predecessor = new Ellipse2D.Double(100, 425, 50, 50);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.setColor(new Color(0.36f, 0.25f, 0.78f, 0.75f));
                    g2d.fillOval(100, 425, 50, 50);
                    g2d.setColor(color);
                    g2d.drawOval(100, 425, 50, 50);
                    g2d.setStroke(s);
                    g2d.drawLine(300, 450, 150, 450);
                    g2d.drawString(FormatHostInfo.formatHostName(predecessor), 100, 525);
                    g2d.drawString(predecessor_key.toString(16), 100, 540);
                }
            }
        }
    }

    private static final String FRIENDLY_NAME = "RingDisplay Output";
    private static final String SYSTEM_NAME = "RingDisplayOutput";
    private static final String VERSION = "1.0.0.0";

    static {
        hasUserInterface = true;
        PluginLoader.getInstance().addFactoryToRegistry(SYSTEM_NAME, RingDisplayOutputPlugin.class, new RingDisplayOutputPluginFactory(), hasUserInterface);
    }

    /* SET GUI FONT */
    private final int FONT_SIZE = 9;
    private Font FONT = new Font("Verdana", Font.PLAIN, FONT_SIZE);
    private InetSocketAddress localAddress;
    private IKey localKey;
    private JTextArea output;
    private JScrollPane outputScroll;
    private InetSocketAddress predecessor;
    private IKey predecessor_key;
    private InetSocketAddress successor;
    private IKey successor_key;

    public String getFriendlyName() {
        return FRIENDLY_NAME;
    }

    public String getSystemName() {
        return SYSTEM_NAME;
    }

    public String getVersion() {
        return VERSION;
    }

    public boolean interested(Event event) {
        return event.getType().equals("SuccessorRepEvent")
                || event.getType().equals("PredecessorRepEvent")
                || event.getType().equals("NodeFailureNotificationRepEvent");
    }

    public void load() {
        super.load();
        pluginPanel = new DisplayPanel();
        output = new JTextArea();
        output.setFont(FONT);
        output.setLineWrap(true);
        output.setEditable(false);

        outputScroll = new JScrollPane(output);
        outputScroll.setPreferredSize(new java.awt.Dimension(590, 160));
        outputScroll.setMinimumSize(new java.awt.Dimension(590, 160));
        outputScroll.setMaximumSize(new java.awt.Dimension(590, 160));

        pluginPanel.setLayout(new java.awt.GridBagLayout());
        pluginPanel.add(outputScroll, new GridBagConstraints(0, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));
    }

    public void receiveEvent(Event event) {
        // FIXME This plugin will not work until this code is changed!
        output.append(event.getType());
        output.append("\n");

        if (event.getType().equals("SuccessorRepEvent")) {
            InetSocketAddress succ = (InetSocketAddress) event.get("succ");
            if (succ != null) {
                IKey succ_key = (IKey) event.get("succ_key");
                successor = succ;
                successor_key = succ_key;
            }
        }
        else {
            if (event.getType().equals("PredecessorRepEvent")) {
                InetSocketAddress pred = (InetSocketAddress) event.get("pred");
                if (pred != null) {
                    IKey pred_key = (IKey) event.get("pred_key");
                    predecessor = pred;
                    predecessor_key = pred_key;
                }
            }
        }
        pluginPanel.repaint();
    }

    public void setLocalState(InetSocketAddress localAddress, IKey localKey) {
        this.localAddress = localAddress;
        this.localKey = localKey;
        pluginPanel.repaint();
    }
}
