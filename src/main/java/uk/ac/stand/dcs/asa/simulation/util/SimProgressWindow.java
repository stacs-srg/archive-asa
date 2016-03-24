package uk.ac.stand.dcs.asa.simulation.util;

import javax.swing.*;
import java.awt.*;

/**
 * @author stuart
 * Implements a simple progress bar which is displayed in its own JFrame.
 */
public class SimProgressWindow extends JFrame {
	
	/* SET GUI FONT */
	private int FONT_SIZE = 9;
	private Font FONT = new java.awt.Font("Verdana",
			Font.PLAIN, FONT_SIZE);
	private Font MONOFONT = new java.awt.Font("Courier",
			Font.PLAIN, 12);
	
	/* SET GUI COLORS */
	private java.awt.Color COLOR_1 = new java.awt.Color(255, 255, 255);
	private java.awt.Color COLOR_2 = new java.awt.Color(0, 0, 0);
	private java.awt.Color COLOR_3 = new java.awt.Color(212, 208, 200);

	// SWING COMPONENTS
	private JPanel mainPanel;
	private JTextArea text = new JTextArea();
	private JProgressBar progress;
	
	public void incrementProgress(){
		int current = progress.getValue();
		if(current<progress.getMaximum()){		
			progress.setValue(current+1);
		}
	}

	public SimProgressWindow(String msg, int min, int max) {
		
		java.net.URL imgURL = SimProgressWindow.class.getResource("/Pictures/tree.jpg");
		this.setSize(new Dimension(400, 100));
		setResizable(true);
		setBackground(COLOR_2);
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		setTitle("Progress Indicator");
		setFont(FONT);
		setForeground(COLOR_1);
		setIconImage((new ImageIcon(imgURL)).getImage());

		
		//jpanel to display progress bar etc.
		mainPanel = new JPanel();
		mainPanel.setLayout(new java.awt.GridBagLayout());
		mainPanel.setPreferredSize(new java.awt.Dimension(400, 100));
		mainPanel.setMinimumSize(new java.awt.Dimension(400, 100));
		mainPanel.setFont(FONT);
		mainPanel.setBackground(COLOR_1);

		text.setText(msg);
		text.setPreferredSize(new java.awt.Dimension(350, 40));
		text.setMinimumSize(new java.awt.Dimension(350, 40));
		//text.setAlignmentX((float) 0.0);
		//text.setAlignmentY((float) 0.0);
		text.setEditable(false);
		text.setSelectionColor(COLOR_3);
		text.setFont(MONOFONT);
		
		progress = new JProgressBar(min,max);
		progress.setPreferredSize(new java.awt.Dimension(300, 25));
		progress.setMinimumSize(new java.awt.Dimension(300, 25));
		progress.setValue(min);
		progress.setStringPainted(true);
		
		mainPanel.add(text, new java.awt.GridBagConstraints(0, 0, 1,
				1, 1.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, 
				new java.awt.Insets(5, 5, 0, 5), 0, 0));

		mainPanel.add(progress, new java.awt.GridBagConstraints(0, 1, 1,
				1, 0.0, 1.0, java.awt.GridBagConstraints.CENTER,
				java.awt.GridBagConstraints.NONE, 
				new java.awt.Insets(0, 5, 5, 5), 0, 0));
		
		//ADD MAIN CONTAINERS TO THE FRAME
		getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
		this.setResizable(false);
		// DISPLAY GUI
		show();
	}
	
	public static void main(String argv[]){
		SimProgressWindow s = new SimProgressWindow("hello",0,100);
		for(int i=0;i<100;i++){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {/* no action */}
			s.incrementProgress();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {/* no action */}
		s.dispose();
	}
}