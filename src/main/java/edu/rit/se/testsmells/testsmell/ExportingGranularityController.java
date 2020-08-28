package edu.rit.se.testsmells.testsmell;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExportingGranularityController {

    private final TestSmellDetector testSmellDetector;
    private final ResultsWriter resultsWriter;
    private List<TestFile> files;

    public ExportingGranularityController(TestSmellDetector testSmellDetector, ResultsWriter resultsWriter) {
        this.testSmellDetector = testSmellDetector;
        this.resultsWriter = resultsWriter;

    }

    public void addSmells(List<TestFile> files) {
        this.files = files;
    }

    public void run() throws IOException {
        for (TestFile file : files) {
            System.out.println(getCurrentDateFormatted() + " Processing: " + file.getTestFilePath());

            testSmellDetector.detectSmells(file);
            resultsWriter.exportSmells(file);
        }
    }

    private Object getCurrentDateFormatted() {
        return (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
    }
}
