package testsmell.smell;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/*
This class checks if the code file contains a Constructor. Ideally, the test suite should not have a constructor. Initialization of fields should be in the setUP() method
If this code detects the existence of a constructor, it sets the class as smelly
 */
public class ConstructorInitialization extends AbstractSmell {

    private List<SmellyElement> smellyElementList;
    private String testFileName;

    public ConstructorInitialization() {
        smellyElementList = new ArrayList<>();
    }

    /**
     * Checks of 'Constructor Initialization' smell
     */
    @Override
    public String getSmellName() {
        return "Constructor Initialization";
    }

    /**
     * Returns true if any of the elements has a smell
     */
    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
    }

    /**
     * Analyze the test file for Constructor Initialization smell
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit,CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        this.testFileName = testFileName;
        ConstructorInitialization.ClassVisitor classVisitor;
        classVisitor = new ConstructorInitialization.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    @Override
    public List<SmellyElement> getSmellyElements() {
        return smellyElementList;
    }


    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        TestClass testClass;
        boolean constructorAllowed=false;

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            for(int i=0;i<n.getExtendedTypes().size();i++){
                ClassOrInterfaceType node = n.getExtendedTypes().get(i);
                constructorAllowed = node.getNameAsString().equals("ActivityInstrumentationTestCase2");
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(ConstructorDeclaration n, Void arg) {
            // This check is needed to handle java files that have multiple classes
            if(n.getNameAsString().equals(testFileName)) {
                if(!constructorAllowed) {
                    testClass = new TestClass(n.getNameAsString());
                    testClass.setHasSmell(true);
                    smellyElementList.add(testClass);
                }
            }
        }
    }
}
