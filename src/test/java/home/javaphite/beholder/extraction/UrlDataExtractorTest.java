package home.javaphite.beholder.extraction;

import home.javaphite.beholder.storage.StorageService;
import home.javaphite.beholder.load.LoadService;
import home.javaphite.beholder.test.tools.scenario.BinaryFunction;
import home.javaphite.beholder.test.tools.log.TestLifecycleLogger;
import home.javaphite.beholder.test.tools.scenario.TestScenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@DisplayName("UrlDataExtractor")
class UrlDataExtractorTest extends TestLifecycleLogger {
    @Test
    void applyFilters_MustReturnStringTransformedWithAllFilters() {
        String htmlDocSample ="<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Title of the document</title>" +
                                "</head><body>Content of the document......</body></html>";

        String htmlTagsPattern="<\\\\?[^<]+>";
        String punctuationCharsPattern="[. !?,]";

        UrlDataExtractor extractor = new UrlDataExtractor(null, null) {
            @Override
           public Set<Map<String, Object>> extract(String source) {
                return null;
            }
        };
        extractor.addFilter(input->input.replaceAll(htmlTagsPattern,""));
        extractor.addFilter(input->input.replaceAll(punctuationCharsPattern,""));
        extractor.addFilter(String::toLowerCase);

        BinaryFunction<UrlDataExtractor, String, String> action = UrlDataExtractor::applyFilters;
        String expectedResult="titleofthedocumentcontentofthedocument";

        TestScenario scenario = new TestScenario();
        scenario.given("UrlDataExtractor with filters: {}, {}, {}.", extractor,
                        "HTML tags remover","punctuation remover", " to lowercase")
                .given("AND HTML page text: {@}", htmlDocSample)
                .when("UrlDataExtractor's filters applied to text", action)
                .then("Resulting string must be: {@}", expectedResult)
                .perform();
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

        UrlDataExtractor customExtractor = new UrlDataExtractor(null, null) {
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
