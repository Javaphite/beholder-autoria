package home.javaphite.beholder.data;

import home.javaphite.beholder.test.utils.scenario.BinaryFunction;
import home.javaphite.beholder.test.utils.log.LoggedTestCase;
import home.javaphite.beholder.test.utils.scenario.UnaryFunction;
import home.javaphite.beholder.test.utils.scenario.TestScenario;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Tag("home.javaphite.beholder.data.DataSchema")
class DataSchemaTest extends LoggedTestCase {

    enum DataPreset {
        // Base data variant
        STR_INT_DEC_BOOL(new String[]{"name", "age", "height", "online"},
                         new Object[]{"User101", 20, 1.8, Boolean.TRUE}),

        // Variant for type-based incompatibility checks
        STR_CHR_DEC_BOOL(new String[]{"name", "group", "record", "best result"},
                         new Object[]{"User101", 'A', 12.3, Boolean.FALSE}),

        // Variant for fields number based incompatibility checks
        STR_INT_DEC(new String[]{"name", "age", "height"},
                    new Object[]{"User101", 20, 1.8}),

        // Variant for null value based incompatibility checks
        STR_NULL_DEC_BOOL(new String[]{"name", "city", "rating", "online"},
                         new Object[]{"User101", null, 3.15, Boolean.FALSE}),

        // Variant for field names based incompatibility checks
        DIFF_STR_INT_DEC_BOOL(new String[]{"type", "max load", "engine capacity", "isothermal"},
                              new Object[]{"track", 10, 8.5, Boolean.TRUE});

        DataPreset(String[] fields, Object[] values) {
            this.presetValues=values;
            this.presetFields=fields;
        }

        private Object[] presetValues;
        private String[] presetFields;

        @Override
        public String toString() {
            StringBuilder messageBuilder = new StringBuilder("");

            messageBuilder.append(this.name());
            messageBuilder.append(" = { ");

            for (int i=0; i<this.presetValues.length; i++) {
                messageBuilder.append(this.presetValues[i]);
                messageBuilder.append(" ");
            }
            messageBuilder.append("}");

            return messageBuilder.toString();
        }
    }

    @Tag("createDataBlank")
    @Test
    void createDataBlank_MustReturnNullFilledMapWithSchemaDefinedFields() {
        DataSchema schema = schemaOf(DataPreset.STR_INT_DEC);
        UnaryFunction<DataSchema, Map<String, Object>> action = DataSchema::createDataBlank;
        Map<String, Object> expectedReturn = new TreeMap<>();
        expectedReturn.put("name", null);
        expectedReturn.put("age", null);
        expectedReturn.put("height", null);

        TestScenario scenario = new TestScenario();
        scenario.given("<DataSchema> {@}", schema)
                .when("<DataSchema's> createDataBlank method invoked", action)
                .then("Returned map must be equal to {@}", expectedReturn)
                .perform();

        countAsPassed();
    }

    @Tag("isValid")
    @ParameterizedTest
    @EnumSource(value = DataPreset.class,
                names = {"STR_INT_DEC_BOOL","STR_CHR_DEC_BOOL", "STR_INT_DEC", "STR_NULL_DEC_BOOL"})
    void testingIsValidDataBehavior_OnCompatibleAndDiffKindsOfNonCompatibleData(DataPreset testedPreset) {
        DataPreset samplePreset = DataPreset.STR_INT_DEC_BOOL;
        DataSchema schema = schemaOf(samplePreset);
        Map<String, Object> data = dataOf(testedPreset);
        BinaryFunction<DataSchema, Map<String, Object>, Boolean> action = DataSchema::isValid;
        Boolean expectedResult = (testedPreset==samplePreset)? Boolean.TRUE: Boolean.FALSE;

        TestScenario scenario = new TestScenario();
        scenario.given("DataSchema: {@}", schema)
                .given("AND data: {}", data, testedPreset)
                .when("Data tested for validity with isValid method", action)
                .then("Result must be {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    @Tag("hashCode")
    @ParameterizedTest
    @EnumSource(value = DataPreset.class,
            names = {"STR_INT_DEC_BOOL","STR_CHR_DEC_BOOL", "DIFF_STR_INT_DEC_BOOL", "STR_NULL_DEC_BOOL"})
    void testingHashCodeBehavior_OnEqualAndDiffKindsOfNonEqualSchemas(DataPreset testedPreset) {
        DataPreset samplePreset = DataPreset.STR_INT_DEC_BOOL;
        DataSchema firstSchema = schemaOf(samplePreset);
        DataSchema secondSchema = schemaOf(testedPreset);
        BinaryFunction<DataSchema, DataSchema, Boolean> action = (ds1, ds2) -> ds1.hashCode()==ds2.hashCode();
        Boolean expectedResult = (testedPreset==samplePreset)? Boolean.TRUE: Boolean.FALSE;

        TestScenario scenario = new TestScenario();
        scenario.given("First DataSchema: {@} ({})", firstSchema, firstSchema.hashCode())
                .given("AND second DataSchema: {@} ({})", secondSchema, secondSchema.hashCode())
                .when("Hashcodes of given schemas compared for equality", action)
                .then("Result must be {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    @Tag("equals")
    @ParameterizedTest
    @EnumSource(value = DataPreset.class,
            names = {"STR_INT_DEC_BOOL","STR_CHR_DEC_BOOL", "DIFF_STR_INT_DEC_BOOL","STR_NULL_DEC_BOOL"})
    void testingEqualsBehavior_OnEqualAndDiffKindsOfNonEqualSchemas(DataPreset testedPreset) {
        DataPreset samplePreset=DataPreset.STR_INT_DEC_BOOL;
        DataSchema firstSchema= schemaOf(samplePreset);
        DataSchema secondSchema= schemaOf(testedPreset);
        BinaryFunction<DataSchema, DataSchema, Boolean> action = DataSchema::equals;
        Boolean expectedResult=(testedPreset==samplePreset)? Boolean.TRUE: Boolean.FALSE;

        TestScenario scenario=new TestScenario();
        scenario.given("First DataSchema: {@}", firstSchema)
                .given("AND second DataSchema: {@}", secondSchema)
                .when("Given schemas compared for equality with equals method", action)
                .then("Result must be {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    // Helper method: creates DataSchema from DataPreset
    private DataSchema schemaOf(DataPreset preset) {
        Map<String, Class<?>> schemaDescriptor = new LinkedHashMap<>();
        String[] fields = preset.presetFields;
        Object[] values = preset.presetValues;

        for (int i=0; i<fields.length; i++){
            // There is no Class<T> instance for null values, so used Class<Integer> by default
            schemaDescriptor.put(fields[i], values[i]!=null? values[i].getClass(): Integer.class);
        }
        return DataSchema.getSchema(schemaDescriptor);
    }

    // Helper method: creates data imitation from DataPreset
    private Map<String, Object> dataOf(DataPreset preset) {
        Map<String, Object> data = new LinkedHashMap<>();
        String[] fields = preset.presetFields;
        Object[] values = preset.presetValues;

        for (int i=0; i<fields.length; i++){
            data.put(fields[i], values[i]);
        }
        return data;
    }

}