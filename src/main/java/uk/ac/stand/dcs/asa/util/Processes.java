/*
 * Created on Jan 26, 2005 at 11:49:28 PM.
 */
package uk.ac.stand.dcs.asa.util;

import com.mindbright.jca.security.SecureRandom;
import com.mindbright.ssh2.SSH2ConsoleRemote;
import com.mindbright.ssh2.SSH2Exception;
import com.mindbright.ssh2.SSH2SimpleClient;
import com.mindbright.ssh2.SSH2Transport;
import com.mindbright.util.RandomSeed;
import com.mindbright.util.SecureRandomAndPad;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Utility class for creating local or remote OS processes.
 *
 * @author graham
 */
public class Processes {

	/**
	 * Default port for SSH connection.
	 */
	private static final int SSH_PORT = 22;

	/**
	 * Shell command to echo current PID.
	 */
	private static final String ECHO_PID_COMMAND = "echo $$ ";

	/**
	 * Shell command to destroy a process.
	 */
	private static final String KILL_COMMAND = "kill -9 ";

	/**
	 * Shell command to get the children of a process.
	 */
	private static final String PSTREE_COMMAND = "pstree -p ";

	/**
	 * Separator character in shell command.
	 */
	private static final String SHELL_SEPARATOR = "; ";

	/**
	 * Message used in SSH disconnection.
	 */
	private static final String DISCONNECT_MESSAGE = "User disconnects";

	/**
	 * Number of closing brackets in 'pstree' output, used when scanning for remote PID information.
	 */
	private static final int EXPECTED_BRACKETS = 2;

	/**
	 * Used to help interpret output of process echoing its PID.
	 */
	private static final String MARKER = "XXX";
	
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
     * Runs the specified command in a new OS process. Output is written to local System.out and System.err.
     * 
     * @param command the command to be executed
     * @return a handle to the new OS process
     * @throws IOException if an error occurs when reading output from the process
     */
    public static Process runProcess(String command) throws IOException {

        return runProcess(command, System.out, System.err);
    }

