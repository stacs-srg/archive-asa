/**
 * Created on Nov 11, 2005 at 12:30:40 PM.
 */
package uk.ac.stand.dcs.asa.storage.experiments.performance.filesystem;

import junit.framework.TestCase;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.RandomBytes;
import uk.ac.stand.dcs.asa.util.RandomGUID;

import java.io.*;
import java.util.Date;

/**
 * Infrastructure for running experiments.
 * 
 * Terminology:
 * 
 *    experiment - test of a particular task on a particular platform, e.g. file creation on OS X raw file system
 *    variant -    particular kind of test, e.g. medium file size
 *    action -     single measurement
 *    
 * An experiment involves one or more variants.
 * A variant involves a number of actions, repeated to test consistency
 *
 * @author graham
 */
public abstract class AbstractExperiment extends TestCase {

	private static final boolean CLEAR_DIRECTORY_BY_RENAMING = true;   // To try to work around various WebDAV oddities.
	
	protected String experiment_description;
	protected String variant_title;
	protected String file_creation_directory_path;
	protected int number_of_files;
	protected int file_size;
	protected int number_of_runs;
	
	private PrintStream output_stream;
	protected String ROOT_DIR_NAME = "root";
	
	//String test_directory_path = "/Users/graham/filesystemtest";
	protected String test_directory_path = "/Volumes/localhost/filesystemtest";    // For JUnit tests.

	public abstract void runVariants() throws Exception;
	public abstract void setupVariant() throws Exception;
	public abstract void setupAction() throws Exception;
	public abstract void runAction();
	
	public AbstractExperiment() {
		// Needed for JUnit.
	}

	public AbstractExperiment(String experiment_description, String file_creation_directory_path, PrintStream output_stream, int number_of_files, int number_of_runs) throws Exception {
		
		this.experiment_description = experiment_description;
		this.file_creation_directory_path = file_creation_directory_path;
		this.number_of_files = number_of_files;
		this.number_of_runs = number_of_runs;
		
		this.output_stream = output_stream;
	}
	
	public void runExperiment() throws Exception {
		
		outputExperimentDescription();
		runVariants();

		println();
		println();
		println();
		println();
		println();
		println();
		println();
		println();
		println();
		println();
		println();
		println();
		println();
	}

	public void runVariant() throws Exception {
		
		setupVariant();
		outputVariantDescription();
		printVariantDescription();
		
		System.out.print("run/" + number_of_runs);
		
		for (int i = 0; i < number_of_runs; i++) {
			
			System.gc();
			System.out.print(" " + (i + 1));
			System.out.flush();
			runSingleMeasurement();
		}
		
		println();
		println();
		println();
		
		System.out.println();
	}
	
	public void outputExperimentDescription() {

		println(experiment_description);
	}
	
	public void outputVariantDescription() {
		
		println(variant_title);
		println(new Date().toString());
		println();

		print("file size (KB)");
		separator();
		println(String.valueOf(file_size / 1024));
		
		print("number of files");
		separator();
		println(String.valueOf(number_of_files));
		
		print("timings (ms)");
		separator();
	}

	public void printVariantDescription() {
		
		System.out.println(variant_title);
	}

	public void runSingleMeasurement() throws Exception {
		
		setupAction();
		
		long start_time = System.currentTimeMillis();
		runAction();
		long end_time = System.currentTimeMillis();
		
		print(String.valueOf(end_time - start_time));
		separator();
	}

	protected String[] makeFilePaths() {
		
		String[] paths = new String[number_of_files];
		for (int i = 0; i < paths.length; i++) paths[i] = makeFilePath();
		return paths;
	}

	private String makeFilePath() {
		
		return file_creation_directory_path + System.getProperty("file.separator") + ROOT_DIR_NAME + System.getProperty("file.separator") + randomFileName();
	}

	private String randomFileName() {
		
		return (new RandomGUID()).toString();
	}

	public void clearDirectory() throws Exception {
		
		File directory = new File(file_creation_directory_path + System.getProperty("file.separator") + ROOT_DIR_NAME);
		
		// Attempt to work round Apache WebDAV problem on OS X, giving sporadic failure of deletions.
		// However, this doesn't work either - OS X passes the destination for a move as a full URL rather
		// than just a path, which gets rejected by Apache.
		
		if (CLEAR_DIRECTORY_BY_RENAMING) {
			
			if (directory.exists()) {
			
				File new_location = new File(file_creation_directory_path + System.getProperty("file.separator") + randomFileName());
				
				System.out.println("attempting to rename directory " + directory.getPath() + " to " + new_location.getPath());
				
				if (!directory.renameTo(new_location)) throw new Exception("directory " + directory.getPath() + " could not be renamed to " + new_location.getPath());
				
				System.out.println("finished renaming");
			}
			
			createDirectory();
		}
		else {
		
			if (directory.exists()) {
				File[] existing_files = directory.listFiles();
				for (int i = 0; i < existing_files.length; i++) {
					
					int retries = 0;
					
					while (!existing_files[i].delete() && retries < 10) {
						System.out.println("retrying...");
						retries++;
					}
						
					if (existing_files[i].exists()) throw new Exception("existing file " + existing_files[i].getPath() + " could not be deleted");
				}
			}
		}
	}

	public void createDirectory() {
		
		File directory = new File(file_creation_directory_path + System.getProperty("file.separator") + ROOT_DIR_NAME);
		directory.mkdirs();
	}

	public void print(String s) {
		
		if (output_stream != null) output_stream.print(s);
		else                       System.out.print(s);
	}
	
	public void println(String s) {
		
		print(s + "\n");
	}
	
	public void println() {
		
		println("");
	}
	
	public void separator() {
		
		print(", ");
	}
	
	protected void setVariantTitle(String variant_description) {
		
		this.variant_title = variant_description;
	}
	
	protected void setFileSize(int file_size) {
	
		this.file_size = file_size;
	}
	
	protected byte[] makeFileContents() {
		
		return RandomBytes.generateRandomBytes(file_size);
	}
	
	protected void createFiles(String[] paths) {
		
		for (int i = 0; i < paths.length; i++) createFile(paths[i]);
	}
	
	public void createFile(String path) {
		
		File f = new File(path);
		if (f.exists()) Error.hardError("file already exists");
		
		try {
			if (!f.createNewFile()) throw new IOException("couldn't create file");
		}
		catch (IOException e) {

			Error.hardExceptionError("couldn't create file", e); }
	}
	
	protected void createFiles(String[] paths, byte[] contents) {
		
		for (int i = 0; i < paths.length; i++) createFile(paths[i], contents);
	}
	
	public void createFile(String path, byte[] contents) {
		
		createFile(path);
		setFileContents(path, contents);
	}
	
	public void setFileContents(String path, byte[] contents) {
		
		File f = new File(path);
		
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(f);
		} catch (FileNotFoundException e) { Error.hardExceptionError("couldn't create file output stream", e); }
		
		try {
			stream.write(contents);
		} catch (IOException e) { Error.hardExceptionError("couldn't write to file", e); }
		
		try {
			stream.flush();
		} catch (IOException e) { Error.hardExceptionError("couldn't flush output stream", e); }
	}
	
	public void deleteFile(String path) {
		
		File f = new File(path);
		if (!f.exists()) Error.hardError("file does not exist");
		
		if (!f.delete()) Error.hardError("could not delete file");
	}
}
