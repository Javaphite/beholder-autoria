package home.javaphite.beholder.extraction;

import com.fasterxml.jackson.databind.JsonNode;
import home.javaphite.beholder.data.DataSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static home.javaphite.beholder.utils.JacksonUtils.getJsonTree;

/**
 *  Extracts adverts information from <a href="https://AUTO.RIA.com">auto.ria.com</a> using its REST API. <br>
 *  For more details please visit: <a href="https://developers.ria.com">developers.ria.com</a>
 */

public class AutoRiaApiExtractor extends UrlDataExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(AutoRiaApiExtractor.class);
    private static final int ADVERTS_PER_PAGE = 100;
    private String apiKey;

    public AutoRiaApiExtractor(String apiKey, DataSchema schema, String searchRequest) {
        super(schema, searchRequest);
        this.apiKey = apiKey;
    }

    //TODO: add javaDoc comment
    public static String prepareInfoRequest(String apiKey, String id) {
        String requestTemplate = "https://developers.ria.com/auto/info?api_key={API_KEY}&auto_id={ADVERT_ID}";
        String request = requestTemplate.replace("{API_KEY}", apiKey);
        request = request.replace("{ADVERT_ID}", id);
        LOG.debug("Prepared info request: {}", request);
        return request;
    }

    //TODO: add javaDoc comment
    public static String prepareSearchRequest(String apiKey, String... params) {
        StringBuilder paramsBuilder = new StringBuilder(25);
        Arrays.stream(params).forEach(param -> paramsBuilder.append('&').append(param));
        String request = "https://developers.ria.com/auto/search?api_key={API_KEY}{PARAMETERS}";
        request = request.replace("{API_KEY}", apiKey);
        request = request.replace("{PARAMETERS}", paramsBuilder);
        LOG.debug("Prepared search request: {}", request);
        return request;
    }

    @Override
    public Set<Map<String, Object>> extract(String source) {
        LOG.info("Extracting adverts information for search request: {}", sourceUrl);
        int ignored = 0;
        List<String> ids = getIds();
        List<JsonNode> adverts = new ArrayList<>();
        ids.forEach(id -> adverts.add(getAdvertInfo(id)));

        Set<Map<String, Object>> dataEntries = new LinkedHashSet<>();
        for (JsonNode advert: adverts) {
            Map<String, Object> dataEntry = dataSchema.createDataBlank();
            for (String field : dataEntry.keySet()) {
                JsonNode value = advert.findValue(field);
                dataEntry.put(field, value.asText());
            }
            addValidIgnoreElse(dataEntries, dataEntry);
        }
        LOG.info("Adverts info extraction completed: prepared - {}, ignored - {}", dataEntries.size(), ids.size() - dataEntries.size());
        return dataEntries;
    }

    // Returns all adverts ids relevant to search request stored in extractor's sourceUrl.
    private List<String> getIds() {
        List<String> ids = new ArrayList<>();
        int pages = getLastPageIndex(ADVERTS_PER_PAGE);
        for (int i=0; i<=pages; i++) {
            ids.addAll(getIdsFromPage(i));
        }
        return ids;
    }

    // Returns adverts ids from particular page of search request.
    private List<String> getIdsFromPage(int pageIndex) {
        String pageRequest = sourceUrl + "&page=" + pageIndex + "&countpage=" + ADVERTS_PER_PAGE;
        String pageResponse = loadService.loadContent(pageRequest);
        JsonNode responseJson = getJsonTree(pageResponse);
        JsonNode jasonIdsArray = responseJson.findValue("ids");
        List<String> ids = new ArrayList<>();
        jasonIdsArray.forEach(id -> ids.add(id.textValue()));
        return ids;
    }

    // Evaluates index of last page in search request using number of results per page as parameter.
    private int getLastPageIndex(int actualAdsPerPage) {
        // Construct GET request with 0 advert ids per page just to get other info for evaluations
        String searchStatsRequest = sourceUrl + "&countpage=0";
        String searchStatsResponse = loadService.loadContent(searchStatsRequest);
        JsonNode jsonResponse = getJsonTree(searchStatsResponse);
        int totalRelevantAds = jsonResponse.findValue("count").asInt();
        return Math.floorDiv(totalRelevantAds, actualAdsPerPage);
    }

    // Retrieves advert information by id using auto.ria.com REST API
    private JsonNode getAdvertInfo(String advertId) {
        String infoRequest = prepareInfoRequest(apiKey, advertId);
        String infoResponse = loadService.loadContent(infoRequest);
        return getJsonTree(infoResponse);
    }

    // Stores data in specified set if it fits to extractor's data schema, or ignores it else.
    private void addValidIgnoreElse(Set<Map<String, Object>> dataEntries, Map<String, Object> dataEntry) {
        if (dataSchema.isValid(dataEntry)) {
            dataEntries.add(dataEntry);
        }
        else {
            LOG.warn("Data entry {} not suits to schema {} and will be IGNORED!", dataEntry, dataSchema);
        }
    }
}
