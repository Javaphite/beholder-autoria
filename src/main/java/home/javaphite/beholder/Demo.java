package home.javaphite.beholder;

import home.javaphite.beholder.data.DataSchema;
import home.javaphite.beholder.extraction.AutoRiaApiExtractor;
import home.javaphite.beholder.extraction.UrlDataExtractor;
import home.javaphite.beholder.load.LoadService;
import home.javaphite.beholder.load.loaders.UrlLoader;
import home.javaphite.beholder.storage.ConcurrentStorageService;
import home.javaphite.beholder.storage.accessors.ElasticSearchAccessor;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/*
Code below uses auto.ria.com API.
For more details please visit: https://AUTO.RIA.com, https://developers.ria.com
*/

public class Demo {
    static RestHighLevelClient client;
    static final Integer TIMEOUT_MILLIS = 120000;

    public static void main(String[] args) throws IOException {
        // Configuration
        // Could be defined with Spring
        client = getLocalhostClient();

        String apiKey = "PsOocosKlJkraQej9xHoQ9xlJBtSaP5Z0Kjxb7x4";
        String parameters = "category_id=1&page=3";
        String searchRequest = "https://developers.ria.com/auto/search?api_key=" + apiKey + "&" + parameters;
        String infoRequest = "https://developers.ria.com/auto/info?api_key=" + apiKey + "&auto_id=";
        LoadService<String> loadService = new LoadService<>();
        loadService.setResolver(UrlLoader::new);
        ConcurrentStorageService<Map<String, Object>> storageService = new ConcurrentStorageService<>();
        storageService.setStorageAccessor(new ElasticSearchAccessor(client, "orders", "order", "autoId"));
        Map<String, Class<?>> fields = new LinkedHashMap<>();
        fields.put("autoId", String.class);
        fields.put("markName", String.class);
        fields.put("modelName", String.class);

        DataSchema schema = DataSchema.getSchema(fields);
        UrlDataExtractor autoriaExtractor = new AutoRiaApiExtractor(schema, searchRequest, infoRequest);
        autoriaExtractor.setLoadService(loadService);
        autoriaExtractor.setStorageService(storageService);

        // One-thread logic looks like that
        autoriaExtractor.extractAndSend();
        storageService.store();

        client.close();
    }

    // Test-helping methods
    private static File createTestFile(String text) {
        File newTempFile = null;

        try {
            newTempFile = File.createTempFile("autoria_", ".tmp");
            BufferedWriter writer = new BufferedWriter(new FileWriter(newTempFile));
            writer.append(text);
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            System.out.print(e);
        }

        return newTempFile;
    }

    private static void createNewIndex(String name) throws IOException {
        IndicesClient admin = client.indices();
        CreateIndexRequest newIndexRequest = new CreateIndexRequest(name);
        newIndexRequest.timeout(TIMEOUT_MILLIS + "ms");
        admin.create(newIndexRequest);
    }

    private static RestHighLevelClient getLocalhostClient() {
        RestClientBuilder builder = RestClient.builder( new HttpHost("localhost", 9200, "http"));
        builder.setRequestConfigCallback(configBuilder -> configBuilder.setConnectTimeout(TIMEOUT_MILLIS).setSocketTimeout(TIMEOUT_MILLIS));
        builder.setMaxRetryTimeoutMillis(TIMEOUT_MILLIS);

        return new RestHighLevelClient(builder);
    }

    private static void createMapping(String json, String index, String typeName) throws IOException {
        IndicesClient admin = client.indices();
        PutMappingRequest request = new PutMappingRequest(index);
        request.type(typeName);
        request.source(json, XContentType.JSON);
        admin.putMapping(request);
    }
}
