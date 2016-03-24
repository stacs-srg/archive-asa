/*
 * Created on Jan 18, 2005 at 11:56:41 AM.
 */
package uk.ac.stand.dcs.asa.jchord.deploy;

import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * GUI for interactive node launching.
 *
 * @author graham
 */
class NodeLauncherFrameSinglePane extends JFrame {
    
    private static final int FRAME_POS_Y = 100;
    private static final int FRAME_POS_X = 100;
    private static final int FRAME_HEIGHT = 434;
    private static final int FRAME_WIDTH = 520;
    
    private static final int DEFAULT_NUMBER_OF_NODES = 1;
    
    private static final String NUMBER_OF_NODES_TOOLTIP =                      "The number of nodes to be started";
    private static final String KNOWN_NODE_PORT_TOOLTIP =                      "The port of the known node in the existing ring";
    private static final String KNOWN_NODE_HOST_TOOLTIP =                      "The IP address of the known node in the existing ring";
    private static final String KNOWN_NODE_DISABLED_EXPLANATION =              " (disabled because new ring will be created)";
    private static final String TARGET_VISUALISER_HOST_TOOLTIP =               "The IP address of the host on which the visualiser is running";
    private static final String FIRST_PORT_TOOLTIP =                           "The port for the first node to be created";
    private static final String VISUALISER_HOST_TOOLTIP =                      "The IP address of the host on which to start the visualiser (non-local not yet implemented)";
    private static final String NODES_HOST_TOOLTIP =                           "The IP address of the host on which to start the nodes (non-local not yet implemented)";
    private static final String RANDOMISE_JOIN_POSITION_TOOLTIP =              "Randomise the joining position for new nodes";
    private static final String RANDOMISE_JOIN_POSITION_DISABLED_EXPLANATION = " (disabled because existing ring will be joined)";

    
    private static String DEFAULT_VISUALISER_HOST;
    private static String DEFAULT_TARGET_VISUALISER_HOST;
    private static String DEFAULT_NODES_HOST;
    private static String DEFAULT_KNOWN_NODE_HOST;
    
    static {
        String local_address = "";
        try {
            local_address = InetAddress.getLocalHost().getHostName();
            
            DEFAULT_VISUALISER_HOST =        local_address;
            DEFAULT_TARGET_VISUALISER_HOST = local_address;
            DEFAULT_NODES_HOST =             local_address;
            DEFAULT_KNOWN_NODE_HOST =        local_address;
        }
        catch (UnknownHostException e) { Error.hardError("error getting local address"); }
    }
    
    private static final int DEFAULT_FIRST_PORT = 1111;
    private static final int DEFAULT_KNOWN_NODE_PORT = 1111;

    private JTextField visualiser_host_textfield;
    private ButtonGroup button_group;
    private JTextField first_port_textfield;
    private JTextField nodes_host_textfield;
    private JButton start_nodes_button;
    private JTextField number_of_nodes_textfield;
    private JTextField known_node_port_textfield;
    private JTextField known_node_host_textfield;
    private JButton start_visualiser_button;
    private JTextField target_visualiser_host_textfield;
    private JComboBox process_menu;
    private JButton kill_button;
    private JButton kill_all_button;
    private JRadioButton new_ring_button;
    private JCheckBox randomise_join_position_button;
    private JLabel known_node_host_label;
    private JLabel known_node_port_label;

    
    private int first_port = DEFAULT_FIRST_PORT;
    
