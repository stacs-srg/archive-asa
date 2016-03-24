/*
 * Created on Jul 30, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User interface elements shared between different graph rendering plugins.
 * 
 * @author gjh1
 */
public class GraphButtonsPanel extends JPanel implements ActionListener {
    private JButton zoomInButton;
    private JButton zoomOutButton;

    public GraphButtonsPanel() {
        super();
        zoomInButton = new JButton("Zoom In");
        zoomInButton.addActionListener(this);
        add(zoomInButton);
        zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.addActionListener(this);
        add(zoomOutButton);
    }

    public void actionPerformed(ActionEvent e) {
        // Overriden by each specific usage of this class
    }

    /**
     * @return the <code>JButton</code> used to zoom in on the graph
     */
    public JButton getZoomInButton() {
        return zoomInButton;
    }

    /**
     * @return the <code>JButton</code> used to zoom out on the graph
     */
    public JButton getZoomOutButton() {
        return zoomOutButton;
    }
}
