package edu.rit.se.testsmells.testsmell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractSmell {
    private final MethodValidator methodValidator;
    private final Set<SmellyElement> smellyElementList;

    public abstract String getSmellName();

    public AbstractSmell() {
        methodValidator = MethodValidator.getInstance();
        smellyElementList = new CopyOnWriteArraySet<>();
    }

    public abstract AbstractSmell recreate();

    protected <T extends Node & NodeWithSimpleName> String getFullMethodName(CompilationUnit unit, T node) {
        String className;
        try {
            className = node.getParentNode().map(x -> (ClassOrInterfaceDeclaration) x).map(NodeWithSimpleName::getNameAsString).orElse("");
        } catch (ClassCastException exception) {
            if (node.getParentNode().isPresent() && node.getParentNode().get() instanceof ObjectCreationExpr) {
                className = ((ObjectCreationExpr) node.getParentNode().get()).getType().getNameAsString() + "##AnonymousClass";
                System.err.println("Anonymous function detected! Using interface name with \"##AnonymousClass\" suffix.");
            } else {
                System.err.println("Failed solving " + node.getNameAsString() + " parent's class name. Expected method within a class and received " + node.getClass() + " whose parent is " + (node.getParentNode().isPresent() ? "of type " + node.getParentNode().get().getClass() : "unavailable."));
                throw exception;
            }
        }

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
