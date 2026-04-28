package com.example.api.models.measuringpoint;

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
public class VibrationReportSettings {

    @JsonProperty("agenda_id")
    private String agendaId;

    private Map<String, Map<String, Integer>> settings;
}
