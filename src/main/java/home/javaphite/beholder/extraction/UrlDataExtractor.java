package home.javaphite.beholder.extraction;

import home.javaphite.beholder.data.DataSchema;
import home.javaphite.beholder.load.LoadService;
import home.javaphite.beholder.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public abstract class UrlDataExtractor {
    static final Logger LOG = LoggerFactory.getLogger(UrlDataExtractor.class);
    String sourceUrl;
    private List<UnaryOperator<String>> filters = new LinkedList<>();
    DataSchema dataSchema;
    LoadService loadService;
    StorageService storageService;

    UrlDataExtractor(DataSchema dataSchema, String sourceUrl) {
        this.dataSchema = dataSchema;
        this.sourceUrl = sourceUrl;
    }

    abstract Set<Map<String, Object>> extract(String source);

    String applyFilters(String unfilteredString) {
        String resultingString = unfilteredString;
        for (UnaryOperator<String> filter: filters) {
            resultingString = filter.apply(resultingString);
        }
        return resultingString;
    }

    /**
     * Template method for getting text content of URL defined source,
     * extraction data from it and queuing those data for storage.
     *
     * @implNote relies on concrete implementation of {@code extract} method.
     */
    public void extractFromSource() {
        LOG.info("Getting content of {}", sourceUrl);
        String uploadedText = loadService.loadContent(sourceUrl);
        String filteredText = applyFilters(uploadedText);
        Set<Map<String, Object>> dataLines = extract(filteredText);
        dataLines.forEach(storageService::queue);
        LOG.info("Data of {} prepared and queued for storage", sourceUrl);
    }

    public void addFilter(UnaryOperator<String> filter) {
        filters.add(filter);
    }

    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    public void setLoadService(LoadService loadService) {
        this.loadService = loadService;
    }
}
