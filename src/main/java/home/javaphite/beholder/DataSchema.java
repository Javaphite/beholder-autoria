package home.javaphite.beholder;

import java.util.*;

/**
 * Public class for definition of some structured data format.
 *
 * @author Javaphite<javaphite@gmail.com>
 *
 *
 * @version 1.0
 * @since 10/05/2018
 */

public class DataSchema {
    // Pool of cached DataSchemas
    private static List<DataSchema> cachedSchemaPool=new ArrayList<>();
    
    private final Map<String, Class<?>> fields;
    
    private DataSchema(Map<String, Class<?>> fields){
        // Make copy of input Map to prevent possible outer references to private field
        this.fields=new LinkedHashMap<>(fields);
    }
    
    /**
     * Static factory method that returns instance of DataSchema class
     * defined with <i>{@code map}</i> parameter.
     * @param map describes structure of data format to be represented with
     *            instance of {@link DataSchema}. <br>
     *            Consists of: <br>
     *            {@link String} keys - names of data fields;<br>
     *            {@link Class} values - types of data fields.
     * @return instance of {@link DataSchema} described by <i>{@code map}</i> parameter.
     */
    public static DataSchema getSchema(Map<String, Class<?>> map){
        
        if (cachedSchemaPool.contains(Objects.requireNonNull(map))) {
            return cachedSchemaPool.get(cachedSchemaPool.indexOf(map));
        } else {
            DataSchema newSchema=new DataSchema(Objects.requireNonNull(map));
            cachedSchemaPool.add(newSchema);
            return newSchema;
        }
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
     */
    public boolean isValidData(Map<String, Object> testedData) {
        if (Objects.isNull(testedData)) return false;
        
        if (testedData.size() != fields.size()) return false;
        
        for ( String fieldName : testedData.keySet() ) {
            // Checking names of fields
            if (!fields.containsKey(fieldName)) return false;
            
            // Checking types of fields
            if (!fields.get(fieldName).isInstance(testedData.get(fieldName))) return false;
        }
        // If and only if all checks passed we got here
        return true;
    }
    
    @Override
    public int hashCode(){
        int hashcode=0;
        int hashCodeBase=113; // prime number used as base for hashcode
        
        hashcode+=Arrays.deepHashCode(fields.keySet().toArray())*hashCodeBase;
        hashcode+=Arrays.deepHashCode(fields.values().toArray());
        
        return hashcode;
    }
    
    @Override
    public boolean equals(Object dataSchema){
        if (dataSchema==null || !(dataSchema instanceof DataSchema)) return false;
        
        if (this==dataSchema) return true;
        
        boolean result;
        result=Arrays.deepEquals(this.fields.keySet().toArray(), ((DataSchema) dataSchema).fields.keySet().toArray());
        result&=Arrays.deepEquals(this.fields.values().toArray(), ((DataSchema) dataSchema).fields.values().toArray());
        
        return result;
    }
}
