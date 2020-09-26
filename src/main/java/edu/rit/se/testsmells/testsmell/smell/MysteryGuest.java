package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.TestMethod;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * When a test uses external resources, such as a file containing test data, the test is no longer self contained.
 * Consequently, there is not enough information to understand the tested functionality, making it hard to use that test as documentation.
 * Moreover, using external resources introduces hidden dependencies: if some force changes or deletes such a resource, tests start failing.
 * Chances for this increase when more tests use the same resource.
 * A. van Deursen, L. Moonen, A. Bergh, G. Kok, “Refactoring Test Code”, Technical Report, CWI, 2001.
 */
public class MysteryGuest extends AbstractSmell {

    private static final boolean doClassLevelVariableCheck = false;

    public MysteryGuest() {
        super();
    }

    /**
     * Checks of 'Mystery Guest' smell
     */
    @Override
    public String getSmellName() {
        return "Mystery Guest";
    }

    /**
     * Analyze the test file for test methods that use external resources
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        MysteryGuest.ClassVisitor classVisitor;
        classVisitor = new MysteryGuest.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private List<String> mysteryTypes = new ArrayList<>(
                Arrays.asList(
                        "File",
                        "FileOutputStream",
                        "SQLiteOpenHelper",
                        "SQLiteDatabase",
                        "Cursor",
                        "Context",
                        "HttpClient",
                        "HttpResponse",
                        "HttpPost",
                        "HttpGet",
                        "SoapObject"
                ));

        private MethodDeclaration currentMethod = null;
        private int mysteryCount = 0;
        TestMethod testMethod;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                testMethod.setHasSmell(mysteryCount > 0);
                testMethod.addDataItem("MysteryCount", String.valueOf(mysteryCount));

                addSmellyElement(testMethod);

                //reset values for next method
                currentMethod = null;
                mysteryCount = 0;
            }
        }

        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
            super.visit(n, arg);
            if (doClassLevelVariableCheck || currentMethod != null) {
                for (String variableType : mysteryTypes) {
                    //check if the type variable encountered is part of the mystery type collection
                    if ((n.getVariable(0).getType().asString().equals(variableType))) {
                        //check if the variable has been mocked
                        for (AnnotationExpr annotation : n.getAnnotations()) {
                            if (annotation.getNameAsString().equals("Mock") || annotation.getNameAsString().equals("Spy"))
                                break;
                        }
                        // variable is not mocked, hence it's a smell
                        mysteryCount++;
                    }
                }
            }
        }
    }
}
