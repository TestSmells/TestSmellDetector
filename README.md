# Test Smell Detector

## Introduction

Unit test code, just like any other source code, is subject to bad programming practices, known also as anti-patterns, defects and smells *[1]*. Smells, being symptoms of bad design or implementation decisions, has been proven to be responsible for decreasing the quality of software systems from various aspects, such as making it harder to understand, more complex to maintain, more prone to errors and bugs *[2]*.

Test smells are defined as bad programming practices in unit test code (such as how test cases are organized, implemented and interact with each other) that indicate potential design problems in the test source code *[3]* *[4]* *[5]* *[6]*.



## Project Overview

The purpose of this project is twofold:

1. Contribute to the list of existing test smells, by proposing new test smells that developers need to be aware of.
2. Provide developers with a tool to automatically detect test smell in their unit test code. 



## Tool Usage

#### Input

Prior to executing the tool, an CSV file needs to be created. The CSV file specifies the list of test files (and their associated production file). This file will be used as input to the tool. The format of the file should be:

```
appName,pathToTestFile,pathToProductionFile
```

 Example:

`myCoolApp,F:\Apps\myCoolApp\code\test\GraphTest.java,F:\Apps\myCoolApp\code\src\Graph.java`
`myCoolApp,F:\Apps\myCoolApp\code\test\EmployeeTest.java,F:\Apps\myCoolApp\code\src\Employee.java`
`myCoolApp,F:\Apps\myCoolApp\code\test\EmployeeRelationship.java`

*Note: In the event a production file is not associated with a test file, then detection for test smells that require production files are not run.*

#### Execution

Once the CSV file has been created, the path to the CSV file need to be passed as an argument when executing the jar.

`java -jar .\TestSmellDetector.jar pathToInputFile.csv`

Example:

`java -jar .\TestSmellDetector.jar "F:\Projects\TestSmellDetector\inputFile.csv"`

#### Output

The tool outputs a CSV file containing the results of the execution. The output CSV file will be created in the same location as the jar. The CSV file contains the path of the test files (and their associated production file) along with the detection status for each smell. A detection of status of 'true' indicates that the associated smell exists in the test file.



## Detected Smells

