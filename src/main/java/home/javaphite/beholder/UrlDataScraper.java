package home.javaphite.beholder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

abstract class UrlDataScraper implements DataExtractor<String, Map<String, Object>> {
    private DataSchema dataSchema;
    private String sourceUrl;
    private List<UnaryOperator<String>> filters;
    LoaderService<String> loaderService;
    AccessorService<Map<String, Object>> accessorService;

    UrlDataScraper(DataSchema dataSchema, String sourceUrl, List<UnaryOperator<String>> filters){
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
        String uploadedText = loaderService.getContent(sourceUrl);
        String filteredText = applyFilters(uploadedText);
        Set<Map<String, Object>> dataLines = extract(filteredText);

        for (Map<String, Object> line : dataLines) {
            accessorService.queue(line);
        }
    }

    void setAccessorService(AccessorService<Map<String, Object>> accessorService) {
        this.accessorService = accessorService;
    }

    void setLoaderService(LoaderService<String> loaderService) {
        this.loaderService = loaderService;
    }
}
