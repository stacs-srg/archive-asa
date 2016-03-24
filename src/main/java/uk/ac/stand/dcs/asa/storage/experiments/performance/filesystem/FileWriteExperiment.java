/**
 * Created on Nov 8, 2005 at 3:26:06 PM.
 */
package uk.ac.stand.dcs.asa.storage.experiments.performance.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;


/**
 * Tests file write performance.
 *
 * @author graham
 */
public class FileWriteExperiment extends AbstractExperiment {

	private String[] paths;
	private byte[] contents;

	public FileWriteExperiment() {
		// Needed for JUnit.
	}
	
	public FileWriteExperiment(String experiment_description, String file_creation_directory_path, PrintStream output_stream, int number_of_files, int number_of_runs) throws Exception {
		
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
	}

	public void setupAction() throws Exception {
		
		clearDirectory();
		createDirectory();
		
		createFiles(paths);
	}
	
	public void runAction() {

		for (int i = 0; i < paths.length; i++) setFileContents(paths[i], contents);
	}
	
	public void testFileWrite() {
		
		int number_of_files = 3;
		int file_size = 347327;
		
		File test_directory = new File(test_directory_path + System.getProperty("file.separator") + ROOT_DIR_NAME);

		try {
			FileWriteExperiment test_instance = new FileWriteExperiment("", test_directory_path, null, number_of_files, 1);
	
			test_instance.setFileSize(file_size);
		
			test_instance.setupVariant();
			test_instance.setupAction();
			
			// Check that files have been created and all are empty.
			File[] created_files = test_directory.listFiles();
			assertTrue(created_files != null);
			assertEquals(created_files.length, number_of_files);
			for (int i = 0; i < created_files.length; i++)
				assertEquals(created_files[i].length(), 0);
			
			test_instance.runAction();
			
			// Check that files have been written and have the correct contents.
			created_files = test_directory.listFiles();
			assertTrue(created_files != null);
			assertEquals(created_files.length, number_of_files);
			
			for (int i = 0; i < created_files.length; i++) {
				
				assertEquals(created_files[i].length(), file_size);
				
				byte[] read_buffer = new byte[file_size];

				FileInputStream stream = new FileInputStream(created_files[i]);
				stream.read(read_buffer);
				
				for (int j = 0; j < file_size; j++)
					assertEquals(read_buffer[j], test_instance.contents[j]);
			}
		}
		catch (Exception e) { fail(e.getMessage()); }
	}
}
