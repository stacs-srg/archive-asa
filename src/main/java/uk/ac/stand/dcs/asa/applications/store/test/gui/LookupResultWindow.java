/*
 * Created on 13-Sep-2004
 */
package uk.ac.stand.dcs.asa.applications.store.test.gui;

import uk.ac.stand.dcs.asa.interfaces.IKey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetSocketAddress;

/**
 * @author stuart
 */
public class LookupResultWindow extends JFrame implements ActionListener, WindowListener{
	
	private JPanel mainPanel;
	
	public LookupResultWindow (InetSocketAddress result){
		this.initWindow();
	}
	
	
	/**
	 * @throws java.awt.HeadlessException
	 */
	
	private void initWindow(){
		java.net.URL imgURL = LookupGUI.class.getResource("/Pictures/icon_L.jpg");
		this.setSize(new Dimension(200, 200));
		setResizable(false);
		setBackground(LookupGUI.COLOR_2);
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		setTitle("Lookup Result");
		setFont(LookupGUI.FONT);
		setForeground(LookupGUI.COLOR_1);
		setIconImage((new ImageIcon(imgURL)).getImage());
		
		getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
	}
	
	public LookupResultWindow(IKey k, String lookupString, String result, InetSocketAddress host){
		this.initWindow();
	}
	
	public void actionPerformed(ActionEvent arg0) {
		
		// No action.
	}

	public void windowActivated(WindowEvent arg0) {
		
		// No action.
	}

	public void windowClosed(WindowEvent arg0) {
		
		// No action.
	}

	public void windowClosing(WindowEvent arg0) {
		
		// No action.
	}

	public void windowDeactivated(WindowEvent arg0) {
		
		// No action.
	}

	public void windowDeiconified(WindowEvent arg0) {
		
		// No action.
	}

	public void windowIconified(WindowEvent arg0) {
		
		// No action.
	}

	public void windowOpened(WindowEvent arg0) {
		
		// No action.
	}
}
