package home.javaphite.beholder.test.tools.log;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Simple test lifecycle logger based on JUnit 5 lifecycle and extension model.
 * Provides basic logging for its subclasses.
*/

@ExtendWith(TestLifecycleLogger.class)
public class TestLifecycleLogger implements TestExecutionExceptionHandler {
    protected static final Logger LOG = LoggerFactory.getLogger(TestLifecycleLogger.class);
    private static int testsOverall;
    private static int testsFailed;

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        LOG.debug("Exception thrown during test execution: ", throwable);
        if (throwable instanceof AssertionError) {
            testsFailed++;
        }
        throw throwable;
    }

    @BeforeAll
    static void initAll(TestInfo testInfo) {
        // Add tested class name to logging context
        String className = testInfo.getDisplayName();
        MDC.put("className", className);
        LOG.info("Testing {}", className);
    }

    @BeforeEach
    void initEach(TestInfo testInfo) {
        String methodName = testInfo.getTags() + " " + testInfo.getDisplayName();
        LOG.info("Starting test {}", methodName);
    }

    @AfterEach
    void tearDownEach() {
       testsOverall++;
    }

    @AfterAll
    static void tearDownAll() {
        int testsPassed = testsOverall-testsFailed;
        String result = (testsPassed == testsOverall)? "SUCCESS": "FAIL";
        LOG.info("Result of {} tests: {}/{} passed - {}", MDC.get("className"), testsPassed, testsOverall, result);

        // Clear logging context
        MDC.remove("className");
        // Reset counters
        testsOverall=0;
        testsFailed=0;
    }
}


