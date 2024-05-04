package edu.ncsu.csc.CoffeeMaker.common;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Class for handy utils shared across all of the API tests
 *
 * @author Samarth Jadia (ssjadia), Kai Presler-Marshall
 *
 */
public class TestUtils {
    private static Gson gson = new Gson();

    /**
     * Uses Google's GSON parser to serialize a Java object to JSON. Useful for
     * creating JSON representations of our objects when calling API methods.
     *
     * @param obj
     *            to serialize to JSON
     * @return JSON string associated with object
     */
    public static String asJsonString ( final Object obj ) {
        return gson.toJson( obj );
    }

    /**
     * Uses Google's GSON parser to deserialize a JSON string to a Java object.
     * Useful for building a Java model in memory for fluent parsing from JSON
     * strings when calling API methods.
     *
     * @param str
     *            JSON string to deserialize
     * @param objClass
     *            class to deserialize to
     * @return Java object associated with string
     *
     * @throws JsonSyntaxException
     */
    public static <T> T asObject ( final String str, final Class< ? extends T> objClass ) {
        return gson.fromJson( str, objClass );
    }

    /**
     * Deserializes a collection of serialized objects to a list.
     *
     * @param str
     *            JSON string to deserialize
     * @param objClass
     *            class of the contained objects
     * @return Java List containing objects, associated with the input string.
     *
     * @throws JsonSyntaxException
     */
    public static <T> List<T> asObjectList ( final String str, final Class< ? extends T> objClass )
            throws JsonSyntaxException {
        return gson.fromJson( str, TypeToken.getParameterized( List.class, objClass ).getType() );
    }

    /**
     * Deserializes a collection of serialized objects to a map.
     *
     * @param str
     *            JSON string to deserialize
     * @param keyClass
     *            class of the contained keys
     * @param valueClass
     *            class of the contained values
     * @return Java Map containing K/V pairs, associated with the input string.
     *
     * @throws JsonSyntaxException
     */
    public static <K, V> Map<K, V> asObjectMap ( final String str, final Class< ? extends K> keyClass,
            final Class< ? extends V> valueClass ) throws JsonSyntaxException {
        return gson.fromJson( str, TypeToken.getParameterized( Map.class, keyClass, valueClass ).getType() );
    }
}
