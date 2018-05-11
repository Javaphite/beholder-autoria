package home.javaphite.beholder;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataSchemaTest extends TestCase{
    // Presets of data to be used in tests
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
        
    }
   
    @ParameterizedTest
    @EnumSource(value= DataPreset.class,
                names={"STR_INT_DEC_BOOL","STR_CHR_DEC_BOOL", "STR_INT_DEC", "STR_NULL_DEC_BOOL"})
    @Tag("isValidData")
     void isValidDataMethodTest(DataPreset testedPreset) {
        DataPreset schemaPreset = DataPreset.STR_INT_DEC_BOOL;
        DataSchema schema= schemaOf(schemaPreset);
        Map<String, Object> testedData=dataOf(testedPreset);
        
        // Log input test parameters with printPreset helper-method
        StringBuilder messageBuilder=new StringBuilder("INPUT:");
        messageBuilder.append(printPreset(schemaPreset, "SCHEMA"));
        messageBuilder.append(printPreset(testedPreset, "DATA"));
        logger.trace(messageBuilder.toString());
        
        // Test logic
                try {
                    boolean checkResult=schema.isValidData(testedData);
                    
                    if (schemaPreset==testedPreset) {
                        logger.trace(LogMsg.TEST_RESULT.msg, "expected isValidData return true, get - " + checkResult);
                        
                        assertTrue(checkResult, LogMsg.FAIL.msg);
                        logger.trace(LogMsg.SUCCESS.msg);
                        
                    } else {
                        logger.trace(LogMsg.TEST_RESULT.msg, "expected isValidData return false, get - " + checkResult);
                        
                        assertFalse(checkResult, LogMsg.FAIL.msg);
                        logger.trace(LogMsg.SUCCESS.msg);
                    }
                } catch (AssertionError error) {           //catch AssertionError for having chance to log this event
                    logger.error(error.getMessage());
                    throw new AssertionError(error);
                }
                
               testsPassed++;
        }
    
    @ParameterizedTest
    @EnumSource(value= DataPreset.class,
            names={"STR_INT_DEC_BOOL","STR_CHR_DEC_BOOL", "DIFF_STR_INT_DEC_BOOL", "STR_NULL_DEC_BOOL"})
    @Tag("hashCode")
     void hashCodeTest(DataPreset targetPreset) {
        DataPreset samplePreset = DataPreset.STR_INT_DEC_BOOL;
        DataSchema sampleSchema= schemaOf(samplePreset);
        DataSchema targetSchema= schemaOf(targetPreset);
    
        // Log input test parameters with printPreset helper-method
        StringBuilder messageBuilder=new StringBuilder("INPUT:");
        messageBuilder.append(printPreset(samplePreset, "Sample"));
        messageBuilder.append(" Resulted DataSchema's hashcode - ");
        messageBuilder.append(sampleSchema.hashCode());
        messageBuilder.append(printPreset(targetPreset, "Target"));
        messageBuilder.append(" Resulted DataSchema's hashcode - ");
        messageBuilder.append(targetSchema.hashCode());
        
        logger.trace(messageBuilder.toString());
        
        // Test logic
            try{
                if (samplePreset==targetPreset) {
                    assertTrue(sampleSchema.hashCode()==targetSchema.hashCode(),
                            LogMsg.FAIL +" Hashcodes of equal presets must be the same!");
                    
                    logger.trace(LogMsg.SUCCESS.msg);
                } else {
                    assertFalse(sampleSchema.hashCode()==targetSchema.hashCode(),
                            LogMsg.FAIL +" Hashcodes of non-compatible presets must be different!");
                    
                    logger.trace(LogMsg.SUCCESS.msg);
                }
    
            } catch (AssertionError error) {
                logger.error(error.getMessage());
                throw new AssertionError(error);
            }
        testsPassed++;
    }
    
    @ParameterizedTest
    @EnumSource(value= DataPreset.class,
            names={"STR_INT_DEC_BOOL","STR_CHR_DEC_BOOL", "DIFF_STR_INT_DEC_BOOL","STR_NULL_DEC_BOOL"})
    @Tag("equals")
     void equalsTest(DataPreset targetPreset) {
        DataPreset samplePreset = DataPreset.STR_INT_DEC_BOOL;
        DataSchema schema= schemaOf(samplePreset);
    
        // Log input test parameters with printPreset helper-method
        StringBuilder messageBuilder=new StringBuilder("INPUT:");
        messageBuilder.append(printPreset(samplePreset, "Sample"));
        messageBuilder.append(printPreset(targetPreset, "Target"));
        logger.trace(messageBuilder.toString());
        
        boolean isPresetsEqual=samplePreset.equals(targetPreset);
        logger.trace(LogMsg.TEST_RESULT.msg," DataSchemas based on presets above considered equal=" + isPresetsEqual);
    
        // Test logic
        try{
            if (samplePreset==targetPreset) {
                assertTrue(isPresetsEqual,
                        LogMsg.FAIL + " Method equals on equal presets must return true!");
                logger.trace(LogMsg.SUCCESS.msg);
            } else {
                assertFalse(isPresetsEqual,
                        LogMsg.FAIL  + "Method equals of non-compatible presets must return false!");
                logger.trace(LogMsg.SUCCESS.msg);
            }
        } catch (AssertionError error) {
            logger.error(error.getMessage());
            throw new AssertionError(error);
        }
        testsPassed++;
    }
    
    // Helper method: builds message about DataPreset used in tests
    private String printPreset(DataPreset preset, String tag){
        StringBuilder messageBuilder=new StringBuilder("");
        messageBuilder.append(System.lineSeparator());
        messageBuilder.append("\t\t\t\t\t");
        messageBuilder.append(tag);
        messageBuilder.append(": ");
        messageBuilder.append(preset);
        messageBuilder.append(" = { ");
        for (int i=0; i<preset.presetValues.length; i++) {
            messageBuilder.append(preset.presetValues[i]);
            messageBuilder.append(" ");
        }
        messageBuilder.append("}");
        
        return messageBuilder.toString();
    }
    
    // Helper method: creates DataSchema stub of DataPreset
    private DataSchema schemaOf(DataPreset preset){
        Map<String, Class<?>> schemaDescriptor = new LinkedHashMap<>();
        String[] fields=preset.presetFields;
        Object[] values=preset.presetValues;
        
        for (int i=0; i<fields.length; i++){
            // There is no Class<T> instance for null values,
            // so used Class<Integer> by default as far as we used
            // STR_INT_DEC_BOOL vs STR_NULL_DEC_BOOL presets
            // for null values check
            schemaDescriptor.put(fields[i], values[i]!=null? values[i].getClass(): Integer.class);
        }
        
        return DataSchema.getSchema(schemaDescriptor);
    }
    
    // Helper method: creates data imitation of DataPreset
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
