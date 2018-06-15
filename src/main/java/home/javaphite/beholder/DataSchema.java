package home.javaphite.beholder;

import com.google.common.base.Objects;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * Class for definition of some structured data format.
 *
 * @author Javaphite<javaphite@gmail.com>
 *
 *
 * @version 1.0
 * @since 10/05/2018
 */

class DataSchema {
    private final Map<String, Class<?>> fields;

    private DataSchema(Map<String, Class<?>> fields) {
        this.fields = new LinkedHashMap<>(fields);
    }

    /**
     * Static factory method for DataSchema
     * @param map describes structure of data format to be represented with
     *            new instance of {@link DataSchema}. <br>
     *            Consists of: <br>
     *            {@link String} keys - names of fields;<br>
     *            {@link Class} values - types of fields.
     * @return instance of {@link DataSchema} described by <i>{@code map}</i> parameter.
     */
    static DataSchema getSchema(Map<String, Class<?>> map) {
        return new DataSchema(requireNonNull(map));
    }

    /**
     * Creates empty-valued data structure with fields defined by this DataSchema
     * @return {@link Map} which's keys are field names of this DataSchema and values are {@code null}
     */
    Map<String, Object> createDataBlank() {
        Map<String, Object> dataBlank = new LinkedHashMap<>();
        Iterator<String> i = fields.keySet().iterator();
        i.forEachRemaining(field -> dataBlank.put(field, null));

        return dataBlank;
    }

    /**
     * Validation method for testing if some portion of data suits to this {@link DataSchema}.
     * @param testedData {@link Map} that represents some portion of data. <br>
     *                    Consists of: <br>
     *                    {@link String} keys - names of data fields;<br>
     *                    {@link Object} values - values associated with fields names.
     * @return {@code true} if all of following is true: <br>
     *          1) <i>{@code testedData}</i> not {@code null}; <br>
     *          2) <i>{@code testedData}</i> consists of the same <u>number of fields</u>
     *              as this {@link DataSchema}; <br>
     *          3) <i>{@code testedData}</i> consists of the same <u>fields</u> (names and types)
     *              as this {@link DataSchema} (order not taken into account); <br>
     **/
    boolean isValid(Map<String, Object> testedData) {
        if (isNull(testedData)) return false;
        if (testedData.size() != fields.size()) return false;

        for (String fieldName : testedData.keySet()) {
            Class<?> expectedType = fields.get(fieldName);
            Object actualValue = testedData.get(fieldName);

            if (!fields.containsKey(fieldName)) return false;
            if (!expectedType.isInstance(actualValue)) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return fields.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSchema schema = (DataSchema) o;
        return Objects.equal(fields, schema.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fields);
    }
}
