package com.example.api.models.measuringpoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class GraphSettings {

    @JsonProperty("graph_type")
    private String graphType;

    @JsonProperty("y_min")
    private Integer yMin;

    @JsonProperty("y_max")
    private Integer yMax;

    @JsonProperty("play_sound")
    private Boolean playSound;

    @JsonProperty("show_transient_ppvzx")
    private Boolean showTransientPpvzx;

    @JsonProperty("layout_mode")
    private String layoutMode;

    @JsonProperty("normalized_scaling")
    private Boolean normalizedScaling;
}
