package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestMethod;
import testsmell.Util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ResourceOptimism extends AbstractSmell {

    private List<SmellyElement> smellyElementList;

    public ResourceOptimism() {
        smellyElementList = new ArrayList<>();
    }

    /**
     * Checks of 'Resource Optimism' smell
     */
    @Override
    public String getSmellName() {
        return "Resource Optimism";
    }

    /**
     * Returns true if any of the elements has a smell
     */
    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
    }

    /**
     * Analyze the test file for the 'ResourceOptimism' smell
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        ResourceOptimism.ClassVisitor classVisitor;
        classVisitor = new ResourceOptimism.ClassVisitor();
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
        private MethodDeclaration currentMethod = null;
        private int resourceOptimismCount = 0;
        private boolean hasSmell = false;
        TestMethod testMethod;
        private List<String> methodVariables = new ArrayList<>();
        private List<String> classVariables = new ArrayList<>();


        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n) || Util.isValidSetupMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                testMethod.setHasSmell(methodVariables.size() >= 1 || hasSmell==true);
                testMethod.addDataItem("ResourceOptimismCount", String.valueOf(resourceOptimismCount));

                smellyElementList.add(testMethod);

                //reset values for next method
                currentMethod = null;
                resourceOptimismCount = 0;
                hasSmell = false;
                methodVariables = new ArrayList<>();
            }
        }

        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
            if (currentMethod != null) {
                for (VariableDeclarator variableDeclarator : n.getVariables()) {
                    if (variableDeclarator.getType().equals("File")) {
                        methodVariables.add(variableDeclarator.getNameAsString());
                    }
                }
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(ObjectCreationExpr n, Void arg) {
            if (currentMethod != null) {
                if (n.getParentNode().isPresent()) {
                    if (!(n.getParentNode().get() instanceof VariableDeclarator)) { // VariableDeclarator is handled in the override method
                        if (n.getType().asString().equals("File")) {
                            hasSmell = true;
                        }
                    }
                }
            } else {
                System.out.println(n.getType());
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(VariableDeclarator n, Void arg) {
            if (currentMethod != null) {
                if (n.getType().asString().equals("File")) {
                    methodVariables.add(n.getNameAsString());
                }
            } else {
                if (n.getType().asString().equals("File")) {
                    classVariables.add(n.getNameAsString());
                }
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(FieldDeclaration n, Void arg) {
            for (VariableDeclarator variableDeclarator : n.getVariables()) {
                if (variableDeclarator.getType().equals("File")) {
                    classVariables.add(variableDeclarator.getNameAsString());
                }
            }
            super.visit(n, arg);
        }


        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                if (n.getNameAsString().equals("exists") ||
                        n.getNameAsString().equals("isFile") ||
                        n.getNameAsString().equals("notExists")) {
                    if (n.getScope().isPresent()) {
                        if(n.getScope().get() instanceof NameExpr) {
                            if (methodVariables.contains(((NameExpr) n.getScope().get()).getNameAsString())) {
                                methodVariables.remove(((NameExpr) n.getScope().get()).getNameAsString());
                            }
                        }
                    }
                }
            }
        }


    }


}


