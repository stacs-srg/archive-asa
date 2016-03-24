/*
 * Created on Jan 18, 2005 at 11:56:41 AM.
 */
package uk.ac.stand.dcs.asa.jchord.deploy;

import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
class NodeLauncherFrame extends JFrame {
    
	private static final int FRAME_POS_Y = 100;
    private static final int FRAME_POS_X = 100;
    private static final int FRAME_HEIGHT = 340;
    private static final int FRAME_WIDTH = 500;
    
    private static final int DEFAULT_NUMBER_OF_NODES = 1;
    
    private static final String NUMBER_OF_NODES_TOOLTIP =                      StringConstants.getString("NodeLauncherFrame.NUMBER_OF_NODES_TOOLTIP"); //$NON-NLS-1$
    
    private static final String KNOWN_NODE_HOST_TOOLTIP =                      StringConstants.getString("NodeLauncherFrame.KNOWN_NODE_HOST_TOOLTIP"); //$NON-NLS-1$
    private static final String KNOWN_NODE_DISABLED_EXPLANATION =              StringConstants.getString("NodeLauncherFrame.KNOWN_NODE_DISABLED_EXPLANATION"); //$NON-NLS-1$
    private static final String KNOWN_NODE_HOST_DISABLED_TOOLTIP =             KNOWN_NODE_HOST_TOOLTIP + KNOWN_NODE_DISABLED_EXPLANATION;
    
    private static final String KNOWN_NODE_PORT_TOOLTIP =                      StringConstants.getString("NodeLauncherFrame.KNOWN_NODE_PORT_TOOLTIP"); //$NON-NLS-1$
    private static final String KNOWN_NODE_PORT_DISABLED_TOOLTIP =             KNOWN_NODE_PORT_TOOLTIP + KNOWN_NODE_DISABLED_EXPLANATION;
    
    private static final String TARGET_VISUALISER_HOST_TOOLTIP =               StringConstants.getString("NodeLauncherFrame.TARGET_VISUALISER_HOST_TOOLTIP"); //$NON-NLS-1$
    private static final String FIRST_PORT_TOOLTIP =                           StringConstants.getString("NodeLauncherFrame.FIRST_PORT_TOOLTIP"); //$NON-NLS-1$
    private static final String VISUALISER_HOST_TOOLTIP =                      StringConstants.getString("NodeLauncherFrame.VISUALISER_HOST_TOOLTIP"); //$NON-NLS-1$
    private static final String NODES_HOST_TOOLTIP =                           StringConstants.getString("NodeLauncherFrame.NODES_HOST_TOOLTIP"); //$NON-NLS-1$
    
    private static final String RANDOMISE_JOIN_POSITION_TOOLTIP =              StringConstants.getString("NodeLauncherFrame.RANDOMISE_JOIN_POSITION_TOOLTIP"); //$NON-NLS-1$
    private static final String RANDOMISE_JOIN_POSITION_DISABLED_EXPLANATION = StringConstants.getString("NodeLauncherFrame.RANDOMISE_JOIN_POSITION_DISABLED_EXPLANATION"); //$NON-NLS-1$
    private static final String RANDOMISE_JOIN_POSITION_DISABLED_TOOLTIP =     RANDOMISE_JOIN_POSITION_TOOLTIP + RANDOMISE_JOIN_POSITION_DISABLED_EXPLANATION;
    
    private static final String USERNAME_TOOLTIP =                             StringConstants.getString("NodeLauncherFrame.USERNAME_TOOLTIP"); //$NON-NLS-1$
    
