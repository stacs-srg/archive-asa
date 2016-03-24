/*
 * Created on Aug 3, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output.jgraph;


import uk.ac.stand.dcs.asa.networkmonitor.core.Attributes;
import uk.ac.stand.dcs.asa.networkmonitor.core.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Swing component used as the user object for graph cells.
 * 
 * @author gjh1
 */
public class NodeComponent extends JPanel implements ActionListener {
    private static ImageIcon icon;
    private static final String IMAGE_DIR = "/gjh1/networkMonitor/core/images/toolbarButtonGraphics";
    private static URL imageURL;
    private JButton button;
    private Node node;

    public NodeComponent(Node node) {
        super(new BorderLayout());
        this.node = node;
        if (icon == null) {
            imageURL = this.getClass().getResource(IMAGE_DIR + "/development/Server24.gif");
            if (imageURL != null) {
                icon = new ImageIcon(imageURL);
            }
        }
        setBorder(null);
        button = new JButton(icon);
        button.setToolTipText((String) node.getAttribute(Attributes.HOST));
        button.addActionListener(this);
        add(button, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        // When the user clicks on the node, display an info window
        NodeInfoWindow infoWindow = new NodeInfoWindow((String) node.getAttribute(Attributes.HOST));
        infoWindow.setLocationRelativeTo(null);
        infoWindow.setVisible(true);
    }

    /**
     * @return the node containing the information to be rendered by this component
     */
    public Node getNode() {
        return node;
    }

    // Overridden because the tooltip is obtained from the JButton
    public String getToolTipText(MouseEvent event) {
        return button.getToolTipText(event);
    }
}
