package edu.rit.se.testsmells.testsmell;

import edu.rit.se.testsmells.testsmell.internal.ExtractingByMerge;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class ReportController {
    private final ResultsWriter resultsWriter;
    private final List<ReportGranularity> configuredGranularities;
    private ExtractingStrategy extractor = new ExtractingByMerge();

    ReportController(ResultsWriter resultsWriter, List<ReportGranularity> granularities) {
        this.resultsWriter = resultsWriter;

        configuredGranularities = granularities;
    }

    public static ReportController createReportController(ResultsWriter resultsWriter) throws IOException {
        return new ReportController(resultsWriter, readProperties());
    }

    private static List<ReportGranularity> readProperties() throws IOException {
        final String PROPERTIES_FILENAME = "test-smells.properties";
        final String PROPERTIES_KEY = "report.granularity";
        Properties prop = new Properties();
        prop.load(Objects.requireNonNull(ReportController.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME)));
        String granularityConfig = prop.getProperty(PROPERTIES_KEY);
        return Arrays.stream(granularityConfig.split(",")).map(ReportGranularity::valueOf).collect(Collectors.toList());
    }

    public void report(List<TestFile> files) throws IOException {
        for (ReportGranularity config : configuredGranularities) {
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

    private List<Report> mergeSmellyElements(List<AbstractSmell> smells, Class<?> type) {

        return extractor.extract(smells, type);
    }

    private void reportSmellyElements(List<AbstractSmell> smells, Class<?> type) throws IOException {
        List<Report> smellyElementReports = mergeSmellyElements(smells, type);
        for (Report report : smellyElementReports) {
            resultsWriter.exportSmells(report);
        }
    }

    enum ReportGranularity {FILE, CLASS, METHOD}
}
