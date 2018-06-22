package home.javaphite.beholder;

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
import java.util.Set;

/*
Code below uses auto.ria.com API.
For more details please visit: https://AUTO.RIA.com, https://developers.ria.com
*/

public class Tryout {
    static RestHighLevelClient client;
    static final Integer TIMEOUT_MILLIS = 120000;

    public static void main(String[] args) throws IOException {
        // Configuration
        // Could be defined with Spring
        client = getLocalhostClient();

        String apiKey = "_api_key_";
        String parameters = "category_id=1";
        String searchRequest = "https://developers.ria.com/auto/search?api_key=" + apiKey +"&" + parameters;
        String infoRequest = "https://developers.ria.com/auto/info?api_key=" + apiKey + "&auto_id=";
        LoadService<String> loadService = new LoadService<>();
        loadService.setResolver(UrlLoader::new);
        Map<String, Class<?>> fields = new LinkedHashMap<>();
        fields.put("autoId", String.class);
        fields.put("markName", String.class);
        fields.put("modelName", String.class);

        DataSchema schema = DataSchema.getSchema(fields);
        UrlDataExtractor autoriaExtractor = new AutoRiaApiExtractor(schema, searchRequest, infoRequest);
        autoriaExtractor.setLoadService(loadService);

        // One-thread logic looks like that
        String searchResponse = loadService.getContent(searchRequest);
        Set<Map<String, Object>> orders = autoriaExtractor.extract(searchResponse);

        ElasticSearchAccessor accessor = new ElasticSearchAccessor(client, "orders", "order");
        for (Map<String, Object> order: orders) {
            accessor.push(order, "autoId");
        }

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
        CreateIndexRequest newIndexRequest = new CreateIndexRequest("orders");
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
