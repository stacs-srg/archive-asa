/*
 * Created on Jul 27, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for displaying brief information about the application.
 * 
 * @author gjh1
 */
public class AboutDialog extends JDialog {
    public AboutDialog(Frame parent, String appName, String appVersion) {
        super(parent, "About " + appName, true);
        buildGUI(appName, appVersion);
        setLocationRelativeTo(parent);
    }

    private void buildGUI(String appName, String appVersion) {
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
        labelPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        labelPanel.setBackground(Color.WHITE);
        JLabel nameLabel = new JLabel(appName);
        nameLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 24));
        JLabel versionLabel = new JLabel("Version: " + appVersion);
        versionLabel.setForeground(Color.GRAY);
        JLabel authorLabel = new JLabel("Graeme Hamilton");
        labelPanel.add(nameLabel);
        labelPanel.add(versionLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        labelPanel.add(authorLabel);
        getContentPane().add(labelPanel);
        pack();
        setResizable(false);
    }
}
