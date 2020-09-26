package edu.rit.se.testsmells.testsmell;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class is utilized to write output to a CSV file
 */
public class ResultsWriter {

    private String outputFile;
    private FileWriter writer;

    /**
     * Creates the file into which output it to be written into. Results from each file will be stored in a new file
     *
     * @throws IOException Failed to create/open output file
     */
    private ResultsWriter() throws IOException {
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis());
        outputFile = MessageFormat.format("{0}_{1}_{2}.{3}", "Output", "TestSmellDetection", time, "csv");
        writer = new FileWriter(outputFile, false);
    }

    public String getOutputFile() {
        return outputFile;
    }

    /**
     * Factory method that provides a new instance of the ResultsWriter
     *
     * @return new ResultsWriter instance
     * @throws IOException Failed to create/open output file
     */
    public static ResultsWriter createResultsWriter() throws IOException {
        return new ResultsWriter();
    }

    public void writeCSVHeader(TestSmellDetector testSmellDetector, SmellsContainer anyFile) throws IOException {
        List<String> headers = new ArrayList<>(anyFile.getTestDescriptionEntries().keySet());
        headers.addAll(testSmellDetector.getTestSmellNames());
        writeCSV(headers);
    }

    public void exportSmells(ReportController.ReportOutput data) throws IOException {
        List<String> entries = new ArrayList<>();
        entries.add(data.getName());
        entries.addAll(data.getData().values());
        data.getSmellsPresence().values().forEach(e -> entries.add(e.toString()));
        writeCSV(entries);
    }

    public void exportSmells(SmellsContainer fileTestSmells) throws IOException {
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
    public void writeCSV(List<String> dataValues) throws IOException {
        writer = new FileWriter(outputFile, true);

        for (int i = 0; i < dataValues.size(); i++) {
            writer.append(String.valueOf(dataValues.get(i)));

            if (i != dataValues.size() - 1) writer.append(",");
            else writer.append(System.lineSeparator());

        }
        writer.flush();
        writer.close();
    }
}
