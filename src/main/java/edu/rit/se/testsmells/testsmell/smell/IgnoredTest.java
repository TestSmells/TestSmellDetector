package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.TestClass;
import edu.rit.se.testsmells.testsmell.TestMethod;

import java.io.FileNotFoundException;

public class IgnoredTest extends AbstractSmell {


    private CompilationUnit testFileCompilationUnit;

    public IgnoredTest() {
        super();
    }

    /**
     * Checks of 'Ignored Test' smell
     */
    @Override
    public String getSmellName() {
        return "IgnoredTest";
    }

    /**
     * Analyze the test file for test methods that contain Ignored test methods
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        IgnoredTest.ClassVisitor classVisitor;
        classVisitor = new IgnoredTest.ClassVisitor();
        this.testFileCompilationUnit = testFileCompilationUnit;
        classVisitor.visit(this.testFileCompilationUnit, null);
    }

    /**
     * Visitor class
     */
    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        TestMethod testMethod;
        TestClass testClass;

        /**
         * This method will check if the class has the @Ignore annotation
         */
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (n.getAnnotationByName("Ignore").isPresent()) {
                testClass = new TestClass(getFullClassName(testFileCompilationUnit, n));
                testClass.setHasSmell(true);
                addSmellyElement(testClass);
            }
            super.visit(n, arg);
        }

        /**
         * The purpose of this method is to 'visit' all test methods in the test file.
         */
        @Override
        public void visit(MethodDeclaration n, Void arg) {

            //JUnit 4
            //check if test method has Ignore annotation
            if (n.getAnnotationByName("Test").isPresent()) {
                if (n.getAnnotationByName("Ignore").isPresent()) {
                    testMethod = new TestMethod(getFullMethodName(testFileCompilationUnit, n));
                    testMethod.setHasSmell(true);
                    addSmellyElement(testMethod);
                    return;
                }
            }

            //JUnit 3
            //check if test method is not public
            if (n.getNameAsString().toLowerCase().startsWith("test")) {
                if (!n.getModifiers().contains(Modifier.PUBLIC)) {
                    testMethod = new TestMethod(getFullMethodName(testFileCompilationUnit, n));
                    testMethod.setHasSmell(true);
                    addSmellyElement(testMethod);
                    return;
                }
            }
        }

    }
}
