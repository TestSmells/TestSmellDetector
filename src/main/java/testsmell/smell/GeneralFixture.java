package testsmell.smell;

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
import testsmell.ISmell;
import testsmell.MethodSmell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GeneralFixture implements ITestSmell {

    List<ISmell> smellList;
    List<MethodDeclaration> methodList;
    MethodDeclaration setupMethod;
    List<FieldDeclaration> fieldList;
    List<String> setupFields;

    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();
        methodList = new ArrayList<>();
        fieldList = new ArrayList<>();
        setupFields = new ArrayList<>();

        GeneralFixture.ClassVisitor classVisitor = new GeneralFixture.ClassVisitor();
        //This call will populate the list of test methods and identify the setup method [visit(ClassOrInterfaceDeclaration n)]
        classVisitor.visit(cu, null);

        //Proceed with general fixture analysis if setup method exists
        if (setupMethod != null) {
            System.out.println(setupMethod.getNameAsString());
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

        return smellList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration methodDeclaration = null;
        private MethodDeclaration currentMethod = null;
        ISmell methodSmell;
        private int fixtureCount = 0;

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            NodeList<BodyDeclaration<?>> members = n.getMembers();
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i) instanceof MethodDeclaration) {
                    methodDeclaration = (MethodDeclaration) members.get(i);

                    //Get a list of all test methods
                    if (methodDeclaration.getAnnotationByName("Test").isPresent() || methodDeclaration.getNameAsString().toLowerCase().startsWith("test")) {
                        methodList.add(methodDeclaration);
                    }

                    //Get the setup method
                    if (methodDeclaration.getNameAsString().toLowerCase().equals("setup")) {
                        setupMethod = methodDeclaration;
                    }
                }

                //Get all fields in the class
                if (members.get(i) instanceof FieldDeclaration) {
                    fieldList.add((FieldDeclaration) members.get(i));
                }
            }
        }

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
            if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
                currentMethod = n;

                //call visit(NameExpr) for current method
                super.visit(n, arg);

                methodSmell = new MethodSmell(n.getNameAsString());
                methodSmell.setHasSmell(fixtureCount != setupFields.size());
                smellList.add(methodSmell);

                fixtureCount = 0;
                currentMethod = null;
            }
        }

        @Override
        public void visit(NameExpr n, Void arg) {
            if (currentMethod != null) {
                //check if the variable contained in the current test method is also contained in the setup method
                if (setupFields.contains(n.getNameAsString())) {
                    fixtureCount++;
                    System.out.println(currentMethod.getNameAsString() + " : " + n.getName().toString());
                }
            }

            super.visit(n, arg);
        }


    }
}
