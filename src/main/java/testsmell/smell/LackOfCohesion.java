package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
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
import java.util.stream.Collectors;


public class LackOfCohesion extends AbstractSmell {

    private List<SmellyElement> smellyElementList;
    private String testFileName;
    private List<MethodDeclaration> testMethods;
    private MethodDeclaration setupMethod;
    private List<FieldDeclaration> testFields;
    private List<String> setupFields;
    private List<String> strTestFields;
    private int fieldsInMethods = 0;
    private List<String> currentFields;
    private boolean smelly = false;

    public LackOfCohesion() {
        smellyElementList = new ArrayList<>();
        testMethods = new ArrayList<>();
        testFields = new ArrayList<>();
        setupFields = new ArrayList<>();
        currentFields = new ArrayList<>();
        strTestFields = new ArrayList<>();
    }

    @Override
    public String getSmellName() { return "Lack of Cohesion"; }

    @Override
    public boolean getHasSmell() {
        if(smelly) System.out.println("smelly");
        return smelly;
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
                                strTestFields.add(testFields.get(j).getVariable(k).getNameAsString());
                                if(testFields.get(j).getVariable(k).getNameAsString().equals(assignExpr.getTarget().toString())) {
                                    setupFields.add(assignExpr.getTarget().toString());
                                }
                            }
                        }
                    }
                }
            }
        }

        strTestFields = strTestFields.stream().distinct().collect(Collectors.toList());

        for (MethodDeclaration method : testMethods) {
            classVisitor.visit(method, null);
        }

        float cohesion;
        if(testMethods.size() == 1) cohesion = (1.f / (float) strTestFields.size()) * (float) fieldsInMethods - (float) testMethods.size();
        else cohesion = ((1.f / (float) strTestFields.size()) * (float) fieldsInMethods - (float) testMethods.size()) / (1.f - (float) testMethods.size());
        System.out.println(cohesion);
        if(cohesion > 0.4f) smelly = true;

    }

    @Override
    public List<SmellyElement> getSmellyElements() {
        return null;
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
                currentFields.stream().forEach(x -> {
                    if(strTestFields.contains(x)) fieldsInMethods++;
                });
                currentFields.clear();
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
