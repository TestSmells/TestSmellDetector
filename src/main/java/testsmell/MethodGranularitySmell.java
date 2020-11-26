package testsmell;

public abstract class MethodGranularitySmell extends AbstractSmell {

    /**
     * Returns in output the number of test cases in a test suite (a file) that suffer of a given smell
     */
    public abstract int getNumberOfSmellyTests();
}
