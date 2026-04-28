package com.example.api.models.report;

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
public class BillingReportWrapper {

    @JsonProperty("self_url")
    private String selfUrl;

    private String id;
    private Long expiry;

    @JsonProperty("datetime_to")
    private String datetimeTo;

    @JsonProperty("datetime_from")
    private String datetimeFrom;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("billing_report")
    private List<BillingReport> billingReport;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BillingReport {
        private String timezone;

        @JsonProperty("timestamp_to")
        private Long timestampTo;

        @JsonProperty("timestamp_from")
        private Long timestampFrom;

        @JsonProperty("project_id")
        private String projectId;           // only for deserialising from json when GET /billing_report
        @JsonProperty("project_ids")
        private List<Integer> projectsIds;  // only for serialising to json when POST /billing_report

        private String name;

        @JsonProperty("maintainer_name")
        private String maintainerName;

        @JsonProperty("maintainer_id")
        private Integer maintainerId;

        private Integer id;

        @JsonProperty("default_price")
        private Double defaultPrice;

        @JsonProperty("datetime_to")
        private String datetimeTo;

        @JsonProperty("datetime_from")
        private String datetimeFrom;

        @JsonProperty("customer_contact_last_name")
        private String customerContactLastName;

        @JsonProperty("customer_contact_id")
        private Integer customerContactId;

        @JsonProperty("customer_contact_first_name")
        private String customerContactFirstName;

        @JsonProperty("customer_company")
        private String customerCompany;

        private List<MeasuringPoint> mps;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeasuringPoint {

        @JsonProperty("timestamp_to")
        private Long timestampTo;

        @JsonProperty("timestamp_from")
        private Long timestampFrom;

        private List<Sensor> sensors;

        @JsonProperty("sensor_type")
        private String sensorType;

        private Double price;
        private String name;

        @JsonProperty("mp_days")
        private Integer mpDays;

        private Integer id;
        private String description;

        @JsonProperty("datetime_to")
        private String datetimeTo;

        @JsonProperty("datetime_from")
        private String datetimeFrom;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sensor {

        @JsonProperty("timestamp_to")
        private Long timestampTo;

        @JsonProperty("timestamp_from")
        private Long timestampFrom;

        @JsonProperty("sensor_serial")
        private Integer sensorSerial;

        private Integer days;

        @JsonProperty("datetime_to")
        private String datetimeTo;

        @JsonProperty("datetime_from")
        private String datetimeFrom;
    }
}
