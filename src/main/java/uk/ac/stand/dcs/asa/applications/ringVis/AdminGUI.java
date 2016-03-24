package uk.ac.stand.dcs.asa.applications.ringVis;

import org.jgraph.JGraph;
import uk.ac.stand.dcs.asa.interfaces.IFailureSuspector;
import uk.ac.stand.dcs.asa.util.TimeoutFailureSuspector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;

public class AdminGUI extends JFrame implements ActionListener {

	private static final String BACKGROUND_IMAGE_PATH = "/Pictures/background.jpg";
    private static final String WINDOW_TITLE = "JCHORD ADMINISTRATION";
    private static final String ICON_PATH = "/Pictures/icon2.jpg";
    private static final int WINDOW_HEIGHT = 700;
    private static final int WINDOW_WIDTH = 900;
    
    private JChordGraphLayout gl;
	private JGraph graph;

	/* SET GUI FONT */
	private int FONT_SIZE = 9;
	private Font FONT = new Font("Verdana", Font.PLAIN, FONT_SIZE);

	/* SET GUI COLORS */
	private Color COLOR_1 = new Color(255, 255, 255);
	private Color COLOR_2 = new Color(0, 0, 0);
	private Color COLOR_3 = new Color(212, 208, 200);

	// SWING COMPONENTS
	private JTabbedPane mainTabbedPanel = new JTabbedPane();
	private JScrollPane jsp;
	private JScrollPane diagnosticsTextPane;
	private JScrollPane changeTextPane;
	private JScrollPane errorTextPane;
	private JComboBox ipChoice = new JComboBox();
	
	private JTextField keyChoice = new JTextField();
	private JLabel keyLabel = new JLabel("Key:");
	private JTextField IPChoice = new JTextField();
	private JLabel IPLabel = new JLabel("IP:");
	private JTextField portChoice = new JTextField();
	private JLabel portLabel = new JLabel("Port:");
	
	private JTextField IPVis = new JTextField();
	private JLabel IPVisLabel = new JLabel("Visualiser IP:");
	private JTextField portVis= new JTextField();
	private JLabel portVisLabel = new JLabel("Visualiser Port:");
	
	private JPanel systemState = new JPanel();
	private JPanel zoomMenu = new JPanel();
	private JPanelListener mainMenuPanel;
	private JTextArea diagnosticsText = new JTextArea();
	private JTextArea errorText = new JTextArea();
	private JTextArea changeText = new JTextArea();
	private JButton zoomIn = new JButton("ZOOM IN");
	private JButton zoomOut = new JButton("ZOOM OUT");

	private Image background;
    private JChordCellDirectory cell_directory;
    private JChordGraphModel graph_model;
    private IFailureSuspector failure_suspector;

