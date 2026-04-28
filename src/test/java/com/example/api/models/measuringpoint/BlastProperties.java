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
@JsonInclude(JsonInclude.Include.ALWAYS)    // needed to be ALWAYS to make attribute null in mp-json, if not set.
public class BlastProperties {

    private Integer alert;

    @JsonProperty("operation_factor")
    private String operationFactor;

    @JsonProperty("build_factor")
    private String buildFactor;
    private Integer alarm;

    @JsonProperty("distance_dependent")
    private Boolean distanceDependent;

    @JsonProperty("distance_unit")
    private String distanceUnit;

    @JsonProperty("uncorrected_frequency_names")
    private String uncorrectedFrequencyNames;

    @JsonProperty("max_values")
    private String maxValues;

    @JsonProperty("distance_unit_names")
    private String distanceUnitNames;

    @JsonProperty("material_factor")
    private String materialFactor;

    @JsonProperty("static_distance")
    private Integer staticDistance;

    @JsonProperty("guide_value")
    private Double guideValue;

    @JsonProperty("uncorrected_frequency")
    private Integer uncorrectedFrequency;
}
