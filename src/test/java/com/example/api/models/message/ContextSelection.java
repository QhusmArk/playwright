package com.example.api.models.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@Builder
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class ContextSelection {

    @JsonProperty("company")
    private Boolean companyName;
    @JsonProperty("project_name")
    private Boolean projectName;
    @JsonProperty("notification_name")
    private Boolean messageName;
    @JsonProperty("measure_point")
    private Boolean measurePointName;
    @JsonProperty("location_description")
    private Boolean measurePointDescription;
    @JsonProperty("sensor_type")
    private Boolean sensorName;
    @JsonProperty("sensor_serial")
    private Boolean sensorSerial;
    private Boolean value;
    private Boolean date;
    private Boolean time;
    private Boolean channel;
    private Boolean standard;

    public ContextSelection setAllSelectors(Boolean state) {
        this.companyName = state;
        this.projectName = state;
        this.messageName = state;
        this.measurePointName = state;
        this.measurePointDescription = state;
        this.sensorName = state;
        this.sensorSerial = state;
        this.value = state;
        this.date = state;
        this.time = state;
        this.channel = state;
        this.standard = state;

        return this;
    }

    public ContextSelection thenContextCompanyName(final Boolean companyName) {
        this.companyName = companyName;
        return this;
    }

    public ContextSelection thenContextProjectName(final Boolean projectName) {
        this.projectName = projectName;
        return this;
    }

    public ContextSelection thenContextMessageName(final Boolean messageName) {
        this.messageName = messageName;
        return this;
    }

    public ContextSelection thenContextMeasuringPointName(final Boolean measurePointName) {
        this.measurePointName = measurePointName;
        return this;
    }

    public ContextSelection thenContextMeasuringPointDescription(final Boolean measurePointDescription) {
        this.measurePointDescription = measurePointDescription;
        return this;
    }

    public ContextSelection thenContextSensorName(final Boolean sensorName) {
        this.sensorName = sensorName;
        return this;
    }

    public ContextSelection thenContextSensorSerial(final Boolean sensorSerial) {
        this.sensorSerial = sensorSerial;
        return this;
    }

    public ContextSelection thenContextValue(final Boolean value) {
        this.value = value;
        return this;
    }

    public ContextSelection thenContextDate(final Boolean date) {
        this.date = date;
        return this;
    }

    public ContextSelection thenContextTime(final Boolean time) {
        this.time = time;
        return this;
    }

    public ContextSelection thenContextChannel(final Boolean channel) {
        this.channel = channel;
        return this;
    }

    public ContextSelection thenContextStandard(final Boolean standard) {
        this.standard = standard;
        return this;
    }
}
