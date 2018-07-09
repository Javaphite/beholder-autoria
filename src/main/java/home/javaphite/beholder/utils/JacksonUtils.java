package home.javaphite.beholder.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Utility class for JSON processing methods based on FasterXML/Jackson framework.
 * Made for beholder project purposes only.
 */

public final class JacksonUtils {
   private static final Logger LOG = LoggerFactory.getLogger(JacksonUtils.class);

    /**
     * Parses JSON string to JSON tree.
     * @param jsonString JSON string to be parsed
     * @return JSON tree in {@link JsonNode}
     */
   public static JsonNode getJsonTree(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonString);
        }
        catch (IOException jsonReadingError) {
            LOG.error("JSON reading error: ", jsonReadingError);
            throw new UncheckedIOException(jsonReadingError);
        }
    }
}
