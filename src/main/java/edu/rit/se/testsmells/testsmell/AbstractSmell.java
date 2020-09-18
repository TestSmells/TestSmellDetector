package edu.rit.se.testsmells.testsmell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSmell {
    private final MethodValidator methodValidator;
    private final List<SmellyElement> smellyElementList;

    public abstract String getSmellName();

    public abstract void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException;

    public AbstractSmell() {
        methodValidator = new MethodValidator(); //TODO: dependency should be injected (or, at least, be a singleton)
        smellyElementList = new ArrayList<>();
    }

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    public List<SmellyElement> getSmellyElements() {
        return smellyElementList;
    }

    public void addSmellyElement(SmellyElement elem) {
        smellyElementList.add(elem);
    }

    /**
     * Returns true if any of the elements has a smell
     */
    public boolean hasSmell() {
        return smellyElementList.stream().anyMatch(SmellyElement::hasSmell);
    }

    protected boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    protected boolean isValidTestMethod(MethodDeclaration method) {
        return methodValidator.isValidTestMethod(method);
    }

    protected boolean isValidSetupMethod(MethodDeclaration method) {
        return methodValidator.isValidSetupMethod(method);
    }
}
