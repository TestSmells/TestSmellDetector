package edu.rit.se.testsmells.testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.rit.se.testsmells.testsmell.AbstractSmell;
import edu.rit.se.testsmells.testsmell.TestMethod;

import java.io.FileNotFoundException;
import java.util.*;

public class GeneralFixture extends AbstractSmell {


    List<MethodDeclaration> methodList;
    MethodDeclaration setupMethod;
    List<FieldDeclaration> fieldList;
    List<String> setupFields;
    private CompilationUnit testFileCompilationUnit;

    public GeneralFixture() {
        super();
        methodList = new ArrayList<>();
        fieldList = new ArrayList<>();
        setupFields = new ArrayList<>();
    }

    @Override
    public AbstractSmell recreate() {
        return new GeneralFixture();
    }

    /**
     * Checks of 'General Fixture' smell
     */
    @Override
    public String getSmellName() {
        return "General Fixture";
    }

    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        GeneralFixture.ClassVisitor classVisitor;
        classVisitor = new GeneralFixture.ClassVisitor();
        this.testFileCompilationUnit = testFileCompilationUnit;
        classVisitor.visit(this.testFileCompilationUnit, null); //This call will populate the list of test methods and identify the setup method [visit(ClassOrInterfaceDeclaration n)]

        //Proceed with general fixture analysis if setup method exists
        if (setupMethod != null) {
            //Get all fields that are initialized in the setup method
            //The following code block will identify the class level variables (i.e. fields) that are initialized in the setup method
            // TODO: There has to be a better way to do this identification/check!
            Optional<BlockStmt> blockStmt = setupMethod.getBody();
            NodeList nodeList = blockStmt.get().getStatements();
            for (int i = 0; i < nodeList.size(); i++) {
                for (int j = 0; j < fieldList.size(); j++) {
                    for (int k = 0; k < fieldList.get(j).getVariables().size(); k++) {
                        if (nodeList.get(i) instanceof ExpressionStmt) {
                            ExpressionStmt expressionStmt = (ExpressionStmt) nodeList.get(i);
                            if (expressionStmt.getExpression() instanceof AssignExpr) {
                                AssignExpr assignExpr = (AssignExpr) expressionStmt.getExpression();
                                if (fieldList.get(j).getVariable(k).getNameAsString().equals(assignExpr.getTarget().toString())) {
                                    setupFields.add(assignExpr.getTarget().toString());
                                }
                            }
                        }
                    }
                }
            }
        }

        for (MethodDeclaration method : methodList) {
            //This call will visit each test method to identify the list of variables the method contains [visit(MethodDeclaration n)]
            classVisitor.visit(method, null);
        }
    }


    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        TestMethod testMethod;
        private Set<String> fixtureCount = new HashSet<>();

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            NodeList<BodyDeclaration<?>> members = n.getMembers();
            for (BodyDeclaration<?> member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) member;

                    //Get a list of all test methods
                    if (isValidTestMethod(methodDeclaration)) {
                        methodList.add(methodDeclaration);
                    }

                    //Get the setup method
                    if (isValidSetupMethod(methodDeclaration)) {
                        //It should have a body
                        if (methodDeclaration.getBody().isPresent()) {
                            setupMethod = methodDeclaration;
                        }
                    }
                }

                //Get all fields in the class
                if (member instanceof FieldDeclaration) {
                    fieldList.add((FieldDeclaration) member);
                }
            }
        }

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (isValidTestMethod(n)) {
                currentMethod = n;

                //call visit(NameExpr) for current method
                super.visit(n, arg);

                testMethod = new TestMethod(getFullMethodName(testFileCompilationUnit, n));
                testMethod.setHasSmell(fixtureCount.size() != setupFields.size());
                addSmellyElement(testMethod);

                fixtureCount = new HashSet<>();
                currentMethod = null;
            }
        }

        @Override
        public void visit(NameExpr n, Void arg) {
            if (currentMethod != null) {
                //check if the variable contained in the current test method is also contained in the setup method
                if (setupFields.contains(n.getNameAsString())) {
                    fixtureCount.add(n.getNameAsString());
                }
            }

            super.visit(n, arg);
        }


    }
}
