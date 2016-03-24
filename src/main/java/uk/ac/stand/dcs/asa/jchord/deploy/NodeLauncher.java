/*
 * Created on Jan 6, 2005 at 8:53:26 PM.
 */
package uk.ac.stand.dcs.asa.jchord.deploy;

import com.mindbright.ssh2.SSH2SimpleClient;
import uk.ac.stand.dcs.asa.applications.ringVis.JChordViewerGUI;
import uk.ac.stand.dcs.asa.applications.ringVis.SingleNode;
import uk.ac.stand.dcs.asa.util.CommandLineArgs;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.Network;
import uk.ac.stand.dcs.asa.util.Processes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Observable;

/**
 * Provides API for launching JChord nodes in multiple address spaces.
 *
 * @author graham
 */
public class NodeLauncher extends Observable {
	 
	private static final String LOCAL_HOST = "localhost";
	
	//private static final long RANDOM_SEED = 28377363;
	
	private static final String OS_NAME_MAC =	 StringConstants.getString("OS_NAME_MAC"); //$NON-NLS-1$
	private static final String OS_NAME_WINDOWS = StringConstants.getString("OS_NAME_WINDOWS"); //$NON-NLS-1$
	private static final String OS_NAME_LINUX =   StringConstants.getString("OS_NAME_LINUX"); //$NON-NLS-1$
	
	private static final String OS_NAME = System.getProperty("os.name"); //$NON-NLS-1$
	
	private static final String QUOTE = "\""; //$NON-NLS-1$

	private static String DEFAULT_TOOLS_JAR_WINDOWS = StringConstants.getString("DEFAULT_TOOLS_JAR_WINDOWS"); //$NON-NLS-1$
	private static String DEFAULT_TOOLS_JAR_LINUX =   StringConstants.getString("DEFAULT_TOOLS_JAR_LINUX"); //$NON-NLS-1$
	
	private static String VIEWER_CLASS_NAME = JChordViewerGUI.class.getName();
	private static String NODE_CLASS_NAME =   SingleNode.class.getName();

	/********************************************************************************************************************************************/
	
	private String classpath_flag, java_command;
	
	/********************************************************************************************************************************************/
	
	/**
	 * <pre>Either launches specified set of nodes, or displays GUI to allow them to be specified interactively.
	 * 
	 * Usage: java NodeLauncher [-n<number of nodes>] [-h<node host:port>] [-k<known node host:port> | -r] [-a<admin node host>] [-l] [-t<tools.jar path>]
	 * 
	 * -n specifies the number of nodes to be launched
	 * -h specifies the address of the host on which the nodes are to be launched, and the port of the first node
	 * -k specifies the address and port of a node within an existing ring
	 * -r specifies that nodes forming a new ring should join using a randomly selected node already in the ring
	 * -a specifies the address of an admin node
	 * -l specifies that a new admin node should be launched at the address specified by -a
	 * -t specifies the location of the tools.jar file
	 * 
	 * If either -n or -h is not specified, a GUI window is displayed and the other arguments (apart from -t) are ignored.
	 * 
	 * Examples:
	 * 
	 * java NodeLauncher -n3 -hedradour.dcs.st-and.ac.uk:1111 -aedradour.dcs.st-and.ac.uk -r -l
	 * java NodeLauncher -n2 -hedradour.dcs.st-and.ac.uk:1114 -kedradour.dcs.st-and.ac.uk:1111 -aedradour.dcs.st-and.ac.uk
	 * </pre>
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {

		Map args_map = CommandLineArgs.parseCommandLineArgs(args);

		// Read command line arguments.
		String number_of_nodes_string =  (String)args_map.get("-n");
		String node_address_port =       (String)args_map.get("-h");
		String known_node_host_port =	 (String)args_map.get("-k");
		String randomise_join_position = (String)args_map.get("-r");
		String admin_host =			 (String)args_map.get("-a");
		String admin_node_flag =		 (String)args_map.get("-l");
		String tools_path =			 (String)args_map.get("-t");
		
		NodeLauncher launcher;
		
		if (tools_path != null) launcher = new NodeLauncher(tools_path);
		else	                   launcher = new NodeLauncher();

		try {
			
			int number_of_nodes = Integer.parseInt(number_of_nodes_string);

			InetSocketAddress node_address = Network.processHostPortParameter(node_address_port, 0);
			
			String nodes_host =	node_address.getHostName();
			int nodes_first_port = node_address.getPort();

			// Launch a new admin node if necessary.
			if (admin_node_flag != null && admin_host != null) launcher.createAdminNodeProcess(admin_host);
			
			// Launch the nodes.
			if (known_node_host_port == null) launcher.launchNewRing(number_of_nodes, nodes_host, nodes_first_port, admin_host, (randomise_join_position != null));
			else							  launcher.launchIntoExistingRing(number_of_nodes, nodes_host, nodes_first_port, known_node_host_port, admin_host);
		}
		catch (NumberFormatException e) { usage(); }
		catch (NullPointerException e)  { usage(); }
		catch (UnknownHostException e)  { Error.exceptionError("unknown host", e); }
		catch (IOException e)		   { Error.exceptionError("IO exception", e); }
	}

	/**
	 * Creates a new node launcher with default tools.jar location.
	 */
	public NodeLauncher() {
		
		this(getToolsJarPath());
	}
	
