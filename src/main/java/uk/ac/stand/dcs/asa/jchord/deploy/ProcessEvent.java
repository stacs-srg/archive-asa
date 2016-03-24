/**
 * Created on Aug 8, 2005 at 8:36:59 AM.
 */
package uk.ac.stand.dcs.asa.jchord.deploy;

/**
 * Wrapper containing a process and details of the host where it has been created.
 *
 * @author graham
 */
public class ProcessEvent {

	private Process process;
	private String host;
	private int port;

	public ProcessEvent(Process process, String host, int port) {
		
		this.process = process;
		this.host = host;
		this.port = port;
	}

	public Process getProcess() {
		return process;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
