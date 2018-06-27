package home.javaphite.beholder.data;

import home.javaphite.beholder.test.tools.scenario.BinaryFunction;
import home.javaphite.beholder.test.tools.log.TestLifecycleLogger;
import home.javaphite.beholder.test.tools.scenario.UnaryFunction;
import home.javaphite.beholder.test.tools.scenario.TestScenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@DisplayName("DataSchema")
class DataSchemaTest extends TestLifecycleLogger {
    @Test
    @Tag("createDataBlank")
    void returnNullFilledMapWithSchemaDefinedFields() {
        DataPreset preset = DataPreset.USER_SHORT;
        DataSchema schema = schemaOf(preset);
        UnaryFunction<DataSchema, Map<String, Object>> action = DataSchema::createDataBlank;
        Map<String, Object> expectedReturn = new TreeMap<>();
        for (String s: preset.presetFields) {
            expectedReturn.put(s, null);
        }
        TestScenario scenario = new TestScenario();
        scenario.given("<DataSchema> {@}", schema)
                .when("<DataSchema's> createDataBlank method invoked", action)
                .then("Returned map must be equal to {@}", expectedReturn)
                .perform();
    }

    @ParameterizedTest
    @EnumSource(value = DataPreset.class,
                names = {"USER","USER_RECORD", "USER_SHORT", "USER_UNCOMPLETED"})
    @Tag("isValid")
    void returnTrueOnCompatibleDataElseFalse(DataPreset testedPreset) {
        DataSchema schema = schemaOf(DataPreset.USER);
        Map<String, Object> data = dataOf(testedPreset);
        BinaryFunction<DataSchema, Map<String, Object>, Boolean> action = DataSchema::isValid;
        Boolean expectedResult = (DataPreset.USER == testedPreset)? Boolean.TRUE: Boolean.FALSE;

        TestScenario scenario = new TestScenario();
        scenario.given("DataSchema: {@}", schema)
                .given("AND data: {@}", data)
                .when("Data tested for validity", action)
                .then("Result must be {@}", expectedResult)
                .perform();
    }

    private enum DataPreset {
        // Base variant
        USER(new String[]{"name", "age", "version", "online"},
                new Object[]{"User101", 20, 1.8, Boolean.TRUE}),

        // For type incompatibility checks
        USER_RECORD(new String[]{"name", "group", "record", "best result"},
                new Object[]{"User101", 'A', 12.3, Boolean.FALSE}),

        // For fields number incompatibility checks
        USER_SHORT(new String[]{"name", "age", "version"},
                new Object[]{"User101", 20, 1.8}),

        // For null value incompatibility checks
        USER_UNCOMPLETED(new String[]{"name", "age", "rating", "online"},
                new Object[]{"User101", null, 3.15, Boolean.FALSE});

        private final Object[] presetValues;
        private final String[] presetFields;

        DataPreset(String[] fields, Object[] values) {
            this.presetValues = values;
            this.presetFields = fields;
        }

        @Override
        public String toString() {
            StringBuilder messageBuilder = new StringBuilder(60);
            messageBuilder.append(name());
            messageBuilder.append(" = { ");
            for (Object value : presetValues) {
                messageBuilder.append(value);
                messageBuilder.append(' ');
            }
            messageBuilder.append('}');

            return messageBuilder.toString();
        }
    }

    // Creates DataSchema for DataPreset
    private DataSchema schemaOf(DataPreset preset) {
        Map<String, Class<?>> schemaDescriptor = new LinkedHashMap<>();
        String[] fields = preset.presetFields;
        Object[] values = preset.presetValues;

        for (int i=0; i<fields.length; i++) {
            // Class<Integer> is used by default for null values
            schemaDescriptor.put(fields[i], (null != values[i])? values[i].getClass(): Integer.class);
        }
        return DataSchema.getSchema(schemaDescriptor);
    }

    // Creates data from DataPreset
    private Map<String, Object> dataOf(DataPreset preset) {
        Map<String, Object> data = new LinkedHashMap<>();
        String[] fields = preset.presetFields;
        Object[] values = preset.presetValues;

        for (int i=0; i<fields.length; i++) {
            data.put(fields[i], values[i]);
        }
        return data;
    }
}