	/**
	 * Creates a new node launcher with specified tools.jar location.
	 * 
	 * @param tools_jar_path the location of the tools.jar file
	 */
	public NodeLauncher(String tools_jar_path) {
		
		// TODO bug on OSX when Eclipse workspace folder name includes space.
	  
		String user_dir =	   System.getProperty ("user.dir"); //$NON-NLS-1$
		String java_home_dir =  System.getProperty ("java.home"); //$NON-NLS-1$
		String file_separator = System.getProperty ("file.separator"); //$NON-NLS-1$
		String path_separator = System.getProperty ("path.separator"); //$NON-NLS-1$
		
		String project_jar_path =  user_dir + file_separator + "bin" + file_separator + StringConstants.getString("PROJECT_JAR_NAME"); //$NON-NLS-1$ //$NON-NLS-2$
		String compiler_jar_path = user_dir + file_separator + "lib" + file_separator + StringConstants.getString("COMPILER_JAR_NAME"); //$NON-NLS-1$ //$NON-NLS-2$
		String soap_jar_path =	 user_dir + file_separator + "lib" + file_separator + StringConstants.getString("SOAP_JAR_NAME"); //$NON-NLS-1$ //$NON-NLS-2$
		String xerces_jar_path =   user_dir + file_separator + "lib" + file_separator + StringConstants.getString("XERCES_JAR_NAME"); //$NON-NLS-1$ //$NON-NLS-2$
		String rafda_jar_path =	user_dir + file_separator + "lib" + file_separator + StringConstants.getString("RRT_JAR_NAME"); //$NON-NLS-1$ //$NON-NLS-2$
		String bcel_jar_path =	 user_dir + file_separator + "lib" + file_separator + StringConstants.getString("BCEL_JAR_NAME"); //$NON-NLS-1$ //$NON-NLS-2$
		String jgraph_jar_path =   user_dir + file_separator + "lib" + file_separator + StringConstants.getString("JGRAPH_JAR_NAME"); //$NON-NLS-1$ //$NON-NLS-2$
		String jug_jar_path =	  user_dir + file_separator + "lib" + file_separator + StringConstants.getString("JUG_JAR_NAME"); //$NON-NLS-1$ //$NON-NLS-2$
		
		String classpath = project_jar_path +  path_separator +
						   compiler_jar_path + path_separator +
						   soap_jar_path +	 path_separator +
						   xerces_jar_path +   path_separator +
						   rafda_jar_path +	path_separator +
						   bcel_jar_path +	 path_separator +
						   jgraph_jar_path +   path_separator +
						   jug_jar_path;
		
		// Platform dependent parts
		
		if (OS_NAME.equals(OS_NAME_LINUX)) {
			
			// Paths don't need to be quoted, do need tools.jar in classpath.
			//
			// Construct command to invoke a new JVM using appropriate classpath. Example command (actually all on one line):
			//
			//	/usr/java/j2sdk1.4.2_04/jre/bin/java
			//		   -cp /user/staff/graham/workspace/JChord/bin/JChord.jar:/user/staff/graham/workspace/JChord/lib/javacompiler.jar:
			//			   /user/staff/graham/workspace/JChord/lib/soap.jar:/user/staff/graham/workspace/JChord/lib/xerces.jar:
			//			   /user/staff/graham/workspace/JChord/lib/rrt.jar:/user/staff/graham/workspace/JChord/lib/bcel-5.1.jar:
			//			   /user/staff/graham/workspace/JChord/lib/jgraph.jar:/user/staff/graham/workspace/JChord/lib/jug.jar:
			//			   /usr/java/j2sdk1.4.2_04/lib/tools.jar

			java_command = java_home_dir + file_separator + "bin" + file_separator + "java"; //$NON-NLS-1$ //$NON-NLS-2$
			
			classpath_flag = " -cp " + classpath + path_separator + tools_jar_path; //$NON-NLS-1$
		}
		else
		if (OS_NAME.equals(OS_NAME_MAC)) {
			
			// Paths don't need to be quoted, don't need tools.jar in classpath.
			//
			// Construct command to invoke a new JVM using appropriate classpath. Example command (actually all on one line):
			//
			//	/System/Library/Frameworks/JavaVM.framework/Versions/1.4.2/Home/bin/java
			//		   -cp /Users/graham/Documents/Research/ASA/code/JChord/bin/JChord.jar:/Users/graham/Documents/Research/ASA/code/JChord/lib/javacompiler.jar:
			//			   /Users/graham/Documents/Research/ASA/code/JChord/lib/soap.jar:/Users/graham/Documents/Research/ASA/code/JChord/lib/xerces.jar:
			//			   /Users/graham/Documents/Research/ASA/code/JChord/lib/rrt.jar:/Users/graham/Documents/Research/ASA/code/JChord/lib/bcel-5.1.jar:
			//			   /Users/graham/Documents/Research/ASA/code/JChord/lib/jgraph.jar:/Users/graham/Documents/Research/ASA/code/JChord/lib/jug.jar

			java_command = java_home_dir + file_separator + "bin" + file_separator + "java"; //$NON-NLS-1$ //$NON-NLS-2$
			
			classpath_flag = " -cp " + classpath; //$NON-NLS-1$
		}
		else
		if (OS_NAME.startsWith(OS_NAME_WINDOWS)) {
			
		   	// Paths do need to be quoted, do need tools.jar in classpath.
			//
			// Construct command to invoke a new JVM using appropriate classpath. Example command (actually all on one line):
			//
			//	"C:\Program Files\Java\j2re1.4.2_05\bin\java"
			//		   -cp "C:\Documents and Settings\graham\Desktop\workspace\JChord\bin\JChord.jar;C:\Documents and Settings\graham\Desktop\workspace\JChord\lib\javacompiler.jar;
			//				C:\Documents and Settings\graham\Desktop\workspace\JChord\lib\soap.jar;C:\Documents and Settings\graham\Desktop\workspace\JChord\lib\xerces.jar;
			//				C:\Documents and Settings\graham\Desktop\workspace\JChord\lib\rrt.jar;C:\Documents and Settings\graham\Desktop\workspace\JChord\lib\bcel-5.1.jar;
			//				C:\Documents and Settings\graham\Desktop\workspace\JChord\lib\jgraph.jar;C:\Documents and Settings\graham\Desktop\workspace\JChord\lib\jug.jar;
			//				C:\j2sdk1.4.2_04\lib\tools.jar"

			java_command = QUOTE + java_home_dir + file_separator + "bin" + file_separator + "java" + QUOTE; //$NON-NLS-1$ //$NON-NLS-2$
			
			classpath_flag = " -cp " + QUOTE + classpath + path_separator + tools_jar_path + QUOTE; //$NON-NLS-1$
		}
		else Error.hardError(StringConstants.getString("UNKNOWN_OS_MESSAGE") + OS_NAME); //$NON-NLS-1$
	}
	
