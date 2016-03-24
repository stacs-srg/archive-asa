/*
 * Created on 09-Aug-2004
 *
 */
package uk.ac.stand.dcs.asa.applications.eventHarness;

import uk.ac.stand.dcs.asa.eventModel.EventFactory;
import uk.ac.stand.dcs.asa.eventModel.P2PHost;
import uk.ac.stand.dcs.asa.eventModel.eventBus.EventBusImpl;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.eventModel.eventBus.consumers.DiagnosticEventStringWriter;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Vector;


/**
 * @author stuart, graham
 */
public class EventGeneratorGUI extends JFrame implements ActionListener  {

	private static EventGeneratorGUI instance;

	/**
	 * 
	 * @uml.property name="eg"
	 * @uml.associationEnd 
	 * @uml.property name="eg" multiplicity="(1 1)"
	 */
	private EventGenerator eg;

	
	/* SET GUI FONT */
	private int FONT_SIZE = 9;

	private java.awt.Font FONT = new java.awt.Font("Verdana",
			java.awt.Font.PLAIN, FONT_SIZE);

	/* SET GUI COLORS */
	private java.awt.Color COLOR_1 = new java.awt.Color(255, 255, 255);

	private java.awt.Color COLOR_2 = new java.awt.Color(0, 0, 0);

	/**
	 * 
	 * @uml.property name="mainTabbedPanel"
	 * @uml.associationEnd 
	 * @uml.property name="mainTabbedPanel" multiplicity="(1 1)"
	 */
	// SWING COMPONENTS
	private JTabbedPane mainTabbedPanel = new JTabbedPane();

	/**
	 * 
	 * @uml.property name="sendErrorEvent"
	 * @uml.associationEnd 
	 * @uml.property name="sendErrorEvent" multiplicity="(0 1)"
	 */
	private JPanel sendErrorEvent;

	/**
	 * 
	 * @uml.property name="sendDiagnosticEvent"
	 * @uml.associationEnd 
	 * @uml.property name="sendDiagnosticEvent" multiplicity="(0 1)"
	 */
	private JPanel sendDiagnosticEvent;

	/**
	 * 
	 * @uml.property name="sendJChordNodeEvent"
	 * @uml.associationEnd 
	 * @uml.property name="sendJChordNodeEvent" multiplicity="(0 1)"
	 */
	private JPanel sendJChordNodeEvent;

	/**
	 * 
	 * @uml.property name="errorMsgLabel"
	 * @uml.associationEnd 
	 * @uml.property name="errorMsgLabel" multiplicity="(1 1)"
	 */
	//components for error event panel
	private JLabel errorMsgLabel = new JLabel("Enter an error message");
	private JTextField errorMsg = new JTextField();
	private JButton errorSendMsg = new JButton("Create Error Event_Old");
	private CustomListModel error_clm = new CustomListModel(new Vector());
	private JList errorMsgList = new JList(error_clm);
	private JScrollPane errorListScroller = new JScrollPane(errorMsgList);
	private JLabel errorWSlabel = new JLabel("ErrorEvent_WS host:");
	private JTextField errorWShostname = new JTextField();
	private JLabel errorWSplabel = new JLabel("ErrorEvent_WS port:");
	private JTextField errorWSport = new JTextField();
	private JLabel errorWSnlabel = new JLabel("ErrorEvent_WS service name:");
	private JTextField errorWSname = new JTextField();
	private JButton errorWSconnect = new JButton("Connect to ErrorEvent_WS");
	private JTextArea errorWSoutput = new JTextArea();
	private JScrollPane errorWSoutputPane;

	//components for diagnostic event panel
	private JLabel diagnosticMsgLabel = new JLabel("Enter a diagnostic message");
	private JTextField diagnosticMsg = new JTextField();
	private JButton diagnosticSendMsg = new JButton("Create Diagnostic Event_Old");
	private CustomListModel diagnostic_clm = new CustomListModel(new Vector());
	private JList diagnosticMsgList = new JList(diagnostic_clm);
	private JScrollPane diagnosticListScroller = new JScrollPane(diagnosticMsgList);
	private JLabel diagnosticWSlabel = new JLabel("DiagnosticEvent_WS host:");
	private JTextField diagnosticWShostname = new JTextField();
	private JLabel diagnosticWSplabel = new JLabel("DiagnosticEvent_WS port:");
	private JTextField diagnosticWSport = new JTextField();
	private JLabel diagnosticWSnlabel = new JLabel("DiagnosticEvent_WS service name:");
	private JTextField diagnosticWSname = new JTextField();
	private JButton diagnosticWSconnect = new JButton("Connect to DiagnosticEvent_WS");
	private JTextArea diagnosticWSoutput = new JTextArea();
	private JScrollPane diagnosticWSoutputPane;
		
