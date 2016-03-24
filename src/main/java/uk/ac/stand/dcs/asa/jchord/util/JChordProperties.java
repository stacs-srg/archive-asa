package uk.ac.stand.dcs.asa.jchord.util;

import uk.ac.stand.dcs.asa.util.Error;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;

/**
 * A class used to hold application properties. This implements the Singleton
 * pattern.
 * 
 * @author Alan Dearle
 * @version 1
 */
public class JChordProperties extends Properties {
	private static String propertiesFilename = "Config.txt";

	private static String header = "JChord Properties";

	private static JChordProperties instance = new JChordProperties(
			propertiesFilename);

	public JChordProperties(String filename) {
		super();
		JChordProperties.propertiesFilename = filename;
		init();
	}

	public static JChordProperties getProperties() {
		return instance;
	}

	protected void init() {
		
		URL fileURL = JChordProperties.class.getResource("/Config.txt");
		FileInputStream fileInputStream = null;
		DataInputStream dis = null;
		
		try {
			File file = new File("/Config.txt");
			fileInputStream = new FileInputStream(file);
			dis = new DataInputStream(fileInputStream);
			load(dis);
		}
		catch (java.io.FileNotFoundException e) {
			dis = null;
			Error.exceptionError("error finding properties file", e);
		}
		catch (java.io.IOException e) {
			Error.exceptionError("error reading properties file", e);
		}
		finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (java.io.IOException e) {
					Error.exceptionError("error closing input stream", e);
				}
			}

			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (java.io.IOException e) {
					Error.exceptionError("error closing input stream", e);
				}
			}
		}
	}

	public int getKeyLength() {
		try {
			return Integer.parseInt(getProperty("keylength"));
		} catch (Exception e) {
			Error.hardExceptionError("Properties.getKeyLength: integer conversion error", e);
			return -1; // not called
		}
	}

	public String getProperty(String name) {
		String result = super.getProperty(name);
		if (result == null)
			try {
				result = System.getProperty(name);
				if (result == null) {
					Error.hardError("Properties.getProperty: " + name + " not found");
				}
			} catch (Exception e) {
				Error.hardExceptionError("Properties.getProperty: " + name + " not found", e);
			}
		return result;
	}

	public void save() {
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(propertiesFilename);
			super.store(out, header);
		}
		catch (java.io.IOException e) {
			Error.exceptionError("error saving properties", e);
		}
		finally {
			if (out != null) {
				try {
					out.close();
				}
				catch (java.io.IOException e) {
					Error.exceptionError("error closing output stream", e);
				}
			}
		}
	}
}