	/********************************************************************************************************************************************/

	/**
	 * Launches a number of nodes on the local host, running on consecutively numbered ports. The nodes form a new ring. An admin host
	 * for receiving events is specified.
	 * 
	 * @param number_of_nodes the number of nodes to launch
	 * @param nodes_first_port the first port in the sequence
	 * @param admin_host the IP name or address of the host to receive admin events
	 * @param randomise_join_position true if nodes other than the first created should pick a random node to join
	 */
	public Process[] launchLocalNodes(int number_of_nodes, int nodes_first_port, String admin_host, boolean randomise_join_position) {
		
		return launchNodes(number_of_nodes, LOCAL_HOST, nodes_first_port, admin_host, randomise_join_position, null);
	}

	/**
	 * Launches a number of nodes on the given host, running on consecutively numbered ports. The nodes form a new ring. No admin host
	 * for receiving events is specified.
	 * 
	 * @param number_of_nodes the number of nodes to launch
	 * @param nodes_host the IP name or address of the host on which the nodes are to be created (currently only local host)
	 * @param nodes_first_port the first port in the sequence
	 * @param randomise_join_position true if nodes other than the first created should pick a random node to join
	 */
	public Process[] launchNodes(int number_of_nodes, String nodes_host, int nodes_first_port, boolean randomise_join_position) {
		
		return launchNodes(number_of_nodes, nodes_host, nodes_first_port, "", randomise_join_position, null);
	}
	