	//misc
	/*private JPanelListener mainMenuPanel;
	private Image background;*/

	private EventGeneratorGUI() {
	    InetSocketAddress insa = new InetSocketAddress( "127.0.0.1",12121 );
		eg= new EventGenerator(new P2PHost(insa,null));	// Stuart said just make it null <<<<<< mistake is here!
		initGUI();
	}

	/**
	 * @return the only instance of this class (Singleton pattern)
	 */
	public static EventGeneratorGUI getInstance() {
		if (instance == null) instance = new EventGeneratorGUI();
		return instance;
	}

	
	/**
	 * Initialise all GUI components and display it.
	 *
	 */
	public void initGUI() {
		java.net.URL imgURL = EventGeneratorGUI.class.getResource("/Pictures/icon_E.jpg");
		this.setSize(new Dimension(600, 600));
		setResizable(true);
		setBackground(COLOR_2);
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		setTitle("Event_Old Generator");
		setFont(FONT);
		setForeground(COLOR_1);
		setIconImage((new ImageIcon(imgURL)).getImage());
		
		/* END: Code from http://www.jgraph.com/doc/tutorial/t1.html */
		// MAIN MENU PANEL AND COMPONENTS
		//java.net.URL backgroundURL = EventGeneratorGUI.class
		//		.getResource("/Pictures/background.jpg");
		//background = new ImageIcon(backgroundURL).getImage();
		
		//mainMenuPanel = new JPanelListener(background);
		//mainMenuPanel.setBackground(COLOR_1);
		//mainMenuPanel.setLayout(new java.awt.GridBagLayout());
		//mainMenuPanel.setPreferredSize(new Dimension(100, 600));
		//mainMenuPanel.setForeground(COLOR_2);

		// TABBED PANEL AND COMPONENTS
		mainTabbedPanel.setSize(600, 600);
		mainTabbedPanel.setBackground(COLOR_1);
		mainTabbedPanel.setForeground(COLOR_2);
		mainTabbedPanel.setFont(FONT);
		
		sendErrorEvent = new JPanel();
		sendErrorEvent.setBackground(COLOR_1);
		sendErrorEvent.setLayout(new java.awt.GridBagLayout());
		sendErrorEvent.setPreferredSize(new Dimension(600, 600));
		sendErrorEvent.setForeground(COLOR_2);
		
		sendDiagnosticEvent = new JPanel();
		sendDiagnosticEvent.setBackground(COLOR_1);
		sendDiagnosticEvent.setLayout(new java.awt.GridBagLayout());
		sendDiagnosticEvent.setPreferredSize(new Dimension(600, 600));
		sendDiagnosticEvent.setForeground(COLOR_2);
		
		sendJChordNodeEvent = new JPanel();
		sendJChordNodeEvent.setBackground(COLOR_1);
		sendJChordNodeEvent.setLayout(new java.awt.GridBagLayout());
		sendJChordNodeEvent.setPreferredSize(new Dimension(600, 600));
		sendJChordNodeEvent.setForeground(COLOR_2);

		//components for error event panel
		errorMsgLabel.setFont(FONT);
		errorMsgLabel.setForeground(COLOR_2);
		
		errorMsg.setPreferredSize(new java.awt.Dimension(160, 22));
		errorMsg.setMinimumSize(new java.awt.Dimension(160, 22));
		errorMsg.setMaximumSize(new java.awt.Dimension(160, 22));
		//errorMsg.setMargin(new java.awt.Insets(5, 0, 0, 0));
		errorMsg.setFont(FONT);
		
		errorSendMsg.setFont(FONT);
		errorSendMsg.addActionListener(this);
		
		errorMsgList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		errorMsgList.setLayoutOrientation(JList.VERTICAL);
		errorMsgList.addMouseListener(new CustomJListMouseListener(errorMsg,errorMsgList));
		errorMsgList.setFont(FONT);
		errorListScroller.setPreferredSize(new java.awt.Dimension(590, 160));
		errorListScroller.setMinimumSize(new java.awt.Dimension(590, 160));
		errorListScroller.setMaximumSize(new java.awt.Dimension(590, 160));
		
		errorWSlabel.setFont(FONT);
		errorWSlabel.setForeground(COLOR_2);
		
		errorWShostname.setPreferredSize(new java.awt.Dimension(160, 22));
		errorWShostname.setMinimumSize(new java.awt.Dimension(160, 22));
		errorWShostname.setMaximumSize(new java.awt.Dimension(160, 22));
		//errorWShostname.setMargin(new java.awt.Insets(5, 0, 0, 0));
		errorWShostname.setFont(FONT);
		
		errorWSplabel.setFont(FONT);
		errorWSplabel.setForeground(COLOR_2);
		
		errorWSport.setPreferredSize(new java.awt.Dimension(160, 22));
		errorWSport.setMinimumSize(new java.awt.Dimension(160, 22));
		errorWSport.setMaximumSize(new java.awt.Dimension(160, 22));
		//errorWSport.setMargin(new java.awt.Insets(5, 0, 0, 0));
		errorWSport.setFont(FONT);
		errorWSport.setText(Integer.toString(EventViewer.EventDeliveryServices_port));
		
		errorWSnlabel.setFont(FONT);
		errorWSnlabel.setForeground(COLOR_2);
		
		errorWSname.setPreferredSize(new java.awt.Dimension(160, 22));
		errorWSname.setMinimumSize(new java.awt.Dimension(160, 22));
		errorWSname.setMaximumSize(new java.awt.Dimension(160, 22));
		//errorWSname.setMargin(new java.awt.Insets(5, 0, 0, 0));
		errorWSname.setFont(FONT);
		errorWSname.setText(EventViewer.EventServiceName);
		
		errorWSconnect.setFont(FONT);
		errorWSconnect.addActionListener(this);
		
		//errorWSoutput.setText("SYSTEM ERROR OUTPUT\n");
		errorWSoutput.setBackground(COLOR_1);
		errorWSoutput.setFont(FONT);
		//errorWSoutput.setSelectionColor(COLOR_3);
		
		errorWSoutput.setEditable(false);
		errorWSoutputPane = new JScrollPane(errorWSoutput);
		errorWSoutputPane.setPreferredSize(new java.awt.Dimension(590, 160));
		errorWSoutputPane.setMinimumSize(new java.awt.Dimension(590, 160));
		errorWSoutputPane.setMaximumSize(new java.awt.Dimension(590, 160));
		
		//components for diagnostic event panel
		diagnosticMsgLabel.setFont(FONT);
		diagnosticMsgLabel.setForeground(COLOR_2);
		
		diagnosticMsg.setPreferredSize(new java.awt.Dimension(160, 22));
		diagnosticMsg.setMinimumSize(new java.awt.Dimension(160, 22));
		diagnosticMsg.setMaximumSize(new java.awt.Dimension(160, 22));
		//diagnosticMsg.setMargin(new java.awt.Insets(5, 0, 0, 0));
		diagnosticMsg.setFont(FONT);
		
		diagnosticSendMsg.setFont(FONT);
		diagnosticSendMsg.addActionListener(this);
		
		diagnosticMsgList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		diagnosticMsgList.setLayoutOrientation(JList.VERTICAL);
		diagnosticMsgList.addMouseListener(new CustomJListMouseListener(diagnosticMsg,diagnosticMsgList));
		diagnosticMsgList.setFont(FONT);
		diagnosticListScroller.setPreferredSize(new java.awt.Dimension(590, 160));
		diagnosticListScroller.setMinimumSize(new java.awt.Dimension(590, 160));
		diagnosticListScroller.setMaximumSize(new java.awt.Dimension(590, 160));
		
		diagnosticWSlabel.setFont(FONT);
		diagnosticWSlabel.setForeground(COLOR_2);
		
		diagnosticWShostname.setPreferredSize(new java.awt.Dimension(160, 22));
		diagnosticWShostname.setMinimumSize(new java.awt.Dimension(160, 22));
		diagnosticWShostname.setMaximumSize(new java.awt.Dimension(160, 22));
		//diagnosticWShostname.setMargin(new java.awt.Insets(5, 0, 0, 0));
		diagnosticWShostname.setFont(FONT);
		
		diagnosticWSplabel.setFont(FONT);
		diagnosticWSplabel.setForeground(COLOR_2);
		
		diagnosticWSport.setPreferredSize(new java.awt.Dimension(160, 22));
		diagnosticWSport.setMinimumSize(new java.awt.Dimension(160, 22));
		diagnosticWSport.setMaximumSize(new java.awt.Dimension(160, 22));
		//diagnosticWSport.setMargin(new java.awt.Insets(5, 0, 0, 0));
		diagnosticWSport.setFont(FONT);
		diagnosticWSport.setText(Integer.toString(EventViewer.EventDeliveryServices_port));
		
		diagnosticWSnlabel.setFont(FONT);
		diagnosticWSnlabel.setForeground(COLOR_2);
		
		diagnosticWSname.setPreferredSize(new java.awt.Dimension(160, 22));
		diagnosticWSname.setMinimumSize(new java.awt.Dimension(160, 22));
		diagnosticWSname.setMaximumSize(new java.awt.Dimension(160, 22));
		//diagnosticWSname.setMargin(new java.awt.Insets(5, 0, 0, 0));
		diagnosticWSname.setFont(FONT);
		diagnosticWSname.setText(EventViewer.EventServiceName);
		
		diagnosticWSconnect.setFont(FONT);
		diagnosticWSconnect.addActionListener(this);
		
		//diagnosticWSoutput.setText("SYSTEM ERROR OUTPUT\n");
		diagnosticWSoutput.setBackground(COLOR_1);
		diagnosticWSoutput.setFont(FONT);
		//diagnosticWSoutput.setSelectionColor(COLOR_3);
		
		diagnosticWSoutput.setEditable(false);
		diagnosticWSoutputPane = new JScrollPane(diagnosticWSoutput);
		diagnosticWSoutputPane.setPreferredSize(new java.awt.Dimension(590, 160));
		diagnosticWSoutputPane.setMinimumSize(new java.awt.Dimension(590, 160));
		diagnosticWSoutputPane.setMaximumSize(new java.awt.Dimension(590, 160));
		//////////////////////////////////////////////////
		//												//
		//			ADD COMPONENTS TO PANELS //
		//												//
		//////////////////////////////////////////////////
		
		//components for error event panel
		sendErrorEvent.add(errorMsgLabel, new java.awt.GridBagConstraints(0, 0, 2,
				1, 1.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		sendErrorEvent.add(errorMsg, new java.awt.GridBagConstraints(0, 1, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		sendErrorEvent.add(errorSendMsg, new java.awt.GridBagConstraints(1, 1, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		sendErrorEvent.add(errorListScroller, new java.awt.GridBagConstraints(0, 2, 2, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		sendErrorEvent.add(errorWSlabel, new java.awt.GridBagConstraints(0, 4, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		sendErrorEvent.add(errorWShostname, new java.awt.GridBagConstraints(1, 4, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		sendErrorEvent.add(errorWSplabel, new java.awt.GridBagConstraints(0, 5, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		sendErrorEvent.add(errorWSport, new java.awt.GridBagConstraints(1, 5, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		sendErrorEvent.add(errorWSnlabel, new java.awt.GridBagConstraints(0, 6, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		sendErrorEvent.add(errorWSname, new java.awt.GridBagConstraints(1, 6, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		sendErrorEvent.add(errorWSconnect, new java.awt.GridBagConstraints(0, 7, 2, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		sendErrorEvent.add(errorWSoutputPane, new java.awt.GridBagConstraints(0, 8, 2, 
				1, 0.0, 1.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		
		//components for diagnostic event panel
		sendDiagnosticEvent.add(diagnosticMsgLabel, new java.awt.GridBagConstraints(0, 0, 2,
				1, 1.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		sendDiagnosticEvent.add(diagnosticMsg, new java.awt.GridBagConstraints(0, 1, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		sendDiagnosticEvent.add(diagnosticSendMsg, new java.awt.GridBagConstraints(1, 1, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		sendDiagnosticEvent.add(diagnosticListScroller, new java.awt.GridBagConstraints(0, 2, 2, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		sendDiagnosticEvent.add(diagnosticWSlabel, new java.awt.GridBagConstraints(0, 4, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		sendDiagnosticEvent.add(diagnosticWShostname, new java.awt.GridBagConstraints(1, 4, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		sendDiagnosticEvent.add(diagnosticWSplabel, new java.awt.GridBagConstraints(0, 5, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		sendDiagnosticEvent.add(diagnosticWSport, new java.awt.GridBagConstraints(1, 5, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		sendDiagnosticEvent.add(diagnosticWSnlabel, new java.awt.GridBagConstraints(0, 6, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		sendDiagnosticEvent.add(diagnosticWSname, new java.awt.GridBagConstraints(1, 6, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		sendDiagnosticEvent.add(diagnosticWSconnect, new java.awt.GridBagConstraints(0, 7, 2, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		sendDiagnosticEvent.add(diagnosticWSoutputPane, new java.awt.GridBagConstraints(0, 8, 2, 
				1, 0.0, 1.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		
		// ADD TABS TO TABBED PANEL
		if (imgURL != null) {
			mainTabbedPanel.addTab("Error Event_Old", new javax.swing.ImageIcon(
					imgURL), sendErrorEvent);
			mainTabbedPanel.addTab("Diagnostic Event_Old", new javax.swing.ImageIcon(
					imgURL), sendDiagnosticEvent);
			mainTabbedPanel.addTab("JChord Node Event_Old", new javax.swing.ImageIcon(
					imgURL), sendJChordNodeEvent);
		} else {
			mainTabbedPanel.addTab("Error Event_Old", sendErrorEvent);
			mainTabbedPanel.addTab("Diagnostic Event_Old", sendDiagnosticEvent);
			mainTabbedPanel.addTab("JChord Node Event_Old", sendJChordNodeEvent);
		}
		
		//ADD MAIN CONTAINERS TO THE FRAME
		getContentPane().add(mainTabbedPanel, java.awt.BorderLayout.CENTER);
		//getContentPane().add(mainMenuPanel, java.awt.BorderLayout.EAST);
		
		// DISPLAY GUI
		show();
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Create Error Event_Old")){
			String msg = errorMsg.getText();
			eg.sendErrorEvent(EventFactory.makeErrorEvent(errorMsg.getText()));
			if(msg.compareTo("")!=0){
				error_clm.addElementAtStart(msg);
			}
			errorMsg.setText("");
		}else if(e.getActionCommand().equals("Create Diagnostic Event_Old")){
			String msg = diagnosticMsg.getText();
			eg.sendDiagnosticEvent(EventFactory.makeDiagnosticEvent(diagnosticMsg.getText()));
			if(msg.compareTo("")!=0){
				diagnostic_clm.addElementAtStart(msg);
			}
			diagnosticMsg.setText("");
		}else if(e.getActionCommand().equals("Connect to ErrorEvent_WS")){
			String hostname=errorWShostname.getText();
			String portString=errorWSport.getText();
			String serviceName=errorWSname.getText();
//			if(serviceName==""&&serviceName==null)serviceName=EventViewer.ErrorEventServiceName;
			if(serviceName==""&&serviceName==null)serviceName=EventViewer.EventServiceName;
			errorWSoutput.append("ErrorEvent_WS service is "+hostname+":"+portString+"\t[\""+serviceName+"\"]\n");
			InetAddress host = null;
			try {
				host = InetAddress.getByName(hostname);
			} catch (UnknownHostException e1){
				errorWSoutput.append("Could not resolve address.\n\n");
				e1.printStackTrace();
				return;
			}
			int port;
			try {
				port=Integer.parseInt(portString);
			}catch(NumberFormatException e2){
				errorWSoutput.append("Not a valid port number.\n\n");
				e2.printStackTrace();
				return;
			}
			if(eg.setErrorEvent_WShost(host,port,serviceName)){
				errorWSoutput.append("CONNECTED\n\n");
				errorWSconnect.setEnabled(false);
				errorWShostname.setEnabled(false);
				errorWSport.setEnabled(false);
				errorWSname.setEnabled(false);
			}else{
				errorWSoutput.append("Failed to connect to specified ErrorEvent_WS \n\n");
			}
		}else if(e.getActionCommand().equals("Connect to DiagnosticEvent_WS")){
			String hostname=diagnosticWShostname.getText();
			String portString=diagnosticWSport.getText();
			String serviceName=diagnosticWSname.getText();
//			if(serviceName==""&&serviceName==null)serviceName=EventViewer.ErrorEventServiceName;
			if(serviceName==""&&serviceName==null)serviceName=EventViewer.EventServiceName;
			diagnosticWSoutput.append("DiagnosticEvent_WS service is "+hostname+":"+portString+"\t[\""+serviceName+"\"]\n");
			InetAddress host = null;
			try {
				host = InetAddress.getByName(hostname);
			} catch (UnknownHostException e1){
				diagnosticWSoutput.append("Could not resolve address.\n\n");
				e1.printStackTrace();
				return;
			}
			int port;
			try {
				port=Integer.parseInt(portString);
			}catch(NumberFormatException e2){
				diagnosticWSoutput.append("Not a valid port number.\n\n");
				e2.printStackTrace();
				return;
			}
			if(eg.setDiagnosticEvent_WShost(host,port,serviceName)){
				diagnosticWSoutput.append("CONNECTED\n\n");
				diagnosticWSconnect.setEnabled(false);
				diagnosticWShostname.setEnabled(false);
				diagnosticWSport.setEnabled(false);
				diagnosticWSname.setEnabled(false);
			}else{
				diagnosticWSoutput.append("Failed to connect to specified ErrorEvent_WS \n\n");
			}
		}
	}

	public static void main(String[] args) {
		EventBus bus = new EventBusImpl();
		Diagnostic.initialise(bus);
		Error.enableLocalErrorReporting();

		// Create new instances of these classes but don't need to do anything with them here.
		bus.register(new DiagnosticEventStringWriter());
		getInstance();
	}
}
