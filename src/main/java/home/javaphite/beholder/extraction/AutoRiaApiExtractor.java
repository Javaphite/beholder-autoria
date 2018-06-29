package home.javaphite.beholder.extraction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.javaphite.beholder.data.DataSchema;

import javax.xml.bind.DataBindingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
Code below uses auto.ria.com API.
For more details please visit: https://AUTO.RIA.com, https://developers.ria.com
*/

// NEEDS TEST
// NEEDS CLEANING
public class AutoRiaApiExtractor extends UrlDataExtractor {
    private String orderInfoRequestTemplate;

    public AutoRiaApiExtractor(DataSchema schema, String searchRequest, String orderInfoRequestTemplate) {
        super(schema, searchRequest);
        this.orderInfoRequestTemplate = orderInfoRequestTemplate;
    }

    @Override
    public Set<Map<String, Object>> extract(String source) {
        List<String> orderIds = new ArrayList<>();
        List<String> orders = new ArrayList<>();
        Set<Map<String, Object>> dataEntries = new LinkedHashSet<>();

        JsonNode responseTree = getJsonTree(source);
        for (JsonNode node : responseTree.findValue("ids")) {
            orderIds.add(node.textValue());
        }

        for (String id : orderIds) {
            String infoRequest = orderInfoRequestTemplate + id;
            String orderInfo = loadService.loadContent(infoRequest);
            orders.add(orderInfo);
        }

        for (String orderInfo : orders) {
            JsonNode orderJsonTree = getJsonTree(orderInfo);
            Map<String, Object> dataEntry = dataSchema.createDataBlank();

            for (String field : dataEntry.keySet()) {
                JsonNode value = orderJsonTree.findValue(field);
                dataEntry.put(field, value.asText());
            }

            if (dataSchema.isValid(dataEntry)) {
                dataEntries.add(dataEntry);
            }
            else {
                throw new RuntimeException("Data reading error!" + dataEntry);
            }
        }

        return dataEntries;
    }

    private JsonNode getJsonTree(String jsonString) {
        JsonNode jsonTree;
        ObjectMapper mapper = new ObjectMapper();

        try {
            jsonTree = mapper.readTree(jsonString);
        }
        catch (IOException jsonReadingError) {
            LOG.error("JSON reading error: ", jsonReadingError);
            throw new DataBindingException(jsonReadingError);
        }

        return jsonTree;
    }
}
