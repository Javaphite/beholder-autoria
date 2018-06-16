package home.javaphite.beholder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

abstract class UrlDataExtractor implements DataExtractor<String, Map<String, Object>> {
    static final Logger logger = LoggerFactory.getLogger(UrlDataExtractor.class);

    DataSchema dataSchema;
    private String sourceUrl;
    private List<UnaryOperator<String>> filters;
    LoadService<String> loadService;
    AccessorService<Map<String, Object>> accessorService;

    UrlDataExtractor(DataSchema dataSchema, String sourceUrl, List<UnaryOperator<String>> filters) {
        this.dataSchema=dataSchema;
        this.sourceUrl=sourceUrl;
        this.filters=new ArrayList<>(filters);
    }

    String applyFilters(String unfilteredString) {
        String resultingString=unfilteredString;
        for (UnaryOperator<String> filter:filters) resultingString=filter.apply(resultingString);

        return resultingString;
    }

    void extractAndSend() {
        String uploadedText = loadService.getContent(sourceUrl);
        String filteredText = applyFilters(uploadedText);
        Set<Map<String, Object>> dataLines = extract(filteredText);

        for (Map<String, Object> line : dataLines) {
            accessorService.queue(line);
        }
    }

    void setAccessorService(AccessorService<Map<String, Object>> accessorService) {
        this.accessorService = accessorService;
    }

    void setLoadService(LoadService<String> loadService) {
        this.loadService = loadService;
    }
}
