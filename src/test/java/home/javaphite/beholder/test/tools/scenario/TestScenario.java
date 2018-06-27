package home.javaphite.beholder.test.tools.scenario;

import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//TODO: add javaDocs to this class' API
public final class TestScenario {
    private Logger logger;

    private List<String> givenDescriptions = new ArrayList<>();
    private List<String> whenDescriptions = new ArrayList<>();
    private List<String> thenDescriptions = new ArrayList<>();
    private List<Object> givens = new ArrayList<>();
    private MetaFunction<?> when;
    private Object then;

    public TestScenario() {
        this.logger= LoggerFactory.getLogger(TestScenario.class);
    }

    public <T> TestScenario given(String description, T given, Object...infoValues)
            throws IllegalArgumentException {
        givenDescriptions.add(fillPlaceholders(description, given, infoValues));
        givens.add(given);
        return this;
    }

    public <R> TestScenario when(String description, MetaFunction<R> action, Object...infoValues)
            throws IllegalArgumentException {
        whenDescriptions.add(fillPlaceholders(description, action, infoValues));
        this.when=action;
        return this;
    }

    public <R> TestScenario then(String description, R expectedResult, Object...infoValues)
            throws IllegalArgumentException {
        thenDescriptions.add(fillPlaceholders(description, expectedResult, infoValues));
        this.then = expectedResult;
        return this;
    }

    public void perform() throws AssertionError {
        Object result;

        switch (givens.size()) {
            case 1: result = when.apply(givens.get(0)); break;
            case 2: result = when.apply(givens.get(0), givens.get(1)); break;
            case 3: result = when.apply(givens.get(0), givens.get(1), givens.get(2)); break;
            default:
                throw new IndexOutOfBoundsException("Only 1-3 givens supported, but was " + givens.size());
        }

        check(result);
    }

    private void check(Object result){
        logger.info("TEST DESCRIPTION: {}", this);

        try {
            logger.info("RESULT: {}", Objects.toString(result));
            Assertions.assertEquals(then, result, "Test failed!");
            logger.info("Test passed.");
        }
        catch (AssertionError error) {
            logger.error(error.getMessage());
            throw new AssertionError(error);
        }
    }

    private String fillPlaceholders(String stringWithPlaceholders, Object mainFiller, Object...secondaryFillers) {
        String mainPlaceholderPattern="\\{@}";
        String secondaryPlaceholderPattern="\\{}";
        String result;

        try {
            result = Objects.requireNonNull(stringWithPlaceholders, "String with placeholders couldn't be null.");

            Object nonNullMainFiller = Objects.requireNonNull(mainFiller, "Null fillers not allowed, but main filler is null!");
            result = result.replaceAll(mainPlaceholderPattern, nonNullMainFiller.toString());

            for (int i = 0; i < secondaryFillers.length; i++) {
                Object nonNullFiller = Objects.requireNonNull(secondaryFillers[i], "Null fillers not allowed, but element " + i + " is null!");
                result = result.replaceFirst(secondaryPlaceholderPattern, nonNullFiller.toString());
            }
        } catch (NullPointerException exception){
            throw new IllegalArgumentException(exception);
        }

        return result;
    }

    private String printDescriptions(List<String> descriptions, String title){
        StringBuilder strForm = new StringBuilder("");
        String separator = System.lineSeparator();
        String tabulation = "\t\t\t\t\t";

        strForm.append(separator).append(tabulation).append(title);

        for (String element: descriptions) {
                strForm.append(separator).append(tabulation).append(" - ").append(element);
        }
        return strForm.toString();
    }

    @Override
    public String toString() {
        StringBuilder strForm = new StringBuilder("");

        strForm.append(printDescriptions(givenDescriptions, "GIVEN:"))
                .append(printDescriptions(whenDescriptions, "WHEN:"))
                .append(printDescriptions(thenDescriptions, "THEN:"));

        return strForm.toString();
    }

}