	/**
	 * Launches a number of nodes on the given host, running on consecutively numbered ports. The nodes join an existing ring. No admin host
	 * for receiving events is specified.
	 * 
	 * @param number_of_nodes the number of nodes to launch
	 * @param nodes_host the IP name or address of the host on which the nodes are to be created (currently only local host)
	 * @param nodes_first_port the first port in the sequence
	 * @param known_node_host the IP name or address of a host in the existing ring
	 * @param known_node_port the port on which the host in the existing ring is communicating
	 */
	public Process[] launchNodes(int number_of_nodes, String nodes_host, int nodes_first_port, String known_node_host, int known_node_port) {

		return launchNodes(number_of_nodes, nodes_host, nodes_first_port, known_node_host, known_node_port, "");
	}

	/**
	 * Launches a number of nodes on the given host, running on consecutively numbered ports. The nodes form a new ring. An admin host
	 * for receiving events is specified.
	 * 
	 * @param number_of_nodes the number of nodes to launch
	 * @param nodes_host the IP name or address of the host on which the nodes are to be created (currently only local host)
	 * @param nodes_first_port the first port in the sequence
	 * @param admin_host the IP name or address of the host to receive admin events
	 * @param randomise_join_position true if nodes other than the first created should pick a random node to join
	 * @param ssh_client
	 */
	public Process[] launchNodes(int number_of_nodes, String nodes_host, int nodes_first_port, String admin_host, boolean randomise_join_position, SSH2SimpleClient ssh_client) {
		
		// Keep track of processes.
		Process[] processes = new Process[number_of_nodes];
		
		// Keep track of where the nodes have been created.
		InetSocketAddress[] node_addresses = new InetSocketAddress[number_of_nodes];

		// Create first node forming new ring.
//		try {
//			if (admin_host.equals("")) processes[0] = createLocalNodeProcess(nodes_first_port);
//			else					   processes[0] = createLocalNodeProcess(admin_host, nodes_host, nodes_first_port);
			
			node_addresses[0] = new InetSocketAddress(nodes_host, nodes_first_port);
//		}
//		catch (IOException e1) { Error.exceptionError(StringConstants.getString("FIRST_NODE_CREATION_ERROR_MESSAGE"), e1); } //$NON-NLS-1$
		
		// Create other nodes joining new ring.
		int port = nodes_first_port + 1;
		
		//Random rand = new Random(RANDOM_SEED);

		for (int i = 1; i < number_of_nodes; i++) {
			
			//int node_to_join_index = 0;
			//if (randomise_join_position) node_to_join_index = rand.nextInt(i);
			
			//String node_to_join_address = node_addresses[node_to_join_index].getHostName();
			//int node_to_join_port =	   node_addresses[node_to_join_index].getPort();
			
//			try {
//				if (admin_host.equals("")) processes[i] = createLocalNodeProcess(nodes_host, port, node_to_join_address, node_to_join_port);
//				else					   processes[i] = createLocalNodeProcess(admin_host, nodes_host, port, node_to_join_address, node_to_join_port);
//			}
//			catch (IOException e1) { Error.exceptionError(StringConstants.getString("SUBSIDIARY_NODE_CREATION_ERROR_MESSAGE"), e1); } //$NON-NLS-1$
			
			node_addresses[i] = new InetSocketAddress(nodes_host, port);
			
			port++;
		}
		
		return processes;
	}
	
