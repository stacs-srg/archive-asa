/**
 * Created on Nov 8, 2005 at 3:26:06 PM.
 */
package uk.ac.stand.dcs.asa.storage.experiments.performance.filesystem;

import uk.ac.stand.dcs.asa.util.Error;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;


/**
 * Tests file read performance.
 *
 * @author graham
 */
public class FileReadExperiment extends AbstractExperiment {

	private String[] paths;
	private byte[] contents;
	private byte[] read_buffer;

	public FileReadExperiment() {
		// Needed for JUnit.
	}
	
	public FileReadExperiment(String experiment_description, String file_creation_directory_path, PrintStream output_stream, int number_of_files, int number_of_runs) throws Exception {
		
		super(experiment_description,file_creation_directory_path, output_stream, number_of_files, number_of_runs);
	}

	//**************************************************************************************************

	public void runVariants() throws Exception {

		setFileSize(1024);
		setVariantTitle("small files");
		runVariant();
		
		setFileSize(102400);
		setVariantTitle("medium files");
		runVariant();
		
		setFileSize(10240000);
		setVariantTitle("large files");
		runVariant();
	}
	
	public void setupVariant() throws Exception {
		
		paths = makeFilePaths();
		contents = makeFileContents();
		
		clearDirectory();
		createDirectory();
		
		createFiles(paths, contents);
		
		read_buffer = new byte[file_size];
	}

	public void setupAction() throws Exception {
		
		// No specific setup required for each action.
	}
	
	public void runAction() {

		for (int i = 0; i < paths.length; i++) readFile(paths[i]);
	}
	
	public void readFile(String path) {
		
		File f = new File(path);
		if (!f.exists()) Error.hardError("file does not exist");
		
		try {
			FileInputStream stream = new FileInputStream(f);
			stream.read(read_buffer);
		}
		catch (IOException e) { Error.hardExceptionError("couldn't read from file", e); }
	}
	
	public void testFileRead() {
		
		int number_of_files = 3;
		int file_size = 347327;

		try {
			FileReadExperiment test_instance = new FileReadExperiment("", test_directory_path, null, number_of_files, 1);
	
			test_instance.setFileSize(file_size);
		
			test_instance.runVariant();

			for (int j = 0; j < file_size; j++)
				assertEquals(test_instance.read_buffer[j], test_instance.contents[j]);
		}
		catch (Exception e) {
			System.out.println("error: " + e.getMessage());
			fail(e.getMessage()); }
	}
}
