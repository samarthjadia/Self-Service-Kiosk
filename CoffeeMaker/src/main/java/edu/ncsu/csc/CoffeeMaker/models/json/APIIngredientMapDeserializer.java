package edu.ncsu.csc.CoffeeMaker.models.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class APIIngredientMapDeserializer extends JsonDeserializer<Map<String, Integer>> {
    @Override
    public Map<String, Integer> deserialize ( final JsonParser p, final DeserializationContext ctxt )
            throws IOException, JsonProcessingException {
        final Map<String, Integer> m = new HashMap<String, Integer>();
        final JsonNode root = p.getCodec().readTree( p );
        root.fields().forEachRemaining( e -> m.put( e.getKey(), e.getValue().asInt() ) );
        return m;
    }
}