	/**
	 * Launches a number of nodes on the given host, running on consecutively numbered ports. The nodes join an existing ring. An admin host
	 * for receiving events is specified.
	 * 
	 * @param number_of_nodes the number of nodes to launch
	 * @param nodes_host the IP name or address of the host on which the nodes are to be created (currently only local host)
	 * @param nodes_first_port the first port in the sequence
	 * @param known_node_host the IP name or address of a host in the existing ring
	 * @param known_node_port the port on which the host in the existing ring is communicating
	 * @param admin_host the IP name or address of the host to receive admin events
	 */
	public Process[] launchNodes(int number_of_nodes, String nodes_host, int nodes_first_port, String known_node_host, int known_node_port, String admin_host) {
		
		// Keep track of processes.
		Process[] processes = new Process[number_of_nodes];
		
		int port = nodes_first_port;
		
		for (int i = 0; i < number_of_nodes; i++) {
			
//			try {
//				if (admin_host.equals("")) processes[i] = createLocalNodeProcess(nodes_host, port, known_node_host, known_node_port);
//				else					   processes[i] = createLocalNodeProcess(admin_host, nodes_host, port, known_node_host, known_node_port);
//			}
//			catch (IOException e1) { Error.exceptionError(StringConstants.getString("SUBSIDIARY_NODE_CREATION_ERROR_MESSAGE"), e1); } //$NON-NLS-1$
//			
			port++;
		}
		
		return processes;
	}

   /**
	 * Launches a new admin node on the local host, running in a new OS process.
	 * 
	 * @return the new OS process
	 * @throws IOException if an error occurs when reading output from the process
	 */
	public Process createAdminNodeProcess() throws IOException {
		
		return createAdminNodeProcess(LOCAL_HOST);
	}
	
	/**
	 * Launches a new admin node, running in a new OS process.
	 * 
	 * @param admin_host the IP name or address of the host on which the node is to be created (currently only local host)
	 * @return the new OS process
	 * @throws IOException if an error occurs when reading output from the process
	 */
	public Process createAdminNodeProcess(String admin_host) throws IOException {
		
		String create_admin_node_command = makeVisualiserCreationCommand(admin_host);
		return Processes.runProcess(create_admin_node_command);
	}
	
	/**
	 * Launches a new isolated node to form a new ring, running in a new OS process, on the local host.
	 * @param port the port on which the node should communicate
	 * 
	 * @return the new OS process
	 * @throws IOException if an error occurs when reading output from the process
	 */
	public Process createLocalNodeProcess(int port) throws IOException {
		
		String local_host = InetAddress.getLocalHost().getHostName();

		String create_node_command = makeNodeCreationCommand(local_host, port);
		
		return runProcessAndNotifyObservers(create_node_command, local_host, port, null);
	}
	
	/**
	 * Launches a new isolated node to form a new ring, running in a new OS process, on a remote host.
	 * 
	 * @param node_host the IP name or address of the host on which the node is to be created
	 * @param port the port on which the node should communicate
	 * @return the new OS process
	 * @throws IOException if an error occurs when reading output from the process
	 */
	public Process createRemoteNodeProcess(String node_host, int port, SSH2SimpleClient ssh_client) throws IOException {
		
		String create_node_command = makeNodeCreationCommand(node_host, port);
		
		return runProcessAndNotifyObservers(create_node_command, node_host, port, ssh_client);
	}
	
	/**
	 * Launches a new isolated node to form a new ring, running in a new OS process, on the local host.
	 * 
	 * @param admin_host the IP name or address of the host to receive admin events
	 * @param port the port on which the node should communicate
	 * @return the new OS process
	 * @throws IOException if an error occurs when reading output from the process
	 */
	public Process createLocalNodeProcess(String admin_host, int port) throws IOException {
		
		String local_host = InetAddress.getLocalHost().getHostName();

		String create_node_command = makeNodeCreationCommand(admin_host, local_host, port);
		
		return runProcessAndNotifyObservers(create_node_command, local_host, port, null);
	}
	