Provided below are the test smell detected by the tool along with the detection strategy. Examples of each smell type are available [here](https://github.com/TestSmells/TestSmellDetector/blob/master/TestSmellExamples.md).

#### Assertion Roulette

Occurs when a test method has multiple non-documented assertions. Multiple assertion statements in a test method without a descriptive message impacts readability/understandability/maintainability as it’s not possible to understand the reason for the failure of the test.

Detection: A test method having more that one assertion statement without an explanation message.

#### General Fixture

Occurs when a test case fixture is too general and the test methods only access part of it. A test setup/fixture method that initializes fields that are not accessed by test methods indicates that the fixture is too generalized. A drawback of it being too general is that unnecessary work is being done when a test method is run.

Detection: A field initialized in the setup() method but not accessed in the body of a test method

#### Mystery Guest

Occurs when a test method utilizes external resources (e.g. files, database, etc.). Use of external resources in test methods will result in stability and performance issues. Developers should use mock objects in place of external resources.

Detection: Test method body contains calls to external resources such as external storage, databases, etc.

#### Sensitive Equality

Occurs when the `toString` method is used within a test method. Test methods verify objects by invoking the default `toString()` method of the object and comparing the output against an specific string. Changes to the implementation of `toString()` might result in failure. The correct approach is to implement a custom method within the object to perform this comparison.

Detection: If a test method contains an assertion call that contains an invocation of `toString()`

#### Eager Test

Occurs when a test method invokes several methods of the production object. This smell results in difficulties in test comprehension and maintenance.

#### Lazy Test

Occurs when multiple test methods invoke the same method of the production object.

#### Conditional Test Logic

Test methods need to be simple and execute all statements in the production method. Conditions within the test method will alter the behavior of the test and its expected output, and would lead to situations where the test fails to detect defects in the production method since test statements were not executed as a condition was not met.

Detection: A test method that contains one or more control statements (i.e if statement, switch statement, conditional expression, for statement, foreach statement and while statement) 

#### Constructor Initialization

Ideally, the test suite should not have a constructor. Initialization of fields should be in the `setUp()` method.

Detection: A test class that contains a constructor declaration

#### Default Test

By default Android Studio creates default test classes when a project is created. These classes are meant to serve as an example for developers when wring unit tests and should either be removed or renamed. Having such files in the project will cause developers to start adding test methods into these files, making the default test class a container of all test cases. This also  would possibly cause problems when the classes need to be renamed in the future.

Detection: A test class is named either *ExampleUnitTest* or *ExampleInstrumentedTest*

#### Duplicate Assert

This smell occurs when a test method tests for the same condition multiple times within the same test method. If the test method needs to test the same condition using different values, a new test method should be utilized; the name of the test method should be an indication of the test being performed.

Detection: A test method that contains more than one assertion statement with the same parameters

#### Empty Test

Occurs when a test method does not contain executable statements. An empty test can be considered problematic and more dangerous than not having a test case at all since JUnit will indicate that the test passes even if there are no executable statements present in the method body. As such, developers introducing behavior-breaking changes into production class, will not be notified of the alternated outcomes as JUnit will report the test as passing.

Detection: A test method that does not contain a single executable statement in its body

#### Exception Catching & Throwing

This smell occurs when a test method explicitly a passing or failing of a test method is dependent on the production method throwing an exception. Developers should utilize Junit's exception handling to automatically pass/fail the test instead of writing custom exception handling code or throwing an exception.

Detection: A test method that contains either a throw statement or a catch clause

#### Redundant Print

Print statements in unit tests are redundant as unit tests are executed as part of an automated process with little to no human intervention.

Detection: A test method that invokes either the `print` or `println` or `printf` or `write` method of the System class

#### Redundant Assertion

This smell occurs when test methods contain assertion statements that are either always true or always false.

Detection: A test method that contains an assertion statement in which the expected and actual parameters are the same

#### Unknown Test

It is possible for a test method to written sans an assertion statement, in such an instance JUnit will show the test method as passing if the statements within the test method did not result in an exception, when executed. New developers to the project will find it difficult in understanding the purpose of such test methods (more so if the name of the test method is not descriptive enough).

Detection: A test method that does not contain a single assertion statement and `@Test(expected)` annotation parameter

#### Wait And See

Explicitly causing a thread to sleep can lead to unexpected results as the processing time for a task can differ on different devices. Developers introduce this smell when they need to pause execution of statements in a test method for a certain duration (i.e. simulate an external event) and then continuing with execution.

Detection: A test method that invokes the `Thread.sleep()` method



## Contact

For more information or to contribute to this project, you can reach:

1. [Anthony Peruma](https://github.com/shehan)
2. [Mohamed Wiem Mkaouer](https://github.com/mkaouer)
3. [Khaled Almalki](https://github.com/khaledalmalki)



## References

[1] Fowler, M., & Beck, K. (1999). Refactoring: improving the design of existing code. Addison-Wesley Professional.

[2] Mäntylä, M. V., & Lassenius, C. (2006). Subjective evaluation of software evolvability using code smells: An empirical study. Empirical Software Engineering, 11(3), 395-431.

[3] Van Deursen, A., Moonen, L., van den Bergh, A., & Kok, G. (2001, May). Refactoring test code. In *Proceedings of the 2nd international conference on extreme programming and flexible processes in software engineering (XP2001)* (pp. 92-95).

[4] Greiler, M., Zaidman, A., Deursen, A. V., & Storey, M. A. (2013, May). Strategies for avoiding text fixture smells during software evolution. In *Proceedings of the 10th Working Conference on Mining Software Repositories* (pp. 387-396). IEEE Press.

[5] Palomba, F., Di Nucci, D., Panichella, A., Oliveto, R., & De Lucia, A. (2016, May). On the diffusion of test smells in automatically generated test code: An empirical study. In *Proceedings of the 9th International Workshop on Search-Based Software Testing* (pp. 5-14). ACM.

[6] Meszaros, G. (2007). *xUnit test patterns: Refactoring test code*. Pearson Education.