package com.example.api.models.device;

import com.example.api.models.agenda.C50Agenda;
import com.example.api.models.project.Project;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {

    private Integer serial;

    private String notes;

    @JsonProperty("calibration_date")
    private String calibrationDate;

    @JsonProperty("custom_name")
    private String customName;          //User selected text in physical device

    @JsonProperty("description")
    private String description;         //User selected text in client

    @JsonProperty("mon_state")
    private List<Map<String, String>> monState;

    @JsonProperty("battery_voltage")
    private List<Map<String, String>> batteryVoltage;

    @JsonProperty("battery_type_name")
    private String batteryTypeName;

    @JsonProperty("external_power")
    private Boolean externalPower;

    @JsonProperty("battery_level")
    private List<Map<String, String>> batteryLevel;

    private List<Map<String, String>> humidity;

    @JsonProperty("temperature_board")
    private List<Map<String, String>> temperatureBoard;

    private List<Map<String, String>> rssi;

    private Boolean disabled;

    @JsonProperty("config_id")
    private String configId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("last_communication")
    private String lastCommunication;           // Creator

    @JsonProperty("infra_timestamp_last_read")
    private String lastRead;                    // Legacy, device and device/all

    @JsonProperty("timestamp_last_read")
    private String timestampLastRead;           // Legacy, device and device/all

    @JsonProperty("self_url")
    private String selfUrl;

    private BigInteger created;

    private String type;

    // belongs to Change?
    private Vib vib;
    // belongs to Change?
    private String state;

    private Change change;

    private String standard;    // only C2x
    @JsonProperty("standard_text")
    private String standardText;

    @JsonProperty("frequency_weighting")
    private String frequencyWeighting;

    private List<Trigger> triggers; // only C2x

    //todo: to be deprecated?
    private Logger logger;
    private Gps gps;

    @JsonProperty("upload_schedule")
    private Integer[] uploadSchedule;

    @JsonProperty("hash")
    private String hash;

    @JsonProperty("sensors_hash")
    private String sensorsHash;

    private Status status;

    @JsonProperty("user_input_url")
    private String userInputUrl;

    private List<Sensor> sensors;

    @JsonProperty("sms_list")
    @JsonDeserialize(using = SmsRecipient.SmsListDeserializer.class)
    @JsonSerialize(using = SmsRecipient.SmsListSerializer.class)    // as we sometimes need to send an empty list to api
    private List<Object> smsRecipients; // Can contain Integer or SmsRecipient

    @JsonProperty("post_trig_time")
    private Integer postTrigTime;
    private String timezone;

    @JsonProperty("connected_projects")
    private List<Project> connectedProjects;

    @JsonProperty("sound_sensor_preset")
    private Integer preset;

    @JsonProperty("agenda")
    private C50Agenda c50Agenda;
}