	/**
	 * Launches a new isolated node to form a new ring, running in a new OS process, on a remote host.
	 * 
	 * @param admin_host the IP name or address of the host to receive admin events
	 * @param node_host the IP name or address of the host on which the node is to be created
	 * @param port the port on which the node should communicate
	 * @param ssh_client an SSH client connection to the remote host
	 * @return the new OS process
	 * @throws IOException if an error occurs when reading output from the process
	 */
	public Process createRemoteNodeProcess(String admin_host, String node_host, int port, SSH2SimpleClient ssh_client) throws IOException {
		
		String create_node_command = makeNodeCreationCommand(admin_host, node_host, port);
		
		return runProcessAndNotifyObservers(create_node_command, node_host, port, ssh_client);
	}
	
	/**
	 * Launches a new node to join an existing ring, running in a new OS process, on the local host.
	 * 
	 * @param port the port on which the node should communicate
	 * @param known_node_host the IP name or address of a host in the existing ring
	 * @param known_node_port the port on which the host in the existing ring is communicating
	 * @return the new OS process
	 * @throws IOException if an error occurs when reading output from the process
	 */
	public Process createLocalNodeProcess(int port, String known_node_host, int known_node_port) throws IOException {
		
		String local_host = InetAddress.getLocalHost().getHostName();

		String create_node_command = makeNodeCreationCommand(local_host, port, known_node_host, known_node_port);
		
		return runProcessAndNotifyObservers(create_node_command, local_host, port, null);
	}

	/**
	 * Launches a new node to join an existing ring, running in a new OS process, on a remote host.
	 * 
	 * @param node_host the IP name or address of the remote host on which the node is to be created
	 * @param port the port on which the node should communicate
	 * @param known_node_host the IP name or address of a host in the existing ring
	 * @param known_node_port the port on which the host in the existing ring is communicating
	 * @param ssh_client an SSH client connection to the remote host
	 * @return the new OS process
	 * @throws IOException if an error occurs when reading output from the process
	 */
	public Process createRemoteNodeProcess(String node_host, int port, String known_node_host, int known_node_port, SSH2SimpleClient ssh_client) throws IOException {
		
		String create_node_command = makeNodeCreationCommand(node_host, port, known_node_host, known_node_port);
		
		return runProcessAndNotifyObservers(create_node_command, node_host, port, ssh_client);
	}

	/**
	 * Launches a new local node to join an existing ring, running in a new OS process, on the local host.
	 * 
	 * @param admin_host the IP name or address of the host to receive admin events
	 * @param port the port on which the node should communicate
	 * @param known_node_host the IP name or address of a host in the existing ring
	 * @param known_node_port the port on which the host in the existing ring is communicating
	 * @return the new OS process
	 * @throws IOException if an error occurs when reading output from the process
	 */
	public Process createLocalNodeProcess(String admin_host, int port, String known_node_host, int known_node_port) throws IOException {
	   
		String local_host = InetAddress.getLocalHost().getHostName();
		
		String create_node_command = makeNodeCreationCommand(admin_host, local_host, port, known_node_host, known_node_port);
		
		return runProcessAndNotifyObservers(create_node_command, local_host, port, null);
	}
	
	/**
	 * Launches a new remote node to join an existing ring, running in a new OS process, on a remote host.
	 * 
	 * @param admin_host the IP name or address of the host to receive admin events
	 * @param node_host the IP name or address of the remote host on which the node is to be created
	 * @param port the port on which the node should communicate
	 * @param known_node_host the IP name or address of a host in the existing ring
	 * @param known_node_port the port on which the host in the existing ring is communicating
	 * @param ssh_client an SSH client connection to the remote host
	 * @return the new OS process
	 * @throws IOException if an error occurs when reading output from the process
	 */
	public Process createRemoteNodeProcess(String admin_host, String node_host, int port, String known_node_host, int known_node_port, SSH2SimpleClient ssh_client) throws IOException {
		
		String create_node_command = makeNodeCreationCommand(admin_host, node_host, port, known_node_host, known_node_port);
		
		return runProcessAndNotifyObservers(create_node_command, node_host, port, ssh_client);
	}
	
	/********************************************************************************************************************************************/
	
	private static void usage() {
		
		System.out.println("Usage: java NodeLauncher [-n<number of nodes>] [-h<node host:port>] [-k<known node host:port> | -r] [-a<admin node host>] [-l] [-t<tools.jar path>]");
	}

