package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import testsmell.ISmell;

import java.util.List;

public interface ITestSmell{
   List<ISmell> runAnalysis(CompilationUnit cu);
}
