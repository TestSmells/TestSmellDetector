package testsmell;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

public class Util {

    public static boolean isValidTestMethod(MethodDeclaration n) {
        boolean valid = false;

        if (!n.getAnnotationByName("Ignore").isPresent()) {
            //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
            if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
                //must be a public method
                if (n.getModifiers().stream().anyMatch(m -> m.getKeyword() == Modifier.Keyword.PUBLIC)) {
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
                if (n.getModifiers().stream().anyMatch(m -> m.getKeyword() == Modifier.Keyword.PUBLIC)) {
                    valid = true;
                }
            }
        }

        return valid;
    }

    public static boolean isInt(String s)
    {
        try
        { int i = Integer.parseInt(s); return true; }

        catch(NumberFormatException er)
        { return false; }
    }

    public static boolean isNumber(String str) {
        try {
            double v = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
        }
        return false;
    }

    /**
     * Replicates the old {@link JavaParser#parse(InputStream)} behavior without {@link StaticJavaParser},
     * since other projects may mess with the static configuration.
     */
    public static CompilationUnit parseJava(InputStream code) {
        JavaParser parser = new JavaParser();
        parser.getParserConfiguration()
            .setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE) // use latest supported java version
            .setAttributeComments(false); // exclude comments

        ParseResult<CompilationUnit> parseResult = parser.parse(code);
        if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
            return parseResult.getResult().get();
        } else {
            throw new ParseProblemException(parseResult.getProblems());
        }
    }

    public static CompilationUnit parseJava(String code) {
        return parseJava(new ByteArrayInputStream(code.getBytes()));
    }
}
