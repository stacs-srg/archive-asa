/*
 * Created on 10-Aug-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.applicationFramework.exampleApplication.RingDisplay;
import uk.ac.stand.dcs.asa.eventModel.Event;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.interfaces.IKey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.net.URL;

public class AbstractP2PAppGUI extends JFrame implements EventConsumer{
    protected final String window_title;
    protected final String icon_path;
    protected final int window_height;
    protected final int window_width;
    
    /* SET GUI FONT */
    protected int fontSize = 9;
    protected  Font font = new Font("Verdana", Font.PLAIN, fontSize);

    /* SET GUI COLORS */
    protected  Color fgColor = new Color(255, 255, 255);
    protected  Color bgColor = new Color(0, 0, 0);
    protected  Color highlightColor = new Color(212, 208, 200);
    
    protected EventBus myBus;
    protected AbstractP2PApplication P2Papp;
    
    protected URL imgURL; 
    
    protected JTabbedPane mainTabbedPanel = new JTabbedPane();
    protected P2PNodeConfigPanel nodeConfig;
    
    protected JPanel buttons = new JPanel();
    protected JButton undock = new JButton("Undock Tab");
    
    protected JScrollPane diagnostics;
    protected JScrollPane error;
    protected JTextArea diagnosticText = new JTextArea();
    protected JTextArea errorText = new JTextArea();
    
    private RingDisplay display = new RingDisplay();
    
    protected InetSocketAddress localNetAddress;
    protected IKey localKey;
    /**
     * @param icon_path
     * @param window_height
     * @param window_title
     * @param window_width
     * @throws HeadlessException
     */
    public AbstractP2PAppGUI(AbstractP2PApplication app, EventBus bus, String icon_path, String window_title, int window_width, int window_height) throws HeadlessException {
        super();
        
        // TODO Auto-generated constructor stub
        this.icon_path = icon_path;
        this.window_height = window_height;
        this.window_title = window_title;
        this.window_width = window_width;
        
        myBus=bus;
        P2Papp=app;
        
        nodeConfig = new P2PNodeConfigPanel(font, bgColor, fgColor,app);
        imgURL = AbstractP2PAppGUI.class.getResource(icon_path);
        setSize(new Dimension(window_width, window_height));
        setResizable(true);
        setBackground(bgColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(window_title);
        setFont(font);
        setForeground(fgColor);
        setIconImage((new ImageIcon(imgURL)).getImage());
        
        //TABBED PANEL AND COMPONENTS
        mainTabbedPanel.setSize(900, 700);
        mainTabbedPanel.setMinimumSize(new Dimension(900, 700));
        mainTabbedPanel.setMaximumSize(new Dimension(900, 700));
        mainTabbedPanel.setBackground(highlightColor);
        mainTabbedPanel.setForeground(bgColor);
        mainTabbedPanel.setFont(font);
        
        undock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                undock();
            }
        });
        
        //DIAGNOSTICS OUTPUT PANEL AND COMPONENTS
        diagnosticText.setText("DIAGNOSTIC OUTPUT\n");
        diagnosticText.setBackground(fgColor);
        diagnosticText.setFont(font);
        diagnosticText.setSelectionColor(highlightColor);
        diagnostics = new JScrollPane(diagnosticText);

        //ERROR OUTPUT PANEL AND COMPONENTS
        errorText.setText("ERROR OUTPUT\n");
        errorText.setBackground(fgColor);
        errorText.setFont(font);
        errorText.setSelectionColor(highlightColor);
        error = new JScrollPane(errorText);
        
        undock.setFont(font);
        
        if (imgURL != null) {
            mainTabbedPanel.addTab("Configuration", new ImageIcon(imgURL), nodeConfig);
            mainTabbedPanel.addTab("DIAGNOSTICS",   new ImageIcon(imgURL), diagnostics);
            mainTabbedPanel.addTab("ERRORS",  new ImageIcon(imgURL), error);
            mainTabbedPanel.addTab("Display", new ImageIcon(imgURL), display);
        } else {
            mainTabbedPanel.addTab("Configuration", nodeConfig);
            mainTabbedPanel.addTab("DIAGNOSTICS", diagnostics);
            mainTabbedPanel.addTab("ERRORS", error);
            mainTabbedPanel.addTab("Display", display);
        }
        
        //ADD MAIN CONTAINERS TO THE FRAME
        buttons.setLayout(new java.awt.GridBagLayout());
        buttons.add(undock, new java.awt.GridBagConstraints(0, 0, 1, 
                1, 1.0, 1.0, java.awt.GridBagConstraints.NORTHWEST ,
                java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 5,
                        5), 0, 0));
        getContentPane().add(buttons, BorderLayout.BEFORE_FIRST_LINE);
        getContentPane().add(mainTabbedPanel, BorderLayout.CENTER);
        
        myBus.register(this);
        myBus.register(display);
    }
    
    private void undock(){
        int index=mainTabbedPanel.getSelectedIndex();
        JComponent component= (JComponent)mainTabbedPanel.getSelectedComponent();
        String title = mainTabbedPanel.getTitleAt(index);
        ImageIcon icon= (ImageIcon) mainTabbedPanel.getIconAt(index);
        mainTabbedPanel.remove(index);
        new UndockedWindow(component,index,mainTabbedPanel,title,icon);
    }
    
    public void networkInitOutput(String string) {
        nodeConfig.displayText(string);
    }
    
    public static String FATAL_INIT_ERROR_STRING = "Successfully initialised P2P node but could not extract local state.";
    
    public void initialiseApp() {
        localNetAddress=P2Papp.getLocalAddress();
        localKey=P2Papp.getLocalKey();
        if(localNetAddress==null || localKey==null){            
            networkInitOutput(FATAL_INIT_ERROR_STRING);
        }else{
            display.setLocalState(localNetAddress,localKey);
        }
    }
    
    public void disableInputComponents() {
        nodeConfig.disableInputComponents();
    }
    
    /**
     * @see uk.ac.stand.dcs.asa.applicationFramework.interfaces.StateVisualiser#receiveEvent(uk.ac.stand.dcs.asa.eventModel.Event)
     */
    public void receiveEvent(Event e){
        
        if(e.getType().equals("ErrorEvent")){
            errorText.append((String)e.get("msg"));
            errorText.append("\n");
        }else{
            if(e.getType().equals("DiagnosticEvent")){
                diagnosticText.append((String)e.get("msg"));
                diagnosticText.append("\n");
            }else{
                errorText.append("Unrecognised Event: "+e.getType());
            }
        }
        
    }

    /**
     * @see uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer#interested(uk.ac.stand.dcs.asa.eventModel.Event)
     */
    public boolean interested(Event event) {
        return event.getType().equals("ErrorEvent")
        ||event.getType().equals("DiagnosticEvent");
    }
    
}
