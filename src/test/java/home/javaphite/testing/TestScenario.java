package home.javaphite.testing;

import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//TODO: add javaDocs to this class' API
public final class TestScenario<T,R>{
    private Logger logger;

    private List<String> givenDescriptions=new ArrayList<>();
    private List<String> whenDescriptions=new ArrayList<>();
    private List<String> thenDescriptions=new ArrayList<>();
    private List<T> given = new ArrayList<>();
    private MultiFunction<T, R> when;
    private R then;

    public TestScenario(){
        this.logger= LoggerFactory.getLogger(TestScenario.class);
    }

    public TestScenario<T,R> given(String description, T givenValue, Object...additionalValues)
            throws IllegalArgumentException {
        givenDescriptions.add(fillPlaceholders(description, givenValue, additionalValues));
        given.add(givenValue);
        return this;
    }

    public TestScenario<T,R> when(String description, MultiFunction<T, R> when, Object...additionalValues)
            throws IllegalArgumentException {
        whenDescriptions.add(fillPlaceholders(description, when, additionalValues));
        this.when=when;
        return this;
    }

    public TestScenario<T,R> then(String description, R thenValue, Object...additionalValues)
            throws IllegalArgumentException {
        thenDescriptions.add(fillPlaceholders(description, thenValue, additionalValues));
        then=thenValue;
        return this;
    }

    public void perform() throws AssertionError{
        check(given, when, then);
    }

    private void check(List<T> given,  MultiFunction<T, R> when, R then){
        T[] arrayOfGivens = Array.newInstance(arrayOfGivens,0);

        R result = when.apply(arrayOfGivens);

        logger.trace("TEST DESCRIPTION: {}", this.toString());

        try {
            logger.trace("RESULT: {}", Objects.toString(result));
            Assertions.assertEquals(then, result, "Test failed!");
            logger.trace("Test passed.");
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
            result = stringWithPlaceholders.replaceAll(mainPlaceholderPattern, nonNullMainFiller.toString());

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
