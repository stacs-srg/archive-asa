package uk.ac.stand.dcs.asa.simulation.util;

import uk.ac.stand.dcs.asa.interfaces.IP2PNode;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TreeViewGUI extends JFrame {
	
	private String rootTag;
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
	private JTabbedPane mainTabbedPanel = new JTabbedPane();
	private JPanel treePanel;
	private JScrollPane jsp;
	private JScrollPane stats;
	private JTextArea statsTextArea = new JTextArea();
	
	private JLabel headingLabel = new JLabel("Selected Node");
	private JLabel keyLabel = new JLabel("Key:");
	private JTextField keyDisplay = new JTextField();

	public TreeViewGUI( DefaultMutableTreeNode[] trees, String rootTag, String statsString ) {
		
		if(rootTag==null)this.rootTag="Simulation";
		else this.rootTag=rootTag;
		
		DefaultMutableTreeNode t = new DefaultMutableTreeNode(this.rootTag);
		for(int i=0;i<trees.length;i++){
			t.add(trees[i]);
		}
	    final JTree jtree = new JTree(t);
	    jtree.setFont(MONOFONT);
	    
		java.net.URL imgURL = TreeViewGUI.class.getResource("/Pictures/tree.jpg");
		this.setSize(new Dimension(900, 700));
		setResizable(true);
		setBackground(COLOR_2);
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		setTitle("JCHORD TREE VIEWER");
		setFont(FONT);
		setForeground(COLOR_1);
		setIconImage((new ImageIcon(imgURL)).getImage());

		// TABBED PANEL AND COMPONENTS
		mainTabbedPanel.setSize(900, 700);
		mainTabbedPanel.setBackground(COLOR_3);
		mainTabbedPanel.setForeground(COLOR_2);
		mainTabbedPanel.setFont(FONT);
	
		//jpanel to display tree etc.
		treePanel = new JPanel();
		treePanel.setLayout(new java.awt.GridBagLayout());
		//treePanel.setPreferredSize(new java.awt.Dimension(900, 700));
		//treePanel.setMinimumSize(new java.awt.Dimension(900, 700));
		treePanel.setFont(FONT);
		treePanel.setBackground(COLOR_1);
		
		// scrollpane to hold jtree
		jsp = new JScrollPane(jtree);
		jsp.setPreferredSize(new java.awt.Dimension(900, 500));
		jsp.setMinimumSize(new java.awt.Dimension(900, 500));
		jsp.setFont(MONOFONT);
		jsp.setBackground(COLOR_1);
						
		statsTextArea.setText("Simulation Statistics\n---------------------\n\n");
		statsTextArea.append(statsString);
		statsTextArea.setEditable(false);
		statsTextArea.setSelectionColor(COLOR_3);
		statsTextArea.setFont(MONOFONT);
		stats= new JScrollPane(statsTextArea);
		stats.setBackground(COLOR_1);
		stats.setPreferredSize(new java.awt.Dimension(700, 600));
		stats.setMinimumSize(new java.awt.Dimension(700, 600));

		
		
		//components
		headingLabel.setFont(FONT);
		headingLabel.setForeground(COLOR_2);
		
		keyLabel.setFont(FONT);
		keyLabel.setForeground(COLOR_2);
		
		keyDisplay.setPreferredSize(new java.awt.Dimension(400, 22));
		keyDisplay.setMinimumSize(new java.awt.Dimension(400, 22));
		keyDisplay.setMaximumSize(new java.awt.Dimension(400, 22));
		//errorMsg.setMargin(new java.awt.Insets(5, 0, 0, 0));
		keyDisplay.setFont(MONOFONT);
		keyDisplay.setEditable(false);
		
		//add components to treePanel
		treePanel.add(jsp, new java.awt.GridBagConstraints(0, 0, 2,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, 
				new java.awt.Insets(5, 5, 0, 5), 0, 0));
		treePanel.add(headingLabel, new java.awt.GridBagConstraints(0, 1, 2,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, 
				new java.awt.Insets(5, 5, 0, 5), 0, 0));
		treePanel.add(keyLabel, new java.awt.GridBagConstraints(0, 2, 1,
				1, 0.0, 0.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, 
				new java.awt.Insets(5, 5, 5, 5), 0, 0));
		treePanel.add(keyDisplay, new java.awt.GridBagConstraints(1, 2, 1,
				1, 1.0, 1.0, java.awt.GridBagConstraints.NORTHWEST ,
				java.awt.GridBagConstraints.NONE, 
				new java.awt.Insets(5, 5, 5, 5), 0, 0));
		
		// ADD TABS TO TABBED PANEL
		if (imgURL != null) {
			mainTabbedPanel.addTab("Spanning tree", new javax.swing.ImageIcon(
					imgURL), treePanel);
			mainTabbedPanel.addTab("Statistics", new javax.swing.ImageIcon(
					imgURL), stats);
		} else {
			mainTabbedPanel.addTab("Spanning tree", jsp);
			mainTabbedPanel.addTab("Statistics", stats);
		}
		
		//ADD MAIN CONTAINERS TO THE FRAME
		getContentPane().add(mainTabbedPanel, java.awt.BorderLayout.CENTER);
		
		//SETUP THE MOUSE LISTENER
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = jtree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = jtree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					mySingleClick(selRow, selPath);
					
					//distinguish between single and double clicks as shown below.
					//if(e.getClickCount() == 1) {
					//	mySingleClick(selRow, selPath);
					//}else if(e.getClickCount() == 2) {
					//	myDoubleClick(selRow, selPath);
					//}
				}
			}

			private void mySingleClick(int selRow, TreePath selPath) {
				if(selPath.getPathCount()>1){
					selPath.getLastPathComponent();
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)selPath.getLastPathComponent();
					Object[] path = node.getUserObjectPath();
					try{
						IP2PNode jcn = (IP2PNode)path[path.length-1];
						String key = jcn.getKey().bigIntegerRepresentation().toString(16);
						if(key.length()<40){
							for(int i=0;i<40-key.length();i++){
								key="0"+key;
							}
						}
						keyDisplay.setText(key);
					}catch(ClassCastException e){
						e.printStackTrace();
						Error.hardError("Node in tree was not a JChordNode as expected");
					} catch (Exception e) {
			            Error.exceptionError("JChordNode.getKey threw exception", e);
                    }
				}
			}
		};
		jtree.addMouseListener(ml);

		// DISPLAY GUI
		show();
	}
}