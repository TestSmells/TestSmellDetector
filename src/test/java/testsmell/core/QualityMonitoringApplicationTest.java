package testsmell.core;

import testsmell.ResultsWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


public class QualityMonitoringApplicationTest {
    private Logger logger = Logger.getAnonymousLogger();
    private QualityMonitoringApplication mainApplication = new QualityMonitoringApplication();

    @Test
    public void whenInvalidInputNoCsvProcessing() {
        Assertions.assertThrows(RuntimeException.class, ()->{
            mainApplication.processProjectViaCsvFile(new File("."));
        });
    }

    @Test
    public void whenInvalidInputNoProjectProcessing() {
        Assertions.assertThrows(RuntimeException.class, ()->{
            mainApplication.processProject(new File("./TESTING"));
        });
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
        // given:
        ProjectMonitoringInputGenerator inputGenerator = new ProjectMonitoringInputGenerator(new File("."));
        List<String> inputList = inputGenerator.getInputList();
        logger.info("input: "+inputGenerator.getText());

        // when:
        ResultsWriter resultsWriter = mainApplication.createProcessedResultsWriter(inputList);
        logger.info("output: "+resultsWriter.getOutput());

        // then:
        Assertions.assertEquals(resultsWriter.getOutput().split("\n").length, 1+inputList.size());
    }


}
