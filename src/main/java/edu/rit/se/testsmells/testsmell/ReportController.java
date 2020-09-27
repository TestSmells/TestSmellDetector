package edu.rit.se.testsmells.testsmell;

import edu.rit.se.testsmells.testsmell.internal.Extractor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class ReportController {
    private final CSVWriter csvWriter;
    private final List<ReportGranularity> configuredGranularities;
    private ExtractingStrategy extractor = new Extractor();

    ReportController(CSVWriter csvWriter, List<ReportGranularity> granularities) {
        this.csvWriter = csvWriter;

        configuredGranularities = granularities;
    }

    public static ReportController createReportController(CSVWriter csvWriter) throws IOException {
        return new ReportController(csvWriter, readProperties());
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
            csvWriter.setOutputFilePrefix(config.toString());
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
            csvWriter.closeFile();
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
            csvWriter.exportSmells(file);
        }
    }

    private List<Report> mergeSmellyElements(List<AbstractSmell> smells, Class<?> type) {

        return extractor.extract(smells, type);
    }

    private void reportSmellyElements(List<AbstractSmell> smells, Class<?> type) throws IOException {
        List<Report> smellyElementReports = mergeSmellyElements(smells, type);
        csvWriter.exportSmells(smellyElementReports);
    }

    enum ReportGranularity {FILE, CLASS, METHOD}
}
