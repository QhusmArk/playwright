package com.example.api.models.measuringpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Properties {
    Integer dbcorr;

    @JsonProperty("alert_high")
    String alertHigh;

    @JsonProperty("alarm_high")
    String alarmHigh;

    @JsonProperty("table_threshold_high")
    String tableThresholdHigh;
}
