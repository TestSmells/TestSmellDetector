package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.SmellyElement;
import edu.rit.se.testsmells.testsmell.TestClass;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/*
By default Android Studio creates default test classes when a project is created. These classes are meant to serve as an example for developers when wring unit tests
This code marks the class as smelly if the class name corresponds to the name of the default test classes
 */
public class DefaultTest extends AbstractSmell {

    private List<SmellyElement> smellyElementList;

    public DefaultTest() {
        smellyElementList = new ArrayList<>();
    }

    /**
     * Checks of 'Default Test' smell
     */
    @Override
    public String getSmellName() {
        return "Default Test";
    }

    /**
     * Returns true if any of the elements has a smell
     */
    public boolean hasSmell() {
        return smellyElementList.stream().filter(x -> x.hasSmell()).count() >= 1;
    }

    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit,CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        DefaultTest.ClassVisitor classVisitor;
        classVisitor = new DefaultTest.ClassVisitor();
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

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (n.getNameAsString().equals("ExampleUnitTest") || n.getNameAsString().equals("ExampleInstrumentedTest")) {
                testClass = new TestClass(n.getNameAsString());
                testClass.setHasSmell(true);
                smellyElementList.add(testClass);
            }
            super.visit(n, arg);
        }
    }
}
