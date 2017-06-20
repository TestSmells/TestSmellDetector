package testsmell;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import testsmell.smell.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TestSmellDetector {

    private ITestSmell ar,se,cl,mg,dt,et;

    public TestSmellDetector(){
        ar = new AssertionRoulette();
        se = new SensitiveEquality();
        cl = new ConditionalTestLogic();
        mg = new MysteryGuest();
        dt = new DefaultTest();
        et = new EmptyTest();

    }

    public void detectSmells(String absoluteFilePath) throws FileNotFoundException {
        if(absoluteFilePath.length()!=0){
            FileInputStream fTemp = new FileInputStream(absoluteFilePath);
            CompilationUnit compilationUnit = JavaParser.parse(fTemp);

            //System.out.println("AssertionRoulette: "+ar.runAnalysis(compilationUnit).size());
            //System.out.println("ConditionalTestLogic: "+cl.runAnalysis(compilationUnit).size());
            //System.out.println("SE: "+se.runAnalysis(compilationUnit).size());
            //System.out.println("MG: "+mg.runAnalysis(compilationUnit).size());
            System.out.println("DT: "+dt.runAnalysis(compilationUnit).size());
        }

    }


}
