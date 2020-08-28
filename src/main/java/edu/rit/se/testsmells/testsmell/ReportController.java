package edu.rit.se.testsmells.testsmell;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

enum ReportGranularity {FILE, CLASS, METHOD}

public class ReportController {
    private final ResultsWriter resultsWriter;
    private final List<ReportGranularity> configuredGranularties;
    private final String PROPERTIES_FILENAME = "test-smells.properties";
    private String PROPERTIES_KEY = "report.granularity";

    public ReportController(ResultsWriter resultsWriter) throws IOException {
        this.resultsWriter = resultsWriter;

        configuredGranularties = readProperties();
    }

    private List<ReportGranularity> readProperties() throws IOException {
        Properties prop = new Properties();
        prop.load(getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILENAME));
        String granularityConfig = prop.getProperty(PROPERTIES_KEY);
        return Arrays.stream(granularityConfig.split(",")).map(ReportGranularity::valueOf).collect(Collectors.toList());
    }

    public void report(List<TestFile> files) throws IOException {
        for (ReportGranularity config : configuredGranularties) {
            switch (config) {
                case CLASS:
                    reportTestClasses(files);
                    break;
                case METHOD:
                    reportTestMethods(files);
                    break;
                case FILE:
                    reportTestFiles(files);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + config);
            }
        }
    }

    private void reportTestMethods(List<TestFile> files) throws IOException {
        for (TestFile file : files) {
            reportSmellyElements(file.getTestSmells(), TestMethod.class);
        }
    }

    private void reportTestClasses(List<TestFile> files) throws IOException {
        for (TestFile file : files) {
            reportSmellyElements(file.getTestSmells(), TestClass.class);
        }
    }

    private void reportTestFiles(List<TestFile> files) throws IOException {
        for (TestFile file : files) {
            resultsWriter.exportSmells(file);
        }
    }

    private void reportSmellyElements(List<AbstractSmell> smells, Class<?> type) throws IOException {
        for (AbstractSmell smell : smells) {
            List<SmellyElement> smellyMethods = smell.getSmellyElements().stream().filter(type::isInstance).collect(Collectors.toList());
            for (SmellyElement elem : smellyMethods) {
                resultsWriter.exportSmells(elem);
            }
        }
    }
}
