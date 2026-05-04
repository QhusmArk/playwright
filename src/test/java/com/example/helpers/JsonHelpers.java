package com.example.helpers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonHelpers {

    public static List<String> getArrayInNode(JsonNode nodeForThisKey) {
        List<String> arrayOfValues = new ArrayList<>();
        nodeForThisKey.forEach(node ->  arrayOfValues.add(node.asText()));
        return arrayOfValues;
    }

    public static JsonNode getNode(String json, String k) {
        JsonNode rootNode;
        try {
            rootNode = parseJson(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rootNode.get(k);
    }

    // Method to parse JSON string and return the root node
    public static JsonNode parseJson(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonString);
    }

    /**
     * @return all keys if the top layer of a json
     */
    public static List<String> getAllKeys(String jsonString) {
        List<String> keys = new ArrayList<>();
        JsonFactory factory = new JsonFactory();
        JsonParser parser = null;
        try {
            parser = factory.createParser(jsonString);
            // Ensure we only capture keys at the first node level
            while (!parser.isClosed()) {
                JsonToken jsonToken = parser.nextToken();

                // Break loop if we encounter the end of the first object
                if (JsonToken.END_OBJECT.equals(jsonToken)) {
                    break;
                }

                if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                    String fieldName = parser.getCurrentName();
                    keys.add(fieldName);

                    // Skip the value if it's a nested object or array to stay at the first level
                    JsonToken nextToken = parser.nextToken();
                    if (JsonToken.START_OBJECT.equals(nextToken) || JsonToken.START_ARRAY.equals(nextToken)) {
                        parser.skipChildren();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return keys;
    }
}
