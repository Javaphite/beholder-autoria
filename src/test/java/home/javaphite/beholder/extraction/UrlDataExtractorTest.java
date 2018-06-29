package home.javaphite.beholder.extraction;

import home.javaphite.beholder.test.tools.log.TestLifecycleLogger;
import home.javaphite.beholder.test.tools.scenario.BinaryFunction;
import home.javaphite.beholder.test.tools.scenario.TestScenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

@DisplayName("UrlDataExtractor")
class UrlDataExtractorTest extends TestLifecycleLogger {
    @Test
    @Tag("applyFilters")
    void returnsStringTransformedWithAllFilters() {
        String unfilteredText ="123_[TeXt]..";
        String filteredText ="text";
        UrlDataExtractor extractor = new TestExtractor();
        // Replaces all digits and punctuation signs
        extractor.addFilter(input->input.replaceAll("[0-9]?\\p{Punct}?",""));
        extractor.addFilter(String::toLowerCase);
        BinaryFunction<UrlDataExtractor, String, String> action = UrlDataExtractor::applyFilters;

        TestScenario scenario = new TestScenario();
        scenario.given("UrlDataExtractor with filters: removing digits and punctuation, lowercase.", extractor)
                .given("AND text: {@}", unfilteredText)
                .when("UrlDataExtractor's filters applied to text", action)
                .then("Resulting string must be: {@}", filteredText)
                .perform();
    }

    private static class TestExtractor extends UrlDataExtractor {
        TestExtractor() {
            super(null, null);
        }

        @Override
        public Set<Map<String, Object>> extract(String source) {
            return null;
        }
    }
}
