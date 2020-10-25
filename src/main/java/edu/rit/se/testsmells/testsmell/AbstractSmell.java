package edu.rit.se.testsmells.testsmell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractSmell {
    private final MethodValidator methodValidator;
    private final Set<SmellyElement> smellyElementList;

    public abstract String getSmellName();

    public AbstractSmell() {
        methodValidator = MethodValidator.getInstance();
        smellyElementList = new CopyOnWriteArraySet<>();
    }

    protected <T extends Node & NodeWithSimpleName> String getFullMethodName(CompilationUnit unit, T node) {

        String className = node.getParentNode().map(x -> (ClassOrInterfaceDeclaration) x).map(NodeWithSimpleName::getNameAsString).orElse("");

        return getPackageClass(unit) + className + "." + node.getNameAsString();
    }

    protected String getFullClassName(CompilationUnit unit, NodeWithSimpleName node) {
        return getPackageClass(unit) + node.getNameAsString();
    }

    private String getPackageClass(CompilationUnit unit) {
        return unit.getPackageDeclaration().map(PackageDeclaration::getName).map(Name::asString).map(x -> x + ".").orElse("");
    }

    public abstract void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException;

    public void clear() {
        for (SmellyElement smellyElement : smellyElementList) {
            smellyElement.clear();
        }
        smellyElementList.clear();
    }

    /**
     * Returns the set of analyzed elements (i.e. test methods)
     */
    public List<SmellyElement> getSmellyElements() {
        return new ArrayList<>(smellyElementList);
    }

    public void addSmellyElement(SmellyElement elem) {
        smellyElementList.add(elem);
        elem.addDetectedSmell(this);
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
