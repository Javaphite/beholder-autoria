package home.javaphite.beholder.demo;

import com.fasterxml.jackson.databind.JsonNode;
import home.javaphite.beholder.data.DataSchema;
import home.javaphite.beholder.extraction.AutoRiaApiExtractor;
import home.javaphite.beholder.extraction.UrlDataExtractor;
import home.javaphite.beholder.load.LoadService;
import home.javaphite.beholder.storage.StorageService;
import home.javaphite.beholder.utils.JacksonUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
Code below uses auto.ria.com API.
For more details please visit: https://AUTO.RIA.com, https://developers.ria.com
*/

public final class Demo {
    private static final Logger LOG = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        RestHighLevelClient client = context.getBean(RestHighLevelClient.class);
        String apiKey = args[0];
        String indexName = "demo";
        String docType = "advert";

        if (!indexExists(indexName, client)) {
            createNewIndex(indexName, client);
            createMapping("file:src/main/resources/mapping/advert.json", indexName, docType, client);
        }

        String[] searchRequests = new String[3];
        // Search for CARS adverts only + published for last hour
        searchRequests[0] = AutoRiaApiExtractor.prepareSearchRequest(apiKey, "category_id=1", "top=1");
        // Search for MOTORBIKES
        searchRequests[1] = AutoRiaApiExtractor.prepareSearchRequest(apiKey, "category_id=2", "top=1");
        // Search for CARGO vehicles
        searchRequests[2] = AutoRiaApiExtractor.prepareSearchRequest(apiKey, "category_id=6", "top=1");

        DataSchema advertSchema = context.getBean(DataSchema.class);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
        // Schedule adverts info extraction to run each hour
        for (int i=0; i < searchRequests.length; i++) {
            UrlDataExtractor extractor = context.getBean(AutoRiaApiExtractor.class, apiKey, advertSchema, searchRequests[i]);
            executorService.scheduleAtFixedRate(extractor::extractFromSource, 1,60, TimeUnit.MINUTES);
        }
        // Schedule all extracted data to be stored each hour
        StorageService storageService = context.getBean(StorageService.class);
        executorService.scheduleAtFixedRate(storageService::store, 3,60, TimeUnit.MINUTES);

    }

    private static boolean indexExists(String name, RestHighLevelClient client) {
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(name);
        try {
            return client.indices().exists(getIndexRequest);
        }
        catch (IOException getIndexError) {
            LOG.error("Cannot check if index {} exists: ", name, getIndexError);
            throw new UncheckedIOException(getIndexError);
        }
    }

    private static void createNewIndex(String name, RestHighLevelClient client) throws IOException {
        CreateIndexRequest newIndexRequest = new CreateIndexRequest(name);
        client.indices().create(newIndexRequest);
    }

    private static void createMapping(String mappingJsonFilepath, String index, String typeName, RestHighLevelClient client)
            throws IOException {
        LoadService loadService = new LoadService();
        String mappingJson = loadService.loadContent(mappingJsonFilepath);
        PutMappingRequest request = new PutMappingRequest(index);
        request.type(typeName);
        request.source(mappingJson, XContentType.JSON);
        client.indices().putMapping(request);
    }

    private static DataSchema getSchemaForMapping(String filepath, String idField) {
        LoadService loadService = new LoadService();
        String jsonMapping = loadService.loadContent(filepath);
        JsonNode mappingTree = JacksonUtils.getJsonTree(jsonMapping);
        JsonNode fields = mappingTree.get("properties");
        Map<String, Class<?>> fieldsMap = new HashMap<>();
        if (null != fields) {
            fields.fieldNames().forEachRemaining(field -> fieldsMap.put(field, String.class));
            fieldsMap.put(idField, String.class);
        }
        else {
            LOG.error("File {} is not valid mapping JSON file.", filepath);
            throw new IllegalArgumentException("Invalid mapping file.");
        }
       return DataSchema.getSchema(fieldsMap);
    }
}
