/**
 * Created on Aug 5, 2005 at 4:36:18 PM.
 */
package uk.ac.stand.dcs.asa.jchord.deploy;

import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.FormatHostInfo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Provides GUI for launching JChord nodes in multiple address spaces.
 *
 * @author graham
 */
public class NodeLauncherGUI implements Observer {

    private static final int DISPLAY_OFFSET = 50;
    
	private Map label_to_process_map;
	private NodeLauncherFrame control_frame;
	private NodeLauncher node_launcher;
	
	public static void main(String[] args) {
		
		new NodeLauncherGUI();
	}

    public NodeLauncherGUI() {

        label_to_process_map = new HashMap();
        
		control_frame = new NodeLauncherFrame();
		node_launcher = new NodeLauncher();
		
		node_launcher.addObserver(this);
        
        control_frame.setStartVisualiserAction(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                try { node_launcher.createAdminNodeProcess(control_frame.getVisualiserHost()); }
                catch (IOException e1) {
                    Error.exceptionError(StringConstants.getString("VISUALISER_CREATION_ERROR_MESSAGE"), e1); //$NON-NLS-1$
                }
            }
        });
        
        control_frame.setStartNodesAction(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                String visualiser_host = control_frame.getTargetVisualiserHost();
                String nodes_host = control_frame.getNodesHost();
                
                int first_port = control_frame.getFirstPort();
                int number_of_nodes = control_frame.getNumberOfNodes();
                
                if (control_frame.createNewRing()) {
                    
                    boolean randomise_join_position = control_frame.randomiseJoinPosition();
                    node_launcher.launchNodes(number_of_nodes, nodes_host, first_port, visualiser_host, randomise_join_position, null);
                }
                else  {
                    
	                String known_node_host = control_frame.getKnownNodeHost();
	                int known_node_port = control_frame.getKnownNodePort();
	                
	                node_launcher.launchNodes(number_of_nodes, nodes_host, first_port, known_node_host, known_node_port, visualiser_host);
                }
                
                control_frame.setFirstPort(first_port + number_of_nodes);
            }
        });
        
        control_frame.setKillAction(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
            	killProcess();
            }
        });
        
        control_frame.setKillAllAction(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                while (!label_to_process_map.isEmpty()) killProcess();
            }
        });
           
        // Set the control frame's position in the top right of the screen.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen_size = toolkit.getScreenSize();
        control_frame.setLocation(screen_size.width - control_frame.getWidth() - DISPLAY_OFFSET, DISPLAY_OFFSET);
        
        control_frame.setVisible(true);
    }
    
	public void update(Observable o, Object arg) {
		
		if (arg instanceof ProcessEvent) {
			
			ProcessEvent event = (ProcessEvent) arg;
			
			String label = makeLabel(event.getHost(), event.getPort());
	
	        control_frame.addNodeToMenu(label);
	        label_to_process_map.put(label, event.getProcess());
		}
	}
	
    /********************************************************************************************************************************************/
    
    private void killProcess() {
        
        String node_label = control_frame.selectedNode();
        Process p = (Process) label_to_process_map.get(node_label);
        p.destroy();
        control_frame.removeNodeFromMenu(node_label);
        label_to_process_map.remove(node_label);
        
        // If the known node has been killed, reset the entry in the GUI to a live one.

        if (node_label.equals(makeLabel(control_frame.getKnownNodeHost(), control_frame.getKnownNodePort()))) {
            
            String first_live_node = control_frame.selectedNode();
            
            if (first_live_node == null) {
                control_frame.setKnownNodeHost();
                control_frame.setKnownNodePort();
            }
            else {
                control_frame.setKnownNodeHost(labelToHost(first_live_node));
                control_frame.setKnownNodePort(labelToPort(first_live_node));
            }
        }
    }

    private String makeLabel(String host, int port) {
        
        return FormatHostInfo.formatHostName(host, port);
    }


    private String labelToHost(String label) {
        
        return label.substring(0, label.indexOf(':'));
    }

    private int labelToPort(String label) {
        
        return Integer.parseInt(label.substring(label.indexOf(':') + 1));
    }
}
