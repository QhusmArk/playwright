package com.example.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ScheduledReport {

    private Integer id;
    private String name;
    @JsonProperty("file_format")
    private String fileFormat;
    @JsonProperty("file_type")
    private String fileType;
    @JsonProperty("recurring_type")
    private String recurringType;
    @JsonProperty("recurring_time")
    private String recurringTime;
    @JsonProperty("send_on")
    private String sendOn;
    @JsonProperty("report_type")
    private List<String> reportType;
    @JsonProperty("output_options")
    private List<String> outputOptions;
    private Boolean disabled;
    @JsonProperty("measure_points")
    private List<Integer> measurePoints;
    @JsonProperty("user_recipients")
    private List<Integer> userRecipients;

}
