/**
 * Created on Nov 8, 2005 at 3:26:06 PM.
 */
package uk.ac.stand.dcs.asa.storage.experiments.performance.filesystem;

import java.io.File;
import java.io.PrintStream;

/**
 * Tests file creation performance.
 *
 * @author graham
 */
public class FileCreationExperiment extends AbstractExperiment {

	private String[] paths;
	private byte[] contents;

	public FileCreationExperiment() {
		// Needed for JUnit.
	}
	
	public FileCreationExperiment(String experiment_description, String file_creation_directory_path, PrintStream output_stream, int number_of_files, int number_of_runs) throws Exception {
		
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
	
	public void setupVariant() {
		
		paths = makeFilePaths();
		contents = makeFileContents();
	}

	/**
	 * Clears the working directory.
	 */
	public void setupAction() throws Exception {
		
		clearDirectory();
		createDirectory();
	}
	
	/**
	 * Creates a set of files, all with the same contents.
	 */
	public void runAction() {

		createFiles(paths, contents);
	}
	
	public void testFileCreation() {
		
		int number_of_files = 3;
		int file_size = 347327;
		
		File test_directory = new File(test_directory_path + System.getProperty("file.separator") + ROOT_DIR_NAME);

		try {
			FileCreationExperiment test_instance = new FileCreationExperiment("", test_directory_path, null, number_of_files, 1);
	
			test_instance.setFileSize(file_size);
		
			test_instance.runVariant();
		}
		catch (Exception e) { fail(e.getMessage()); }
		
		// Test properties.
		File[] created_files = test_directory.listFiles();
		assertTrue(created_files != null);
		assertEquals(created_files.length, number_of_files);
		for (int i = 0; i < created_files.length; i++)
			assertEquals(created_files[i].length(), file_size);
	}
}
