/*
 * Created on 13-Sep-2004
 */
package uk.ac.stand.dcs.asa.applications.store.test.gui;

import uk.ac.stand.dcs.asa.applications.eventHarness.CustomJListMouseListener;
import uk.ac.stand.dcs.asa.applications.eventHarness.CustomListModel;
import uk.ac.stand.dcs.asa.eventModel.eventBus.EventBusImpl;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.eventModel.eventBus.consumers.DiagnosticEventStringWriter;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.RemoteRRTRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * @author stuart
 */
public class LookupGUI extends JFrame implements ActionListener {
    
	private static LookupGUI instance;


	/* SET GUI FONT */
	public static int FONT_SIZE = 9;
	public static java.awt.Font FONT = new java.awt.Font("Verdana",
			java.awt.Font.PLAIN, FONT_SIZE);

	/* SET GUI COLORS */
	public static java.awt.Color COLOR_1 = new java.awt.Color(255, 255, 255);
	public static java.awt.Color COLOR_2 = new java.awt.Color(0, 0, 0);
	public static java.awt.Color COLOR_3 = new java.awt.Color(197, 176, 244);

	// SWING COMPONENTS
	private JTabbedPane mainTabbedPanel = new JTabbedPane();
	private JPanel lookupPanel;
	private JPanel datastorePanel;
	private JPanel diagnosticPanel;

	//components for lookup panel
	private JLabel lookupStringLabel = new JLabel("String for key generation");
	private JTextField lookupString = new JTextField();
	private JButton generateKey = new JButton("Generate Key");
	private JLabel keyLabel = new JLabel("Key");
	private JTextField keyAsString = new JTextField();
	private JButton lookupKey = new JButton("Lookup Node");
	
	private CustomListModel lookupString_clm = new CustomListModel(new Vector());
	private JList lookupStringList = new JList(lookupString_clm);
	private JScrollPane lookupStringScroller = new JScrollPane(lookupStringList);
	private JLabel lookupHostLabel = new JLabel("JChordAPI host:");
	private JTextField lookupHost = new JTextField();
	private JLabel lookupPortLabel = new JLabel("JChordAPI port:");
	private JTextField lookupPort = new JTextField();
	private JLabel lookupServiceNameLabel = new JLabel("JChordAPI service name:");
	private JTextField lookupServiceName = new JTextField();
	private JButton lookupConnect = new JButton("Connect to JChordAPI service");
	private JTextArea lookupOutput = new JTextArea();
	private JScrollPane lookupOutputPane;
	
	//components for lookup panel
	private JLabel datastoreStringLabel = new JLabel("String for key generation");
	private JTextField datastoreString = new JTextField();
	private JButton generateDSKey = new JButton("Generate Key");
	private JLabel DSkeyLabel = new JLabel("Key");
	private JTextField DSkeyAsString = new JTextField();
	private JButton storeKey = new JButton("Store Data");
	
	private CustomListModel datastoreString_clm = new CustomListModel(new Vector());
	private JList datastoreStringList = new JList(datastoreString_clm);
	private JScrollPane datastoreStringScroller = new JScrollPane(datastoreStringList);
	private JLabel datastoreHostLabel = new JLabel("JChordAPI host:");
	private JTextField datastoreHost = new JTextField();
	private JLabel datastorePortLabel = new JLabel("JChordAPI port:");
	private JTextField datastorePort = new JTextField();
	private JLabel datastoreServiceNameLabel = new JLabel("JChordAPI service name:");
	private JTextField datastoreServiceName = new JTextField();
	private JButton datastoreConnect = new JButton("Connect to JChordAPI service");
	private JTextArea datastoreOutput = new JTextArea();
	private JScrollPane datastoreOutputPane;
	private JTextArea dataString = new JTextArea();
	private JScrollPane dataStringPane;
	
	private LookupGUI() {
		initGUI();
	}

