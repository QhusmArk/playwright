package com.example.api.models.measuringpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoiseReportSettings {

    @JsonProperty("agenda_id")
    private String agendaId;

    private Map<String, Map<String, Integer>> settings;

}
