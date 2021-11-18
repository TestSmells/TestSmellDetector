package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.TestMethod;
import testsmell.Util;
import thresholds.Thresholds;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EagerTest extends AbstractSmell {

    private static final String TEST_FILE = "Test";
    private static final String PRODUCTION_FILE = "Production";
    private String productionClassName;
    private List<MethodDeclaration> productionMethods;
    private int eagerCount;

    public EagerTest(Thresholds thresholds) {
        super(thresholds);
        productionMethods = new ArrayList<>();
    }

    /**
     * Checks of 'Eager Test' smell
     */
    @Override
    public String getSmellName() {
        return "Eager Test";
    }

    /**
     * Analyze the test file for test methods that exhibit the 'Eager Test' smell
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {

        if (productionFileCompilationUnit == null)
            throw new FileNotFoundException();

        EagerTest.ClassVisitor classVisitor;

        classVisitor = new EagerTest.ClassVisitor(PRODUCTION_FILE);
        classVisitor.visit(productionFileCompilationUnit, null);

        classVisitor = new EagerTest.ClassVisitor(TEST_FILE);
        classVisitor.visit(testFileCompilationUnit, null);
        eagerCount = classVisitor.overallEager;
    }

    public int getEagerCount() {
        return eagerCount;
    }

    /**
     * Visitor class
     */
    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        TestMethod testMethod;
        private int eagerCount = 0;
        private int overallEager = 0;
        private List<String> productionVariables = new ArrayList<>();
        private List<String> calledMethods = new ArrayList<>();
        private String fileType;

        public ClassVisitor(String type) {
            fileType = type;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (Objects.equals(fileType, PRODUCTION_FILE)) {
                productionClassName = n.getNameAsString();
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(EnumDeclaration n, Void arg) {
            if (Objects.equals(fileType, PRODUCTION_FILE)) {
                productionClassName = n.getNameAsString();
            }
            super.visit(n, arg);
        }

        /**
         * The purpose of this method is to 'visit' all test methods.
         */
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            // ensure that this method is only executed for the test file
            if (Objects.equals(fileType, TEST_FILE)) {
                if (Util.isValidTestMethod(n)) {
                    currentMethod = n;
                    testMethod = new TestMethod(currentMethod.getNameAsString());
                    testMethod.setSmell(false); //default value is false (i.e. no smell)
                    super.visit(n, arg);

                    boolean isSmelly = eagerCount > thresholds.getEagerTest();
                    //the method has a smell if there is more than 1 call to production methods
                    testMethod.setSmell(isSmelly);
                    smellyElementsSet.add(testMethod);

                    //reset values for next method
                    currentMethod = null;
                    overallEager += eagerCount;
                    eagerCount = 0;
                    productionVariables = new ArrayList<>();
                    calledMethods = new ArrayList<>();
                }
            } else { //collect a list of all public/protected members of the production class
                for (Modifier modifier : n.getModifiers()) {
                    if (modifier.name().toLowerCase().equals("public") || modifier.name().toLowerCase().equals("protected")) {
                        productionMethods.add(n);
                    }
                }

            }
        }


        /**
         * The purpose of this method is to identify the production class methods that are called from the test method
         * When the parser encounters a method call:
         * 1) the method is contained in the productionMethods list
         * or
         * 2) the code will check the 'scope' of the called method
         * A match is made if the scope is either:
         * equal to the name of the production class (as in the case of a static method) or
         * if the scope is a variable that has been declared to be of type of the production class (i.e. contained in the 'productionVariables' list).
         */
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            NameExpr nameExpr = null;
            if (currentMethod != null) {
                if (productionMethods.stream().anyMatch(i -> i.getNameAsString().equals(n.getNameAsString()) &&
                        i.getParameters().size() == n.getArguments().size())) {
                    eagerCount++;
                    calledMethods.add(n.getNameAsString());
                } else {
                    if (n.getScope().isPresent()) {
                        //this if statement checks if the method is chained and gets the final scope
                        if ((n.getScope().get() instanceof MethodCallExpr)) {
                            getFinalScope(n);
                            nameExpr = tempNameExpr;
                        }
                        if (n.getScope().get() instanceof NameExpr) {
                            nameExpr = (NameExpr) n.getScope().get();
                        }

                        if (nameExpr != null) {
                            //checks if the scope of the method being called is either of production class (e.g. static method)
                            //or
                            ///if the scope matches a variable which, in turn, is of type of the production class
                            if (nameExpr.getNameAsString().equals(productionClassName) ||
                                    productionVariables.contains(nameExpr.getNameAsString())) {
                                if (!calledMethods.contains(n.getNameAsString())) {
                                    eagerCount++;
                                    calledMethods.add(n.getNameAsString());
                                }

                            }
                        }
                    }
                }
            }
            super.visit(n, arg);
        }

        private NameExpr tempNameExpr;

        /**
         * This method is utilized to obtain the scope of a chained method statement
         */
        private void getFinalScope(MethodCallExpr n) {
            if (n.getScope().isPresent()) {
                if ((n.getScope().get() instanceof MethodCallExpr)) {
                    getFinalScope((MethodCallExpr) n.getScope().get());
                } else if ((n.getScope().get() instanceof NameExpr)) {
                    tempNameExpr = ((NameExpr) n.getScope().get());
                }
            }
        }

//        /**
//         * The purpose of this method is to capture the names of all variables, declared in the method body, that are of type of the production class.
//         * The variable is captured as and when the code statement is parsed/evaluated by the parser
//         */
//        @Override
//        public void visit(VariableDeclarationExpr n, Void arg) {
//            if (currentMethod != null) {
//                for (int i = 0; i < n.getVariables().size(); i++) {
//                    if (productionClassName.equals(n.getVariable(i).getType().asString())) {
//                        productionVariables.add(n.getVariable(i).getNameAsString());
//                    }
//                }
//            }
//            super.visit(n, arg);
//        }

        @Override
        public void visit(VariableDeclarator n, Void arg) {
            if (Objects.equals(fileType, TEST_FILE)) {
                if (productionClassName.equals(n.getType().asString())) {
                    productionVariables.add(n.getNameAsString());
                }
            }
            super.visit(n, arg);
        }
    }
}