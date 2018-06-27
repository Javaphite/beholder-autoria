package home.javaphite.beholder.test.tools.log;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(TestLifecycleLogger.class)
public class TestLifecycleLogger implements TestExecutionExceptionHandler {
    protected static final Logger LOG = LoggerFactory.getLogger(TestLifecycleLogger.class);
    private int testsOverall;
    private int testsFailed;

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        if (throwable instanceof AssertionError) {
            testsFailed++;
        }
    }

    @BeforeAll
    static void initAll(TestInfo testInfo) {
        // Add tested class tags string to logging context
        String tags = testInfo.getTags().toString();
        MDC.put("tags", tags);
        LOG.info("Testing {}", tags);
    }

    @BeforeEach
    void initEach(TestInfo testInfo) {
        String testName = testInfo.getTags() + " " + testInfo.getDisplayName();
        LOG.info("Starting test {}", testName);
    }

    @AfterEach
    void tearDownEach() {
       testsOverall++;
    }

    @AfterAll
    void tearDownAll() {
        int testsPassed = testsOverall-testsFailed;
        String result = (testsPassed == testsOverall)? "SUCCESS": "FAIL";
        LOG.info("Result of {} tests: {}/{} passed - {}", MDC.get("tags"), testsPassed, testsOverall, result);

        // Clear logging context
        MDC.remove("tags");
    }
}


