package testsmell

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import testsmell.smell.EagerTest
import thresholds.DefaultThresholds

class TestAssertionsDetection {

    private lateinit var testCompilationUnit: CompilationUnit
    private lateinit var productionCompilationUnit: CompilationUnit
    private lateinit var testFile: TestFile
    private val booleanGranularity: ((AbstractSmell) -> Any) = { it.hasSmell() }

    @BeforeEach
    fun setup() {
        testCompilationUnit = StaticJavaParser.parse(simpleTest)
        productionCompilationUnit = StaticJavaParser.parse(simpleClass)
        testFile = mock(TestFile::class.java)
        Mockito.`when`(testFile.testFileNameWithoutExtension).thenReturn("fake/path")
        Mockito.`when`(testFile.productionFileNameWithoutExtension).thenReturn("fake/path")
    }

    /**
     * Check whether production calls made in the assertions count toward detection of
     * eagerness. In theory, they should. This might not apply (or carefully considered) in the
     * context of generated tests, where the assertions are placed at the end of the search process.
     */
    @Test
    fun `Assertions counting into eager detection`() {
        val smell = EagerTest(DefaultThresholds())
        smell.runAnalysis(testCompilationUnit, productionCompilationUnit,
            testFile.testFileNameWithoutExtension, testFile.productionFileNameWithoutExtension)
        Mockito.`when`(testFile.testSmells).thenReturn(listOf(smell))
        val values = testFile.testSmells.map { booleanGranularity.invoke(it) }
        Assertions.assertTrue(values[0] as Boolean)
    }

    private val simpleClass = """
        public class Calculator {

            private int numberOne;
            private int numberTwo;

            public Calculator(int numberOne, int numberTwo) {
                this.numberOne = numberOne;
                this.numberTwo = numberTwo;
            }

            public int sum() {
                return numberOne + numberTwo;
            }

            public int sub() {
                return numberOne - numberTwo;
            }

            public int mul() {
                return numberOne * numberTwo;
            }
        }
    """.trimIndent()

    private val simpleTest = """
        import static org.junit.jupiter.api.Assertions.assertEquals;

        import org.junit.jupiter.api.Test;

        public class CalculatorTest {

            @Test
            public void testDummy() {
                Calculator calc = new Calculator(10, 5);
                int m = calc.mul();
                assertEquals(m, 50);
                assertEquals(calc.sum(), 15);
                assertEquals(calc.sub(), 5);
            }
        }
    """.trimIndent()
}
