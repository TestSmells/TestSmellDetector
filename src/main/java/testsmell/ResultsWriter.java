package testsmell;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * This class is utilized to write output to a CSV file
 */
public class ResultsWriter {
    private static final DateFormat dateFormatForOutput = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    private final File outputFile;
    private final StringBuilder output = new StringBuilder();


    /**
     * Creates the file into which output it to be written into. Results from each file will be stored in a new file
     * @throws IOException
     */
    private ResultsWriter()  {
        String time =  dateFormatForOutput.format(Calendar.getInstance().getTime());
        outputFile = new File(".", MessageFormat.format("{0}_{1}_{2}.{3}", "Output","TestSmellDetection",time, "csv"));
    }

    public void save(){

        try (FileWriter writer = new FileWriter(outputFile,false)){
            writer.write(output.toString());
            writer.flush();
            System.out.println("saved: " + outputFile.getCanonicalPath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Factory method that provides a new instance of the ResultsWriter
     * @return new ResultsWriter instance
     * @throws IOException
     */
    public static ResultsWriter createResultsWriter() throws IOException {
        return new ResultsWriter();
    }

    /**
     * Writes column names into the CSV file
     * @param columnNames the column names
     * @throws IOException
     */
    public void writeColumnName(List<String> columnNames) throws IOException {
        writeOutput(columnNames);
    }

    /**
     * Writes column values into the CSV file
     * @param columnValues the column values
     * @throws IOException
     */
    public void writeLine(List<String> columnValues) throws IOException {
        writeOutput(columnValues);
    }

    /**
     * Appends the input values into the CSV file
     * @param dataValues the data that needs to be written into the file
     * @throws IOException
     */
    private void writeOutput(List<String> dataValues)throws IOException {

        for (int i=0; i<dataValues.size(); i++) {
            output.append(String.valueOf(dataValues.get(i)));

            if(i!=dataValues.size()-1)
                output.append(",");
            else
                output.append(System.lineSeparator());

        }
    }

    public String getOutput() {
        return output.toString();
    }
}
