package com.example.api.models.device;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Each logger has "sms_list" in the response. For Creators the sms_list is integer, and for legacy it's an object.
 * To be able to deserialise to either, this class represents a Legacy sms_list. When deserialising for Creator the int/s is stored as List<Object>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRecipient {
    private boolean service;
    private String number;
    private boolean data;
    private String comment;

    @NoArgsConstructor
    public static class SmsListSerializer extends JsonSerializer<List<Object>> {
        @Override
        public void serialize(List<Object> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                // If the list is null, nothing is written, matching our handling of non-inclusion for null fields.
            } else {
                gen.writeStartArray();
                for (Object item : value) {
                    if (item instanceof Integer) {
                        gen.writeNumber((Integer) item);
                    } else if (item instanceof SmsRecipient) {
                        gen.writeObject(item);
                    }
                }
                gen.writeEndArray();
            }
        }
    }

    @NoArgsConstructor
    public static class SmsListDeserializer extends JsonDeserializer<List<Object>> {

        @Override
        public List<Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            List<Object> results = new ArrayList<>();
            if (node.isArray()) {
                for (JsonNode subNode : node) {
                    if (subNode.isInt()) {
                        Integer number = subNode.asInt();
                        results.add(number);
//                        System.out.println("Deserialized an integer: " + number);
                    } else if (subNode.isObject()) {
                        SmsRecipient recipient = p.getCodec().treeToValue(subNode, SmsRecipient.class);
                        results.add(recipient);
//                        System.out.println("Deserialized a SmsRecipient: " + recipient);
                    }
                }
            }
            return results;
        }
    }
}
