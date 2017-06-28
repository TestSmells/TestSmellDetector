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

        BufferedReader in = new BufferedReader(new FileReader("G:\\Tools\\TestSmellDetector\\testFiles.csv"));
        String str;

        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter();
        List<String> columnNames = null;
        List<String> columnValues = null;

        Map<String,String> data;
        while((str = in.readLine()) != null){
            data = testSmellDetector.detectSmells(str);

            if(columnNames==null){
                columnNames = testSmellDetector.getTestSmellName();
                columnNames.add(0,"FilePath");
                resultsWriter.writeColumnName(columnNames);
            }

            columnValues = new ArrayList<>();
            for (String column: columnNames) {
                columnValues.add(data.get(column));
            }
            resultsWriter.writeLine(columnValues);


        }




        //testSmellDetector.detectSmells("C:\\Projects\\TestSmells_ExisitngTools\\samples\\ar.rulosoft.mimanganu\\app\\src\\androidTest\\java\\ar\\rulosoft\\mimanganu\\TestServers.java");
        //testSmellDetector.detectSmells("C:\\Projects\\TestSmells\\HashOperationPresenterTest.java");

        System.out.println("end");
    }
}
