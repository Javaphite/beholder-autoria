package home.javaphite.beholder.extraction;

import home.javaphite.beholder.storage.StorageService;
import home.javaphite.beholder.load.LoadService;
import home.javaphite.beholder.test.utils.scenario.BinaryFunction;
import home.javaphite.beholder.test.utils.log.LoggedTestCase;
import home.javaphite.beholder.test.utils.scenario.TestScenario;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.UnaryOperator;

@Tag("home.javaphite.beholder.extraction.UrlDataExtractor")
class UrlDataExtractorTest extends LoggedTestCase {
    @Test
    void applyFilters_MustReturnStringTransformedWithAllFilters() {
        String htmlDocSample ="<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Title of the document</title>" +
                                "</head><body>Content of the document......</body></html>";

        List<UnaryOperator<String>> filters = new ArrayList<>();
        String htmlTagsPattern="<\\\\?[^<]+>";
        String punctuationCharsPattern="[. !?,]";
        filters.add(input->input.replaceAll(htmlTagsPattern,""));
        filters.add(input->input.replaceAll(punctuationCharsPattern,""));
        filters.add(String::toLowerCase);

        UrlDataExtractor extractor = new UrlDataExtractor(null, null, filters) {
            @Override
           public Set<Map<String, Object>> extract(String dataInDelimitedString) {
                return null;
            }
        };

        BinaryFunction<UrlDataExtractor, String, String> action = UrlDataExtractor::applyFilters;
        String expectedResult="titleofthedocumentcontentofthedocument";

        TestScenario scenario = new TestScenario();
        scenario.given("UrlDataExtractor with filters: {}, {}, {}.", extractor,
                        "HTML tags remover","punctuation remover", " to lowercase")
                .given("AND HTML page text: {@}", htmlDocSample)
                .when("UrlDataExtractor's filters applied to text", action)
                .then("Resulting string must be: {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    @Test
    void extractAndSend_BehaviorTest() {
        UrlDataExtractor extractor = getCustomExtractor();
        FakeStorageService storageService = (FakeStorageService) extractor.storageService;

        BinaryFunction<UrlDataExtractor, FakeStorageService, Map<String, Object> > action =
                (e, s) -> { e.extractAndSend(); return s.queuedData.peek(); };

        Map<String, Object> expectedResult = getDataStub();

        TestScenario scenario = new TestScenario();
        scenario.given("UrlDataExtractor: associated with some StorageService", extractor)
                .given("AND some StorageService used by scrapper", storageService)
                .when("UrlDataExtractor's extractAndSend method called AND we peek last element in <DataAccessor's> queue", action)
                .then("Returned element must be: {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    private Map<String, Object> getDataStub() {
        Map<String, Object> dataStub = new HashMap<>();
        String fieldValuesAsStrings = getFakeLoadService().getContent(null);
        String[] fieldValues = fieldValuesAsStrings.split(";");
        dataStub.put("field1", fieldValues[0]);
        dataStub.put("field2", fieldValues[1]);
        dataStub.put("field3", fieldValues[2]);

        return dataStub;
    }

    private UrlDataExtractor getCustomExtractor() {
        Map<String, Object> dataStub = getDataStub();

        UrlDataExtractor customExtractor = new UrlDataExtractor(null, null, Collections.emptyList()) {
            @Override
            public Set<Map<String, Object>> extract(String dataInString) {
                Set<Map<String, Object>> dataLines = new HashSet<>();
                dataLines.add(dataStub);
                return dataLines;
            }
        };
        customExtractor.setLoadService(getFakeLoadService());
        customExtractor.setStorageService(getFakeStorageService());

        return customExtractor;
    }

    private LoadService<String> getFakeLoadService() {
        LoadService<String> fakeLoadService = new LoadService<String>() {
            @Override
            public String getContent(String link) {
                return "Line;Red;100";
            }
        };
        return fakeLoadService;
    }

    private StorageService<Map<String, Object>> getFakeStorageService() {
        return new FakeStorageService();
    }

    private class FakeStorageService implements StorageService<Map<String, Object>> {
        Queue<Map<String, Object>> queuedData = new ArrayDeque<>();

        @Override
        public void queue(Map<String, Object> data) {
            queuedData.add(data);
        }

        @Override
        public void store() {}
    }
}
