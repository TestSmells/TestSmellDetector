package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
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
import testsmell.*;

import java.io.FileNotFoundException;
import java.util.*;


public class LackOfCohesion extends AbstractSmell {

    private List<SmellyElement> smellyElementList;
    private String testFileName;
    private List<MethodDeclaration> testMethods;
    private MethodDeclaration setupMethod;
    private List<FieldDeclaration> testFields;
    private List<String> setupFields;
    private int fieldsInMethods = 0;
    private List<String> currentFields;

    public LackOfCohesion() {
        smellyElementList = new ArrayList<>();
        testMethods = new ArrayList<>();
        testFields = new ArrayList<>();
        setupFields = new ArrayList<>();
        currentFields = new ArrayList<>();
    }

    @Override
    public String getSmellName() { return "Lack of Cohesion"; }

    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
    }

    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        this.testFileName = testFileName;
        LackOfCohesion.ClassVisitor classVisitor;
        classVisitor = new LackOfCohesion.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);

        if(setupMethod != null) {
            //Get all fields that are initialized in the setup method
            //May not be needed, depending on smell variant chosen
            Optional<BlockStmt> blockStmt = setupMethod.getBody();
            NodeList nodeList = blockStmt.get().getStatements();
            for(int i = 0; i < nodeList.size(); i ++){
                for(int j = 0; j < testFields.size(); j ++){
                    for(int k = 0; k < testFields.get(j).getVariables().size(); k ++){
                        if(nodeList.get(i) instanceof ExpressionStmt) {
                            ExpressionStmt expressionStmt = (ExpressionStmt) nodeList.get(i);
                            if(expressionStmt.getExpression() instanceof AssignExpr) {
                                AssignExpr assignExpr = (AssignExpr) expressionStmt.getExpression();
                                if(testFields.get(j).getVariable(k).getNameAsString().equals(assignExpr.getTarget().toString())) {
                                    setupFields.add(assignExpr.getTarget().toString());
                                }
                            }
                        }
                    }
                }
            }
        }

        for (MethodDeclaration method : testMethods) {
            classVisitor.visit(method, null);
        }

    }

    @Override
    public List<SmellyElement> getSmellyElements() {
        return smellyElementList;
    }


    private class ClassVisitor extends VoidVisitorAdapter<Void> {

        private MethodDeclaration methodDeclaration = null;
        private MethodDeclaration currentMethod = null;
        TestMethod testMethod;
        private Set<String> fixtureCount = new HashSet<>();


        @Override
        public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
            NodeList<BodyDeclaration<?>> members = declaration.getMembers();
            for(int i = 0; i < members.size(); i ++) {
                if (members.get(i) instanceof MethodDeclaration) {
                    methodDeclaration = (MethodDeclaration) members.get(i);
                    if (Util.isValidTestMethod(methodDeclaration)) {
                        testMethods.add(methodDeclaration);
                    }
                    if (Util.isValidSetupMethod(methodDeclaration)) {
                        if (methodDeclaration.getBody().isPresent()) {
                            setupMethod = methodDeclaration;
                        }
                    }
                }
                if(members.get(i) instanceof FieldDeclaration) {
                    testFields.add((FieldDeclaration) members.get(i));
                }
            }
        }


        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                super.visit(n, arg);

                testMethod = new TestMethod(n.getNameAsString());
                getAllChildNodes(n);
                
            }
        }

        //Get all NameExpr inside of testMethod
        public void getAllChildNodes(Node n){
            List<Node> children = n.getChildNodes();
            children.stream().forEach(x -> {
                if(x instanceof NameExpr)  currentFields.add(((NameExpr) x).getName().toString());
                else getAllChildNodes(x);
            });
        }

        @Override
        public void visit(NameExpr n, Void args) {
            if(currentMethod != null) {

            }
        }

    }

}
