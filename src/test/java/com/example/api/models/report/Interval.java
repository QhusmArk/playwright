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
 * NB. The json intervals in /searchId/data is not suitable for deserialising to POJO as:
 * 1. intervals occur in two levels
 * 2. measuring point id is the key in the List<Map<String, Interval>>
 * This means that we need to add the json-node 'measuringPoints' inside the top level interval.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Interval {

    // Map of measuring point IDs -> list of transient data
    @JsonIgnoreProperties({"timestamp", "datetime"}) // prevent infinite recursion
    private Map<String, InnerIntervals> measuringPoints = new HashMap<>();

    private Long timestamp;
    private String datetime;


    // Capture all dynamic keys as measuring point IDs
    @JsonAnySetter
    public void setDynamicKey(String key, Object value) {
        if (!key.equals("timestamp") && !key.equals("datetime")) {
            ObjectMapper mapper = new ObjectMapper();
            InnerIntervals parsed = mapper.convertValue(value, InnerIntervals.class);
            measuringPoints.put(key, parsed);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InnerIntervals {
        private List<IntervalData> intervals;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IntervalData {
        private String value;
        private Boolean regon;
        private Boolean regoff;
        private Boolean overload;
        @JsonProperty("no_data")
        private Boolean noData;
        @JsonProperty("meta_id")
        private String metaId;
        private String label;
        private Object invalid;
        @JsonProperty("timestamp_max")
        private String timestampMax;
        private String max;
    }
}
