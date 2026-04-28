package com.example.api.models.measuringpoint;

import com.fasterxml.jackson.annotation.JsonFilter;
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
@JsonFilter("mpFilter")
public class MeasuringPoint {

    private Integer id;

    private String name;
    private Integer order;

    @JsonProperty("sensor_type")
    private String sensorType;

    @JsonProperty("datetime_from")
    private String datetimeFrom;

    @JsonProperty("datetime_to")
    private String datetimeTo;

    private Boolean active;
    private String timezone;

    @JsonProperty("blast_standard")
    private String blastStandard;

    private Location location;
    private List<Sensor> sensors;

    @JsonProperty("custom_agenda_settings")
    private CustomAgendaSettings customAgendaSettings;

    @JsonProperty("noise_report_settings")
    private NoiseReportSettings noiseReportSettings;

    @JsonProperty("vibration_report_settings")
    private VibrationReportSettings vibrationReportSettings;

    @JsonProperty("graph_settings")
    private GraphSettings graphSettings;

    @JsonProperty("blast_properties")
    private BlastProperties blastProperties;

    @JsonProperty("self_url")
    private String selfUrl;

    private Integer price;

    private Properties properties;
}