	private static final String AUTHENTICATION_METHOD_TITLE =                  StringConstants.getString("NodeLauncherFrame.AUTHENTICATION_METHOD_TITLE"); //$NON-NLS-1$
	private static final String PUBLIC_KEY_AUTH_BUTTON_TEXT =                  StringConstants.getString("NodeLauncherFrame.PUBLIC_KEY_AUTH_BUTTON_TEXT"); //$NON-NLS-1$
	private static final String PUBLIC_KEY_AUTH_TOOLTIP =                      StringConstants.getString("NodeLauncherFrame.PUBLIC_KEY_AUTH_TOOLTIP"); //$NON-NLS-1$
	private static final String PASSWORD_AUTH_BUTTON_TEXT =                    StringConstants.getString("NodeLauncherFrame.PASSWORD_AUTH_BUTTON_TEXT"); //$NON-NLS-1$
	private static final String PASSWORD_AUTH_TOOLTIP =                        StringConstants.getString("NodeLauncherFrame.PASSWORD_AUTH_TOOLTIP"); //$NON-NLS-1$

	private static final String PASSWORD_AUTH_LABEL_TEXT =                     StringConstants.getString("NodeLauncherFrame.PASSWORD_AUTH_LABEL_TEXT"); //$NON-NLS-1$
	private static final String PASSWORD_TOOLTIP =                             StringConstants.getString("NodeLauncherFrame.PASSWORD_TOOLTIP"); //$NON-NLS-1$
	private static final String PASSWORD_DISABLED_EXPLANATION =                StringConstants.getString("NodeLauncherFrame.PASSWORD_DISABLED_EXPLANATION"); //$NON-NLS-1$
	private static final String PASSWORD_DISABLED_TOOLTIP =                    PASSWORD_TOOLTIP + PASSWORD_DISABLED_EXPLANATION;
	
	private static final String PRIVATE_KEY_PATH_LABEL_TEXT =                  StringConstants.getString("NodeLauncherFrame.PRIVATE_KEY_PATH_LABEL_TEXT"); //$NON-NLS-1$
	private static final String PRIVATE_KEY_PATH_TOOLTIP =                     StringConstants.getString("NodeLauncherFrame.PRIVATE_KEY_PATH_TOOLTIP"); //$NON-NLS-1$
	private static final String PRIVATE_KEY_PATH_DISABLED_EXPLANATION =        StringConstants.getString("NodeLauncherFrame.PRIVATE_KEY_PATH_DISABLED_EXPLANATION"); //$NON-NLS-1$
	private static final String PRIVATE_KEY_PATH_DISABLED_TOOLTIP =            PRIVATE_KEY_PATH_TOOLTIP + PRIVATE_KEY_PATH_DISABLED_EXPLANATION;
	
	private static final String PASSPHRASE_LABEL_TEXT =                        StringConstants.getString("NodeLauncherFrame.PASSPHRASE_LABEL_TEXT"); //$NON-NLS-1$
	private static final String PASSPHRASE_TOOLTIP =                           StringConstants.getString("NodeLauncherFrame.PASSPHRASE_TOOLTIP"); //$NON-NLS-1$
	private static final String PASSPHRASE_DISABLED_EXPLANATION =              StringConstants.getString("NodeLauncherFrame.PASSPHRASE_DISABLED_EXPLANATION"); //$NON-NLS-1$
	private static final String PASSPHRASE_DISABLED_TOOLTIP =                  PASSPHRASE_TOOLTIP + PASSPHRASE_DISABLED_EXPLANATION;
	
    private static String DEFAULT_VISUALISER_HOST;
    private static String DEFAULT_TARGET_VISUALISER_HOST;
    private static String DEFAULT_NODES_HOST;
    private static String DEFAULT_KNOWN_NODE_HOST;
    
    static {
        String local_address = ""; //$NON-NLS-1$
        try {
            local_address = InetAddress.getLocalHost().getHostName();
            
            DEFAULT_VISUALISER_HOST =        local_address;
            DEFAULT_TARGET_VISUALISER_HOST = local_address;
            DEFAULT_NODES_HOST =             local_address;
            DEFAULT_KNOWN_NODE_HOST =        local_address;
        }
        catch (UnknownHostException e) { Error.hardError("error getting local address"); } //$NON-NLS-1$
    }
    
