package home.javaphite.beholder;

import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class TestCase {
    protected static Logger logger;

    private static int testsOverall=0;
    private static int testsPassed=0;

    //TODO: add JavaDoc comments for this method
    //TODO: add few overloaded variants for special situations: no when, void method in when
    protected <I,O> void check(I given, Function<I,O> when, O then, GwtDescription description){
        StringBuilder assertionFailedMessage=new StringBuilder();

        testsOverall++;
        logger.trace("TEST DESCRIPTION: {}{}", System.lineSeparator(), description.toString());

        try {
            O result = when.apply(given);

            logger.trace("RESULT: {}", Objects.toString(result));

            assertionFailedMessage.append("Test failed! Expected ");
            assertionFailedMessage.append(then);
            assertionFailedMessage.append(", but get ");
            assertionFailedMessage.append(result);

            Assertions.assertEquals(result, then, assertionFailedMessage.toString());

            testsPassed++;
            logger.trace("Test passed.");
        } catch (AssertionError error){
            logger.error(error.getMessage());

            throw new AssertionError(error);
        }
    }

    /*protected <I> void check(I given, Consumer<I> when, Predicate<I> then, GwtDescription description){
        try {
            when.accept(given);
            Boolean result=then.test(given);
            logger.trace("Result: " + Objects.toString(result));
            Assertions.assertTrue(result);

        } catch (AssertionError error){

            throw new AssertionError(error);
        }
    }*/


    public class GwtDescription{
        private List<String> givens=new ArrayList<>();
        private List<String> whens=new ArrayList<>();
        private List<String> thens=new ArrayList<>();

        public void given(String given, Object...fillers) throws IllegalArgumentException{
                givens.add(fillPlaceholders(given, fillers));
        }

        public void when(String when, Object...fillers) throws IllegalArgumentException{
            whens.add(fillPlaceholders(when, fillers));
        }

        public void then(String then, Object...fillers) throws IllegalArgumentException{
            thens.add(fillPlaceholders(then, fillers));
        }

        private String fillPlaceholders(String given, Object...fillers){
           String placeholderPattern="\\{}";
           String resultString;

            try {
               resultString = Objects.requireNonNull(given, "Main string couldn't be null.");

               for (int i = 0; i < fillers.length; i++) {
                   Object filler=Objects.requireNonNull(fillers[i], "Null fillers not allowed, but element " + i + " is null!");
                   resultString=resultString.replaceFirst(placeholderPattern, filler.toString());
               }
           } catch (NullPointerException exception){
               throw new IllegalArgumentException(exception);
           }

           return resultString;
        }

        private String printList(List<String> list, String title){
            StringBuilder strForm=new StringBuilder("");
            String separator=System.lineSeparator();
            String tabulation="\t\t\t\t\t";

            strForm.append(tabulation);
            strForm.append(title);
            strForm.append(separator);

            for (String element: list) {
                strForm.append(tabulation);
                strForm.append(" - ");
                strForm.append(element);
                strForm.append(separator);
            }
            return strForm.toString();
        }

        @Override
        public String toString() {
            StringBuilder strForm=new StringBuilder("");

            strForm.append(printList(givens, "GIVEN:"));
            strForm.append(printList(whens, "WHEN:"));
            strForm.append(printList(thens, "THEN:"));

            return strForm.toString();
        }
    }

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
