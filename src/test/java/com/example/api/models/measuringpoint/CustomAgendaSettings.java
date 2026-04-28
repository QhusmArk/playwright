package com.example.api.models.measuringpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomAgendaSettings {

    @JsonProperty("agenda_id")
    private String agendaId;

    private String type;
    private Settings settings;  // Is actually a List<String, Map<String, Integer>>
}
