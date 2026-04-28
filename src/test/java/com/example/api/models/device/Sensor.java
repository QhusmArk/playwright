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
public class Sensor {

    private Integer serial;
    private String state;
    @JsonProperty("transient_time")
    private Integer transientTime;
    @JsonProperty("interval_time")
    private Integer intervalTime;
    private String type;
    private List<Channel> channels;
    @JsonProperty("standard_info")
    private StandardInfo standardInfo;
}
