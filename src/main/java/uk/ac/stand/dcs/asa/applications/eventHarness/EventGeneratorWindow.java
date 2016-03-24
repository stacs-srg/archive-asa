/*
 * Created on 09-Aug-2004
 */
package uk.ac.stand.dcs.asa.applications.eventHarness;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author stuart, graham
 */
public class EventGeneratorWindow extends JFrame implements ActionListener{

	private int FONT_SIZE = 9;

	private java.awt.Font FONT = new java.awt.Font("Verdana",
			java.awt.Font.PLAIN, FONT_SIZE);

	/* SET GUI COLORS */
	private java.awt.Color COLOR_1 = new java.awt.Color(255, 255, 255);

	private java.awt.Color COLOR_2 = new java.awt.Color(0, 0, 0);

	/**
	 * 
	 * @uml.property name="mainMenuPanel"
	 * @uml.associationEnd 
	 * @uml.property name="mainMenuPanel" multiplicity="(1 1)"
	 */
	public JPanelListener mainMenuPanel;

	private Image background;

	public void actionPerformed(ActionEvent arg0) {
        
        // No action
	}

	public void initGUI() {
		java.net.URL imgURL = EventGeneratorGUI.class.getResource("/Pictures/icon3.jpg");
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
		java.net.URL backgroundURL = EventGeneratorGUI.class
				.getResource("/Pictures/background.jpg");
		background = new ImageIcon(backgroundURL).getImage();
		mainMenuPanel = new JPanelListener(background);

		mainMenuPanel.setBackground(COLOR_1);
		mainMenuPanel.setLayout(new java.awt.GridBagLayout());
		mainMenuPanel.setPreferredSize(new Dimension(100, 600));
		mainMenuPanel.setForeground(COLOR_2);

		// TABBED PANEL AND COMPONENTS
/*		mainTabbedPanel.setSize(700, 675);
		mainTabbedPanel.setBackground(COLOR_3);
		mainTabbedPanel.setForeground(COLOR_2);
		mainTabbedPanel.setFont(FONT);
		
		jsp = new JScrollPane();
		jsp.setPreferredSize(new java.awt.Dimension(200, 200));
		jsp.setMinimumSize(new java.awt.Dimension(200, 500));
		jsp.setFont(FONT);
		jsp.setBackground(COLOR_1);
		// SET SYSTEM STATE PREFERENCES
		systemState.setPreferredSize(new java.awt.Dimension(700, 600));
		systemState.setMinimumSize(new java.awt.Dimension(700, 600));
		systemState.setMaximumSize(new java.awt.Dimension(700, 600));
		systemState.setBackground(COLOR_1);
		systemState.setFont(FONT);
		systemState.setLayout(new java.awt.BorderLayout());
		// ADD COMPONENTS TO SYSTEM STATE
		systemState.add(jsp, java.awt.BorderLayout.CENTER);


		// NODE PROPERTIES PANEL AND COMPONENTS
		ipChoice.setSize(new java.awt.Dimension(100, 25));
		ipChoice.setFont(FONT);
		ipChoice.setBackground(COLOR_1);
		ipChoice.setSize(new java.awt.Dimension(160, 25));

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

		//////////////////////////////////////////////////
		//												//
		//			ADD COMPONENTS TO PANELS //
		//												//
		//////////////////////////////////////////////////
		
		// ADD TABS TO TABBED PANEL
		if (imgURL != null) {
			mainTabbedPanel.addTab("SYSTEM STATUS", new javax.swing.ImageIcon(
					imgURL), systemState);
			mainTabbedPanel.addTab("DIAGNOSTICS", new javax.swing.ImageIcon(
					imgURL), diagnosticsTextPane);
			mainTabbedPanel.addTab("NODE ERRORS", new javax.swing.ImageIcon(
					imgURL), errorTextPane);
		} else {
			mainTabbedPanel.addTab("SYSTEM STATUS", systemState);
			mainTabbedPanel.addTab("DIAGNOSTICS", diagnosticsTextPane);
			mainTabbedPanel.addTab("NODE ERRORS", errorTextPane);
		}
		
		//ADD MAIN CONTAINERS TO THE FRAME
		getContentPane().add(mainTabbedPanel, java.awt.BorderLayout.CENTER);*/
		getContentPane().add(mainMenuPanel, java.awt.BorderLayout.EAST);
		
		// DISPLAY GUI
		show();
	}
	
	public EventGeneratorWindow(){
		initGUI();
	}
	
	public static void main(String[] args) {

		// Create new instance of this class but don't need to do anything with it here.
	    new EventGeneratorWindow();
	}
}
