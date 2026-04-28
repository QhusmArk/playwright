package com.example.api.models.agenda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Definition {

    private String start;
    private String stop;

    @JsonProperty("repeat_value")
    private Integer repeatValue;

    @JsonProperty("period_type")
    private String periodType;

    private Integer label;

    private List<Child> child;
}
