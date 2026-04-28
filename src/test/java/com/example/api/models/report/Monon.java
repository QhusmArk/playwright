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
public class Monon {

    private long timestamp;

    @JsonProperty("monon")
    private boolean isMonon;

    @JsonProperty("datetime_start")
    private String datetimeStart;

    private String datetime;

    @JsonIgnoreProperties({"timestamp", "datetime", "datetime_start", "monon"}) // prevent infinite recursion
    private Map<String, MpData> measuringPoints = new HashMap<>();

    @JsonAnySetter
    public void setDynamicKey(String key, Object value) {
        if (!key.equals("timestamp") && !key.equals("monon") && !key.equals("datetime_start") && !key.equals("datetime")) {

            ObjectMapper mapper = new ObjectMapper();
            MpData parsed = mapper.convertValue(value, MpData.class);
            measuringPoints.put(key, parsed);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MpData {
        @JsonProperty("monons")
        private List<MpValue> mpValues;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MpValue {
        private int value;      // intervals

        @JsonProperty("meta_id")
        private String metaId;

        private String label;
    }
}

