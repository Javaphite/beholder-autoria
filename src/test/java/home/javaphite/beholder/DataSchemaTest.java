package home.javaphite.beholder;

import home.javaphite.testing.LoggedTestCase;
import org.junit.jupiter.api.Tag;

import java.util.LinkedHashMap;
import java.util.Map;

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

    /*@Test
    void isValidDataMethodMustReturnTrueOnCompatibleData(){
        DataSchema givenSchema= schemaOf(DataPreset.STR_INT_DEC_BOOL);
        DataPreset givenDataPreset = DataPreset.STR_INT_DEC_BOOL;
        Boolean expectedResult=Boolean.TRUE;

        Object[] given=new Object[2];
        given[0]=givenSchema;
        given[1]=dataOf(givenDataPreset);

        GwtDescription description=new GwtDescription();
        description.given("DataSchema: {}", givenSchema);
        description.given("AND data: {}", givenDataPreset);
        description.when("Data tested for validity with isValidData method");
        description.then("Result must be {}", expectedResult);

        check(given,
                (givens)->((DataSchema) givens[0]).isValidData((Map<String, Object>) givens[1]),
                expectedResult, description);
    }

    @ParameterizedTest
    @EnumSource(value= DataPreset.class,
                names={"STR_CHR_DEC_BOOL", "STR_INT_DEC", "STR_NULL_DEC_BOOL"})
    void isValidDataMethodMustReturnFalseOnAnyIncompatibleData(DataPreset incompatibleData){
        DataSchema givenSchema= schemaOf(DataPreset.STR_INT_DEC_BOOL);
        DataPreset givenDataPreset = incompatibleData;
        Boolean expectedResult=Boolean.FALSE;

        Object[] given={givenSchema, dataOf(givenDataPreset)};

        GwtDescription description=new GwtDescription();
        description.given("DataSchema: {}", givenSchema);
        description.given("AND data: {}", givenDataPreset);
        description.when("Data tested for validity with isValidData method");
        description.then("Result must be {}", expectedResult);

        check(given,
                (givens)->((DataSchema) givens[0]).isValidData((Map<String, Object>) givens[1]),
                expectedResult, description);
    }

    @Test
    void hashCodesOfEqualSchemasMustBeTheSame(){
        DataSchema givenFirstSchema=schemaOf(DataPreset.STR_INT_DEC_BOOL);
        DataSchema givenSecondSchema=schemaOf(DataPreset.STR_INT_DEC_BOOL);
        Boolean expectedResult=Boolean.TRUE;

        DataSchema[] given={givenFirstSchema, givenSecondSchema};

        GwtDescription description=new GwtDescription();
        description.given("First DataSchema: {} ({})", givenFirstSchema, givenFirstSchema.hashCode());
        description.given("Second DataSchema: {} ({})", givenSecondSchema, givenSecondSchema.hashCode());
        description.when("Hashcodes of given schemas compared for equality");
        description.then("Result must be {}", expectedResult);

        check(given, (givens)->(givens[0].hashCode()==givens[1].hashCode()), expectedResult, description);

    }

    @ParameterizedTest
    @EnumSource(value= DataPreset.class,
            names={"STR_CHR_DEC_BOOL", "DIFF_STR_INT_DEC_BOOL", "STR_NULL_DEC_BOOL"})
    void hashCodesOfDifferentSchemasMustBeDifferent(DataPreset dataPreset){
        DataSchema givenFirstSchema=schemaOf(DataPreset.STR_INT_DEC_BOOL);
        DataSchema givenSecondSchema=schemaOf(dataPreset);
        Boolean expectedResult=Boolean.FALSE;

        DataSchema[] given={givenFirstSchema, givenSecondSchema};

        GwtDescription description=new GwtDescription();
        description.given("First DataSchema: {} ({})", givenFirstSchema, givenFirstSchema.hashCode());
        description.given("Second DataSchema: {} ({})", givenSecondSchema, givenSecondSchema.hashCode());
        description.when("Hashcodes of given schemas compared for equality");
        description.then("Result must be {}", expectedResult);

        check(given, (givens)->(givens[0].hashCode()==givens[1].hashCode()), expectedResult, description);

    }

    @Test
    void equalsOnEqualSchemasMustReturnTrue(){
        DataSchema givenFirstSchema=schemaOf(DataPreset.STR_INT_DEC_BOOL);
        DataSchema givenSecondSchema=schemaOf(DataPreset.STR_INT_DEC_BOOL);
        Boolean expectedResult=Boolean.TRUE;

        DataSchema[] given={givenFirstSchema, givenSecondSchema};

        GwtDescription description=new GwtDescription();
        description.given("First DataSchema: {}", givenFirstSchema);
        description.given("Second DataSchema: {}", givenSecondSchema);
        description.when("Given schemas compared for equality with equals method");
        description.then("Result must be {}", expectedResult);

        check(given, (givens)->(givens[0].equals(givens[1])), expectedResult, description);
    }

    @ParameterizedTest
    @EnumSource(value= DataPreset.class,
            names={"STR_CHR_DEC_BOOL", "DIFF_STR_INT_DEC_BOOL","STR_NULL_DEC_BOOL"})
    void equalsOnNaturallyDifferentSchemasMustReturnFalse(){
        DataSchema givenFirstSchema=schemaOf(DataPreset.STR_INT_DEC_BOOL);
        DataSchema givenSecondSchema=schemaOf(DataPreset.STR_INT_DEC_BOOL);
        Boolean expectedResult=Boolean.FALSE;

        DataSchema[] given={givenFirstSchema, givenSecondSchema};

        GwtDescription description=new GwtDescription();
        description.given("First DataSchema: {}", givenFirstSchema);
        description.given("Second DataSchema: {}", givenSecondSchema);
        description.when("Given schemas compared for equality with equals method");
        description.then("Result must be {}", expectedResult);

        check(given, (givens)->(givens[0].equals(givens[1])), expectedResult, description);
    }*/

    // Helper method: creates DataSchema stub from DataPreset
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
