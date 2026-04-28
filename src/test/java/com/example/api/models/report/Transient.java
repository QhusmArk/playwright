package com.example.api.models.report;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NB. The json transient in /searchId/data is not suitable for deserialising to POJO as:
 * 1. transients occur in two levels
 * 2. measuring point id is the key in the List<Map<String, Transient>>
 * This means that we need to add the json-node 'measuringPoints' inside the top level transient.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transient {

    // Map of measuring point IDs -> list of transient data
    @JsonIgnoreProperties({"timestamp", "datetime"}) // prevent infinite recursion
    private Map<String, InnerTransients> measuringPoints = new HashMap<>();

    private Long timestamp;
    private String datetime;


    // Capture all dynamic keys as measuring point IDs
    @JsonAnySetter
    public void setDynamicKey(String key, Object value) {
        if (!key.equals("timestamp") && !key.equals("datetime")) {
            ObjectMapper mapper = new ObjectMapper();
            InnerTransients parsed = mapper.convertValue(value, InnerTransients.class);
            measuringPoints.put(key, parsed);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InnerTransients {
        private List<TransientData> transients;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransientData {
        private String value;
        private String url;
        private String unit;

        @JsonProperty("transient_url")
        private String transientUrl;

        private Long timestamp;
        private Boolean overload;

        @JsonProperty("meta_id")
        private String metaId;

        private String label;
        private String frequency;
        private String datetime;
    }
}
