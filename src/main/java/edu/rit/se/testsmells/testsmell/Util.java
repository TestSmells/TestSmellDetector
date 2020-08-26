package edu.rit.se.testsmells.testsmell;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;

public class Util {

    public static boolean isValidTestMethod(MethodDeclaration n) {
        boolean valid = false;

        if (!n.getAnnotationByName("Ignore").isPresent()) {
            //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
            if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
                //must be a public method
                if (n.getModifiers().contains(Modifier.PUBLIC)) {
                    valid = true;
                }
            }
        }

        return valid;
    }

    public static boolean isValidSetupMethod(MethodDeclaration n) {
        boolean valid = false;

        if (!n.getAnnotationByName("Ignore").isPresent()) {
            //only analyze methods that either have a @Before annotation (Junit 4) or the method name is 'setUp'
            if (n.getAnnotationByName("Before").isPresent() || n.getNameAsString().equals("setUp")) {
                //must be a public method
                if (n.getModifiers().contains(Modifier.PUBLIC)) {
                    valid = true;
                }
            }
        }

        return valid;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException er) {
            return false;
        }
    }

    public static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
