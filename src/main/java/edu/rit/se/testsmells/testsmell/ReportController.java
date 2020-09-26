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

    private List<ReportOutput> mergeSmellyElements(List<ReportCell> elements) {
        List<String> gruopedByElemName = elements.stream().map(c -> c.name).distinct().collect(Collectors.toList());
        return gruopedByElemName.stream().map(name -> ReportOutput.fromCell(filterByName(elements, name))).collect(Collectors.toList());
    }

    private List<ReportCell> filterByName(List<ReportCell> elements, String name) {
        return elements.stream().filter(c1 -> c1.name.equals(name)).collect(Collectors.toList());
    }

    private void reportSmellyElements(List<AbstractSmell> smells, Class<?> type) throws IOException {
        List<ReportCell> c = smells.stream().flatMap(s -> s.getSmellyElements().stream().filter(type::isInstance).map(se -> ReportCell.fromSmellElem(se, s.getSmellName()))).collect(Collectors.toList());
        for (ReportOutput output : mergeSmellyElements(c)) {
            resultsWriter.exportSmells(output);
        }
    }

    private static class ReportCell {
        private String smellType;
        private String name;
        private Map<String, String> data;
        private boolean hasSmell;

        static ReportCell fromSmellElem(SmellyElement elem, String smellType) {
            ReportCell rc = new ReportCell();
            rc.smellType = smellType;
            rc.name = elem.getElementName();
            rc.data = elem.getData();
            rc.hasSmell = elem.hasSmell();
            return rc;
        }
    }

    static class ReportOutput {
        private Map<String, Boolean> smellsPresence;
        private Map<String, String> data;
        private String name;

        public static ReportOutput fromCell(List<ReportCell> cells) {
            ReportOutput output = new ReportOutput();

            String elementName = cells.get(0).name;
            assert cells.stream().allMatch(s -> s.name.equals(elementName));

            output.name = elementName;
            output.data = new HashMap<>();
            output.smellsPresence = new HashMap<>();

            cells.forEach(cell -> {
                output.data.putAll(cell.data);
                boolean hasSmell = output.smellsPresence.getOrDefault(cell.smellType, false) || cell.hasSmell;
                output.smellsPresence.put(cell.smellType, hasSmell);
            });
            return output;
        }

        public Map<String, Boolean> getSmellsPresence() {
            return smellsPresence;
        }

        public Map<String, String> getData() {
            return data;
        }

        public String getName() {
            return name;
        }
    }
}
