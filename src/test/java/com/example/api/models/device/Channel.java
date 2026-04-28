package com.example.api.models.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {

    private String name;

    // Creator?
    @JsonProperty("trigger_value")
    private Double triggerValue;
    @JsonProperty("trigger_enable")
    private Boolean triggerEnable;

    // Legacy/D10?
    @JsonProperty("max_threshold_enable")
    private Boolean maxThresholdEnable;
    @JsonProperty("max_threshold_value")
    private Double maxThresholdValue;
}
