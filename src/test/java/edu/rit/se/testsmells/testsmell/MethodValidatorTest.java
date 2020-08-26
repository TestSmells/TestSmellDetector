package edu.rit.se.testsmells.testsmell;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodValidatorTest {
    MethodValidator sut;

    @BeforeEach
    void setUp() {
        sut = new MethodValidator();
    }

    @Test
    void isValidTestMethod_ignored() {
        String code =
                "class nothing { \n" +
                        "@Ignore \n" +
                        "@Test \n" +
                        "public void sampleTest(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertFalse(sut.isValidTestMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidTestMethod_private() {
        String code =
                "class nothing { \n" +
                        "@Test \n" +
                        "private void sampleTest(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertFalse(sut.isValidTestMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidTestMethod_annotated() {
        String code =
                "class nothing { \n" +
                        "@Test \n" +
                        "public void sampleTest(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertTrue(sut.isValidTestMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidTestMethod_startsWithTest() {
        String code =
                "class nothing { \n" +
                        "public void testSample(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertTrue(sut.isValidTestMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidTestMethod_visibleNonTestMethod() {
        String code =
                "class nothing { \n" +
                        "public void sampleTest(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertFalse(sut.isValidTestMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_ignored() {
        String code =
                "class nothing { \n" +
                        "@Ignore \n" +
                        "@Before \n" +
                        "public void setUp(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertFalse(sut.isValidSetupMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_private() {
        String code =
                "class nothing { \n" +
                        "@Before \n" +
                        "private void setUp(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertFalse(sut.isValidSetupMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_annotated() {
        String code =
                "class nothing { \n" +
                        "@Before \n" +
                        "public void setSut(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertTrue(sut.isValidSetupMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_annotatedAlt() {
        String code =
                "class nothing { \n" +
                        "@BeforeEach \n" +
                        "public void setSut(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertTrue(sut.isValidSetupMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_namedSetUp() {
        String code =
                "class nothing { \n" +
                        "public void setUp(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertTrue(sut.isValidSetupMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_visibleButIsTestMethod() {
        String code =
                "class nothing { \n" +
                        "@Test \n" +
                        "public void sampleTest(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub((MethodDeclaration a) -> assertFalse(sut.isValidSetupMethod(a)));

        assertion.visit(JavaParser.parse(code), null);
    }

    static class MethodVisitorStub extends VoidVisitorAdapter<Void> {
        Consumer<MethodDeclaration> func;

        public MethodVisitorStub(Consumer<MethodDeclaration> func) {
            this.func = func;
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            super.visit(n, arg);
            func.accept(n);
        }
    }
}