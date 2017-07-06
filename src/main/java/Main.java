import testsmell.ResultsWriter;
import testsmell.TestSmellDetector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        TestSmellDetector testSmellDetector = TestSmellDetector.createTestSmellDetector();

        BufferedReader in = new BufferedReader(new FileReader("H:\\Tools\\TestSmellDetector\\TestFiles_tag.csv"));
        String str;

        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter();
        List<String> columnNames = null;
        List<String> columnValues = null;

        Map<String,String> data;
        String[] filePath;
        while((str = in.readLine()) != null){
            data = testSmellDetector.detectSmells(str);
            filePath = str.split("\\\\");

            if(columnNames==null){
                columnNames = testSmellDetector.getTestSmellName();
                columnNames.add(0,"App");
                columnNames.add(1,"FilePath");
                columnNames.add(2,"Version");
                resultsWriter.writeColumnName(columnNames);
            }

            data.put("App",filePath[3]);
            data.put("Version",filePath[4]);
            columnValues = new ArrayList<>();
            for (String column: columnNames) {
                columnValues.add(data.get(column));
            }
            resultsWriter.writeLine(columnValues);


        }
        System.out.println("end");
    }


}
