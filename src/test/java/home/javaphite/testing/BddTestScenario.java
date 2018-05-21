package home.javaphite.testing;

import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

//TODO: add javaDocs to this class' API
public final class BddTestScenario<T,R>{
    private Logger logger;

    private List<String> givenDescriptions=new ArrayList<>();
    private List<String> whenDescriptions=new ArrayList<>();
    private List<String> thenDescriptions=new ArrayList<>();
    private List<T> given=new ArrayList<>();
    private Function<List<T>, R> when;
    private R then;

    public BddTestScenario(){
        this.logger= LoggerFactory.getLogger(BddTestScenario.class);
    }

    //TODO: check if this variant really necessary
    public BddTestScenario(Logger logger){
        this.logger=logger;
    }

    public BddTestScenario<T,R> given(String description, T givenValue, Object...additionalValues) throws IllegalArgumentException{
        givenDescriptions.add(fillPlaceholders(description, givenValue, additionalValues));
        given.add(givenValue);
        return this;
    }

    public BddTestScenario<T,R> when(String description, Function<List<T>, R> when, Object...additionalValues) throws IllegalArgumentException{
        whenDescriptions.add(fillPlaceholders(description, when, additionalValues));
        this.when=when;
        return this;
    }

    public void then(String description, R thenValue, Object...additionalValues) throws IllegalArgumentException{
        thenDescriptions.add(fillPlaceholders(description, thenValue, additionalValues));
        then=thenValue;
    }

    private String fillPlaceholders(String stringWithPlaceholders, Object mainFiller, Object...secondaryFillers){
        String mainPlaceholderPattern="\\{@}";
        String secondaryPlaceholderPattern="\\{}";
        String result;

        try {
            result = Objects.requireNonNull(stringWithPlaceholders, "String with placeholders couldn't be null.");

            Object nonNullMainFiller=Objects.requireNonNull(mainFiller, "Null fillers not allowed, but main filler is null!");
            result=stringWithPlaceholders.replaceAll(mainPlaceholderPattern, nonNullMainFiller.toString());

            for (int i = 0; i < secondaryFillers.length; i++) {
                Object nonNullFiller=Objects.requireNonNull(secondaryFillers[i], "Null fillers not allowed, but element " + i + " is null!");
                result=result.replaceFirst(secondaryPlaceholderPattern, nonNullFiller.toString());
            }
        } catch (NullPointerException exception){
            throw new IllegalArgumentException(exception);
        }

        return result;
    }

    private String printList(List<String> list, String title){
        StringBuilder strForm=new StringBuilder("");
        String separator=System.lineSeparator();
        String tabulation="\t\t\t\t\t";

        strForm.append(separator).append(tabulation).append(title);

        for (String element: list) {
                strForm.append(separator).append(tabulation).append(" - ").append(element);
        }
        return strForm.toString();
    }

    @Override
    public String toString() {
        StringBuilder strForm=new StringBuilder("");

        strForm.append(printList(givenDescriptions, "GIVEN:"))
                .append(printList(whenDescriptions, "WHEN:"))
                .append(printList(thenDescriptions, "THEN:"));

        return strForm.toString();
    }

    public void perform() throws AssertionError{
        check(given, when, then);
    }

    private void check(List<T> given, Function<List<T>, R> when, R then){
        StringBuilder assertionFailedMessage=new StringBuilder();
        R result = when.apply(given);

        assertionFailedMessage.append("Test failed! Expected ")
                .append(then)
                .append(", but get ")
                .append(result);

        logger.trace("TEST DESCRIPTION: {}", this.toString());

        try {
            logger.trace("RESULT: {}", Objects.toString(result));
            Assertions.assertEquals(then, result, assertionFailedMessage.toString());

           logger.trace("Test passed.");
        } catch (AssertionError error) {
            logger.error(error.getMessage());

            throw new AssertionError(error);
        }
    }

}
