package com.example.api.models.report;

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
public class DataTypes {

    private Boolean interval;
    private Boolean blast;
    private Boolean monon;
    private Boolean sio;

    @JsonProperty("transient")
    private Boolean transientData;

}
