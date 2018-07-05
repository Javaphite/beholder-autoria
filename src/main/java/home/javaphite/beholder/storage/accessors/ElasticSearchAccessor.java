package home.javaphite.beholder.storage.accessors;

import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;

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
        String documentId;
        if (data.containsKey(idFieldPattern)) {
            documentId = Objects.toString(data.get(idFieldPattern));
            data.remove(idFieldPattern);
        }
        else {
            documentId = Objects.toString(data.hashCode());
        }
        LOG.debug("Indexing/updating document {} with information: {}", documentId, data);
        UpdateRequest request = new UpdateRequest(index, documentType, documentId);
        request.upsert(data);
        try {
            client.update(request);
        }
        catch (IOException indexRequestError) {
            LOG.error("Index request error: ", indexRequestError);
            throw new UncheckedIOException(indexRequestError);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("");
        try {
            MainResponse info = client.info();
            builder.append(info.getClusterName())
                   .append('/')
                   .append(info.getNodeName())
                   .append('/')
                   .append(index);
        }
        catch (IOException clusterInfoRequestError) {
            LOG.error("Cluster information request error: ", clusterInfoRequestError);
        }
        return builder.toString();
    }

}