	public AdminGUI(InetSocketAddress localAddress) {
	    
	    graph_model = new JChordGraphModel();
		graph = graph_model.getGraph();
		
		cell_directory = new JChordCellDirectory();
		gl = new JChordGraphLayout(this);
		failure_suspector = new TimeoutFailureSuspector();

		// Initialise all GUI components and display it.

		URL imgURL = AdminGUI.class.getResource(ICON_PATH);
		setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setResizable(true);
		setBackground(COLOR_2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(WINDOW_TITLE);
		setFont(FONT);
		setForeground(COLOR_1);
		setIconImage((new ImageIcon(imgURL)).getImage());
		
		// MouseListener that Prints the Cell on Doubleclick
		/* START: Code from http://www.jgraph.com/doc/tutorial/t1.html */
		graph.addMouseListener(new MouseAdapter() {
		    
			public void mousePressed(MouseEvent e) {
			    
				if (e.getClickCount() == 1) {
				    
					// Get Cell under pointer
					int x = e.getX(), y = e.getY();
					Object cell = graph.getFirstCellForLocation(x, y);
					
					// Display Node Key
					if (cell != null) {
					    
						String cellIDString = graph.convertValueToString(cell);
						
						//lookup JChordNodeCell
						JChordNodeCell jnc = cell_directory.lookup(cellIDString);
						
						//display key in 'keyChoice' text field
						keyChoice.setText(jnc.getNode_key().toString());
						
						//display IP address in 'IPChoice' text field
						IPChoice.setText(jnc.getHost().getInet_sock_addr().getAddress().getHostName());
						
						//display port in 'portChoice' text field
						portChoice.setText(Integer.toString(jnc.getHost().getInet_sock_addr().getPort()));
					}
				}
			}
		});
		/* END: Code from http://www.jgraph.com/doc/tutorial/t1.html */
		
		// MAIN MENU PANEL AND COMPONENTS
		URL backgroundURL = AdminGUI.class.getResource(BACKGROUND_IMAGE_PATH);
		background = new ImageIcon(backgroundURL).getImage();
		mainMenuPanel = new JPanelListener(background);

		mainMenuPanel.setBackground(COLOR_1);
		mainMenuPanel.setLayout(new java.awt.GridBagLayout());
		mainMenuPanel.setPreferredSize(new Dimension(100, 600));
		mainMenuPanel.setForeground(COLOR_1);

		// TABBED PANEL AND COMPONENTS
		mainTabbedPanel.setSize(700, 600);
		mainTabbedPanel.setBackground(COLOR_3);
		mainTabbedPanel.setForeground(COLOR_2);
		mainTabbedPanel.setFont(FONT);
		
		// STABILISE SYSTEM PANEL AND COMPONENTS
		// zoom menu and components
		zoomMenu.setSize(new Dimension(800, 100));
		zoomMenu.setPreferredSize(new Dimension(800, 100));
		zoomMenu.setMinimumSize(new Dimension(400, 100));
		zoomMenu.setMaximumSize(new Dimension(800, 100));
		zoomMenu.setLayout(new java.awt.GridBagLayout());
		
		zoomIn.setPreferredSize(new Dimension(120, 20));
		zoomIn.setMinimumSize(new Dimension(120, 20));
		zoomIn.setMaximumSize(new Dimension(120, 20));
		zoomIn.setFont(FONT);
		zoomIn.setForeground(COLOR_2);
		
		zoomOut.setPreferredSize(new Dimension(120, 20));
		zoomOut.setMinimumSize(new Dimension(120, 20));
		zoomOut.setMaximumSize(new Dimension(120, 20));
		zoomOut.setFont(FONT);
		zoomOut.setForeground(COLOR_2);
		
		// scrollpane to hold jgraph
		jsp = new JScrollPane(graph);
		jsp.setPreferredSize(new Dimension(800, 500));
		jsp.setMinimumSize(new Dimension(800, 500));
		jsp.setFont(FONT);
		jsp.setBackground(COLOR_1);
		
		// SET SYSTEM STATE PREFERENCES
		systemState.setPreferredSize(new Dimension(800, 600));
		systemState.setMinimumSize(new Dimension(800, 600));
		systemState.setMaximumSize(new Dimension(800, 600));
		systemState.setBackground(COLOR_1);
		systemState.setFont(FONT);
		systemState.setLayout(new java.awt.BorderLayout());
		
		// ADD COMPONENTS TO SYSTEM STATE
		systemState.add(jsp, java.awt.BorderLayout.CENTER);
		systemState.add(zoomMenu, java.awt.BorderLayout.SOUTH);
		
		// NODE PROPERTIES PANEL AND COMPONENTS
		ipChoice.setSize(new Dimension(100, 25));
		ipChoice.setFont(FONT);
		ipChoice.setBackground(COLOR_1);
		ipChoice.setSize(new Dimension(160, 25));
		
		IPLabel.setFont(FONT);
		IPLabel.setForeground(COLOR_2);
		
		// KEY CHOICE TEXT FIELD PROPERTIES
		keyChoice.setPreferredSize(new Dimension(400, 22));
		keyChoice.setMinimumSize(new Dimension(400, 22));
		keyChoice.setMaximumSize(new Dimension(400, 22));
		keyChoice.setFont(FONT);
		
		keyLabel.setFont(FONT);
		keyLabel.setForeground(COLOR_2);
		
		// IP CHOICE TEXT FIELD PROPERTIES
		IPChoice.setPreferredSize(new Dimension(200, 22));
		IPChoice.setMinimumSize(new Dimension(200, 22));
		IPChoice.setMaximumSize(new Dimension(200, 22));
		IPChoice.setFont(FONT);
		
		
		// PORT CHOICE TEXT FIELD PROPERTIES
		portChoice.setPreferredSize(new Dimension(200, 22));
		portChoice.setMinimumSize(new Dimension(200, 22));
		portChoice.setMaximumSize(new Dimension(200, 22));
		portChoice.setFont(FONT);
		
		portLabel.setFont(FONT);
		portLabel.setForeground(COLOR_2);
		
		
		IPVis.setSize(new Dimension(100, 25));
		IPVis.setFont(FONT);
		IPVis.setBackground(COLOR_1);
		IPVis.setPreferredSize(new Dimension(200, 22));
		IPVis.setMinimumSize(new Dimension(200, 22));
		IPVis.setMaximumSize(new Dimension(200, 22));
		IPVis.setEditable(false);
		
		IPVisLabel.setFont(FONT);
		IPVisLabel.setForeground(COLOR_2);
		
		portVis.setSize(new Dimension(100, 25));
		portVis.setFont(FONT);
		portVis.setBackground(COLOR_1);
		portVis.setPreferredSize(new Dimension(200, 22));
		portVis.setMinimumSize(new Dimension(200, 22));
		portVis.setMaximumSize(new Dimension(200, 22));
		portVis.setEditable(false);
		
		portVisLabel.setFont(FONT);
		portVisLabel.setForeground(COLOR_2);
		
		// DIAGNOSTICS OUTPUT PANEL AND COMPONENTS
		diagnosticsText.setText("SYSTEM DIAGNOSTIC OUTPUT\n");
		diagnosticsText.setBackground(COLOR_1);
		diagnosticsText.setFont(FONT);
		diagnosticsText.setSelectionColor(COLOR_3);
		diagnosticsTextPane = new JScrollPane(diagnosticsText);

		// ERROR OUTPUT PANEL AND COMPONENTS
		errorText.setText("SYSTEM ERROR OUTPUT\n");
		errorText.setBackground(COLOR_1);
		errorText.setFont(FONT);
		errorText.setSelectionColor(COLOR_3);
		errorTextPane = new JScrollPane(errorText);

		// NODE CHANGES OUTPUT PANEL AND COMPONENTS
		changeText.setText("SYSTEM CHANGES OUTPUT\n");
		changeText.setBackground(COLOR_1);
		changeText.setFont(FONT);
		changeText.setSelectionColor(COLOR_3);
		changeTextPane = new JScrollPane(changeText);

		//graph.setDisconnectable(false);
		//graph.setMoveable(false);
		//graph.setEditable(false);
	
		//////////////////////////////////////////////////
		//												//
		//		GIVE ACTIONS TO BUTTONS					//
		//												//
		//////////////////////////////////////////////////
		zoomIn.addActionListener( this );
		zoomOut.addActionListener( this );

		//////////////////////////////////////////////////
		//												//
		//			ADD COMPONENTS TO PANELS //
		//												//
		//////////////////////////////////////////////////
		//		 ADD COMPONENTS TO MENU HOLDING ZOOM BUTTONS ( UNDER THE MAIN SYSTEM
		// DISPLAY ) IN THE SYSTEM STATUS TAB
		zoomMenu.add(zoomIn, new GridBagConstraints(0, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		zoomMenu.add(zoomOut, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		zoomMenu.add(keyLabel, new GridBagConstraints(2, 0, 1, 1, 1.0,
				0.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						5), 0, 0));
		zoomMenu.add(keyChoice, new GridBagConstraints(3, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						5), 0, 0));
	
		zoomMenu.add(IPVisLabel, new GridBagConstraints(0, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						5), 0, 0));
		zoomMenu.add(IPVis, new GridBagConstraints(1, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						5), 0, 0));
	
		zoomMenu.add(IPLabel, new GridBagConstraints(2, 1, 1, 1, 1.0,
				0.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						5), 0, 0));
		zoomMenu.add(IPChoice, new GridBagConstraints(3, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						5), 0, 0));
		
		zoomMenu.add(portVisLabel, new GridBagConstraints(0, 2, 1, 1, 0.0,
				1.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						5), 0, 0));
		zoomMenu.add(portVis, new GridBagConstraints(1, 2, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						5), 0, 0));
		
		zoomMenu.add(portLabel, new GridBagConstraints(2, 2, 1, 1, 1.0,
				1.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						5), 0, 0));
		zoomMenu.add(portChoice, new GridBagConstraints(3, 2, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 0, 0,
						5), 0, 0));
		
		
		// ADD TABS TO TABBED PANEL
		if (imgURL != null) {
			mainTabbedPanel.addTab("SYSTEM STATUS", new ImageIcon(imgURL), systemState);
			mainTabbedPanel.addTab("DIAGNOSTICS",   new ImageIcon(imgURL), diagnosticsTextPane);
			mainTabbedPanel.addTab("NODE CHANGES",  new ImageIcon(imgURL), changeTextPane);
			mainTabbedPanel.addTab("NODE ERRORS",  new ImageIcon(imgURL), errorTextPane);
		} else {
			mainTabbedPanel.addTab("SYSTEM STATUS", systemState);
			mainTabbedPanel.addTab("DIAGNOSTICS", diagnosticsTextPane);
			mainTabbedPanel.addTab("NODE CHANGES", changeTextPane);
			mainTabbedPanel.addTab("NODE ERRORS", errorTextPane);
		}
		
		//ADD MAIN CONTAINERS TO THE FRAME
		getContentPane().add(mainTabbedPanel, BorderLayout.CENTER);
		getContentPane().add(mainMenuPanel, BorderLayout.WEST);
		
		if(localAddress!=null){
		    setVisualiserIPDisplay(localAddress.getAddress());
		    setVisualiserPortDisplay(localAddress.getPort());
		}
		// DISPLAY GUI
		show();
	}

	public void actionPerformed(ActionEvent e) {
	    
		if (e.equals("ZOOM IN")) zoomIn();
		else if (e.equals("ZOOM OUT")) zoomOut();
	}
	
	/**
	 * Lays out the current cells in a circle
	 */
	public void layoutCells() {
	    
		gl.render();
		show();
	}

	/**
	 * Increases the current size of the image of the graph by a factor of 2
	 * using JGraph.setScale
	 */
	private void zoomIn() {
		graph.setScale(graph.getScale() * 2);
	}

	/**
	 * Decreases the current size of the image of the graph by a factor of 2
	 * using JGraph.setScale
	 */
	private void zoomOut() {
		graph.setScale(graph.getScale() / 2);
	}
	
	/**
	 * Add a Diagnostic message received from a node in the system to the 
	 * diagnostics textarea
	 * @param msg The message received from a node in the system
	 */
	public void addDiagnostics(String msg) {
		diagnosticsText.append(msg + "\n");
	}

	/**
	 * Add an Error message received from a node in the system to the 
	 * error textarea
	 * @param msg The message received from a node in the system
	 */
	public void addError(String msg) {
		errorText.append(msg + "\n");
	}

	/**
	 * Add a NodeImplChanged message received from a node in the system to the 
	 * changes textarea
	 * @param msg The message received from a node in the system
	 */
	public void addChange(String msg) {
		changeText.append(msg + "\n");
	}

    public JChordCellDirectory getCellDirectory() {

        return cell_directory;
    }
    
    public JChordGraphModel getGraphModel() {
        
        return graph_model;
    }

    public IFailureSuspector getFailureSuspector() {

        return failure_suspector;
    }
    
    public void setVisualiserPortDisplay(int port){
        this.portVis.setText(Integer.toString(port));
    }
    public void setVisualiserIPDisplay(InetAddress IP){
        this.IPVis.setText(IP.getHostAddress());
    }
}