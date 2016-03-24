/*
 * Created on 24-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.applicationFramework.impl.AbstractP2PAppGUI;
import uk.ac.stand.dcs.asa.applicationFramework.impl.KeyStringStruct;
import uk.ac.stand.dcs.asa.applicationFramework.impl.Message;
import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventBus;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.InetSocketAddress;
import java.util.Enumeration;

/**
 * @author stuart
 */
public class MyP2PAppGUI extends AbstractP2PAppGUI{
    private MyP2PApplication myP2Papp;
	private JPanel application = new JPanel();
	//private TestDrawing testDrawing = new TestDrawing();
	
	
	private JTextField keyChoice = new JTextField();
	private JLabel keyLabel = new JLabel("Key:");
	private JTextField IPChoice = new JTextField();
	private JLabel IPLabel = new JLabel("IP:");
	private JTextField portChoice = new JTextField();
	private JLabel portLabel = new JLabel("Port:");
	
	private JLabel sendMsgLabel = new JLabel("Message:");
	private JScrollPane sendMsgTextScroll;
	private JTextArea sendMsgText = new JTextArea();
	
	private JLabel receivedMsgLabel = new JLabel("Received:");
	private JScrollPane receivedTextScroll;
	private JTextArea receivedText = new JTextArea();
	
	private JLabel keyGenStringLabel = new JLabel("Destination string:");
	private JTextField keyGenString = new JTextField();
	private JButton generateKey = new JButton("GENERATE KEY");
	
	private JLabel destinationKeyLabel = new JLabel("Destination key:");
	private JTextArea destinationKey = new JTextArea();
	private JButton send = new JButton("SEND");
	
	private JLabel keyFromLabel = new JLabel("Key generated from:");
	private JTextField keyFrom = new JTextField();
	
	private JList keyGenStringList = new JList(new DefaultListModel());
	private JScrollPane keyGenStringListScroller = new JScrollPane(keyGenStringList);
	
	private IKey currentKey = null;
     
    private DHTUserInterface dhtui;
	
