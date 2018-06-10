package home.javaphite.beholder;

import home.javaphite.testing.BinaryFunction;
import home.javaphite.testing.LoggedTestCase;
import home.javaphite.testing.TestScenario;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

@Tag("home.javaphite.beholder.UrlDataScraper")
class UrlDataScraperTest extends LoggedTestCase{
    @Test
    void applyFilters_MustReturnStringTransformedWithAllFilters(){
        String textWithHtmlTags="<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Title of the document</title>" +
                                "</head><body>Content of the document......</body></html>";

        List<UnaryOperator<String>> filters = new ArrayList<>();
        String htmlTagsPattern="<\\\\?[^<]+>";
        String punctuationCharsPattern="[. !?,]";
        filters.add(input->input.replaceAll(htmlTagsPattern,""));
        filters.add(input->input.replaceAll(punctuationCharsPattern,""));
        filters.add(String::toLowerCase);

        UrlDataScraper givenScraper = new UrlDataScraper(null, null, filters) {
            @Override
           public Set<Map<String, Object>> extract(String dataInDelimitedString) {
                return null;
            }
        };

        BinaryFunction<UrlDataScraper, String, String> action = UrlDataScraper::applyFilters;
        String expectedResultingText="titleofthedocumentcontentofthedocument";

        TestScenario scenario = new TestScenario();
        scenario.given("UrlDataScraper with filters: {}, {}, {}.", givenScraper,
                        "HTML tags remover","punctuation remover", " to lowercase")
                .given("AND HTML page text: {@}", textWithHtmlTags)
                .when("UrlDataScraper's filters applied to text", action)
                .then("Resulting string must be: {@}", expectedResultingText)
                .perform();

        countAsPassed();
    }

    @Test
    void extractAndSend_BehaviorTest() {
        UrlDataScraper givenScraper = getCustomScraper();
        AccessorService<Map<String, Object>> givenAccessorService = givenScraper.accessorService;

        BinaryFunction<UrlDataScraper, AccessorService<Map<String, Object>>, Map<String, Object> > action =
                (scrapper, accessor) -> {scrapper.extractAndSend();
                                         return accessor.queuedData.peek();
                                        };

        Map<String, Object> expectedResult = getDataStub();

        TestScenario scenario = new TestScenario();
        scenario.given("UrlDataScraper: associated with some AccessorService", givenScraper)
                .given("AND some AccessorService used by scrapper", givenAccessorService)
                .when("UrlDataScraper's extractAndSend method called AND we peek last element in <DataAccessor's> queue", action)
                .then("Returned element must be: {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    private Map<String, Object> getDataStub(){
        Map<String, Object> dataStub = new HashMap<>();
        String fieldValuesAsString = getFakeLoaderService().getContent(null);
        String[] fieldValues = fieldValuesAsString.split(";");
        dataStub.put("field1", fieldValues[0]);
        dataStub.put("field2", fieldValues[1]);
        dataStub.put("field3", fieldValues[2]);

        return dataStub;
    }

    private UrlDataScraper getCustomScraper() {
        Map<String, Object> dataStub = getDataStub();

        UrlDataScraper customScrapper = new UrlDataScraper(null, null, Collections.emptyList()) {
            @Override
            public Set<Map<String, Object>> extract(String dataInString) {
                Set<Map<String, Object>> dataLines = new HashSet<>();
                dataLines.add(dataStub);
                return dataLines;
            }
        };

        customScrapper.setLoaderService(getFakeLoaderService());
        customScrapper.setAccessorService(getFakeAccessorService());

        return customScrapper;
    }

    private LoaderService<String> getFakeLoaderService() {
        LoaderService<String> fakeLoaderService = new LoaderService<String>() {
            @Override
            public String getContent(String link) {
                return "Line;Red;100";
            }
        };
        return fakeLoaderService;
    }

    private AccessorService<Map<String, Object>> getFakeAccessorService() {
        return new AccessorService<Map<String, Object>>() {
            @Override
            public void queue(Map<String, Object> data) {
                queuedData.add(data);
            }
        };
    }
}
