package home.javaphite.beholder;

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

@Tag("home.javaphite.beholder.UrlDataScrapper")
class UrlDataScrapperTest extends LoggedTestCase{
    @Test
    void applyFilters_MustReturnStringTransformedWithAllFilters(){
        String textWithHtmlTags="<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Title of the document</title>" +
                                "</head><body>Content of the document......</body></html>";
        String expectedResultingText="titleofthedocumentcontentofthedocument";

        List<UnaryOperator<String>> filters=new ArrayList<>();
        String htmlTagsPattern="<\\\\?[^<]+>";
        String punctuationCharsPattern="[. !?,]";
        filters.add(input->input.replaceAll(htmlTagsPattern,""));
        filters.add(input->input.replaceAll(punctuationCharsPattern,""));
        filters.add(String::toLowerCase);

        UrlDataScrapper scrapperStub=new UrlDataScrapper(null, null, filters) {
            @Override
           public Set<Map<String, Object>> extract(String dataInDelimitedString) {
                return null;
            }
        };

        TestScenario<Object, String> scenario=new TestScenario<>();
        scenario.given("<UrlDataScrapper> with filters: {}, {}, {}.", scrapperStub,
                        "HTML tag remover","simple punctuation remover", "lowercase converter")
                .given("AND HTML page <text>: {@}", textWithHtmlTags)
                .when("<UrlDataScrapper's> filters applied to <text>",
                        g->((UrlDataScrapper) g[0]).applyFilters( (String) g[1]) )
                .then("Resulting string should be: {@}", expectedResultingText)
                .perform();

        countAsPassed();
    }

    @Test
    void extractAndSend_BehaviorTest() {
        Map<String, Object> expectedResult = getDataStub();
        UrlDataScrapper givenScrapper = getCustomScrapper();
        AccessorService<Map<String, Object>> accessorService = givenScrapper.accessorService;

        TestScenario<UrlDataScrapper, Map<String, Object>> scenario = new TestScenario<>();
        scenario.given("<UrlDataScrapper>: associated with <AccessorService>", givenScrapper)
                .when("<UrlDataScrapper's> extractAndSend method called AND we peek last element in <DataAccessor's> queue",
                        g->{ g[0].extractAndSend();
                            return g[0].accessorService.queuedData.peek();})
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

    private UrlDataScrapper getCustomScrapper() {
        Map<String, Object> dataStub = getDataStub();

        UrlDataScrapper customScrapper = new UrlDataScrapper(null, null, Collections.emptyList()) {
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
            String getContent(String sourceAddress) {
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
