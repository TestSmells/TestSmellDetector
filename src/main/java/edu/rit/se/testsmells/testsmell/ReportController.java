package edu.rit.se.testsmells.testsmell;

import java.io.IOException;
import java.util.*;
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

    public ReportController(ResultsWriter resultsWriter, List<ReportGranularity> granularities) {
        this.resultsWriter = resultsWriter;

        configuredGranularties = granularities;
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

    private List<SmellyElement> mergeSmellyElements(List<SmellyElement> elements) {
        List<SmellyElement> result = new ArrayList<>();
        for (SmellyElement s1 : elements) {
            boolean have = false;
            for (SmellyElement s2 : result) {
                if (s1 == s2) {
                    have = true;
                } else if (s1.getElementName().equals(s2.getElementName())) {
                    have = true;
                    s2.setHasSmell(s2.hasSmell() || s1.hasSmell());
                    s1.getData().forEach(s2::addDataItem);
                }
            }
            if (!have) {
                result.add(s1);
            }
        }
        return result;
    }

    private void reportSmellyElements(List<AbstractSmell> smells, Class<?> type) throws IOException {
        List<SmellyElement> c = smells.stream().map(AbstractSmell::getSmellyElements).flatMap(Collection::stream).filter(type::isInstance).collect(Collectors.toList());
        for (SmellyElement elem : mergeSmellyElements(c)) {
            resultsWriter.exportSmells(elem);
        }
    }
}
