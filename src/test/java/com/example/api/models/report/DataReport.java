package com.example.api.models.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DataReport is an amalgam of Search and Data.
 * Search is the "ingredients" needed to create a DataReport.
 * The content of a DataReport is Data.
 * Data also contains a Search-object, called "meta".
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataReport {

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
    @JsonProperty("data_types")
    private DataTypes dataTypes;
    private Meta meta;

    private List<Transient> transients;
    private List<Interval> intervals;
    private List<Blast> blasts;
    private List<Monon> monons;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private Search raw;
        @JsonProperty("measure_points")
        private List<MeasurePoint> measurePoints;
    }
}
