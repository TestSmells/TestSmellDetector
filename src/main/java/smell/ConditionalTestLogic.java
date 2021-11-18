package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestMethod;
import testsmell.Util;
import thresholds.Thresholds;

import java.io.FileNotFoundException;
import java.util.List;

/*
This class check a test method for the existence of loops and conditional statements in the methods body
 */
public class ConditionalTestLogic extends AbstractSmell {

    public ConditionalTestLogic(Thresholds thresholds) {
        super(thresholds);
    }

    /**
     * Checks of 'Conditional Test Logic' smell
     */
    @Override
    public String getSmellName() {
        return "Conditional Test Logic";
    }

    /**
     * Analyze the test file for test methods that use conditional statements
     */
    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        ConditionalTestLogic.ClassVisitor classVisitor;
        classVisitor = new ConditionalTestLogic.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int conditionCount, ifCount, switchCount, forCount, foreachCount, whileCount = 0;
        TestMethod testMethod;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                currentMethod = n;
                testMethod = new TestMethod(n.getNameAsString());
                testMethod.setSmell(false); //default value is false (i.e. no smell)
                super.visit(n, arg);

                boolean isSmelly = conditionCount > thresholds.getConditionalTestLogic() |
                        ifCount > thresholds.getConditionalTestLogic() |
                        switchCount > thresholds.getConditionalTestLogic() |
                        foreachCount > thresholds.getConditionalTestLogic() |
                        forCount > thresholds.getConditionalTestLogic() |
                        whileCount > thresholds.getConditionalTestLogic();

                testMethod.setSmell(isSmelly);

                testMethod.addDataItem("ConditionCount", String.valueOf(conditionCount));
                testMethod.addDataItem("IfCount", String.valueOf(ifCount));
                testMethod.addDataItem("SwitchCount", String.valueOf(switchCount));
                testMethod.addDataItem("ForeachCount", String.valueOf(foreachCount));
                testMethod.addDataItem("ForCount", String.valueOf(forCount));
                testMethod.addDataItem("WhileCount", String.valueOf(whileCount));

                smellyElementsSet.add(testMethod);

                //reset values for next method
                currentMethod = null;
                conditionCount = 0;
                ifCount = 0;
                switchCount = 0;
                forCount = 0;
                foreachCount = 0;
                whileCount = 0;
            }
        }


        @Override
        public void visit(IfStmt n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                ifCount++;
            }
        }

        @Override
        public void visit(SwitchStmt n, Void arg) {

            super.visit(n, arg);
            if (currentMethod != null) {
                switchCount++;
            }
        }

        @Override
        public void visit(ConditionalExpr n, Void arg) {

            super.visit(n, arg);
            if (currentMethod != null) {
                conditionCount++;
            }
        }

        @Override
        public void visit(ForStmt n, Void arg) {

            super.visit(n, arg);
            if (currentMethod != null) {
                forCount++;
            }
        }

        @Override
        public void visit(ForeachStmt n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                foreachCount++;
            }
        }

        @Override
        public void visit(WhileStmt n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null) {
                whileCount++;
            }
        }
    }

}