	/**
	 * Runs the specified command and notifies observers of the process and host details
	 * @param command the command to be executed
	 * @param node_host the IP name or address of the host on which the node is to be created (currently only local host)
	 * @param port the port on which the node should communicate
	 * 
	 * @return the resulting process
	 * @throws IOException if the process could not be executed
	 */
	private Process runProcessAndNotifyObservers(String command, String node_host, int port, SSH2SimpleClient ssh_client) throws IOException {
		
		Process p = Processes.runProcess(command);
		
		ProcessEvent event = new ProcessEvent(p, node_host, port);
		
		setChanged();
		notifyObservers(event);
		
		return p;
	}
	
	private void launchNewRing(int number_of_nodes, String nodes_host, int nodes_first_port, String admin_host, boolean randomise_join_position) {
		
		if (admin_host == null) {
			
			// No known node or admin node.
			launchNodes(number_of_nodes, nodes_host, nodes_first_port, randomise_join_position);
		}
		else {
			
			// Admin node but no known node.
			launchNodes(number_of_nodes, nodes_host, nodes_first_port, admin_host, randomise_join_position, null);
		}
	}
	
	private void launchIntoExistingRing(int number_of_nodes, String nodes_host, int nodes_first_port, String known_node_host_port, String admin_host) throws UnknownHostException {
		
		InetSocketAddress known_node_address = Network.processHostPortParameter(known_node_host_port, 0);
		
		String known_node_host = known_node_address.getHostName();
		int known_node_port = known_node_address.getPort();
		
		if (admin_host == null) {
			
			// Known node but no admin node.
			launchNodes(number_of_nodes, nodes_host, nodes_first_port, known_node_host, known_node_port);
		}
		else {
			
			// Known node and admin node.
			launchNodes(number_of_nodes, nodes_host, nodes_first_port, known_node_host, known_node_port, admin_host);
		}
	}

	private static String getToolsJarPath() {
		
		if (OS_NAME.equals(OS_NAME_MAC))			  return "";	// Don't need tools.jar in classpath so this will be ignored. //$NON-NLS-1$
		else if (OS_NAME.equals(OS_NAME_LINUX))	   return DEFAULT_TOOLS_JAR_LINUX;
		else if (OS_NAME.startsWith(OS_NAME_WINDOWS)) return DEFAULT_TOOLS_JAR_WINDOWS;
		else {
			Error.hardError(StringConstants.getString("UNKNOWN_OS_MESSAGE") + OS_NAME); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	private String makeVisualiserCreationCommand(String admin_node_address) {
		
		String admin_node_flag = " -s" + admin_node_address; //$NON-NLS-1$
		
		return java_command + classpath_flag + " " + VIEWER_CLASS_NAME + admin_node_flag; //$NON-NLS-1$
	}
	
	private String makeNodeCreationCommand(String local_node_address, int local_node_port) {
		
		String local_node_flag = " -s" + local_node_address + ":" + local_node_port; //$NON-NLS-1$ //$NON-NLS-2$
		
		return java_command + classpath_flag + " " + NODE_CLASS_NAME + local_node_flag; //$NON-NLS-1$
	}
	
	private String makeNodeCreationCommand(String admin_node_address, String local_node_address, int local_node_port) {
		
		String admin_node_flag = " -a" + admin_node_address; //$NON-NLS-1$
		
		return makeNodeCreationCommand(local_node_address, local_node_port) + admin_node_flag; //$NON-NLS-1$
	}
	
	private String makeNodeCreationCommand(String local_node_address, int local_node_port, String known_node_address, int known_node_port) {
		
		String known_node_flag = " -k" + known_node_address + ":" + known_node_port; //$NON-NLS-1$ //$NON-NLS-2$
		
		return makeNodeCreationCommand(local_node_address, local_node_port) + known_node_flag;
	}
  
	private String makeNodeCreationCommand(String admin_node_address, String local_node_address, int local_node_port, String known_node_address, int known_node_port) {
		
		String known_node_flag = " -k" + known_node_address + ":" + known_node_port; //$NON-NLS-1$ //$NON-NLS-2$
		
		return makeNodeCreationCommand(admin_node_address, local_node_address, local_node_port) + known_node_flag;
	}
}
