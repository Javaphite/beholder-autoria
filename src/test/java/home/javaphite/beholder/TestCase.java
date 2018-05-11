package home.javaphite.beholder;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Mother of all test cases
 * */
abstract class TestCase {
    protected static final Logger logger = LoggerFactory.getLogger("testLogger");
    protected static int testsPassed=0;
    protected static int testsOverall=0;
    
    protected enum LogMsg{
        // Each '{}' holds place for string arg
        TEST_CASE_INIT("STARTING TEST CASE FOR {}..."),
        METHOD_TEST_START("TESTING METHOD: {}"),
        METHOD_TEST_END("TEST COMPLETED FOR METHOD: {}."),
        TEST_RESULT("RESULT: {}."),
        TEST_CASE_COMPLETED("TESTCASE COMPLETED FOR {}."),
        TEST_CASE_RESULT("RESULT: {}/{} tests passed - {}!"),
        FAIL("TEST FAILED!"),
        SUCCESS("TEST SUCCESSFUL!");
        
        String msg;
        
        LogMsg(String msg){
            this.msg=msg;
        }
        
        @Override
        public String toString(){
            return this.msg;
        }
        
    }
    
    @BeforeAll
    static void initAll(){
        // Refresh counters
        testsOverall = 0;
        testsPassed = 0;
        
        // Add tested class name to logging context
        MDC.put("className", new DataSchemaTest().getClass().getName().replaceAll("Test", ""));
    
        logger.info(LogMsg.TEST_CASE_INIT.msg, MDC.get("className"));
    }
    
    @BeforeEach
    void initEach(TestInfo testInfo){
        testsOverall++;
        logger.info(LogMsg.METHOD_TEST_START.msg, testInfo.getTags());
    }
    
    @AfterEach
    void tearDownEach(TestInfo testInfo){
        logger.info(LogMsg.METHOD_TEST_END.msg, testInfo.getTags());
    }
    
    @AfterAll
    static void tearDownAll(){
         logger.info(LogMsg.TEST_CASE_COMPLETED + " " +  LogMsg.TEST_CASE_RESULT,
                MDC.get("className"), testsPassed, testsOverall,
                testsPassed!=testsOverall?"FAIL":"SUCCESS");
         
        // Clear logging context
        MDC.remove("className");
    }
    
}
