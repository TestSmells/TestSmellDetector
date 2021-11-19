package testsmell.core;

import testsmell.ResultsWriter;
import testsmell.TestFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MainApplication {

    public static void main(String[] args)  {
        if (args == null || args.length==0||args[0].isEmpty()) {
            System.out.println("Please provide the file containing the paths to the collection of test files");
            return;
        }
        File inputFile = new File(args[0]);

        new MainApplication().processProjectCsvFile(inputFile);

    }

    public boolean processProjectCsvFile(File file) {
        if (!file.isFile()) {
            System.out.println("Please provide a valid file containing the paths to the collection of test files");
            return false;
        }
        try {
            ResultsWriter writer = createProcessedResultsWriter(Files.readAllLines(file.toPath()));
            writer.save();
            System.out.println("processed: " + file);
            return true;
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            return false;
        }
    }
    public List<TestFile> createTestFilesFromLines(List<String> lines) {
        return new ProjectsCsvReader(lines).getTestFiles();
    }

    public ResultsWriter createProcessedResultsWriter(List<String> lines)  throws IOException {
        return new SmellDetectionWriter(createTestFilesFromLines(lines)).createProcessedResultsWriter();
    }
}
