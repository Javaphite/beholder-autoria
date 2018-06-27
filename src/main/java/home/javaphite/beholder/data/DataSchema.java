package home.javaphite.beholder.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * Class for definition of some structured data format.
 */
public final class DataSchema {
    private static final Logger LOG = LoggerFactory.getLogger(DataSchema.class);
    private final Map<String, Class<?>> fields;

    private DataSchema(Map<String, Class<?>> fields) {
        this.fields = new LinkedHashMap<>(fields);
    }

    /**
     * Static factory method for DataSchema
     * @param map describes structure of data format to be represented with
     *            new instance of DataSchema, where {@link String} keys - names of fields
     *            and {@link Class} values - types.
     *  @return instance of DataSchema.
     */
    public static DataSchema getSchema(Map<String, Class<?>> map) {
        LOG.debug("Creating DataSchema for fields: {}", map);
        DataSchema schema = new DataSchema(requireNonNull(map));
        LOG.debug("DataSchema created: {}", schema);
        return schema;
    }

    /**
     * Creates empty-valued data structure with fields defined by this DataSchema
     * @return {@link Map} which's keys are field names of this DataSchema and values are {@code null}
     */
    public Map<String, Object> createDataBlank() {
        Map<String, Object> dataBlank = new LinkedHashMap<>();
        Iterator<String> iterator = fields.keySet().iterator();
        iterator.forEachRemaining(field -> dataBlank.put(field, null));

        LOG.debug("Created new blank of schema {} - {}", this, dataBlank);
        return dataBlank;
    }

    /**
     * Checks if some portion of data suits to this DataSchema.
     * @param testedData {@link Map} that represents some portion of data. <br>
     * @return {@code true} if all of following is true: <br>
     *          1) <i>{@code testedData}</i> not {@code null}; <br>
     *          2) <i>{@code testedData}</i> consists of the same <u>number of fields</u>
     *              as this DataSchema; <br>
     *          3) <i>{@code testedData}</i> consists of the same <u>fields</u> (names and types)
     *              as this DataSchema; <br>
     **/
    public boolean isValid(Map<String, Object> testedData) {
        LOG.debug("Checking if {} valid data for schema {}", testedData, this);
        String invalidDataPrefix = "Invalid data: {}";

        if (isNull(testedData)) {
            LOG.debug(invalidDataPrefix, "data is null.");
            return false;
        }
        if (testedData.size() != fields.size()) {
            LOG.debug(invalidDataPrefix, "wrong number of fields.");
            return false;
        }

        for (String fieldName : testedData.keySet()) {
            Class<?> expectedType = fields.get(fieldName);
            Object actualValue = testedData.get(fieldName);

            if (!fields.containsKey(fieldName)) {
                LOG.debug(invalidDataPrefix, "field not exists: " + fieldName);
                return false;
            }
            if (!expectedType.isInstance(actualValue)) {
                LOG.debug(invalidDataPrefix, "wrong value type in field: " + fieldName);
                return false;
            }
        }
        LOG.debug("Data is valid!");
        return true;
    }

    @Override
    public String toString() {
        return fields.toString();
    }
}
