/*
 * Created on 06-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.util.RemoteRRTRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class P2PNodeConfigPanel extends JPanel {

    private JLabel localIPLabel = new JLabel("Local IP/hostname:");
    private JTextField localIPText = new JTextField();
    private JLabel localPortLabel = new JLabel("Local port:");
    private JTextField localPortText = new JTextField();
    
    private JLabel knownIPLabel = new JLabel("Known node IP/hostname:");
    private JTextField knownIPText = new JTextField();
    private JLabel knownPortLabel = new JLabel("Known node port:");
    private JTextField knownPortText = new JTextField();
    private JButton joinButton = new JButton("Join Network");
    private JTextArea joinCreateOutputText = new JTextArea();
    private JScrollPane joinCreateOutputScroller;

    private JButton createButton = new JButton("Create Network");
    
    private Font FONT;
    private Color FG_COLOR;
    private Color BG_COLOR;
    
    AbstractP2PApplication app;
    
    public P2PNodeConfigPanel(Font f, Color fg, Color bg, AbstractP2PApplication app) {
        super();
        FONT=f;
        FG_COLOR=fg;
        BG_COLOR=bg;
        this.app=app;
        initialise();
    }

    private void initialise(){
        //Net config
        
        setSize(new Dimension(600, 350));
        setLayout(new java.awt.GridBagLayout());
        
        localIPLabel.setFont(FONT);
        localIPLabel.setForeground(FG_COLOR);
        
        localIPText.setPreferredSize(new java.awt.Dimension(160, 22));
        localIPText.setMinimumSize(new java.awt.Dimension(160, 22));
        localIPText.setMaximumSize(new java.awt.Dimension(160, 22));
        localIPText.setFont(FONT);
        
        localPortLabel.setFont(FONT);
        localPortLabel.setForeground(FG_COLOR);
        
        localPortText.setPreferredSize(new java.awt.Dimension(160, 22));
        localPortText.setMinimumSize(new java.awt.Dimension(160, 22));
        localPortText.setMaximumSize(new java.awt.Dimension(160, 22));
        //errorWSport.setMargin(new java.awt.Insets(5, 0, 0, 0));
        localPortText.setFont(FONT);
        localPortText.setText(Integer.toString(RemoteRRTRegistry.DEFAULT_RRT_PORT));
        
        //
        knownIPLabel.setFont(FONT);
        knownIPLabel.setForeground(FG_COLOR);
        
        knownIPText.setPreferredSize(new java.awt.Dimension(160, 22));
        knownIPText.setMinimumSize(new java.awt.Dimension(160, 22));
        knownIPText.setMaximumSize(new java.awt.Dimension(160, 22));
        knownIPText.setFont(FONT);
        
        knownPortLabel.setFont(FONT);
        knownPortLabel.setForeground(FG_COLOR);
        
        knownPortText.setPreferredSize(new java.awt.Dimension(160, 22));
        knownPortText.setMinimumSize(new java.awt.Dimension(160, 22));
        knownPortText.setMaximumSize(new java.awt.Dimension(160, 22));
        //errorWSport.setMargin(new java.awt.Insets(5, 0, 0, 0));
        knownPortText.setFont(FONT);
        knownPortText.setText(Integer.toString(RemoteRRTRegistry.DEFAULT_RRT_PORT));
        
        joinButton.setFont(FONT);
        joinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
//                int lport = -1;
//                int kport = -1;
//                try {
//                    lport=Integer.parseInt(localPortText.getText());
//                } catch (NumberFormatException e) {
//                    Error.errorNoSource("Illegal port number ("+localPortText.getText()+")");
//                }
//                try {
//                    kport=Integer.parseInt(knownPortText.getText());
//                } catch (NumberFormatException e) {
//                    Error.errorNoSource("Illegal port number ("+knownPortText.getText()+")");
//                }
//                
//                if(lport!=-1&&kport!=-1){
                    disableInputComponents();
                    if(app.initialiseP2P(localIPText.getText(),localPortText.getText(),knownIPText.getText(),knownPortText.getText())){
                        highlightJoinInputs(Color.ORANGE);
                        app.initialiseApp();
                    }
//                }
            }
        });
        
        createButton.setFont(FONT);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
//                int port = -1;
//                try {
//                    port=Integer.parseInt(localPortText.getText());
//                } catch (NumberFormatException e) {
//                    Error.errorNoSource("Illegal port number ("+localPortText.getText()+")");
//                }
//                if(port!=-1){
                    disableInputComponents();
                    if(app.initialiseP2P(localIPText.getText(),localPortText.getText())){
                        highlightCreateInputs(Color.ORANGE);
                        app.initialiseApp();
                    }
                }
//            }
        });
        
        
        //errorWSoutput.setText("SYSTEM ERROR OUTPUT\n");
        joinCreateOutputText.setBackground(BG_COLOR);
        joinCreateOutputText.setFont(FONT);
        //errorWSoutput.setSelectionColor(COLOR_3);
        
        joinCreateOutputText.setEditable(false);
        joinCreateOutputScroller = new JScrollPane(joinCreateOutputText);
        joinCreateOutputScroller.setPreferredSize(new java.awt.Dimension(590, 160));
        joinCreateOutputScroller.setMinimumSize(new java.awt.Dimension(590, 160));
        joinCreateOutputScroller.setMaximumSize(new java.awt.Dimension(590, 160));
        
        //add the components
        add(localIPLabel, new java.awt.GridBagConstraints(0, 0, 1, 
                1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
                        5), 0, 0));
        add(localIPText, new java.awt.GridBagConstraints(1, 0, 1, 
                1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
                        0), 0, 0));
        add(localPortLabel, new java.awt.GridBagConstraints(1, 0, 1, 
                1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
                        5), 0, 0));
        add(localPortText, new java.awt.GridBagConstraints(1, 1, 1, 
                1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
                        0), 0, 0));
        add(createButton, new java.awt.GridBagConstraints(0, 2, 2, 
                1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
                        0), 0, 0));
        add(knownIPLabel, new java.awt.GridBagConstraints(0, 3, 1, 
                1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
                        5), 0, 0));
        add(knownIPText, new java.awt.GridBagConstraints(1, 3, 1, 
                1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
                        0), 0, 0));
        add(knownPortLabel, new java.awt.GridBagConstraints(0, 4, 1, 
                1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
                        5), 0, 0));
        add(knownPortText, new java.awt.GridBagConstraints(1, 4, 1, 
                1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
                        0), 0, 0));
        add(joinButton, new java.awt.GridBagConstraints(0, 5, 2, 
                1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
                        0), 0, 0));
        add(joinCreateOutputScroller, new java.awt.GridBagConstraints(0, 6, 2, 
                1, 1.0, 1.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
                        5), 0, 0));
    }

    public JButton getCreateButton() {
        return createButton;
    }

    public JButton getJoinButton() {
        return joinButton;
    }
    
    public void displayText(String s){
        joinCreateOutputText.append(s);
        joinCreateOutputText.append("\n");
    }

    public JTextField getKnownIPText() {
        return knownIPText;
    }

    public JTextField getKnownPortText() {
        return knownPortText;
    }

    public JTextField getLocalIPText() {
        return localIPText;
    }

    public JTextField getLocalPortText() {
        return localPortText;
    }
    
    public void disableInputComponents() {
        joinButton.setEnabled(false);
        createButton.setEnabled(false);
        knownIPText.setEditable(false);
        knownPortText.setEditable(false);
        localIPText.setEditable(false);
        localPortText.setEditable(false);
    }

    public void highlightCreateInputs(Color highlight) {
        localIPText.setBackground(highlight);
        localPortText.setBackground(highlight);
    }
    
    public void highlightJoinInputs(Color highlight) {
        highlightCreateInputs(highlight);
        knownIPText.setBackground(highlight);
        knownPortText.setBackground(highlight);
    }
    
}
