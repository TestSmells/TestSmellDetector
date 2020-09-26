package edu.rit.se.testsmells.testsmell.internal;

import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.ExtractingStrategy;
import edu.rit.se.testsmells.testsmell.Report;
import edu.rit.se.testsmells.testsmell.SmellyElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtractingByMerge implements ExtractingStrategy {
    @Override
    public List<Report> extract(List<AbstractSmell> smells, Class<?> type) {
        return merge(smells.stream().flatMap(smell -> filterAndFlatten(type, smell)).collect(Collectors.toList()));
    }

    private Stream<ReportCell> filterAndFlatten(Class<?> type, AbstractSmell smell) {
        List<SmellyElement> elements = smell.getSmellyElements();
        String smellType = smell.getSmellName();
        return elements.stream().filter(type::isInstance).map(elem -> ReportCell.fromSmellyElement(elem, smellType));
    }

    private List<Report> merge(List<ReportCell> elements) {
        List<String> names = listUniqueNames(elements);
        return names.stream().map(name -> ReportOutput.fromCell(filterByName(elements, name))).collect(Collectors.toList());
    }

    private List<String> listUniqueNames(List<ReportCell> elements) {
        return elements.stream().map(elem -> elem.name).distinct().collect(Collectors.toList());
    }

    private List<ReportCell> filterByName(List<ReportCell> elements, String name) {
        return elements.stream().filter(elem -> elem.name.equals(name)).collect(Collectors.toList());
    }

    /**
     * An intermediate representation of reporting data (private-only POJO)
     * Intended to remind bi-dimensional (TestSmells x SmellyElement) matrix's cell.
     */
    public static class ReportCell {
        private String smellType;
        private String name;
        private Map<String, String> data;
        private boolean hasSmell;

        /**
         * Static factory method to convert from hierarchical SmellyElement into a flat ReportCell representation
         *
         * @param elem      The origin SmellyElement
         * @param smellType The TestSmell's type name
         * @return A valid ReportCell
         */
        static ReportCell fromSmellyElement(SmellyElement elem, String smellType) {
            ReportCell rc = new ReportCell();
            rc.smellType = smellType;
            rc.name = elem.getElementName();
            rc.data = elem.getData();
            rc.hasSmell = elem.hasSmell();
            return rc;
        }
    }

    /**
     * The final output model
     */
    public static class ReportOutput implements Report {
        private Map<String, Boolean> smellsPresence;
        private Map<String, String> data;
        private String name;

        /**
         * Merge ReportCells into a ReportOutput
         *
         * @param cells ReportCells related to the same SmellyElement
         * @return A ReportOutput with all data from cells merged
         */
        static ReportOutput fromCell(List<ReportCell> cells) {
            ReportOutput output = new ReportOutput();

            String elementName = cells.get(0).name;
            assert cells.stream().allMatch(cell -> cell.name.equals(elementName));

            output.name = elementName;
            output.data = new HashMap<>();
            output.smellsPresence = new HashMap<>();

            cells.forEach(cell -> {
                // Merge all cells' data into the same output.data map
                output.data.putAll(cell.data);
                // Use logic-OR operation as hasSmell's aggregation function
                boolean hasSmell = output.smellsPresence.getOrDefault(cell.smellType, false) || cell.hasSmell;
                // Store each smell type in a map with a flag indicating whether it occurs or not
                output.smellsPresence.put(cell.smellType, hasSmell);
            });
            return output;
        }

        Map<String, Boolean> getSmellsPresence() {
            return smellsPresence;
        }

        public Map<String, String> getData() {
            return data;
        }

        public String getName() {
            return name;
        }

        @Override
        public List<String> getEntryValues() {
            List<String> entries = new ArrayList<>();
            entries.add(getName());
            entries.addAll(getData().values());
            getSmellsPresence().values().forEach(e -> entries.add(e.toString()));
            return entries;
        }
    }
}
