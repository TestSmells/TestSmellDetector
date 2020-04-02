package edu.rit.se.testsmells.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.AbstractSmell;
import edu.rit.se.testsmells.TestClass;

import java.io.FileNotFoundException;

/*
By default Android Studio creates default test classes when a project is created. These classes are meant to serve as an example for developers when wring unit tests
This code marks the class as smelly if the class name corresponds to the name of the default test classes
 */
public class DefaultTest extends AbstractSmell {

    /**
     * Checks of 'Default Test' smell
     */
    @Override
    public String getSmellName() {
        return "Default Test";
    }

    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit,CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        DefaultTest.ClassVisitor classVisitor;
        classVisitor = new DefaultTest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
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
