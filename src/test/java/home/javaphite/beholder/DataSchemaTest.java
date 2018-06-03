package home.javaphite.beholder;

import home.javaphite.testing.LoggedTestCase;
import home.javaphite.testing.TestScenario;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Tag("home.javaphite.beholder.DataSchema")
class DataSchemaTest extends LoggedTestCase {

    enum DataPreset{
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

        DataPreset(String[] fields, Object[] values){
            this.presetValues=values;
            this.presetFields=fields;
        }

        private Object[] presetValues;
        private String[] presetFields;

        @Override
        public String toString() {
            StringBuilder messageBuilder=new StringBuilder("");

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
    void createDataBlankMustReturnNullFilledMapWithSchemaDefinedFields(){
        DataSchema givenSchema=schemaOf(DataPreset.STR_INT_DEC);

        Map<String, Object> expectedReturn=new TreeMap<>();
        expectedReturn.put("name", null);
        expectedReturn.put("age", null);
        expectedReturn.put("height", null);

        TestScenario<DataSchema, Map<String, Object>> scenario = new TestScenario<>();
        scenario.given("<DataSchema> {@}", givenSchema)
                .when("<DataSchema's> createDataBlank method invoked", g->g.get(0).createDataBlank())
                .then("Returned map must be equal to {@}", expectedReturn)
                .perform();

        countAsPassed();
    }

    @Tag("isValidData")
    @ParameterizedTest
    @EnumSource(value= DataPreset.class,
                names={"STR_INT_DEC_BOOL","STR_CHR_DEC_BOOL", "STR_INT_DEC", "STR_NULL_DEC_BOOL"})
    void testingIsValidDataBehavior_OnCompatibleAndDiffKindsOfNonCompatibleData(DataPreset testedPreset){
        DataPreset samplePreset = DataPreset.STR_INT_DEC_BOOL;
        DataSchema givenSchema = schemaOf(samplePreset);
        Map<String, Object> givenData = dataOf(testedPreset);
        Boolean expectedResult = (testedPreset==samplePreset)? Boolean.TRUE: Boolean.FALSE;

        TestScenario<Object, Boolean> scenario=new TestScenario<>();
        scenario.given("DataSchema: {@}", givenSchema)
                .given("AND data: {}", givenData, testedPreset)
                .when("Data tested for validity with isValidData method",
                        g->((DataSchema) g.get(0)).isValidData((Map<String, Object>) g.get(1)) )
                .then("Result must be {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    @Tag("hashCode")
    @ParameterizedTest
    @EnumSource(value= DataPreset.class,
            names={"STR_INT_DEC_BOOL","STR_CHR_DEC_BOOL", "DIFF_STR_INT_DEC_BOOL", "STR_NULL_DEC_BOOL"})
    void testingHashCodeBehavior_OnEqualAndDiffKindsOfNonEqualSchemas(DataPreset testedPreset){
        DataPreset samplePreset=DataPreset.STR_INT_DEC_BOOL;
        DataSchema givenFirstSchema= schemaOf(samplePreset);
        DataSchema givenSecondSchema= schemaOf(testedPreset);
        Boolean expectedResult=(testedPreset==samplePreset)? Boolean.TRUE: Boolean.FALSE;

        TestScenario<DataSchema, Boolean> scenario=new TestScenario<>();
        scenario.given("First DataSchema: {@} ({})", givenFirstSchema, givenFirstSchema.hashCode())
                .given("AND second DataSchema: {@} ({})", givenSecondSchema, givenSecondSchema.hashCode())
                .when("Hashcodes of given schemas compared for equality",
                        g->g.get(0).hashCode()==g.get(1).hashCode() )
                .then("Result must be {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    @Tag("equals")
    @ParameterizedTest
    @EnumSource(value= DataPreset.class,
            names={"STR_INT_DEC_BOOL","STR_CHR_DEC_BOOL", "DIFF_STR_INT_DEC_BOOL","STR_NULL_DEC_BOOL"})
    void testingEqualsBehavior_OnEqualAndDiffKindsOfNonEqualSchemas(DataPreset testedPreset){
        DataPreset samplePreset=DataPreset.STR_INT_DEC_BOOL;
        DataSchema givenFirstSchema= schemaOf(samplePreset);
        DataSchema givenSecondSchema= schemaOf(testedPreset);
        Boolean expectedResult=(testedPreset==samplePreset)? Boolean.TRUE: Boolean.FALSE;

        TestScenario<DataSchema, Boolean> scenario=new TestScenario<>();
        scenario.given("First DataSchema: {@}", givenFirstSchema)
                .given("AND second DataSchema: {@}", givenSecondSchema)
                .when("Given schemas compared for equality with equals method",
                        g->g.get(0).equals(g.get(1)) )
                .then("Result must be {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    // Helper method: creates DataSchema from DataPreset
    private DataSchema schemaOf(DataPreset preset){
        Map<String, Class<?>> schemaDescriptor = new LinkedHashMap<>();
        String[] fields=preset.presetFields;
        Object[] values=preset.presetValues;

        for (int i=0; i<fields.length; i++){
            // There is no Class<T> instance for null values, so used Class<Integer> by default
            schemaDescriptor.put(fields[i], values[i]!=null? values[i].getClass(): Integer.class);
        }

        return DataSchema.getSchema(schemaDescriptor);
    }

    // Helper method: creates data imitation from DataPreset
    private Map<String, Object> dataOf(DataPreset preset){
        Map<String, Object> data = new LinkedHashMap<>();
        String[] fields=preset.presetFields;
        Object[] values=preset.presetValues;

        for (int i=0; i<fields.length; i++){
            data.put(fields[i], values[i]);
        }

        return data;
    }

}
