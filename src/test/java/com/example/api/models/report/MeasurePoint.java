package com.example.api.models.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasurePoint {
    private Integer id;
    private String name;
    private Integer aggregator;

    @JsonProperty("sensor_type")
    private String sensorType;

    @JsonProperty("dynamic_meta")
    private Map<String, DynamicMeta> dynamicMeta;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DynamicMeta {
        @JsonProperty("is_legacy")
        private boolean isLegacy;
    }
}

