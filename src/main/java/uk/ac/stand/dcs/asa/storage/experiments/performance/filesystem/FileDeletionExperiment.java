/**
 * Created on Nov 8, 2005 at 3:26:06 PM.
 */
package uk.ac.stand.dcs.asa.storage.experiments.performance.filesystem;

import java.io.File;
import java.io.PrintStream;


/**
 * Tests file deletion performance.
 *
 * @author graham
 */
public class FileDeletionExperiment extends AbstractExperiment {

	private String[] paths;
	private byte[] contents;

	public FileDeletionExperiment() {
		// Needed for JUnit.
	}
	
	public FileDeletionExperiment(String experiment_description, String file_creation_directory_path, PrintStream output_stream, int number_of_files, int number_of_runs) throws Exception {
		
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
		
		createFiles(paths, contents);
	}
	
	public void runAction() {

		for (int i = 0; i < paths.length; i++) deleteFile(paths[i]);
	}
	
	public void testFileDeletion() {
		
		int number_of_files = 3;
		int file_size = 347327;
		
		File test_directory = new File(test_directory_path + System.getProperty("file.separator") + ROOT_DIR_NAME);
		
		try {
			FileDeletionExperiment test_instance = new FileDeletionExperiment("", test_directory_path, null, number_of_files, 1);
	
			test_instance.setFileSize(file_size);
		
			test_instance.setupVariant();
			test_instance.setupAction();
			
			// Check that files have been created and are of the correct length.
			File[] created_files = test_directory.listFiles();
			assertTrue(created_files != null);
			assertEquals(created_files.length, number_of_files);
			for (int i = 0; i < created_files.length; i++)
				assertEquals(created_files[i].length(), file_size);
			
			test_instance.runAction();
			
			// Check that all files have been deleted.
			File[] remaining_files = test_directory.listFiles();
			assertTrue(remaining_files != null);
			assertEquals(remaining_files.length, 0);
		}
		catch (Exception e) { fail(e.getMessage()); }
	}
}
