package com.example.api.models.report.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Analysis {
    @JsonProperty("device_meta")
    private DeviceMeta deviceMeta;
    private List<List<Double>> data;    // The nested list has two elements. list[0]=value_for_x_axis, list[1]=value_for_y_axis

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeviceMeta {
        @JsonProperty("y_label")
        private String yLabel;

        @JsonProperty("x_label")
        private String xLabel;

        private String unit;
        private String type;

        @JsonProperty("trig_type")
        private String trigType;

        private Standard standard;
        private long serial;

        @JsonProperty("sample_rate")
        private int sampleRate;

        @JsonProperty("max_value")
        private MaxValue maxValue;

        @JsonProperty("latest_calibration")
        private String latestCalibration;

        @JsonProperty("data_resolution")
        private int dataResolution;

        @JsonProperty("data_digits")
        private int dataDigits;

        @JsonProperty("config_id")
        private String configId;

        @JsonProperty("avail_standards")
        private List<AvailableStandard> availStandards;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Standard {
        private String name;
        private int code;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MaxValue {
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AvailableStandard {
        @JsonProperty("std_name")
        private String stdName;

        private String std;
    }
}