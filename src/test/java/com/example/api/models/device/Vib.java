package com.example.api.models.device;

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
public class Vib {

    private List<Channel> channels;
    private String standard;

    @JsonProperty("post_trig_time")
    private Integer postTrigTime;
    private Integer interval;

    @JsonProperty("frequency_weighting")
    private String frequencyWeighting;
}
