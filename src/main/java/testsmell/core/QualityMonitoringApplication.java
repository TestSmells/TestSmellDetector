package testsmell.core;

import testsmell.ResultsWriter;
import testsmell.TestFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Application generates a CSV report with data on the code smells in the unit tests of a selected project.
 */
public class QualityMonitoringApplication {

    public static void main(String[] args) {
        File inputFile = new File(".");
        boolean isArgumentWithTargetName = args != null && args.length > 0 && args[0].isEmpty();
        if (isArgumentWithTargetName) {
            inputFile = new File(args[0]);
        }

        QualityMonitoringApplication application = new QualityMonitoringApplication();
        if (!inputFile.exists()) {
            throw new RuntimeException("Please provide a valid file containing the paths to the collection of test files");
        }
        if (inputFile.isDirectory()) {
            application.processProject(inputFile);
        } else {
            application.processProjectViaCsvFile(inputFile);
        }
    }

    public void processProject(File directory) {
        try {
            ProjectMonitoringInputGenerator inputGenerator = new ProjectMonitoringInputGenerator(directory);
            ResultsWriter writer = createProcessedResultsWriter(inputGenerator.getInputList());
            writer.save();
        } catch (Exception ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }

    public void processProjectViaCsvFile(File file) {
        try {
            ResultsWriter writer = createProcessedResultsWriter(Files.readAllLines(file.toPath()));
            writer.save();
            System.out.println("processed: " + file);
        } catch (Exception ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }

    List<TestFile> createTestFilesFromLines(List<String> lines) {
        return new ProjectsCsvReader(lines).getTestFiles();
    }

    ResultsWriter createProcessedResultsWriter(List<String> lines)  throws IOException {
        return new SmellDetectionWriter(createTestFilesFromLines(lines)).createProcessedResultsWriter();
    }
}
