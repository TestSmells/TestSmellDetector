package edu.rit.se.testsmells.testsmell;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodValidator {
    private static MethodValidator instance;

    private MethodValidator() {
    }

    public static MethodValidator getInstance() {
        if (instance == null) {
            instance = new MethodValidator();
        }
        return instance;
    }

    public boolean isValidTestMethod(MethodDeclaration method) {
        return isVisible(method) && (hasAnnotation(method, "Test") || nameStartsWith(method, "test"));
    }

    public boolean isValidSetupMethod(MethodDeclaration method) {
        return isVisible(method) && (hasAnnotation(method, "Before") || hasAnnotation(method, "BeforeEach") || nameStartsWith(method, "setUp"));
    }

    private boolean nameStartsWith(MethodDeclaration method, String value) {
        String method_name = method.getNameAsString().toLowerCase();
        String expected_name = value.toLowerCase();

        return method_name.startsWith(expected_name);
    }

    private boolean isVisible(MethodDeclaration method) {
        return !isIgnored(method) && isPublic(method);
    }

    private boolean hasAnnotation(MethodDeclaration method, String annotation) {
        return method.getAnnotationByName(annotation).isPresent();
    }

    private boolean isPublic(MethodDeclaration method) {
        return method.getModifiers().contains(Modifier.PUBLIC);
    }

    private boolean isIgnored(MethodDeclaration method) {
        return hasAnnotation(method, "Ignore");
    }
}