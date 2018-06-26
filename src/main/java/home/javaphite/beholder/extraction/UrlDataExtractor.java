package home.javaphite.beholder.extraction;

import home.javaphite.beholder.data.DataSchema;
import home.javaphite.beholder.storage.StorageService;
import home.javaphite.beholder.load.LoadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public abstract class UrlDataExtractor implements DataExtractor<String, Map<String, Object>> {
    static final Logger logger = LoggerFactory.getLogger(UrlDataExtractor.class);

    private DataSchema dataSchema;
    private String sourceUrl;
    private List<UnaryOperator<String>> filters;
    private LoadService<String> loadService;
    StorageService<Map<String, Object>> storageService;

    UrlDataExtractor(DataSchema dataSchema, String sourceUrl, List<UnaryOperator<String>> filters) {
        this.dataSchema = dataSchema;
        this.sourceUrl = sourceUrl;
        this.filters = new ArrayList<>(filters);
    }

    String applyFilters(String unfilteredString) {
        String resultingString = unfilteredString;
        for (UnaryOperator<String> filter:filters) {
            resultingString = filter.apply(resultingString);
        }
        return resultingString;
    }

    public void extractAndSend() {
        String uploadedText = loadService.getContent(sourceUrl);
        String filteredText = applyFilters(uploadedText);
        Set<Map<String, Object>> dataLines = extract(filteredText);

        for (Map<String, Object> line : dataLines) {
            storageService.queue(line);
        }
    }

    public void setStorageService(StorageService<Map<String, Object>> storageService) {
        this.storageService = storageService;
    }

    public void setLoadService(LoadService<String> loadService) {
        this.loadService = loadService;
    }
}
