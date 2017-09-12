package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LazyTest extends AbstractSmell {
    private static final String TEST_FILE = "Test";
    private static final String PRODUCTION_FILE = "Production";
    private String productionClassName;
    private List<SmellyElement> smellyElementList;
    private List<MethodUsage> calledProductionMethods;

    public LazyTest() {
        smellyElementList = new ArrayList<>();
        calledProductionMethods = new ArrayList<>();
    }

    /**
     * Checks of 'Lazy Test' smell
     */
    @Override
    public String getSmellName() {
        return "Lazy Test";
    }

    /**
     * Returns true if any of the elements has a smell
     */
    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
    }

    /**
     * Analyze the test file for test methods that exhibit the 'Lazy Test' smell
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit) throws FileNotFoundException {

        if (productionFileCompilationUnit == null)
            throw new FileNotFoundException();

        LazyTest.ClassVisitor classVisitor;

        classVisitor = new LazyTest.ClassVisitor(PRODUCTION_FILE);
        classVisitor.visit(productionFileCompilationUnit, null);

        classVisitor = new LazyTest.ClassVisitor(TEST_FILE);
        classVisitor.visit(testFileCompilationUnit, null);

        for (MethodUsage method: calledProductionMethods) {
            List<MethodUsage> s = calledProductionMethods.stream().filter(x -> x.getProductionMethod().equals(method.getProductionMethod())).collect(Collectors.toList());
            if (s.size()>1){
                if(s.stream().filter(y -> y.getTestMethod().equals(method.getTestMethod())).count() != s.size()){
                    // If counts don not match, this production method is used by multiple test methods. Hence, there is a Lazy Test smell.
                    // If the counts were equal it means that the production method is only used (called from) inside one test method
                    TestMethod testClass = new TestMethod(method.getTestMethod());
                    testClass.setHasSmell(true);
                    smellyElementList.add(testClass);
                }
            }
        }
    }

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    @Override
    public List<SmellyElement> getSmellyElements() {
        return smellyElementList;
    }

    private class MethodUsage{
        private String testMethod,productionMethod;
        public MethodUsage(String testMethod,String productionMethod){
            this.testMethod = testMethod;
            this.productionMethod = productionMethod;
        }

        public String getProductionMethod() {
            return productionMethod;
        }

        public String getTestMethod() {
            return testMethod;
        }
    }

    /**
     * Visitor class
     */
    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        TestMethod testMethod;
       // private int lazyCount = 0;
        private List<String> productionVariables = new ArrayList<>();
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

        /**
         * The purpose of this method is to 'visit' all test methods.
         */
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            // ensure that this method is only executed for the test file
            if (Objects.equals(fileType, TEST_FILE)) {

                //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
                if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
                    currentMethod = n;
                    testMethod = new TestMethod(currentMethod.getNameAsString());
                    testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                    super.visit(n, arg);

                   // testMethod.setHasSmell(lazyCount >= 1); //TODO//the method has a smell if there is more than 1 call to production methods

                    //smellyElementList.add(testMethod);

                    //reset values for next method
                    currentMethod = null;
                    //lazyCount = 0;
                    productionVariables = new ArrayList<>();
                }

            }
        }


        /**
         * The purpose of this method is to identify the production class methods that are called from the test method
         * When the parser encounters a method call, the code will check the 'scope' of the called method.
         * A match is made if the scope is either:
         * equal to the name of the production class (as in the case of a static method) or
         * if the scope is a variable that has been declared to be of type of the production class (i.e. contained in the 'productionVariables' list).
         */
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                if (n.getScope().isPresent()) {
                    if (n.getScope().get() instanceof NameExpr) {
                        //checks if the scope of the method being called is either of production class (e.g. static method)
                        //or
                        ///if the scope matches a variable which, in turn, is of type of the production class
                        if (((NameExpr) n.getScope().get()).getNameAsString().equals(productionClassName) ||
                                productionVariables.contains(((NameExpr) n.getScope().get()).getNameAsString())) {
                            calledProductionMethods.add(new MethodUsage(currentMethod.getNameAsString(),n.getNameAsString()));
                        }
                    }
                }
            }
        }

        /**
         * The purpose of this method is to capture the names of all variables, declared in the method body, that are of type of the production class.
         * The variable is captured as and when the code statement is parsed/evaluated by the parser
         */
        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
            if (currentMethod != null) {
                for (int i = 0; i < n.getVariables().size(); i++) {
                    if (productionClassName.equals(n.getVariable(i).getType().asString())) {
                        productionVariables.add(n.getVariable(i).getNameAsString());
                    }
                }
            }
            super.visit(n, arg);
        }
    }
}