    private static final int DEFAULT_FIRST_PORT = 1111;
    private static final int DEFAULT_KNOWN_NODE_PORT = 1111;

    private JTextField visualiser_host_textfield;
    private JTextField first_port_textfield;
    private JTextField nodes_host_textfield;
    private JTextField number_of_nodes_textfield;
    private JTextField known_node_port_textfield;
    private JTextField known_node_host_textfield;
    private JTextField target_visualiser_host_textfield;
    private JTextField private_key_path_textfield;
	private JTextField user_name_textfield;
	private JPasswordField passphrase_textfield;
	private JPasswordField password_textfield;

    private JComboBox process_menu;
    private JRadioButton new_ring_button;
    private JCheckBox randomise_join_position_button;
    private JRadioButton password_auth_button;
	private JButton start_visualiser_button;
	private JButton kill_button;
	private JButton kill_all_button;
	private JButton start_nodes_button;

    private int first_port = DEFAULT_FIRST_PORT;

    protected NodeLauncherFrame() {
        
        super();
        getContentPane().setLayout(new FlowLayout());
        
        setTitle("JChord Node Launcher"); //$NON-NLS-1$
        setBounds(FRAME_POS_X, FRAME_POS_Y, FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Generated code
        final JTabbedPane tabbedPane = new JTabbedPane();
        getContentPane().add(tabbedPane);

        final JPanel visualiser_panel = new JPanel();
        tabbedPane.addTab("Create Visualiser", null, visualiser_panel, null); //$NON-NLS-1$

        start_visualiser_button = new JButton();
		start_visualiser_button.setToolTipText("Starts a ring visualiser window on the specified host"); //$NON-NLS-1$
        visualiser_panel.add(start_visualiser_button);
        start_visualiser_button.setText("Start Visualiser"); //$NON-NLS-1$
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
        visualiser_panel.add(visualiser_host_label);
        visualiser_host_label.setToolTipText(VISUALISER_HOST_TOOLTIP);
        visualiser_host_label.setText("IP"); //$NON-NLS-1$

        visualiser_host_textfield = new JTextField();
        visualiser_panel.add(visualiser_host_textfield);
        visualiser_host_textfield.setToolTipText(VISUALISER_HOST_TOOLTIP);
        visualiser_host_textfield.setText(DEFAULT_VISUALISER_HOST);
        visualiser_host_textfield.setEnabled(false);

        final JPanel nodes_panel = new JPanel();
        tabbedPane.addTab("Create Nodes", null, nodes_panel, null); //$NON-NLS-1$
        nodes_panel.setLayout(new BoxLayout(nodes_panel, BoxLayout.Y_AXIS));

        final JPanel new_nodes_host_panel = new JPanel();
        final FlowLayout flowLayout_1 = new FlowLayout();
        flowLayout_1.setAlignment(FlowLayout.LEFT);
        new_nodes_host_panel.setLayout(flowLayout_1);
        nodes_panel.add(new_nodes_host_panel);
        new_nodes_host_panel.setBorder(new TitledBorder(new EtchedBorder(), "Host:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null)); //$NON-NLS-1$

        final JLabel nodes_host_label = new JLabel();
        new_nodes_host_panel.add(nodes_host_label);
        nodes_host_label.setText("IP"); //$NON-NLS-1$

        nodes_host_textfield = new JTextField();
        new_nodes_host_panel.add(nodes_host_textfield);
        nodes_host_textfield.setEnabled(false);
        nodes_host_textfield.setText(DEFAULT_NODES_HOST);
        
        nodes_host_label.setToolTipText(NODES_HOST_TOOLTIP);
        nodes_host_textfield.setToolTipText(NODES_HOST_TOOLTIP);

        final JLabel first_port_label = new JLabel();
        new_nodes_host_panel.add(first_port_label);
        first_port_label.setToolTipText(FIRST_PORT_TOOLTIP);
        first_port_label.setText("first port in range"); //$NON-NLS-1$

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
        nodes_panel.add(target_visualiser_panel);
        target_visualiser_panel.setBorder(new TitledBorder(new EtchedBorder(), "Target Visualiser:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null)); //$NON-NLS-1$

        final JLabel target_visualiser_host_label = new JLabel();
        target_visualiser_host_label.setToolTipText(TARGET_VISUALISER_HOST_TOOLTIP);
        target_visualiser_panel.add(target_visualiser_host_label);
        target_visualiser_host_label.setText("IP"); //$NON-NLS-1$

        target_visualiser_host_textfield = new JTextField();
        target_visualiser_host_textfield.setToolTipText(TARGET_VISUALISER_HOST_TOOLTIP);
        target_visualiser_panel.add(target_visualiser_host_textfield);
        target_visualiser_host_textfield.setText(DEFAULT_TARGET_VISUALISER_HOST);
        target_visualiser_host_textfield.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (target_visualiser_host_textfield.getText().equals(DEFAULT_TARGET_VISUALISER_HOST)) target_visualiser_host_textfield.setText(""); //$NON-NLS-1$
            }
            public void focusLost(FocusEvent e) {
                if (target_visualiser_host_textfield.getText().equals("")) target_visualiser_host_textfield.setText(DEFAULT_TARGET_VISUALISER_HOST); //$NON-NLS-1$
            }
        });

        final JPanel ring_panel = new JPanel();
        ring_panel.setLayout(new BorderLayout());
        nodes_panel.add(ring_panel);
        ring_panel.setBorder(new TitledBorder(new EtchedBorder(), "Ring:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null)); //$NON-NLS-1$

        final JPanel new_ring_panel = new JPanel();
        new_ring_panel.setLayout(new BoxLayout(new_ring_panel, BoxLayout.Y_AXIS));
        ring_panel.add(new_ring_panel, BorderLayout.WEST);
        new_ring_button = new JRadioButton();
        new_ring_panel.add(new_ring_button);
        new_ring_button.setToolTipText("Create a new ring"); //$NON-NLS-1$
        new_ring_button.setMargin(new Insets(0, 2, 2, 2));
        new_ring_button.setVerticalAlignment(SwingConstants.TOP);
        new_ring_button.setText("Create New Ring"); //$NON-NLS-1$

        ButtonGroup ring_formation_button_group = new ButtonGroup();
        ring_formation_button_group.add(new_ring_button);
        
        randomise_join_position_button = new JCheckBox();
        new_ring_panel.add(randomise_join_position_button);
        randomise_join_position_button.setToolTipText(RANDOMISE_JOIN_POSITION_TOOLTIP);
        randomise_join_position_button.setMargin(new Insets(0, 2, 2, 2));
        randomise_join_position_button.setVerticalAlignment(SwingConstants.TOP);
        randomise_join_position_button.setText("Randomise Join Position"); //$NON-NLS-1$

        final JPanel start_panel = new JPanel();
        nodes_panel.add(start_panel);
        start_nodes_button = new JButton();
		start_panel.add(start_nodes_button);
        start_nodes_button.setToolTipText("Starts specified number of nodes on the specified host"); //$NON-NLS-1$
        start_nodes_button.setText("Start Node(s)"); //$NON-NLS-1$

        final JPanel number_of_nodes_panel = new JPanel();
        start_panel.add(number_of_nodes_panel);
        final JLabel number_of_nodes_label = new JLabel();
        number_of_nodes_panel.add(number_of_nodes_label);
        number_of_nodes_label.setToolTipText(NUMBER_OF_NODES_TOOLTIP);
        number_of_nodes_label.setText("number of nodes:"); //$NON-NLS-1$

        number_of_nodes_textfield = new JTextField();
        number_of_nodes_textfield.setColumns(3);
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

        final JPanel existing_ring_panel = new JPanel();
        existing_ring_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        existing_ring_panel.setLayout(new BoxLayout(existing_ring_panel, BoxLayout.Y_AXIS));
        ring_panel.add(existing_ring_panel, BorderLayout.EAST);

        final JRadioButton join_ring_button = new JRadioButton();
        existing_ring_panel.add(join_ring_button);
        join_ring_button.setToolTipText("Join via a known node on an existing ring"); //$NON-NLS-1$
        join_ring_button.setText("Join Existing Ring"); //$NON-NLS-1$
        ring_formation_button_group.add(join_ring_button);

        final JPanel known_node_panel = new JPanel();
        existing_ring_panel.add(known_node_panel);
        known_node_panel.setBorder(new TitledBorder(new EtchedBorder(), "Known Node:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null)); //$NON-NLS-1$
        final FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        known_node_panel.setLayout(flowLayout);

        final JLabel known_node_host_label = new JLabel();
        known_node_host_label.setToolTipText(KNOWN_NODE_HOST_TOOLTIP);
        known_node_panel.add(known_node_host_label);
        known_node_host_label.setText("IP"); //$NON-NLS-1$

        known_node_host_textfield = new JTextField();
        known_node_host_textfield.setToolTipText(KNOWN_NODE_HOST_TOOLTIP + KNOWN_NODE_DISABLED_EXPLANATION);
        known_node_panel.add(known_node_host_textfield);
        known_node_host_textfield.setText(DEFAULT_KNOWN_NODE_HOST);

        final JLabel known_node_port_label = new JLabel();
        known_node_port_label.setToolTipText(KNOWN_NODE_PORT_TOOLTIP + KNOWN_NODE_DISABLED_EXPLANATION);
        known_node_panel.add(known_node_port_label);
        known_node_port_label.setText("port"); //$NON-NLS-1$

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
                
                if (selected) known_node_host_textfield.setToolTipText(KNOWN_NODE_HOST_DISABLED_TOOLTIP);
                else          known_node_host_textfield.setToolTipText(KNOWN_NODE_HOST_TOOLTIP);
                
                if (selected) known_node_port_textfield.setToolTipText(KNOWN_NODE_PORT_DISABLED_TOOLTIP);
                else          known_node_port_textfield.setToolTipText(KNOWN_NODE_PORT_TOOLTIP);
                
                if (!selected) randomise_join_position_button.setToolTipText(RANDOMISE_JOIN_POSITION_DISABLED_TOOLTIP);
                else           randomise_join_position_button.setToolTipText(RANDOMISE_JOIN_POSITION_TOOLTIP);
            }
        });
        new_ring_button.setSelected(true);
        
        final JPanel kill_panel = new JPanel();
        tabbedPane.addTab("Kill Nodes", null, kill_panel, null); //$NON-NLS-1$
        
        process_menu = new JComboBox();
        process_menu.setToolTipText("Select a process"); //$NON-NLS-1$
        kill_panel.add(process_menu);
        
        kill_button = new JButton();
		kill_button.setToolTipText("Kill the selected node process"); //$NON-NLS-1$
        kill_panel.add(kill_button);
        kill_button.setText("Kill Process"); //$NON-NLS-1$
        kill_button.setEnabled(false);
        
        kill_all_button = new JButton();
		kill_all_button.setToolTipText("Kill all the remaining node processes"); //$NON-NLS-1$
        kill_panel.add(kill_all_button);
        kill_all_button.setText("Kill All"); //$NON-NLS-1$
        kill_all_button.setEnabled(false);
        
        // Generated code
        final JPanel auth_panel = new JPanel();
        auth_panel.setLayout(new BorderLayout());
        tabbedPane.addTab("Authentication", null, auth_panel, null); //$NON-NLS-1$
        
        // Generated code
        final JPanel user_name_panel = new JPanel();
        user_name_panel.setBorder(new EmptyBorder(50, 5, 5, 5));
        final BorderLayout borderLayout = new BorderLayout();
        borderLayout.setHgap(5);
        user_name_panel.setLayout(borderLayout);
        auth_panel.add(user_name_panel, BorderLayout.NORTH);
        
        // Generated code
        final JLabel user_name_label = new JLabel();
        user_name_panel.add(user_name_label, BorderLayout.WEST);
        user_name_label.setText("user name"); //$NON-NLS-1$
        user_name_label.setToolTipText(USERNAME_TOOLTIP);
        
        // Generated code
        user_name_textfield = new JTextField(System.getProperty(StringConstants.getString("NodeLauncherFrame.USER_NAME_PROPERTY_KEY"))); //$NON-NLS-1$
        user_name_textfield.selectAll();
        user_name_panel.add(user_name_textfield);
        user_name_textfield.setToolTipText(USERNAME_TOOLTIP);
        
        // Generated code
        final JPanel auth_method_panel = new JPanel();
        auth_panel.add(auth_method_panel, BorderLayout.SOUTH);
        auth_method_panel.setLayout(new BoxLayout(auth_method_panel, BoxLayout.Y_AXIS));
        auth_method_panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), AUTHENTICATION_METHOD_TITLE, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null)); //$NON-NLS-1$
        
        // Generated code
        final JPanel password_button_panel = new JPanel();
        password_button_panel.setLayout(new BorderLayout());
        auth_method_panel.add(password_button_panel);
        
        // Generated code
        password_auth_button = new JRadioButton();
        password_button_panel.add(password_auth_button, BorderLayout.WEST);
        password_auth_button.setVerticalAlignment(SwingConstants.TOP);
        password_auth_button.setToolTipText(PASSWORD_AUTH_TOOLTIP); //$NON-NLS-1$
        password_auth_button.setMargin(new Insets(0, 2, 2, 2));
        password_auth_button.setText(PASSWORD_AUTH_BUTTON_TEXT); //$NON-NLS-1$

        ButtonGroup authentication_button_group = new ButtonGroup();
        authentication_button_group.add(password_auth_button);

        // Generated code
        final JPanel password_input_panel = new JPanel();
        password_input_panel.setLayout(new BorderLayout());
        auth_method_panel.add(password_input_panel);
        
        // Generated code
        final JLabel password_label = new JLabel();
        password_label.setBorder(new EmptyBorder(0, 10, 0, 0));
        password_label.setText(PASSWORD_AUTH_LABEL_TEXT); //$NON-NLS-1$
        password_input_panel.add(password_label, BorderLayout.WEST);
        
        // Generated code
        password_textfield = new JPasswordField();
        password_textfield.setColumns(25);
        password_input_panel.add(password_textfield, BorderLayout.EAST);
        
        // Generated code
        final JPanel public_key_button_panel = new JPanel();
        public_key_button_panel.setLayout(new BorderLayout());
        auth_method_panel.add(public_key_button_panel);
        
        // Generated code
        final JRadioButton public_key_auth_button = new JRadioButton();
        authentication_button_group.add(public_key_auth_button);
        public_key_button_panel.add(public_key_auth_button, BorderLayout.WEST);
        public_key_auth_button.setToolTipText(PUBLIC_KEY_AUTH_TOOLTIP); //$NON-NLS-1$
        public_key_auth_button.setText(PUBLIC_KEY_AUTH_BUTTON_TEXT); //$NON-NLS-1$
        
        // Generated code
        final JPanel private_key_path_input_panel = new JPanel();
        private_key_path_input_panel.setLayout(new BorderLayout());
        auth_method_panel.add(private_key_path_input_panel);
        
        // Generated code
        final JLabel private_key_path_label = new JLabel();
        private_key_path_label.setBorder(new EmptyBorder(0, 10, 0, 0));
        private_key_path_input_panel.add(private_key_path_label, BorderLayout.WEST);
        private_key_path_label.setText(PRIVATE_KEY_PATH_LABEL_TEXT); //$NON-NLS-1$
        
        // Generated code
        private_key_path_textfield = new JTextField();
        private_key_path_textfield.setColumns(25);
        private_key_path_input_panel.add(private_key_path_textfield, BorderLayout.EAST);
        
        // Generated code
        final JPanel passphrase_input_panel = new JPanel();
        passphrase_input_panel.setLayout(new BorderLayout());
        auth_method_panel.add(passphrase_input_panel);
        
        // Generated code
        final JLabel passphrase_label = new JLabel();
        passphrase_label.setBorder(new EmptyBorder(0, 10, 0, 0));
        passphrase_input_panel.add(passphrase_label, BorderLayout.WEST);
        passphrase_label.setText(PASSPHRASE_LABEL_TEXT); //$NON-NLS-1$
        
        // Generated code
        passphrase_textfield = new JPasswordField();
        passphrase_textfield.setColumns(25);
        passphrase_input_panel.add(passphrase_textfield, BorderLayout.EAST);
        
        password_auth_button.addChangeListener(new ChangeListener() {
            boolean selected = false;
            public void stateChanged(ChangeEvent e) {
                selected = !selected;
                password_textfield.setEnabled(selected);
                private_key_path_textfield.setEnabled(!selected);
                passphrase_textfield.setEnabled(!selected);
                password_label.setEnabled(selected);
                private_key_path_label.setEnabled(!selected);
                passphrase_label.setEnabled(!selected);
   
                if (selected) password_label.setToolTipText(PASSWORD_TOOLTIP);
                else          password_label.setToolTipText(PASSWORD_DISABLED_TOOLTIP);
                
                if (selected) password_textfield.setToolTipText(PASSWORD_TOOLTIP);
                else          password_textfield.setToolTipText(PASSWORD_DISABLED_TOOLTIP);
                
                if (!selected) private_key_path_label.setToolTipText(PRIVATE_KEY_PATH_TOOLTIP);
                else           private_key_path_label.setToolTipText(PRIVATE_KEY_PATH_DISABLED_TOOLTIP);
                	
                if (!selected) private_key_path_textfield.setToolTipText(PRIVATE_KEY_PATH_TOOLTIP);
                else           private_key_path_textfield.setToolTipText(PRIVATE_KEY_PATH_DISABLED_TOOLTIP);
                
                if (!selected) passphrase_label.setToolTipText(PASSPHRASE_TOOLTIP);
                else           passphrase_label.setToolTipText(PASSPHRASE_DISABLED_TOOLTIP);
                	
                if (!selected) passphrase_textfield.setToolTipText(PASSPHRASE_TOOLTIP);
                else           passphrase_textfield.setToolTipText(PASSPHRASE_DISABLED_TOOLTIP);
            }
        });
        password_auth_button.setSelected(true);
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

    protected void setKnownNodeHost() {
        
        setKnownNodeHost(DEFAULT_KNOWN_NODE_HOST);
    }
    
    protected void setKnownNodeHost(String known_node_host) {
	    
	    known_node_host_textfield.setText(known_node_host);
	}

	protected void setKnownNodePort() {
	    
	    setKnownNodePort(DEFAULT_KNOWN_NODE_PORT);
	}

	protected void setKnownNodePort(int known_node_port) {
        
        known_node_port_textfield.setText(String.valueOf(known_node_port));
    }
    
    protected void setKillAllAction(ActionListener listener) {
	    
	    kill_all_button.addActionListener(listener);
	}

	protected void setKillAction(ActionListener listener) {
	    
	    kill_button.addActionListener(listener);
	}
	
	protected String getUsername() {
		
		return user_name_textfield.getText();
	}
	
	protected String getPassword() {
		
		return new String(password_textfield.getPassword());
	}
	
	protected String getPrivateKeyPath() {
		
		return private_key_path_textfield.getText();
	}
	
	protected String getPassphrase() {
		
		return new String(passphrase_textfield.getPassword());
	}

	private String stripLeadingBlanks(String text) {
	    
	    while (text.startsWith(" ")) text = text.substring(1); //$NON-NLS-1$
	    return text;
	}
}