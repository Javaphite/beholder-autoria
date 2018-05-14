package home.javaphite.beholder;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

abstract class LoggedTestCase extends TestCase {
    private static final Map<String, String> messages = new HashMap<>();

    @BeforeAll
    static void initAll(TestInfo testInfo){
        setLogger(LoggerFactory.getLogger("testLogger"));

        messages.put("initAll", "Testing {}...");
        messages.put("startTest", "Starting test {}...");
        messages.put("result", "Result of {} tests: {}/{} passed - {}.");

        // Add tested class name to logging context
        MDC.put("className", testInfo.getTestClass().toString().replaceAll("s?(Test)?\\[?]?(Optional)?(class)?", ""));

        logger.info(messages.get("initAll"), MDC.get("className"));
    }

    @BeforeEach
    void initEach(TestInfo testInfo){
        //TODO: there must be better way to get test method clear name!
        logger.info(messages.get("startTest"),
                testInfo.getTestMethod().toString().replaceAll(".+(?=(\\.[^.(]+\\())", "").replaceAll("\\.?]?", ""));
    }

    @AfterAll
    static void tearDownAll(){
        logger.info(messages.get("result"),
                MDC.get("className"), getTestsPassed(), getTestsOverall(),
                getTestsOverall()!=getTestsPassed()?"FAIL":"SUCCESS");

        // Clear logging context
        MDC.remove("className");
    }
}
