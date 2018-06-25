package home.javaphite.beholder.storage.accessors;

import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;


// UNDER CONSTRUCTION
// NEEDS TEST
// NEEDS CLEANING
public class ElasticSearchAccessor implements Accessor<Map<String, Object>> {
    private RestHighLevelClient client;
    private String index;
    private String documentType;
    private String idFieldPattern;

    public ElasticSearchAccessor(String host, int port, String protocol, String index, String docType, String idFieldPattern) {
        RestClientBuilder builder = RestClient.builder( new HttpHost(host, port, protocol));
        this.client = new RestHighLevelClient(builder);
        this.index = index;
        this.documentType = docType;
        this.idFieldPattern = idFieldPattern;
    }

    public ElasticSearchAccessor(RestHighLevelClient client, String index, String docType, String idFieldPattern) {
        this.client = client;
        this.index = index;
        documentType = docType;
        this.idFieldPattern = idFieldPattern;
    }

    @Override
    public void push(Map<String, Object> data) {
        String documentId = Objects.toString( data.containsKey(idFieldPattern) ? data.get(idFieldPattern) : data.hashCode() );
        IndexRequest request = new IndexRequest(index, documentType, documentId);
        request.source(data);

        makeIndexRequest(request);
    }

    private boolean exists(String id) {
        boolean exists = false;
        GetRequest request = new GetRequest(index, documentType, id);
        // API owners recommended optimization: source fetching and stored fields features
        // are turned off as irrelevant for exists request
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        try {
            exists = client.exists(request);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        return exists;
    }

    private IndexResponse makeIndexRequest(IndexRequest request) {
        IndexResponse response;
        try {
            response = client.index(request);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return response;
    }

}
