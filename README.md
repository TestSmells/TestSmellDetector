# Test Smell Detector

## Introduction

Unit test code, just like any other source code, is subject to bad programming practices, known also as anti-patterns, defects and smells *[1]*. Smells, being symptoms of bad design or implementation decisions, has been proven to be responsible for decreasing the quality of software systems from various aspects, such as making it harder to understand, more complex to maintain, more prone to errors and bugs *[2]*.

Test smells are defined as bad programming practices in unit test code (such as how test cases are organized, implemented and interact with each other) that indicate potential design problems in the test source code *[3]* *[4]* *[5]* *[6]*.

## Project Overview

The purpose of this project is twofold:

1. Contribute to the list of existing test smells, by proposing new test smells that developers need to be aware of.
2. Provide developers with a tool to automatically detect test smell in their unit test code. 

## Detected Smells

The below table specifies the types of smells that are detected by this tool along with a brief description of each smell and the detection strategy. 
<table>

    <thead>
        <tr>
            <th>Smell Name</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Assertion Roulette</td>
            <td>
                <p>Multiple asserts in a test method without a descriptive message impacts readability/understandability/maintainability as it’s not possible to understand the reason for an asserts failure </p>
                <p>Caused By: Developer implementing the test method is aware of the purpose of the assert output</p>
                <p>Detection: Assert methods called from the body of a test method do not contain a explanation message</p>          
            </td>
        </tr>
        <tr>
            <td>Conditional Test Logic (also known as 'Indented Test')</td>
            <td>
                <p>Test methods should be simple and must execute all statements. Conditions will alter the behavior of the test and expected output</p>
                <p>Caused By: Used to verify complex logic or iterate through a collection</p>
                <p>Detection: Test method body contains one or more  loops, conditional statements (ternary operator, switch/case, if condition)</p>             
            </td>
        </tr>
        <tr>
            <td>Constructor Initialization</td>
            <td>
                <p>Ideally, the test suite should not have a constructor. Initialization of fields should be in the setup() method</p>
                <p>Caused By: Developers are probably unware of the purpose of setup() method</p>
                <p>Detection: Class contains one or more constructors</p>             
            </td>            
        </tr>
        <tr>
            <td>Default Test</td>
            <td>
                <p>By default Android Studio creates default test classes when a project is created. These classes are meant to serve as an example for developers when wring unit tests and should either be removed or renamed</p>
                <p>Caused By: Developers do not write unit tests and hence these artifacts remain in the project or developers start adding test methods into these files and it soon becomes burdensome/risky to refactor</p>
                <p>Detection: The class name is ether 'ExampleUnitTest' or 'ExampleInstrumentedTest'</p>             
            </td>
        </tr>
        <tr>
            <td>Empty Test</td>
            <td>
                <p>A test method that contains an empty body</p>
                <p>Caused By: Created for debugging purposes and then forgotten about or contains commented out code</p>
                <p>Detection: Test method body contains zero statements</p>             
            </td>
        </tr>
        <tr>
            <td>Exception Catching Throwing</td>
            <td>
                <p>Use Junit's exception handling to automatically pass/fail the test instead of writing custom exception handling code or throwing an exception</p>
                <p>Caused By: A passing or failing of test method is dependent on the tested method throwing an exception</p>
                <p>Detection: Class contains one or more constructors</p> 
            </td>
        </tr>
        <tr>
            <td>General Fixture</td>
            <td>
                <p>A test setup/fixture method that initializes fields that are not accessed by test methods indicates that the fixture is too generalized. A drawback of it being too general is that unnecessary work is being done when a test method is run.</p>
                <p>Caused By: The test fixture is implemented to support multiple tests, each having unique requirements</p>
                <p>Detection: A field initialized in the setup() method but not accessed in the body of a test method</p>             
            </td>
        </tr>
        <tr>
            <td>Mystery Guest</td>
            <td>
                <p>Use of external resources in test methods will result in stability and performance issues. Use mock objects in place of external resources</p>
                <p>Caused By: Developers not understanding the concept of mock objects or debugging code that was not removed</p>
                <p>Detection: Test method body contains calls to external resources such as external storage, databases, etc.</p>             
            </td>
        </tr>
        <tr>
            <td>Print Statement</td>
            <td>
                <p>Test methods should not contain print statements as execution of unit tests is an automated process with little to no human intervention. Hence, print statements are redundant.</p>
                <p>Caused By: Created for debugging purposes and then forgotten</p>
                <p>Detection: Test method body contains one or more  System.out.print(),println(),printf() or write() statements</p>             
            </td>
        </tr>
        <tr>
            <td>Redundant Assertion</td>
            <td>
                <p>Test methods containing assert calls that are always true or always false</p>
                <p>Caused By: Possibly done for debugging purposes and then forgotten to be removed or as a mistake</p>
                <p>Detection: If a test method contains an assert call that explicitly returns a true or false (e.g. assertTrue(true) or assertFalse(false))</p>             
            </td>            
        </tr> 
        <tr>
            <td>Sensitive Equality</td>
            <td>
                <p>Test methods verify objects by invoking the default toString() method of the object and comparing the output against an specific string. Changes to the implementation of toString() might result in failure. The correct approach is to implement a custom method within the object to perform this comparison</p>
                <p>Caused By: Developers using a simplistic, but not recommended means of string comparison </p>
                <p>Detection: If a test method contains an assert call that contains an invocation of toString()</p>             
            </td> 
        </tr> 
        <tr>
            <td>Verbose Test</td>
             <td>
                 <p>Similar to the Long Methods code smell, a test method with large number of lines of code impacts readability and maintainability of the method</p>
                 <p>Caused By: When developers fail to keep test methods simple and include too much information and functionality in the methods</p>
                 <p>Detection: The number of statements in the body of a test method exceeds a certain amount</p>
             </td> 
        </tr>                                                             
        <tr>
            <td>Wait And See</td>
            <td>
                <p>Use of Thread.sleep() in test methods can possibly lead to unexpected results as the processing time of tasks on different devices/machines can be different. Use mock objects instead</p>
                <p>Caused By: Used to simulate delays. When developers need the code to wait (i.e. simulate an external event) prior to continuing with the execution</p>
                <p>Detection: Test method body contains one or more Thread.sleep() statements</p>
            </td>             
        </tr> 
        <tr>
            <td>Duplicate Assert</td>
            <td>
                <p>This smell occurs when a test method tests for the same condition multiple times within the same test method. If the test method needs to test the same condition using different values, a new test method should be utilized; the name of the test method should be an indication of the test being performed.</p>
                <p>Caused By: Possible situations that would give rise to this smell include: (1) developers grouping multiple conditions to test a single method, (2) developers performing debugging activities and (3) an accidental copy-paste of code.</p>
                <p>Detection: Test method contains duplicate assert statements</p>
            </td>             
        </tr> 
  </tbody>
</table>

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