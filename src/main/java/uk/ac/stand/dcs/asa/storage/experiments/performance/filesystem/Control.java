package uk.ac.stand.dcs.asa.storage.experiments.performance.filesystem;

import uk.ac.stand.dcs.asa.util.CommandLineInput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Control {

	public static void main(String[] args) throws Exception {
		
		final int NUMBER_OF_FILES = 1;
		final int NUMBER_OF_RUNS = 5;

		System.out.print("Enter file system description: ");
		String file_system_description = CommandLineInput.readLine();

		System.out.print("Enter platform description: ");
		String platform_description = CommandLineInput.readLine();

		System.out.print("Enter output file path: ");
		String output_file_name = CommandLineInput.readLine();
		
		System.out.print("Enter test directory path: ");
		String file_creation_directory_path = CommandLineInput.readLine();
		
		PrintStream output_stream = getPrintStream(output_file_name);

		output_stream.println(file_system_description);
		output_stream.println(platform_description);
		output_stream.println();
		
		System.out.println("\nstarting file creation experiment");
		
		AbstractExperiment creation_experiment = new FileCreationExperiment("File Creation", file_creation_directory_path, output_stream, NUMBER_OF_FILES, NUMBER_OF_RUNS);
		creation_experiment.runExperiment();
//		
//		System.out.println("\nstarting file read experiment");
//		
//		AbstractExperiment read_experiment = new FileReadExperiment("Reading", file_creation_directory_path, output_stream, NUMBER_OF_FILES, NUMBER_OF_RUNS);
//		read_experiment.runExperiment();
//
//		System.out.println("\nstarting file write experiment");
//		
//		AbstractExperiment write_experiment = new FileWriteExperiment("Writing", file_creation_directory_path, output_stream, NUMBER_OF_FILES, NUMBER_OF_RUNS);
//		write_experiment.runExperiment();
//		
//		System.out.println("\nstarting file deletion experiment");
//		
//		AbstractExperiment deletion_experiment = new FileDeletionExperiment("Deletion", file_creation_directory_path, output_stream, NUMBER_OF_FILES, NUMBER_OF_RUNS);
//		deletion_experiment.runExperiment();
		
		output_stream.close();
		
		System.out.println("Completed");
	}
	
	private static PrintStream getPrintStream(String file_name) throws Exception {
	
		File output_file = new File(file_name);
		output_file.createNewFile();
		return new PrintStream(new FileOutputStream(output_file));
	}
}