    /**
     * Runs the specified command in a new OS process.
     * 
     * @param command the command to be executed
     * @param output_stream the stream to which output from the process should be directed
     * @param error_stream the stream to which error output from the process should be directed
     * @return a handle to the new OS process
     * @throws IOException if an error occurs when reading output from the process
     */
    public static Process runProcess(String command, final OutputStream output_stream, final OutputStream error_stream) throws IOException {

        // Run the command in a new OS shell.
        Process p = Runtime.getRuntime().exec(command);

        final PrintStream output_print_stream = new PrintStream(output_stream);
        final PrintStream error_print_stream =  new PrintStream(error_stream);

        // Get streams reading from the standard out and standard error for the new process.
        final BufferedReader command_output_stream = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
        final BufferedReader command_error_stream =  new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getErrorStream())));

        // Start a thread to capture any output and write it locally.
        new Thread() {

            public void run() {

                String s = null;
                try {
                    while ((s = command_output_stream.readLine()) != null) output_print_stream.println(s);
                    command_output_stream.close();

                } catch (IOException e) { /* Ignore read errors; they mean process is done. */ } // $codepro.audit.disable emptyCatchClause

            }
        }.start();

        // Start a thread to capture any errors and write them locally.
        new Thread() {

            public void run() {

                String s = null;
                try {
                    while ((s = command_error_stream.readLine()) != null) error_print_stream.println(s);
                    command_error_stream.close();

                } catch (IOException e) { /* Ignore read errors; they mean process is done. */ } // $codepro.audit.disable emptyCatchClause
            }
        }.start();

        return p;
    }
    
    /**
     * Runs the specified command on a remote machine accessed via SSH. Output from the remote process is passed to standard out and standard error.
     * 
     * @param command the command to be executed
     * @param ssh_client an SSH connection to a remote machine
     * @return a handle to the remote process
     */
    public static Process runProcess(String command, SSH2SimpleClient ssh_client) {

    	return runProcess(command, ssh_client, System.out, System.err);
    }
    
    /**
     * Runs the specified command on a remote machine accessed via SSH.
     * 
     * @param command the command to be executed
     * @param ssh_client an SSH connection to a remote machine
     * @param output_stream the stream to which output from the process should be directed
     * @param error_stream the stream to which error output from the process should be directed
     * @return a handle to the remote process
     */
    public static Process runProcess(String command, final SSH2SimpleClient ssh_client, final OutputStream output_stream, final OutputStream error_stream) {
    	
    	/*
    	 * The MindTerm package supports the creation of a remote process, but not the later destruction of that process should it become necessary. So:
    	 * 
    	 * 1. Prefix the remote user command with a shell command to echo the current process ID (PID).
    	 * 2. Execute the combined commands and process the standard output to extract the PID.
    	 * 3. That gives the PID of the parent shell process.
    	 * 4. Now execute a remote 'pstree' command to get the children of the parent process.
    	 * 5. Process the output of that to get the PID of the user process.
    	 * 6. Store that PID so it can be used in a remote 'kill' command if the process needs to be destroyed.
    	 */
    	
    	// Prefix the command with code to print the process ID to the output stream.
		// This will be the ID of the shell command not of the user command itself.
		final String modified_command = ECHO_PID_COMMAND + MARKER + SHELL_SEPARATOR + command;
		
		// Object to contain the PID once found.
		final ProcessID process_id = new Processes().new ProcessID();

		// Processor to read the output from the remote process and extract the parent PID.
		IStreamProcessor extract_parent_PID = new IStreamProcessor() { // $codepro.audit.disable localVariableNamingConvention
			
			/**
			 * Used to accumulate output from the remote process while scanning for PID information.
			 */
			String main_output = "";
			
			/**
			 * Indicates when processing has been completed.
			 */
			boolean parent_scan_done = false;

			public boolean processByte(int byte_value) {
				
				// If processing is finished then just pass the byte on to the stream.
				if (parent_scan_done) return true;
						
				// Accumulate the output without passing it on.
				main_output += (char)byte_value;
				
				// Test whether the marker indicating the end of the PID has been reached.
				if (main_output.length() >= MARKER.length() && main_output.endsWith(MARKER)) {
					
					// Assume that the PID starts at the beginning of the stream.
					String parent_PID = (main_output.substring(0, main_output.length() - MARKER.length())); // $codepro.audit.disable localVariableNamingConvention
					getChildPID(parent_PID);
					parent_scan_done = true;
				}

				return false;
			}			

			public void setChildPID(String pid) {
				
				process_id.child_PID = pid;
				process_id.semaphore.semSignal();
			}
			
			public void getChildPID(String pid) {
				
				// Command to output the children of the given process.
				final String get_child_command = PSTREE_COMMAND + pid;

				// Processor to read the output from the remote process and extract the child PID.
				IStreamProcessor extract_child_PID = new IStreamProcessor() { // $codepro.audit.disable localVariableNamingConvention
					
					// Expected output from remote process:
					// bash(PID1)---<child process name>(PID2)
					
					// So look for second closing bracket, hoping that the child process name doesn't include any brackets...
					
					/**
					 * Used to accumulate output from the remote process while scanning for child PID information.
					 */
					String pstree_output = "";
					
					/**
					 * Counts the number of closing brackets encountered.
					 */
					int bracket_count = 0;
					
					/**
					 * Indicates when processing has been completed.
					 */
					boolean child_scan_done = false;

					public boolean processByte(int byte_value) {
						
						if (!child_scan_done) {
								
							// Accumulate the output.
							pstree_output += (char)byte_value;
							
							// Test whether the marker indicating the end of the PID has been reached.
							if (byte_value == ')') bracket_count++;
							
							if (bracket_count == EXPECTED_BRACKETS) {
								
								int i = pstree_output.length() - 1;
								
								// Read back to find the corresponding opening bracket.
								while (i >= 0 && pstree_output.charAt(i) != '(') i--;
								
								if (i >= 0) setChildPID(pstree_output.substring(i + 1, pstree_output.length() - 1));
								child_scan_done = true;
							}
						}
						
						return false;
					}
				};

				runProcess(get_child_command, ssh_client, output_stream, error_stream, extract_child_PID, null);
			}
		};

		// Run the combined command to echo the parent PID and run the user command.
		final SSH2ConsoleRemote console = runProcess(modified_command, ssh_client, output_stream, error_stream, extract_parent_PID, null);

		// Return a Java process handle.
		return new Process() {

			public void destroy() {
				
				// Wait until the PID is available - the subsequent command to extract it might not have completed yet.
				process_id.semaphore.semWait();
				
				String kill_command = KILL_COMMAND + process_id.child_PID;
				runProcess(kill_command, ssh_client, output_stream, error_stream, null, null);

				console.close();
			}

			public int exitValue() {
				return waitFor();
			}

			public InputStream getErrorStream() {
				return null;
			}

			public InputStream getInputStream() {
				return console.getStdOut();
			}

			public OutputStream getOutputStream() {
				return console.getStdIn();
			}

			public int waitFor() {
				return console.waitForExitStatus();
			}
		};
    }
    	
    /**
     * Runs the specified command on a remote machine accessed via SSH, using public key authentication. Output from the remote process is
     * passed through the specified processors, and then directed to the specified streams.
     * 
     * Based on example code from MindTerm (http://www.mindterm.com/).
     * 
     * @param command the command to be executed
     * @param ssh_client an SSH connection to a remote machine
     * @param output_stream the stream to which output from the process should be directed
     * @param error_stream the stream to which error output from the process should be directed
     * @param output_processor a processor for the standard output from the new process
     * @param error_processor a processor for the error output from the new process
     * @return a handle to the remote process
     */
    public static SSH2ConsoleRemote runProcess(final String command, final SSH2SimpleClient ssh_client, OutputStream output_stream, OutputStream error_stream,
    		IStreamProcessor output_processor, IStreamProcessor error_processor) {
    
    	/*
		 * Create the remote console to use for command execution.
		 */
		final SSH2ConsoleRemote console = new SSH2ConsoleRemote(ssh_client.getConnection());
		
		final OutputStream intercept_output_stream = makeInterceptStream(output_stream, output_processor);
		final OutputStream intercept_error_stream =  makeInterceptStream(error_stream, error_processor);

		// Create a thread to run the command remotely via SSH.
		new Thread() {

		    public void run() {
		    	
				// Run the command (returns a boolean indicating success, ignored here).
			
				console.command(command, intercept_output_stream, intercept_error_stream);

				// Wait for the remote command to complete or be killed.
				
				console.waitForExitStatus();
				
				// Disconnect the transport layer gracefully
				ssh_client.getTransport().normalDisconnect(DISCONNECT_MESSAGE);
		    }
		    
		}.start();
		
		return console;
    }
        
    /**
	 * Creates an SSH client that authenticates using a public key scheme.
	 * 
     * @param server the machine on which to execute the command
     * @param user the name of the user on the remote machine
     * @param private_key_file_path the path of the local private key file
     * @param pass_phrase the pass phrase for the local private key file
     * @return an SSH client connection
     * @throws IOException if an IO error occurs while attempting to run the remote command
     * @throws SSH2Exception if an SSH error occurs while attempting to run the remote command
     */
	public static SSH2SimpleClient makeClient(InetAddress server, String user, File private_key_file_path, String pass_phrase) throws SSH2Exception, IOException {
    	
    	SSH2Transport transport = makeTransport(server);

		return new SSH2SimpleClient(transport, user, private_key_file_path.getPath(), pass_phrase);
    }
    
	/**
	 * Creates an SSH client that authenticates using a password.
	 * 
     * @param server the machine on which to execute the command
     * @param user the name of the user on the remote machine
     * @param password the password of the user on the remote machine
	 * @return an SSH client connection
     * @throws IOException if an IO error occurs while attempting to run the remote command
     * @throws SSH2Exception if an SSH error occurs while attempting to run the remote command
	 */
	public static SSH2SimpleClient makeClient(InetAddress server, String user, String password) throws SSH2Exception, IOException {
    	
    	SSH2Transport transport = makeTransport(server);

		return new SSH2SimpleClient(transport, user, password);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	    	
    /**
     * Creates a transport object to communicate with a specified SSH server.
     * 
     * @param server the machine on which to execute the command
     * @throws IOException if an IO error occurs while attempting to establish a connection
     * @return an SSH transport connection
     */
    private static SSH2Transport makeTransport(InetAddress server) throws IOException {
    	
		Socket server_socket = new Socket(server, SSH_PORT);
		byte[] seed = RandomSeed.getSystemStateHash();
		SecureRandomAndPad secure_random = new SecureRandomAndPad(new SecureRandom(seed));

		return new SSH2Transport(server_socket, secure_random);
    }
    
	/**
	 * Creates an output stream that processes bytes using the specified processor and passes further output to the specified stream.
	 * 
	 * @param stream OutputStream
	 * @param processor IStreamProcessor
	 * @return OutputStream
	 */
	private static OutputStream makeInterceptStream(final OutputStream stream, final IStreamProcessor processor) {
		
		return new OutputStream() {
			
			public void write(int b) throws IOException {

				// If there is a stream processor, let it decide whether the byte should be passed on to the normal stream.
				if (processor != null) {
					if (processor.processByte(b)) stream.write(b);
				}
				else stream.write(b);
			}

			public void close() throws IOException {

				stream.close();
			}

			public void flush() throws IOException {

				stream.flush();
			}
		};
	}
	
    /**
     * Used to store a PID for a remote process.
     */
    private class ProcessID {

    	/**
    	 * The PID of the remote process.
    	 */
    	String child_PID = "";
    	
    	/**
    	 * Used to synchronise access to the PID value.
    	 */
    	Semaphore semaphore = new Semaphore(0);
	}
}
