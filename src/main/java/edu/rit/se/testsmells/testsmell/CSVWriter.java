package edu.rit.se.testsmells.testsmell;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is utilized to write output to a CSV file
 */
public class CSVWriter {

    private String suffix;
    private FileWriter writer;
    private List<String> headers;
    private String name;

    /**
     * Creates the file into which output it to be written into. Results from each file will be stored in a new file
     *
     */
    private CSVWriter() {
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis());
        suffix = MessageFormat.format("{0}_{1}_{2}.{3}", "Output", "TestSmellDetection", time, "csv");
        writer = null;
    }

    /**
     * Factory method that provides a new instance of the ResultsWriter
     *
     * @return new ResultsWriter instance
     */
    public static CSVWriter createResultsWriter() {
        return new CSVWriter();
    }

    void setOutputFilePrefix(String prefix) throws IOException {
        name = MessageFormat.format("{0}_{1}", prefix, suffix);
        writer = new FileWriter(name, true);
    }

    public String getSuffix() {
        return suffix;
    }

    String getFilename() {
        return name;
    }

    public void writeCSVHeader(List<Report> reports) throws IOException {
        headers = reports.stream().flatMap(report -> report.getEntryKeys().stream()).distinct().collect(Collectors.toList());
        writeCSV(headers);
    }

    public void writeCSVHeader(SmellsContainer anyFile) throws IOException {
        List<String> headers = new ArrayList<>(anyFile.getTestDescriptionEntries().keySet());
        headers.addAll(anyFile.getTestSmells().stream().map(AbstractSmell::getSmellName).collect(Collectors.toList()));
        writeCSV(headers);
    }

    void exportSmells(List<Report> reports) throws IOException {
        for (Report report : reports) {
            List<String> entries = new ArrayList<>();
            for (String column : headers) {
                entries.add(report.getValue(column));
            }
            writeCSV(entries);
        }
    }

    void exportSmells(SmellsContainer fileTestSmells) throws IOException {
        List<String> entries = new ArrayList<>(fileTestSmells.getTestDescriptionEntries().values());
        for (AbstractSmell smell : fileTestSmells.getTestSmells()) {
            try {
                entries.add(String.valueOf(smell.hasSmell()));
            } catch (NullPointerException e) {
                entries.add("");
            }
        }
        writeCSV(entries);
    }

    /**
     * Appends the input values into the CSV file
     *
     * @param dataValues the data that needs to be written into the file
     * @throws IOException Failed to create/open output file
     */
    private void writeCSV(List<String> dataValues) throws IOException {
        for (int i = 0; i < dataValues.size(); i++) {
            writer.append(String.valueOf(dataValues.get(i)));

            if (i != dataValues.size() - 1) writer.append(",");
            else writer.append(System.lineSeparator());

        }
    }

    void closeFile() throws IOException {
        writer.flush();
        writer.close();
    }
}
