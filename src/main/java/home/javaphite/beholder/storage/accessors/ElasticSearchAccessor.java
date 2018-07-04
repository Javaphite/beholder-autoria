package home.javaphite.beholder.storage.accessors;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;

//TODO: implement toString!
// UNDER CONSTRUCTION
// NEEDS TEST
// NEEDS CLEANING
public class ElasticSearchAccessor implements Accessor<Map<String, Object>> {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchAccessor.class);
    private RestHighLevelClient client;
    private String index;
    private String documentType;
    private String idFieldPattern;

    public ElasticSearchAccessor(RestHighLevelClient client, String index, String docType, String idFieldPattern) {
        this.client = client;
        this.index = index;
        documentType = docType;
        this.idFieldPattern = idFieldPattern;
    }

    @Override
    public void push(Map<String, Object> data) {
        String documentId = Objects.toString(data.containsKey(idFieldPattern) ? data.get(idFieldPattern) : data.hashCode());
        IndexRequest request = new IndexRequest(index, documentType, documentId);
        request.source(data);
        indexRequest(request);
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

    private void indexRequest(IndexRequest request) {
        try {
            client.index(request);
        }
        catch (IOException indexRequestError) {
            LOG.error("Index request error: ", indexRequestError);
            throw new UncheckedIOException(indexRequestError);
        }
    }

}
