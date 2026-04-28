package com.example.api.models.agenda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class C50Agenda {

    private List<Timeslot> timeslots;
    private List<Patch> patches;
    private Patch patch_current;
    private String name;
    private String id;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Timeslot {
        private List<Integer> weekdays;
        private String name;
        private Integer id;
        private Integer from;
        private Integer duration;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Patch {
        private Object vib_sensor;
        private Integer timeslot;
        private Object static_sensors;
        private SoundSensor sound_sensor;
        private Object ext_dyn_sensors;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SoundSensor {
        private List<Virtual> virtuals;
        private List<Object> virtual_channels;
        private List<Track> tracks;
        private List<Object> track;

        private Object spectrum;
        private Object record_time;
        private Object rec_downsample;
        private Object no_of_vt_chs;
        private Object no_of_tracks;
        private Object level_range;
        private Object intv_time;
        private Object calibration;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Virtual {
        private Object source;
        private VirtualSettings settings;
        private Object setting;
        private MaxThreshold max_threshold;
        private Integer id;
        private Object delete;
        private Object active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VirtualSettings {
        private String type;
        private Integer rolling_window;
        private Integer intv_time;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Track {
        private Object sum;
        private List<Subtrack> subtracks;
        private List<Object> subtrack;

        private Object statistics;
        private Object no_of_subtracks;
        private Integer id;
        private Object freq_weight;
        private Object delete;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Subtrack {
        private Object time_weight;
        private Object statistics;
        private Object min;
        private MaxThreshold max_threshold;
        private Object max;
        private Integer id;
        private Object delete;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MaxThreshold {
        private Trigger trigger_recording;
        private Trigger trigger_alert;
        private Trigger trigger_alarm;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Trigger {
        private Double value;
        private Boolean enable;
    }
}