	/**
	 * @return The only instance of AdminGUI (Singleton pattern)
	 */
	public static LookupGUI getInstance() {
		if (instance == null) instance = new LookupGUI();
		return instance;
	}

	
	/**
	 * Initialise all GUI components and display it.
	 *
	 */
	public void initGUI() {
		java.net.URL imgURL = LookupGUI.class.getResource("/Pictures/icon_L.jpg");
		this.setSize(new Dimension(600, 600));
		setResizable(true);
		setBackground(COLOR_2);
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		setTitle("Lookup/Store Data");
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
		
		lookupPanel = new JPanel();
		lookupPanel.setBackground(COLOR_1);
		lookupPanel.setLayout(new java.awt.GridBagLayout());
		lookupPanel.setPreferredSize(new Dimension(600, 600));
		lookupPanel.setForeground(COLOR_2);
		
		datastorePanel = new JPanel();
		datastorePanel.setBackground(COLOR_1);
		datastorePanel.setLayout(new java.awt.GridBagLayout());
		datastorePanel.setPreferredSize(new Dimension(600, 600));
		datastorePanel.setForeground(COLOR_2);
		
		diagnosticPanel = new JPanel();
		diagnosticPanel.setBackground(COLOR_1);
		diagnosticPanel.setLayout(new java.awt.GridBagLayout());
		diagnosticPanel.setPreferredSize(new Dimension(600, 600));
		diagnosticPanel.setForeground(COLOR_2);

		//components for lookup panel
		lookupStringLabel.setFont(FONT);
		lookupStringLabel.setForeground(COLOR_2);
		
		lookupString.setPreferredSize(new java.awt.Dimension(160, 22));
		lookupString.setMinimumSize(new java.awt.Dimension(160, 22));
		lookupString.setMaximumSize(new java.awt.Dimension(160, 22));
		lookupString.setFont(FONT);
		
		generateKey.setFont(FONT);
		generateKey.addActionListener(this);
		
		keyLabel.setFont(FONT);
		keyLabel.setForeground(COLOR_2);
		
		keyAsString.setPreferredSize(new java.awt.Dimension(320, 22));
		keyAsString.setMinimumSize(new java.awt.Dimension(320, 22));
		keyAsString.setMaximumSize(new java.awt.Dimension(320, 22));
		keyAsString.setFont(FONT);
		
		lookupKey.setFont(FONT);
		lookupKey.addActionListener(this);
		
		lookupStringList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lookupStringList.setLayoutOrientation(JList.VERTICAL);
		lookupStringList.addMouseListener(new CustomJListMouseListener(lookupString,lookupStringList));
		lookupStringList.setFont(FONT);
		lookupStringScroller.setPreferredSize(new java.awt.Dimension(590, 160));
		lookupStringScroller.setMinimumSize(new java.awt.Dimension(590, 160));
		lookupStringScroller.setMaximumSize(new java.awt.Dimension(590, 160));
		
		lookupHostLabel.setFont(FONT);
		lookupHostLabel.setForeground(COLOR_2);
		
		lookupHost.setPreferredSize(new java.awt.Dimension(160, 22));
		lookupHost.setMinimumSize(new java.awt.Dimension(160, 22));
		lookupHost.setMaximumSize(new java.awt.Dimension(160, 22));
		//errorWShostname.setMargin(new java.awt.Insets(5, 0, 0, 0));
		lookupHost.setFont(FONT);
		
		lookupPortLabel.setFont(FONT);
		lookupPortLabel.setForeground(COLOR_2);
		
		lookupPort.setPreferredSize(new java.awt.Dimension(160, 22));
		lookupPort.setMinimumSize(new java.awt.Dimension(160, 22));
		lookupPort.setMaximumSize(new java.awt.Dimension(160, 22));
		//errorWSport.setMargin(new java.awt.Insets(5, 0, 0, 0));
		lookupPort.setFont(FONT);
		//errorWSport.setText();
		
		lookupServiceNameLabel.setFont(FONT);
		lookupServiceNameLabel.setForeground(COLOR_2);
		
		lookupServiceName.setPreferredSize(new java.awt.Dimension(160, 22));
		lookupServiceName.setMinimumSize(new java.awt.Dimension(160, 22));
		lookupServiceName.setMaximumSize(new java.awt.Dimension(160, 22));
		//errorWSname.setMargin(new java.awt.Insets(5, 0, 0, 0));
		lookupServiceName.setFont(FONT);
		//errorWSname.setText();
		
		lookupConnect.setFont(FONT);
		lookupConnect.addActionListener(this);
		
		//errorWSoutput.setText("SYSTEM ERROR OUTPUT\n");
		lookupOutput.setBackground(COLOR_1);
		lookupOutput.setFont(FONT);
		//errorWSoutput.setSelectionColor(COLOR_3);
		
		lookupOutput.setEditable(false);
		lookupOutputPane = new JScrollPane(lookupOutput);
		lookupOutputPane.setPreferredSize(new java.awt.Dimension(590, 160));
		lookupOutputPane.setMinimumSize(new java.awt.Dimension(590, 160));
		lookupOutputPane.setMaximumSize(new java.awt.Dimension(590, 160));
		
//		components for datastore panel
		datastoreStringLabel.setFont(FONT);
		datastoreStringLabel.setForeground(COLOR_2);
		
		datastoreString.setPreferredSize(new java.awt.Dimension(160, 22));
		datastoreString.setMinimumSize(new java.awt.Dimension(160, 22));
		datastoreString.setMaximumSize(new java.awt.Dimension(160, 22));
		datastoreString.setFont(FONT);
		
		generateDSKey.setFont(FONT);
		generateDSKey.addActionListener(this);
		
		DSkeyLabel.setFont(FONT);
		DSkeyLabel.setForeground(COLOR_2);
		
		DSkeyAsString.setPreferredSize(new java.awt.Dimension(320, 22));
		DSkeyAsString.setMinimumSize(new java.awt.Dimension(320, 22));
		DSkeyAsString.setMaximumSize(new java.awt.Dimension(320, 22));
		DSkeyAsString.setFont(FONT);
		
		storeKey.setFont(FONT);
		storeKey.addActionListener(this);
		
		datastoreStringList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		datastoreStringList.setLayoutOrientation(JList.VERTICAL);
		datastoreStringList.addMouseListener(new CustomJListMouseListener(datastoreString,datastoreStringList));
		datastoreStringList.setFont(FONT);
		datastoreStringScroller.setPreferredSize(new java.awt.Dimension(590, 160));
		datastoreStringScroller.setMinimumSize(new java.awt.Dimension(590, 160));
		datastoreStringScroller.setMaximumSize(new java.awt.Dimension(590, 160));
		
		datastoreHostLabel.setFont(FONT);
		datastoreHostLabel.setForeground(COLOR_2);
		
		datastoreHost.setPreferredSize(new java.awt.Dimension(160, 22));
		datastoreHost.setMinimumSize(new java.awt.Dimension(160, 22));
		datastoreHost.setMaximumSize(new java.awt.Dimension(160, 22));
		//errorWShostname.setMargin(new java.awt.Insets(5, 0, 0, 0));
		datastoreHost.setFont(FONT);
		
		datastorePortLabel.setFont(FONT);
		datastorePortLabel.setForeground(COLOR_2);
		
		datastorePort.setPreferredSize(new java.awt.Dimension(160, 22));
		datastorePort.setMinimumSize(new java.awt.Dimension(160, 22));
		datastorePort.setMaximumSize(new java.awt.Dimension(160, 22));
		//errorWSport.setMargin(new java.awt.Insets(5, 0, 0, 0));
		datastorePort.setFont(FONT);
		//errorWSport.setText();
		
		datastoreServiceNameLabel.setFont(FONT);
		datastoreServiceNameLabel.setForeground(COLOR_2);
		
		datastoreServiceName.setPreferredSize(new java.awt.Dimension(160, 22));
		datastoreServiceName.setMinimumSize(new java.awt.Dimension(160, 22));
		datastoreServiceName.setMaximumSize(new java.awt.Dimension(160, 22));
		//errorWSname.setMargin(new java.awt.Insets(5, 0, 0, 0));
		datastoreServiceName.setFont(FONT);
		//errorWSname.setText();
		
		datastoreConnect.setFont(FONT);
		datastoreConnect.addActionListener(this);
		
		//errorWSoutput.setText("SYSTEM ERROR OUTPUT\n");
		datastoreOutput.setBackground(COLOR_1);
		datastoreOutput.setFont(FONT);
		//errorWSoutput.setSelectionColor(COLOR_3);
		
		datastoreOutput.setEditable(false);
		datastoreOutputPane = new JScrollPane(datastoreOutput);
		datastoreOutputPane.setPreferredSize(new java.awt.Dimension(590, 160));
		datastoreOutputPane.setMinimumSize(new java.awt.Dimension(590, 160));
		datastoreOutputPane.setMaximumSize(new java.awt.Dimension(590, 160));

		dataString.setEditable(true);
		dataStringPane = new JScrollPane(datastoreOutput);
		dataStringPane.setPreferredSize(new java.awt.Dimension(200, 160));
		dataStringPane.setMinimumSize(new java.awt.Dimension(200, 160));
		dataStringPane.setMaximumSize(new java.awt.Dimension(200, 160));
		
		//////////////////////////////////////////////////
		//												//
		//			ADD COMPONENTS TO PANELS //
		//												//
		//////////////////////////////////////////////////
		
		//components for error event panel
		lookupPanel.add(lookupStringLabel, new java.awt.GridBagConstraints(0, 0, 2,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));
		lookupPanel.add(lookupString, new java.awt.GridBagConstraints(0, 1, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));
		lookupPanel.add(generateKey, new java.awt.GridBagConstraints(1, 1, 1, 
				1, 1.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));
		
		lookupPanel.add(keyLabel, new java.awt.GridBagConstraints(0, 2, 2,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));
		lookupPanel.add(keyAsString, new java.awt.GridBagConstraints(0, 3, 2,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));
		lookupPanel.add(lookupKey, new java.awt.GridBagConstraints(0, 4, 1,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));		
		
		lookupPanel.add(lookupStringScroller, new java.awt.GridBagConstraints(0, 5, 2, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		lookupPanel.add(lookupHostLabel, new java.awt.GridBagConstraints(0, 6, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		lookupPanel.add(lookupHost, new java.awt.GridBagConstraints(1, 6, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		lookupPanel.add(lookupPortLabel, new java.awt.GridBagConstraints(0, 7, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		lookupPanel.add(lookupPort, new java.awt.GridBagConstraints(1, 7, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		lookupPanel.add(lookupServiceNameLabel, new java.awt.GridBagConstraints(0, 8, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		lookupPanel.add(lookupServiceName, new java.awt.GridBagConstraints(1, 8, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		lookupPanel.add(lookupConnect, new java.awt.GridBagConstraints(0, 9, 2, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		lookupPanel.add(lookupOutputPane, new java.awt.GridBagConstraints(0, 10, 2, 
				1, 0.0, 1.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 5,
						5), 0, 0));
		
		//components for datastore panel
		datastorePanel.add(datastoreStringLabel, new java.awt.GridBagConstraints(0, 0, 2,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));
		datastorePanel.add(datastoreString, new java.awt.GridBagConstraints(0, 1, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));
		datastorePanel.add(generateDSKey, new java.awt.GridBagConstraints(1, 1, 1, 
				1, 1.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));
		datastorePanel.add(dataStringPane, new java.awt.GridBagConstraints(2, 0, 1, 
				5, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				5), 0, 0));
		datastorePanel.add(DSkeyLabel, new java.awt.GridBagConstraints(0, 2, 2,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));
		datastorePanel.add(DSkeyAsString, new java.awt.GridBagConstraints(0, 3, 3,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));
		datastorePanel.add(storeKey, new java.awt.GridBagConstraints(0, 4, 1,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
				0), 0, 0));		
		
		datastorePanel.add(datastoreStringScroller, new java.awt.GridBagConstraints(0, 5, 3, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		datastorePanel.add(datastoreHostLabel, new java.awt.GridBagConstraints(0, 6, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		datastorePanel.add(datastoreHost, new java.awt.GridBagConstraints(1, 6, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		datastorePanel.add(datastorePortLabel, new java.awt.GridBagConstraints(0, 7, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		datastorePanel.add(datastorePort, new java.awt.GridBagConstraints(1, 7, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		datastorePanel.add(datastoreServiceNameLabel, new java.awt.GridBagConstraints(0, 8, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						5), 0, 0));
		datastorePanel.add(datastoreServiceName, new java.awt.GridBagConstraints(1, 8, 1, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						0), 0, 0));
		datastorePanel.add(datastoreConnect, new java.awt.GridBagConstraints(0, 9, 2, 
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		datastorePanel.add(datastoreOutputPane, new java.awt.GridBagConstraints(0, 10, 3, 
				1, 0.0, 1.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 5,
						5), 0, 0));
		
		// ADD TABS TO TABBED PANEL
		if (imgURL != null) {
			mainTabbedPanel.addTab("Lookup Node", new javax.swing.ImageIcon(
					imgURL), lookupPanel);
			mainTabbedPanel.addTab("Publish Data", new javax.swing.ImageIcon(
					imgURL), datastorePanel);
			mainTabbedPanel.addTab("Diagnostics", new javax.swing.ImageIcon(
					imgURL), diagnosticPanel);
		}
		
		//ADD MAIN CONTAINERS TO THE FRAME
		getContentPane().add(mainTabbedPanel, java.awt.BorderLayout.CENTER);
		//getContentPane().add(mainMenuPanel, java.awt.BorderLayout.EAST);
		
		// DISPLAY GUI
		show();
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Connect to JChordAPI service")){
			System.out.println("Connect");
			
			String hostname=this.lookupHost.getText();
			String portString=this.lookupPort.getText();
			String serviceName=this.lookupServiceName.getText();
			if(serviceName==""&&serviceName==null)serviceName = RemoteRRTRegistry.getInstance().getServiceName(IP2PNode.class);
			this.lookupOutput.append("JChordAPI service is "+hostname+":"+portString+"\t[\""+serviceName+"\"]\n");


			try {
				InetAddress.getByName(hostname);
			} catch (UnknownHostException e1){
				this.lookupOutput.append("Could not resolve address: " + hostname + ".\n\n");
				e1.printStackTrace();
				return;
			}

			try {
				Integer.parseInt(portString);
			}catch(NumberFormatException e2){
				this.lookupOutput.append("Invalid port number: " + portString + ".\n\n");
				e2.printStackTrace();
				return;
			}
			
			// Spurious code...
//			try{
//			    
//			}catch(Exception e3){
//				e3.printStackTrace();
//				this.lookupOutput.append("Failed to connect to specified JChordAPI\n\n");
//			}
			this.lookupOutput.append("CONNECTED\n\n");
			this.lookupConnect.setEnabled(false);
			this.lookupHost.setEnabled(false);
			this.lookupPort.setEnabled(false);
			this.lookupServiceName.setEnabled(false);
		}
		
	    // TODO obsolete code?

//			else if(e.getActionCommand().equals("Create Diagnostic Event_Old")){
//			String msg = diagnosticMsg.getText();
//			eg.sendDiagnosticEvent(new DiagnosticEvent(diagnosticMsg.getText()));
//			if(msg.compareTo("")!=0){
//				diagnostic_clm.addElementAtStart(msg);
//			}
//			diagnosticMsg.setText("");
//		}else if(e.getActionCommand().equals("Connect to ErrorEvent_WS")){
//			String hostname=errorWShostname.getText();
//			String portString=errorWSport.getText();
//			String serviceName=errorWSname.getText();
//			if(serviceName==""&&serviceName==null)serviceName=EventViewer.EventServiceName;
//			errorWSoutput.append("ErrorEvent_WS service is "+hostname+":"+portString+"\t[\""+serviceName+"\"]\n");
//			InetAddress host = null;
//			try {
//				host = InetAddress.getByName(hostname);
//			} catch (UnknownHostException e1){
//				errorWSoutput.append("Could not resolve address.\n\n");
//				e1.printStackTrace();
//				return;
//			}
//			int port;
//			try {
//				port=Integer.parseInt(portString);
//			}catch(NumberFormatException e2){
//				errorWSoutput.append("Not a valid port number.\n\n");
//				e2.printStackTrace();
//				return;
//			}
//			if(eg.setErrorEvent_WShost(host,port,serviceName)){
//				errorWSoutput.append("CONNECTED\n\n");
//				errorWSconnect.setEnabled(false);
//				errorWShostname.setEnabled(false);
//				errorWSport.setEnabled(false);
//				errorWSname.setEnabled(false);
//			}else{
//				errorWSoutput.append("Failed to connect to specified ErrorEvent_WS \n\n");
//			}
//		}else if(e.getActionCommand().equals("Connect to DiagnosticEvent_WS")){
//			String hostname=diagnosticWShostname.getText();
//			String portString=diagnosticWSport.getText();
//			String serviceName=diagnosticWSname.getText();
//			if(serviceName==""&&serviceName==null)serviceName=EventViewer.EventServiceName;
//			diagnosticWSoutput.append("DiagnosticEvent_WS service is "+hostname+":"+portString+"\t[\""+serviceName+"\"]\n");
//			InetAddress host = null;
//			try {
//				host = InetAddress.getByName(hostname);
//			} catch (UnknownHostException e1){
//				diagnosticWSoutput.append("Could not resolve address.\n\n");
//				e1.printStackTrace();
//				return;
//			}
//			int port;
//			try {
//				port=Integer.parseInt(portString);
//			}catch(NumberFormatException e2){
//				diagnosticWSoutput.append("Not a valid port number.\n\n");
//				e2.printStackTrace();
//				return;
//			}
//			if(eg.setDiagnosticEvent_WShost(host,port,serviceName)){
//				diagnosticWSoutput.append("CONNECTED\n\n");
//				diagnosticWSconnect.setEnabled(false);
//				diagnosticWShostname.setEnabled(false);
//				diagnosticWSport.setEnabled(false);
//				diagnosticWSname.setEnabled(false);
//			}else{
//				diagnosticWSoutput.append("Failed to connect to specified ErrorEvent_WS \n\n");
//			}
//		}
	}

	public static void main(String[] args) {
		EventBus bus = new EventBusImpl();
		Diagnostic.initialise(bus);
		Error.enableLocalErrorReporting();
		
		// TODO obsolete code?
		
//		Error.initialise(bus);
//		ErrorEventStringWriterAdapter myErrorWriter = new ErrorEventStringWriterAdapter("myErrorWriter",bus);
		
		// Create new instance of this class but don't need to do anything with it here.
		bus.register(new DiagnosticEventStringWriter());
		LookupGUI.getInstance();
	}
	
}
