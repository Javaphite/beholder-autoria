package home.javaphite.testing;

import org.slf4j.Logger;

public abstract class TestCase {
    protected static Logger logger;

    private static int testsOverall=0;
    private static int testsPassed=0;

    public static void setLogger(Logger loggerRef){
        logger=loggerRef;
    }

    public static int getTestsOverall(){
        return testsOverall;
    }

    public static int getTestsPassed() {
        return testsPassed;
    }
}
