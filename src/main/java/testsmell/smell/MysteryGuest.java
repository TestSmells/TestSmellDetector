package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.MethodSmell;
import testsmell.ISmell;

import java.util.*;

public class MysteryGuest implements ITestSmell {
    List<ISmell> smellList;

    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        MysteryGuest.ClassVisitor classVisitor = new MysteryGuest.ClassVisitor();
        classVisitor.visit(cu, null);

        return smellList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private List<String> mysteryTypes = new ArrayList<>(
                Arrays.asList(
                        "File",
                        "FileOutputStream",
                        "SQLiteOpenHelper",
                        "SQLiteDatabase",
                        "Cursor",
                        "Context",
                        "HttpClient",
                        "HttpResponse",
                        "HttpPost",
                        "HttpGet",
                        "SoapObject"
                ));

        /*
                private List<String> databaseMethods = new ArrayList<>(
                        Arrays.asList(
                                "getWritableDatabase",
                                "getReadableDatabase",
                                "execSQL",
                                "rawQuery"
                        ));
                private List<String> fileMethods = new ArrayList<>(
                        Arrays.asList(
                                "getFilesDir",
                                "getDir",
                                "getCacheDir",
                                "createTempFile",
                                "getExternalStorageState",
                                "getExternalStoragePublicDirectory",
                                "getExternalFilesDir",
                                "getExternalCacheDir",
                                "getFreeSpace",
                                "getTotalSpace",
                                "deleteFile",
                                "fileList",
                                "openFileOutput",
                                "openRawResource"));
        */
        private MethodDeclaration currentMethod = null;
        private int mysteryCount = 0;
        ISmell methodSmell;
        Map<String, String> map;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
            if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
                currentMethod = n;
                methodSmell = new MethodSmell(currentMethod.getNameAsString());
                super.visit(n, arg);

                methodSmell.setHasSmell(mysteryCount > 0);

                map = new HashMap<>();
                map.put("MysteryCount", String.valueOf(mysteryCount));
                methodSmell.setSmellData(map);

                smellList.add(methodSmell);

                //reset values for next method
                currentMethod = null;
                mysteryCount = 0;
            }
        }

        /*
        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null){
                for (String methodName: fileMethods) {
                    if(n.getNameAsString().equals(methodName)){
                        mysteryCount++;
                    }
                }
                for (String methodName: databaseMethods) {
                    if(n.getNameAsString().equals(methodName)){
                        mysteryCount++;
                    }
                }
            }
        }
        */

        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
            super.visit(n, arg);
            //Note: the null check limits the identification of variable types declared within the method body.
            // Removing it will check for variables declared at the class level.
            //TODO: to null check or not to null check???
            if (currentMethod != null) {
                for (String variableType : mysteryTypes) {
                    //check if the type variable encountered is part of the mystery type collection
                    if ((n.getVariable(0).getType().asString().equals(variableType))) {
                        //check if the variable has been mocked
                        for (AnnotationExpr annotation:n.getAnnotations()) {
                            if(annotation.getNameAsString().equals("Mock") || annotation.getNameAsString().equals("Spy"))
                                break;
                        }
                        // variable is not mocked, hence it's a smell
                        mysteryCount++;
                    }
                }
            }
        }
    }
}
