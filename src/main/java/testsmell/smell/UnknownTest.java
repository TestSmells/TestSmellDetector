package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestMethod;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class UnknownTest  extends AbstractSmell {

    private List<SmellyElement> smellyElementList;

    public UnknownTest() {
        smellyElementList = new ArrayList<>();
    }

    /**
     * Checks of 'Unknown Test' smell
     */
    @Override
    public String getSmellName() {
        return "Unknown Test";
    }

    /**
     * Returns true if any of the elements has a smell
     */
    @Override
    public boolean getHasSmell() {
        return smellyElementList.stream().filter(x -> x.getHasSmell()).count() >= 1;
    }

    /**
     * Analyze the test file for test methods that do not have assert statement or exceptions
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit) throws FileNotFoundException {
        UnknownTest.ClassVisitor classVisitor;
        classVisitor = new UnknownTest.ClassVisitor();
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
        TestMethod testMethod;
        List<String> assertMessage = new ArrayList<>();
        boolean hasAssert=false;
        boolean hasExceptionAnnotation=false;


        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
            if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
                Optional<AnnotationExpr> assertAnnotation = n.getAnnotationByName("Test");
                if(assertAnnotation.isPresent()){
                    for(int i=0;i<assertAnnotation.get().getNodeLists().size();i++){
                        NodeList<?> c = assertAnnotation.get().getNodeLists().get(i);
                        for(int j=0;j<c.size();j++)
                        if (c.get(j) instanceof MemberValuePair) {
                            if (((MemberValuePair) c.get(j)).getName().equals("expected") && ((MemberValuePair) c.get(j)).getValue().toString().contains("Exception"));
                            hasExceptionAnnotation=true;
                        }
                    }
                }
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setHasSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                // if there are duplicate messages, then the smell exists
                if (!hasAssert && !hasExceptionAnnotation)
                    testMethod.setHasSmell(true);

                smellyElementList.add(testMethod);

                //reset values for next method
                currentMethod = null;
                assertMessage = new ArrayList<>();
                hasAssert=false;
            }
        }



        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                // if the name of a method being called start with 'assert'
                if (n.getNameAsString().startsWith(("assert"))) {
                    hasAssert = true;
                }
                // if the name of a method being called is 'fail'
                else if (n.getNameAsString().equals("fail")) {
                    hasAssert = true;
                }

            }
        }

    }
}

