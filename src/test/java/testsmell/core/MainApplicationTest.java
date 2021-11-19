package testsmell.core;

import org.apache.commons.io.FileUtils;

import testsmell.ResultsWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


public class MainApplicationTest {
    private Logger logger = Logger.getAnonymousLogger();
    private MainApplication mainApplication = new MainApplication();

    @Test
    public void whenInvalidFileNoProcessing() {
        Assertions.assertFalse( mainApplication.processProjectCsvFile(new File(".")));
    }

    @Test
    public void when3SampleLinesThen3Files() {
        List<String> sampleLines = Arrays.asList("p,a1,a2\np,b1\np,c1,c2".split("\n"));
        Assertions.assertEquals(mainApplication.createTestFilesFromLines(sampleLines).size(), 3);
    }
    @Test
    public void whenEmptyProjectThenEmptyOutcomes() throws IOException {

        ResultsWriter resultsWriter = mainApplication.createProcessedResultsWriter(new ArrayList<>());

        Assertions.assertEquals(resultsWriter.getOutput().split("\n").length, 1);
    }
    @Test
    public void whenRealLinesThenRealOutcomes() throws IOException {
        ProjectSampleInputGenerator inputGenerator = new ProjectSampleInputGenerator(new File("."));


        List<String> inputList = inputGenerator.getInputList();
        logger.info("input: "+inputGenerator.stringBuilder);

        ResultsWriter resultsWriter = mainApplication.createProcessedResultsWriter(inputList);

        logger.info("output1: "+resultsWriter.getOutput());
        Assertions.assertEquals(resultsWriter.getOutput().split("\n").length, 1+inputList.size());

        logger.info("output2: "+resultsWriter.getOutput().split("\n").length);
    }

    private class ProjectSampleInputGenerator  {
        private final List<File> sourceFileList = new ArrayList<>();
        private final List<String> inputList = new ArrayList<>();
        private final StringBuilder stringBuilder = new StringBuilder();
        ProjectSampleInputGenerator(File directory) throws IOException {

            sourceFileList.addAll(FileUtils.listFiles(new File(directory, "src/test"),"java".split(","), true));
            for (File sourceFile: sourceFileList) {
                String localSource = sourceFile.getPath().substring(directory.getPath().length()).replace("\\", "/").replace("/src/test/java/", "");
                localSource = localSource.replace("Test.java",".java");
                File file = new File(directory, "src/main/java/"+localSource);

                String line;
                if (file.exists()) {
                    line = directory.getName()+","+sourceFile.getPath().replace("\\", "/")+","+file.getPath().replace("\\", "/") ;
                } else {
                    line = directory.getName()+","+sourceFile.getPath().replace("\\", "/") ;
                }
                inputList.add(line);
                stringBuilder.append(line).append("\n");
            }
        }

        public List<String> getInputList() {
            return inputList;
        }
    }
}