	public MyP2PAppGUI(MyP2PApplication app, EventBus appBus, DHTComponent dhtc){
        super(app,appBus,"/Pictures/net_icon.gif","P2P Application",900,700);
		// Initialise all GUI components and display it.
	    myP2Papp=app;
        dhtui=new DHTUserInterface(dhtc);
	
		application.setSize(new Dimension(700, 400));
		application.setPreferredSize(new Dimension(700,400));
		application.setMinimumSize(new Dimension(700, 400));
		application.setMaximumSize(new Dimension(700, 400));
		application.setLayout(new java.awt.GridBagLayout());
		
		// KEY CHOICE TEXT FIELD PROPERTIES
		keyChoice.setPreferredSize(new Dimension(400, 22));
		keyChoice.setMinimumSize(new Dimension(400, 22));
		keyChoice.setMaximumSize(new Dimension(400, 22));
		keyChoice.setFont(font);
		keyChoice.setEditable(false);
		keyChoice.setBackground(Color.ORANGE);
		
		keyLabel.setFont(font);
		keyLabel.setForeground(bgColor);
		
		// IP CHOICE TEXT FIELD PROPERTIES
		IPChoice.setPreferredSize(new Dimension(200, 22));
		IPChoice.setMinimumSize(new Dimension(200, 22));
		IPChoice.setMaximumSize(new Dimension(200, 22));
		IPChoice.setFont(font);
		IPChoice.setEditable(false);
		IPChoice.setBackground(Color.ORANGE);
		
		IPLabel.setFont(font);
		IPLabel.setForeground(bgColor);
		
		// PORT CHOICE TEXT FIELD PROPERTIES
		portChoice.setPreferredSize(new Dimension(200, 22));
		portChoice.setMinimumSize(new Dimension(200, 22));
		portChoice.setMaximumSize(new Dimension(200, 22));
		portChoice.setFont(font);
		portChoice.setEditable(false);
		portChoice.setBackground(Color.ORANGE);
		
		portLabel.setFont(font);
		portLabel.setForeground(bgColor);
		
		//RECEIVED MESSAGE TEXT AREA PROPERTIES
		sendMsgLabel.setFont(font);
		sendMsgLabel.setForeground(bgColor);
		
		sendMsgText.setBackground(fgColor);
		sendMsgText.setFont(font);
		sendMsgText.setSelectionColor(highlightColor);
		
		sendMsgTextScroll = new JScrollPane(sendMsgText);
		sendMsgTextScroll.setPreferredSize(new Dimension(500, 200));
		sendMsgTextScroll.setMinimumSize(new Dimension(500, 200));
		sendMsgTextScroll.setMaximumSize(new Dimension(500, 200));
		
		//keys and stuff
		keyGenStringLabel.setFont(font);
		keyGenStringLabel.setForeground(bgColor);
		
		keyGenString.setPreferredSize(new Dimension(200, 22));
		keyGenString.setMinimumSize(new Dimension(200, 22));
		keyGenString.setMaximumSize(new Dimension(200, 22));
		keyGenString.setFont(font);
		
		generateKey.setFont(font);
        generateKey.setEnabled(false);
		
		destinationKeyLabel.setFont(font);
		destinationKeyLabel.setForeground(bgColor);
		
		destinationKey.setPreferredSize(new Dimension(200, 35));
		destinationKey.setMinimumSize(new Dimension(200, 35));
		destinationKey.setMaximumSize(new Dimension(200, 35));
		destinationKey.setFont(font);
		destinationKey.setEditable(false);
		destinationKey.setLineWrap(true);
		destinationKey.setBackground(Color.PINK);
		
		send.setFont(font);
        send.setEnabled(false);
		
		keyFromLabel.setFont(font);
		keyFromLabel.setForeground(bgColor);
		
		keyFrom.setPreferredSize(new Dimension(200, 22));
		keyFrom.setMinimumSize(new Dimension(200, 22));
		keyFrom.setMaximumSize(new Dimension(200, 22));
		keyFrom.setFont(font);
		keyFrom.setEditable(false);
		keyFrom.setBackground(Color.PINK);
		
		//RECEIVED MESSAGE TEXT AREA PROPERTIES
		receivedMsgLabel.setFont(font);
		receivedMsgLabel.setForeground(bgColor);
		
		receivedText.setBackground(fgColor);
		receivedText.setFont(font);
		receivedText.setSelectionColor(highlightColor);
		
		receivedText.setEditable(false);
		receivedTextScroll = new JScrollPane(receivedText);
		receivedTextScroll.setPreferredSize(new Dimension(500, 200));
		receivedTextScroll.setMinimumSize(new Dimension(500, 200));
		receivedTextScroll.setMaximumSize(new Dimension(500, 200));
		
		
		keyGenStringList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		keyGenStringList.setLayoutOrientation(JList.VERTICAL);
		
		keyGenStringList.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {	
			    ListModel lm = keyGenStringList.getModel();
				if (e.getClickCount() == 2) {
					int index = keyGenStringList.locationToIndex(e.getPoint());
					if(index>=0&&index<lm.getSize()){
						KeyStringStruct kss = (KeyStringStruct) lm.getElementAt(index);
						keyGenStringList.ensureIndexIsVisible(index);
						keyGenString.setText(kss.getString());
						keyGenStringList.clearSelection();
					}
				}
			}

            public void mouseEntered(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            public void mouseExited(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            public void mousePressed(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            public void mouseReleased(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }
        });		
		
		keyGenStringList.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent arg0) {
                if(!keyGenStringList.isSelectionEmpty()){
	                //System.out.println("DELETE");
	                if(arg0.getKeyCode()==KeyEvent.VK_DELETE){
	                    int index = keyGenStringList.getSelectedIndex();
	                    DefaultListModel dlm = (DefaultListModel) keyGenStringList.getModel();
						if(index>=0&&index<dlm.getSize()){
							dlm.remove(index);
							keyGenStringList.clearSelection();
						}
	                }
	            }
            }

            public void keyReleased(KeyEvent arg0) {
                // TODO Auto-generated method stub

            }

            public void keyTyped(KeyEvent arg0) {
                // TODO Auto-generated method stub
                
            }
        });
		
		keyGenStringList.setFont(font);
		keyGenStringListScroller.setPreferredSize(new java.awt.Dimension(200, 80));
		keyGenStringListScroller.setMinimumSize(new java.awt.Dimension(200, 80));
		keyGenStringListScroller.setMaximumSize(new java.awt.Dimension(200, 80));
		
		//add components to panels
		application.add(IPLabel, new GridBagConstraints(0, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		application.add(IPChoice, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		
		application.add(portLabel, new GridBagConstraints(2, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		application.add(portChoice, new GridBagConstraints(3, 0, 3, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		
		application.add(keyLabel, new GridBagConstraints(0, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		application.add(keyChoice, new GridBagConstraints(1, 1, 5, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));		
		
		
		application.add(sendMsgLabel, new GridBagConstraints(0, 2, 4, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		application.add(sendMsgTextScroll, new GridBagConstraints(0, 3, 4, 7, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		
		application.add(keyGenStringLabel, new GridBagConstraints(4, 3, 2, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		
		application.add(keyGenString, new GridBagConstraints(4, 4, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		application.add(generateKey, new GridBagConstraints(5, 4, 1, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		
		application.add(keyGenStringListScroller, new GridBagConstraints(4, 5, 2, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		
		application.add(destinationKeyLabel, new GridBagConstraints(4, 6, 2, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		
		application.add(destinationKey, new GridBagConstraints(4, 7, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		application.add(send, new GridBagConstraints(5, 7, 1, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));

		application.add(keyFromLabel, new GridBagConstraints(4, 8, 2, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		
		application.add(keyFrom, new GridBagConstraints(4, 9, 2, 3, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		
		
		application.add(receivedMsgLabel, new GridBagConstraints(0, 10, 6, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
		
		application.add(receivedTextScroll, new GridBagConstraints(0, 11, 6, 1, 1.0,
				1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new java.awt.Insets(5, 5, 0,
						0), 0, 0));
	
       
        
//		nodeConfig.getCreateButton().addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent arg0) {
//                nodeConfig.disableInputComponents();
//                if(P2Papp.initialiseP2P(nodeConfig.getLocalIPText().getText(), nodeConfig.getLocalPortText().getText())){
//                    nodeConfig.highlightCreateInputs(Color.ORANGE);
//                    initialiseApp();
//                }
//            }
//        });
		

		
//		nodeConfig.getJoinButton().addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent arg0) {
//                nodeConfig.disableInputComponents();
//                if(P2Papp.initialiseP2P(nodeConfig.getLocalIPText().getText(),nodeConfig.getLocalPortText().getText(),nodeConfig.getKnownIPText().getText(),nodeConfig.getKnownPortText().getText())){
//                    nodeConfig.highlightJoinInputs(Color.ORANGE);
//                    initialiseApp();
//                }
//            }
//        });		
		
		//		 ADD TABS TO TABBED PANEL
		if (imgURL != null) {
		    mainTabbedPanel.addTab("Application", new ImageIcon(imgURL), application);
			//mainTabbedPanel.addTab("TestDrawing",   new ImageIcon(imgURL), testDrawing);
            mainTabbedPanel.addTab("DHT",   new ImageIcon(imgURL), dhtui);
		} else {
		    mainTabbedPanel.addTab("Application", application);
		    //mainTabbedPanel.addTab("TestDrawing", testDrawing);
            mainTabbedPanel.addTab("DHT", dhtui);
		}
		
		generateKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String s = keyGenString.getText();
                if(s.compareTo("")!=0){
                    keyFrom.setText(s);
                    DefaultListModel dlm = (DefaultListModel) keyGenStringList.getModel();
                    KeyStringStruct kss = defaultListModel_contains(s);
                    if(kss==null){
                        IKey k = makeKey(s);
                        currentKey = k;
                        dlm.add(0,new KeyStringStruct(s,k));
                    }else{
                        currentKey=kss.getKey();
                    }
                    destinationKey.setText(currentKey.toString());
                }
            }
        });
		
		send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(currentKey!=null && sendMsgText.getText()!=""){
                    if(myP2Papp.send(currentKey,sendMsgText.getText())){
                    	sendMsgText.setText("");
                    }
                }
            }
        });
	}

    public void initialiseApp() {
        super.initialiseApp();
	    if(localNetAddress!=null&&localKey!=null){
	        IPChoice.setText(localNetAddress.getAddress().getHostAddress());
	        portChoice.setText(Integer.toString(localNetAddress.getPort()));
	        keyChoice.setText(localKey.toString());
	        generateKey.setEnabled(true);
	        send.setEnabled(true);
	    }
    }

    private IKey makeKey(String s){
	    return SHA1KeyFactory.generateKey(s);
	}
	
	private KeyStringStruct defaultListModel_contains(String s){
	    Enumeration e = ((DefaultListModel)keyGenStringList.getModel()).elements();
	    while(e.hasMoreElements()){
	        Object o = e.nextElement();
	        if(o instanceof KeyStringStruct){
	            KeyStringStruct kss = (KeyStringStruct) o;
	            if(s.compareTo(kss.getString())==0){
	                return kss;
	            }
	        }else{
	            Error.error("DefaultListModel object contained element of wrong type.");
	        }
	    }
	    return null;
	}

    public void displayMessage(Message m) {
        InetSocketAddress from = m.getFrom();
        receivedText.append(FormatHostInfo.formatHostName(from)+">>>\n"+m.getContents()+
                "\n<<<\n\n");
    }
}
