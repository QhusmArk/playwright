package com.example.helpers.builders;

import com.example.api.models.ScheduledReport;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ScheduledReportBuilder implements BuilderInterface<ScheduledReport> {

    private ScheduledReport sdr;

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

    public ScheduledReportBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ScheduledReportBuilder withFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
        return this;
    }

    public ScheduledReportBuilder withFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public ScheduledReportBuilder withRecurringType(String recurringType) {
        this.recurringType = recurringType;
        return this;
    }

    public ScheduledReportBuilder withRecurringTime(String recurringTime) {
        this.recurringTime = recurringTime;
        return this;
    }

    public ScheduledReportBuilder withSendOn(String sendOn) {
        this.sendOn = sendOn;
        return this;
    }

    public ScheduledReportBuilder withReportType(List<String> reportType) {
        if (reportType == null) {
            reportType = new ArrayList<>();
        }
        this.reportType = reportType;
        return this;
    }

    public ScheduledReportBuilder addOutputOption(String outputOption) {
        if (outputOptions == null) {
            outputOptions = new ArrayList<>();
        }
        outputOptions.add(outputOption);
        return this;
    }

    public ScheduledReportBuilder withDisabled(Boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public ScheduledReportBuilder addMeasurePoint(int mpId) {
        if (measurePoints == null) {
            measurePoints = new ArrayList<>();
        }
        measurePoints.add(mpId);
        return this;
    }

    public ScheduledReportBuilder addUserRecipient(int userId) {
        if (userRecipients == null) {
            userRecipients = new ArrayList<>();
        }
        userRecipients.add(userId);
        return this;
    }

    public void build() {
        if (sdr == null) {
            sdr = new ScheduledReport();
        }
        if (name != null) sdr.setName(name);
        if (fileFormat != null) sdr.setFileFormat(fileFormat);
        if (fileType != null) sdr.setFileType(fileType);
        if (recurringType != null) sdr.setRecurringType(recurringType);
        if (recurringTime != null) sdr.setRecurringTime(recurringTime);
        if (sendOn != null) sdr.setSendOn(sendOn);
        if (reportType != null) sdr.setReportType(reportType);
        if (outputOptions != null) sdr.setOutputOptions(outputOptions);
        if (disabled != null) sdr.setDisabled(disabled);
        if (measurePoints != null) sdr.setMeasurePoints(measurePoints);
        if (userRecipients != null) sdr.setUserRecipients(userRecipients);
    }

    @Override
    public ScheduledReport getProvider() {
        return sdr;
    }

    @Override
    public void setProvider(ScheduledReport provider) {
        this.sdr = provider;
    }
    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sdr);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
