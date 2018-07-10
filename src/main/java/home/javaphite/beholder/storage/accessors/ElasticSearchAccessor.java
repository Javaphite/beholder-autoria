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

/**
 * Provides one-way access (write-only) to concrete index/document type in ElasticSearch (ES) instance
 * using RestHighLevelClient and ES REST API.
 */
public class ElasticSearchAccessor implements Accessor<Map<String, Object>> {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchAccessor.class);
    private RestHighLevelClient client;
    private String index;
    private String documentType;
    private String idFieldPattern;

    /**
     * Constructs ES accessor for defined existing index and document type in ES instance.
     * @param client ES REST high-level client instance to be used for communication with ES.
     * @param index name of existing index in ES instance.
     * @param docType name of existing document type (mapping) in {@code index}.
     * @param idFieldPattern string defining name of field to be used as document id (_id).
     */
    public ElasticSearchAccessor(RestHighLevelClient client, String index, String docType, String idFieldPattern) {
        this.client = client;
        this.index = index;
        documentType = docType;
        this.idFieldPattern = idFieldPattern;
    }

    /**
     * Stores data to ES index as independent document of associated type.
     * Uses {@code idFieldPattern} as document id or {@code hashcode} if no appropriate fields found.
     * @param data field-value pairs of document.
     */
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
        request.doc(data);
        request.docAsUpsert(true);
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