    protected NodeLauncherFrameSinglePane() {
        
        super();
        
        getContentPane().setLayout(new BorderLayout());
        setTitle("JChord Node Launcher");
        setBounds(FRAME_POS_X, FRAME_POS_Y, FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel top_panel = new JPanel();
        top_panel.setBorder(new TitledBorder(new EtchedBorder(), "Create Visualiser", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        getContentPane().add(top_panel, BorderLayout.NORTH);

        start_visualiser_button = new JButton();
        start_visualiser_button.setToolTipText("Starts a ring visualiser window on the specified host");
        top_panel.add(start_visualiser_button);
        start_visualiser_button.setText("Start Visualiser");
        start_visualiser_button.addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent arg0) {
                if (arg0.getKeyChar() == '\n') {
                    start_visualiser_button.doClick();
                }
            }

            public void keyPressed(KeyEvent arg0) {/* no action */}

            public void keyReleased(KeyEvent arg0) {/* no action */}
        });
        
        final JLabel visualiser_host_label = new JLabel();
        top_panel.add(visualiser_host_label);
        visualiser_host_label.setToolTipText(VISUALISER_HOST_TOOLTIP);
        visualiser_host_label.setText("IP");

        visualiser_host_textfield = new JTextField();
        top_panel.add(visualiser_host_textfield);
        visualiser_host_textfield.setToolTipText(VISUALISER_HOST_TOOLTIP);
        visualiser_host_textfield.setText(DEFAULT_VISUALISER_HOST);
        visualiser_host_textfield.setEnabled(false);

        final JPanel mid_panel = new JPanel();
        mid_panel.setLayout(new BoxLayout(mid_panel, BoxLayout.Y_AXIS));
        mid_panel.setBorder(new TitledBorder(new EtchedBorder(), "Create Nodes", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        getContentPane().add(mid_panel);

        final JPanel new_nodes_host_panel = new JPanel();
        final FlowLayout flowLayout_1 = new FlowLayout();
        flowLayout_1.setAlignment(FlowLayout.LEFT);
        new_nodes_host_panel.setLayout(flowLayout_1);
        mid_panel.add(new_nodes_host_panel);
        new_nodes_host_panel.setBorder(new TitledBorder(new EtchedBorder(), "Host:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        final JLabel nodes_host_label = new JLabel();
        new_nodes_host_panel.add(nodes_host_label);
        nodes_host_label.setText("IP");

        nodes_host_textfield = new JTextField();
        new_nodes_host_panel.add(nodes_host_textfield);
        nodes_host_textfield.setEnabled(false);
        nodes_host_textfield.setText(DEFAULT_NODES_HOST);
        
        nodes_host_label.setToolTipText(NODES_HOST_TOOLTIP);
        nodes_host_textfield.setToolTipText(NODES_HOST_TOOLTIP);

        final JLabel first_port_label = new JLabel();
        new_nodes_host_panel.add(first_port_label);
        first_port_label.setToolTipText(FIRST_PORT_TOOLTIP);
        first_port_label.setText("first port in range");

        first_port_textfield = new JTextField();
        new_nodes_host_panel.add(first_port_textfield);
        first_port_textfield.setToolTipText(FIRST_PORT_TOOLTIP);
        first_port_textfield.setText(String.valueOf(DEFAULT_FIRST_PORT));
        first_port_textfield.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                try {
                    first_port = Integer.parseInt(first_port_textfield.getText());
                }
                catch (NumberFormatException ex) {
                    first_port_textfield.setText(String.valueOf(DEFAULT_FIRST_PORT));
                }
            }
            public void focusGained(FocusEvent e) {/* no action */}
        });

        final JPanel target_visualiser_panel = new JPanel();
        final FlowLayout flowLayout_2 = new FlowLayout();
        flowLayout_2.setAlignment(FlowLayout.LEFT);
        target_visualiser_panel.setLayout(flowLayout_2);
        mid_panel.add(target_visualiser_panel);
        target_visualiser_panel.setBorder(new TitledBorder(new EtchedBorder(), "Target Visualiser:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        final JLabel target_visualiser_host_label = new JLabel();
        target_visualiser_host_label.setToolTipText(TARGET_VISUALISER_HOST_TOOLTIP);
        target_visualiser_panel.add(target_visualiser_host_label);
        target_visualiser_host_label.setText("IP");

        target_visualiser_host_textfield = new JTextField();
        target_visualiser_host_textfield.setToolTipText(TARGET_VISUALISER_HOST_TOOLTIP);
        target_visualiser_panel.add(target_visualiser_host_textfield);
        target_visualiser_host_textfield.setText(DEFAULT_TARGET_VISUALISER_HOST);
        target_visualiser_host_textfield.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (target_visualiser_host_textfield.getText().equals(DEFAULT_TARGET_VISUALISER_HOST)) target_visualiser_host_textfield.setText("");
            }
            public void focusLost(FocusEvent e) {
                if (target_visualiser_host_textfield.getText().equals("")) target_visualiser_host_textfield.setText(DEFAULT_TARGET_VISUALISER_HOST);
            }
        });

        final JPanel ring_panel = new JPanel();
        ring_panel.setLayout(new BorderLayout());
        mid_panel.add(ring_panel);
        ring_panel.setBorder(new TitledBorder(new EtchedBorder(), "Ring:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        final JPanel new_ring_panel = new JPanel();
        new_ring_panel.setLayout(new BoxLayout(new_ring_panel, BoxLayout.Y_AXIS));
        ring_panel.add(new_ring_panel, BorderLayout.WEST);
        new_ring_button = new JRadioButton();
        new_ring_panel.add(new_ring_button);
        new_ring_button.setToolTipText("Create a new ring");
        new_ring_button.setMargin(new Insets(0, 2, 2, 2));
        new_ring_button.setVerticalAlignment(SwingConstants.TOP);
        new_ring_button.setText("Create New Ring");
        button_group = new ButtonGroup();
        button_group.add(new_ring_button);
        
        randomise_join_position_button = new JCheckBox();
        new_ring_panel.add(randomise_join_position_button);
        randomise_join_position_button.setToolTipText(RANDOMISE_JOIN_POSITION_TOOLTIP);
        randomise_join_position_button.setMargin(new Insets(0, 2, 2, 2));
        randomise_join_position_button.setVerticalAlignment(SwingConstants.TOP);
        randomise_join_position_button.setText("Randomise Join Position");

        final JPanel start_panel = new JPanel();
        mid_panel.add(start_panel);
        start_nodes_button = new JButton();
        start_panel.add(start_nodes_button);
        start_nodes_button.setToolTipText("Starts specified number of nodes on the specified host");
        start_nodes_button.setText("Start Node(s)");

        final JPanel number_of_nodes_panel = new JPanel();
        start_panel.add(number_of_nodes_panel);
        final JLabel number_of_nodes_label = new JLabel();
        number_of_nodes_panel.add(number_of_nodes_label);
        number_of_nodes_label.setToolTipText(NUMBER_OF_NODES_TOOLTIP);
        number_of_nodes_label.setText("number of nodes:");

        number_of_nodes_textfield = new JTextField();
        number_of_nodes_panel.add(number_of_nodes_textfield);
        number_of_nodes_textfield.setToolTipText(NUMBER_OF_NODES_TOOLTIP);
        number_of_nodes_textfield.setText(String.valueOf(DEFAULT_NUMBER_OF_NODES));
        number_of_nodes_textfield.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                // Check that number is legal.
                getNumberOfNodes();
            }
            public void focusGained(FocusEvent e) {/* no action */}
        });
        number_of_nodes_textfield.addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent arg0) {
                if (arg0.getKeyChar() == '\n') {
                    start_nodes_button.doClick();
                }
            }

            public void keyPressed(KeyEvent arg0) {/* no action */}

            public void keyReleased(KeyEvent arg0) {/* no action */}
        });
        
        final JPanel bottom_panel = new JPanel();
        bottom_panel.setBorder(new TitledBorder(new EtchedBorder(), "Kill Nodes", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        getContentPane().add(bottom_panel, BorderLayout.SOUTH);
        
        process_menu = new JComboBox();
        process_menu.setToolTipText("Select a process");
        bottom_panel.add(process_menu);
        
        kill_button = new JButton();
        kill_button.setToolTipText("Kill the selected node process");
        bottom_panel.add(kill_button);
        kill_button.setText("Kill Process");
        kill_button.setEnabled(false);
        
        kill_all_button = new JButton();
        kill_all_button.setToolTipText("Kill all the remaining node processes");
        bottom_panel.add(kill_all_button);
        kill_all_button.setText("Kill All");
        kill_all_button.setEnabled(false);

        final JPanel existing_ring_panel = new JPanel();
        existing_ring_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        existing_ring_panel.setLayout(new BoxLayout(existing_ring_panel, BoxLayout.Y_AXIS));
        ring_panel.add(existing_ring_panel, BorderLayout.EAST);

        final JRadioButton join_ring_button = new JRadioButton();
        existing_ring_panel.add(join_ring_button);
        join_ring_button.setToolTipText("Join via a known node on an existing ring");
        join_ring_button.setText("Join Existing Ring");
        button_group.add(join_ring_button);

        final JPanel known_node_panel = new JPanel();
        existing_ring_panel.add(known_node_panel);
        known_node_panel.setBorder(new TitledBorder(new EtchedBorder(), "Known Node:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        known_node_panel.setLayout(flowLayout);

        known_node_host_label = new JLabel();
        known_node_host_label.setToolTipText(KNOWN_NODE_HOST_TOOLTIP);
        known_node_panel.add(known_node_host_label);
        known_node_host_label.setText("IP");

        known_node_host_textfield = new JTextField();
        known_node_host_textfield.setToolTipText(KNOWN_NODE_HOST_TOOLTIP + KNOWN_NODE_DISABLED_EXPLANATION);
        known_node_panel.add(known_node_host_textfield);
        known_node_host_textfield.setText(DEFAULT_KNOWN_NODE_HOST);

        known_node_port_label = new JLabel();
        known_node_port_label.setToolTipText(KNOWN_NODE_PORT_TOOLTIP + KNOWN_NODE_DISABLED_EXPLANATION);
        known_node_panel.add(known_node_port_label);
        known_node_port_label.setText("port");

        known_node_port_textfield = new JTextField();
        known_node_port_textfield.setToolTipText(KNOWN_NODE_PORT_TOOLTIP);
        known_node_panel.add(known_node_port_textfield);
        known_node_port_textfield.setText(String.valueOf(DEFAULT_KNOWN_NODE_PORT));
        known_node_port_textfield.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                // Check that number is legal.
                getKnownNodePort();
            }
            public void focusGained(FocusEvent e) {/* no action */}
        });
        
        new_ring_button.addChangeListener(new ChangeListener() {
            boolean selected = false;
            public void stateChanged(ChangeEvent e) {
                selected = !selected;
                known_node_host_textfield.setEnabled(!selected);
                known_node_port_textfield.setEnabled(!selected);
                known_node_host_label.setEnabled(!selected);
                known_node_port_label.setEnabled(!selected);
                randomise_join_position_button.setEnabled(selected);
                
                if (selected) known_node_host_textfield.setToolTipText(KNOWN_NODE_HOST_TOOLTIP + KNOWN_NODE_DISABLED_EXPLANATION);
                else          known_node_host_textfield.setToolTipText(KNOWN_NODE_HOST_TOOLTIP);
                
                if (selected) known_node_port_textfield.setToolTipText(KNOWN_NODE_PORT_TOOLTIP + KNOWN_NODE_DISABLED_EXPLANATION);
                else          known_node_port_textfield.setToolTipText(KNOWN_NODE_PORT_TOOLTIP);
                
                if (!selected) randomise_join_position_button.setToolTipText(RANDOMISE_JOIN_POSITION_TOOLTIP + RANDOMISE_JOIN_POSITION_DISABLED_EXPLANATION);
                else           randomise_join_position_button.setToolTipText(RANDOMISE_JOIN_POSITION_TOOLTIP);
            }
        });
        new_ring_button.setSelected(true);
    }
    
    protected String getVisualiserHost() {
        
        return visualiser_host_textfield.getText();
    }

    protected String getTargetVisualiserHost() {
        
        return target_visualiser_host_textfield.getText();
    }

    protected String getNodesHost() {

        return nodes_host_textfield.getText();
    }

    protected int getFirstPort() {

        return first_port;
    }
    
    protected void setFirstPort(int i) {
        
        first_port = i;
        first_port_textfield.setText(String.valueOf(first_port));
    }
    
    protected String getKnownNodeHost() {

        return known_node_host_textfield.getText();
    }

    protected int getKnownNodePort() {

        try {
            return Integer.parseInt(known_node_port_textfield.getText());
        }
        catch (NumberFormatException ex) {
            known_node_port_textfield.setText(String.valueOf(DEFAULT_KNOWN_NODE_PORT));
            return DEFAULT_KNOWN_NODE_PORT;
        }
    }
    
    protected void setStartVisualiserAction(ActionListener listener) {
        
        start_visualiser_button.addActionListener(listener);
    }

    protected void setStartNodesAction(ActionListener listener) {
        
        start_nodes_button.addActionListener(listener);
    }
    
    protected int getNumberOfNodes() {
        
        try {
            return Integer.parseInt(stripLeadingBlanks(number_of_nodes_textfield.getText()));
        }
        catch (NumberFormatException ex) {
            number_of_nodes_textfield.setText(String.valueOf(DEFAULT_NUMBER_OF_NODES));
            return DEFAULT_NUMBER_OF_NODES;
        }
    }
    
    protected boolean createNewRing() {
        
        return new_ring_button.isSelected();
    }

    protected boolean randomiseJoinPosition() {
        
        return randomise_join_position_button.isSelected();
    }
    
    protected void setKillAction(ActionListener listener) {
        
        kill_button.addActionListener(listener);
    }
    
    protected void setKillAllAction(ActionListener listener) {
        
        kill_all_button.addActionListener(listener);
    }
    
    protected void addNodeToMenu(String node_label) {
        
        process_menu.addItem(node_label);
        if (process_menu.getItemCount() == 1) {
            kill_button.setEnabled(true);
            kill_all_button.setEnabled(true);
        }
    }
    
    protected void removeNodeFromMenu(String node_label) {
        
        process_menu.removeItem(node_label);
        if (process_menu.getItemCount() == 0) {
            kill_button.setEnabled(false);
            kill_all_button.setEnabled(false);
        }
    }
    
    protected String selectedNode() {
        
        return (String) (process_menu.getSelectedItem());
    }

    protected void setKnownNodeHost(String known_node_host) {
        
        known_node_host_textfield.setText(known_node_host);
    }
    
    protected void setKnownNodeHost() {
        
        setKnownNodeHost(DEFAULT_KNOWN_NODE_HOST);
    }
    
    protected void setKnownNodePort(int known_node_port) {
        
        known_node_port_textfield.setText(String.valueOf(known_node_port));
    }
    
    protected void setKnownNodePort() {
        
        setKnownNodePort(DEFAULT_KNOWN_NODE_PORT);
    }
    
    private String stripLeadingBlanks(String text) {
        
        while (text.startsWith(" ")) text = text.substring(1);
        return text;
    }
}