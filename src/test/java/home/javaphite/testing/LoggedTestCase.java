package home.javaphite.testing;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public abstract class LoggedTestCase {
    protected static Logger logger;
    private static int testsOverall = 0;
    private static int testsPassed = 0;
    private static final Map<String, String> messages = new HashMap<>();

    @BeforeAll
    static void initAll(TestInfo testInfo) {
        setLogger(LoggerFactory.getLogger("testLogger"));

        messages.put("initAll", "Testing {}");
        messages.put("startTest", "Starting test {}");
        messages.put("result", "Result of {} tests: {}/{} passed - {}");

        // Add tested class name to logging context
        MDC.put("className", testInfo.getTestClass().toString().replaceAll("s?(Test)?\\[?]?(Optional)?(class)?", ""));

        logger.info(messages.get("initAll"), MDC.get("className"));
    }

    @BeforeEach
    void initEach(TestInfo testInfo) {
        logger.info(messages.get("startTest"),
                testInfo.getTestMethod().toString().replaceAll(".+(?=(\\.[^.(]+\\())", "").replaceAll("\\.?]?(\\(.*\\))?", ""));
    }

    @AfterEach
    void tearDownEach(){
        testsOverall++;
    }

    @AfterAll
    static void tearDownAll() {
        logger.info(messages.get("result"),
                MDC.get("className"), getTestsPassed(), getTestsOverall(),
                getTestsOverall() != getTestsPassed() ? "FAIL" : "SUCCESS");

        // Clear logging context
        MDC.remove("className");
    }

    private static void setLogger(Logger loggerRef) {
        logger = loggerRef;
    }

    private static int getTestsOverall() {
        return testsOverall;
    }

    private static int getTestsPassed() {
        return testsPassed;
    }

    public static void countAsPassed(){ testsPassed++;}

}
