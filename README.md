# Test Smell Detector

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
                <p>Multiple asserts in a test method without a descriptive message impacts readability as it’s not possible to understand the reason for an asserts failure </p>
                <p>Caused By: Used to verify complex logic or iterate through a collection</p>
                <p>Detection: Assert methods called from the body of a test method does not contain a explanation message</p>          
            </td>
        </tr>
        <tr>
            <td>Conditional Test Logic (also known as 'Indented Test')</td>
            <td>
                <p>Test methods should be simple and execute all statements. Conditions will alter the behavior of the test and expected output</p>
                <p>Caused By: Used to verify complex logic or iterate through a collection</p>
                <p>Detection: Test method body contains one or more  loops, conditional statements (ternary operator, switch/case, if condition)</p>             
            </td>
        </tr>
        <tr>
            <td>Constructor Initialization</td>
            <td>
                <p>Ideally, the test suite should not have a constructor. Initialization of fields should be in the setUP() method</p>
                <p>Caused By: Developers are probably unware of the purpose of setUP() method</p>
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
                <p>Detection: A field initialized in the setup() but not accessed in the body of a test method</p>             
            </td>
        </tr>
        <tr>
            <td>Mystery Guest</td>
            <td>
                <p>Use of external resources in test methods will result in stability and performance issues. Use mock objects in place of the external resources</p>
                <p>Caused By: Developers not understanding the concept of mock objects or debugging code that was not removed</p>
                <p>Detection: Test method body contains calls to external resources such as external storage, databases, etc.</p>             
            </td>
        </tr>
        <tr>
            <td>Print Statement</td>
            <td>
                <p>Test methods should not contain print statements as execution of unit tests is an automated process with little to no human intervention. Hence, print statements are redundant.</p>
                <p>Caused By: Created for debugging purposes and then forgotten about</p>
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
                <p>Test methods verify objects by invoking the default toString() method of the object and comparing the output against an specific string. Changes to the implementation of toString() might results in a failure. The correct approach is to implement a custom method within the object to perform this comparison</p>
                <p>Caused By: Possibly done for debugging purposes and then forgotten to be removed or as a mistake</p>
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
  </tbody>
</table>
