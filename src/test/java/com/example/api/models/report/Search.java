package com.example.api.models.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Search {

    private String id;

    @JsonProperty("created_at")
    private Integer createdAt;

    private Integer expiry;

    @JsonProperty("finished_at")
    private Integer finishedAt;

    @JsonProperty("transient_url")
    private String transientUrl;

    @JsonProperty("datetime_to")
    private String datetimeTo;

    @JsonProperty("datetime_from")
    private String datetimeFrom;

    private String name;
    private Boolean shared;
    private String state;

    @JsonProperty("measure_points")
    private List<MeasurePoint> measurePoints;   // POST Project Measuring Point Billing report
    private List<Device> devices;               // POST Account Device Billing report

    @JsonProperty("data_types")
    private DataTypes dataTypes;
    private List<String> tags;
    private Integer aggregator;

    private String url;
    private String timezone;

    private Long timestampTo;
    private Long timestampFrom;
    private Long timestampBlastTo;
    private Long timestampBlastFrom;

    private String statsUrl;
    private String projectName;
    private String projectId;

    @JsonProperty("p_id")
    private String projectNumericId;

    private String dataUrl;

    private Integer companyId;
    private String blastStandard;
    private String analysisUrl;
    private String selfUrl;
}
