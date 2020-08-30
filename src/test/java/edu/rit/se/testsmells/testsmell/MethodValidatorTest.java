package edu.rit.se.testsmells.testsmell;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        MethodVisitorStub assertion = new MethodVisitorStub(new TestMethodAssertionCommand(false)::invoke);

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidTestMethod_private() {
        String code =
                "class nothing { \n" +
                        "@Test \n" +
                        "private void sampleTest(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub(new TestMethodAssertionCommand(false)::invoke);

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidTestMethod_annotated() {
        String code =
                "class nothing { \n" +
                        "@Test \n" +
                        "public void sampleTest(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub(new TestMethodAssertionCommand(true)::invoke);

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidTestMethod_startsWithTest() {
        String code =
                "class nothing { \n" +
                        "public void testSample(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub(new TestMethodAssertionCommand(true)::invoke);

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidTestMethod_visibleNonTestMethod() {
        String code =
                "class nothing { \n" +
                        "public void sampleTest(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub(new TestMethodAssertionCommand(false)::invoke);

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

        MethodVisitorStub assertion = new MethodVisitorStub(new SetupMethodAssertionCommand(false)::invoke);

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_private() {
        String code =
                "class nothing { \n" +
                        "@Before \n" +
                        "private void setUp(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub(new SetupMethodAssertionCommand(false)::invoke);

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_annotated() {
        String code =
                "class nothing { \n" +
                        "@Before \n" +
                        "public void setSut(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub(new SetupMethodAssertionCommand(true)::invoke);

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_annotatedAlt() {
        String code =
                "class nothing { \n" +
                        "@BeforeEach \n" +
                        "public void setSut(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub(new SetupMethodAssertionCommand(true)::invoke);

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_namedSetUp() {
        String code =
                "class nothing { \n" +
                        "public void setUp(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub(new SetupMethodAssertionCommand(true)::invoke);

        assertion.visit(JavaParser.parse(code), null);
    }

    @Test
    void isValidSetupMethod_visibleButIsTestMethod() {
        String code =
                "class nothing { \n" +
                        "@Test \n" +
                        "public void sampleTest(){} \n" +
                        "} \n";

        MethodVisitorStub assertion = new MethodVisitorStub(new SetupMethodAssertionCommand(false)::invoke);

        assertion.visit(JavaParser.parse(code), null);
    }

    static class MethodVisitorStub extends VoidVisitorAdapter<Void> {
        Consumer<MethodDeclaration> func;

        public MethodVisitorStub(Consumer<MethodDeclaration> func) {
            this.func = func;
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            func.accept(n);
            super.visit(n, arg);
        }
    }

    private class TestMethodAssertionCommand {
        private final boolean assertionValue;

        private TestMethodAssertionCommand(boolean assertionValue) {
            this.assertionValue = assertionValue;
        }

        public void invoke(MethodDeclaration a) {
            assertEquals(assertionValue, sut.isValidTestMethod(a));
        }
    }

    private class SetupMethodAssertionCommand {
        private final boolean assertionValue;

        private SetupMethodAssertionCommand(boolean assertionValue) {
            this.assertionValue = assertionValue;
        }

        public void invoke(MethodDeclaration a) {
            assertEquals(assertionValue, sut.isValidSetupMethod(a));
        }
    }
}