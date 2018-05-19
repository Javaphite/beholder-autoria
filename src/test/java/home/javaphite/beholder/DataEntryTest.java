package home.javaphite.beholder;

import home.javaphite.testing.BddTestScenario;
import home.javaphite.testing.LoggedTestCase;
import org.junit.jupiter.api.Test;

public class DataEntryTest extends LoggedTestCase {

    @Test
    void bddTest() {
    Integer a = 5;
    Integer b=10;
    Boolean expectedResult=Boolean.TRUE;

        BddTestScenario<Integer,Boolean> testScenario=new BddTestScenario<>();

        testScenario.given("Integer value {@}", a);
        testScenario.given("AND integer {@}", b);
        testScenario.when("Given {} compared to {}", (given)->(given.get(0).compareTo(given.get(1))>0), a, b);
        testScenario.then("Result must be {@}", expectedResult);

        testScenario.perform();


    }

}
