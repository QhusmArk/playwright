package com.example.api.models.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Project {

    //    @JsonProperty(required = true)
    private Integer id;             // database identifier
    //    @JsonProperty(required = true)
    private String name;
    //    @JsonProperty(required = true, value = "project_id")
    @JsonProperty(value = "project_id")
    private String projectId;       // user selected identifier
    //    @JsonProperty(required = true)
    private String description;
    //    @JsonProperty(required = true)
    private Location location;
    //    @JsonProperty(required = true)
    private Boolean active;
    //    @JsonProperty(required = true)
    private String timezone;
    @JsonInclude(JsonInclude.Include.NON_NULL)
//    @JsonProperty(required = true, value = "default_price")
    @JsonProperty(value = "default_price")
    private Integer defaultPrice;

    //    @JsonProperty(required = true, value = "datetime_from")
    @JsonProperty(value = "datetime_from")
    private String datetimeFrom;
    //    @JsonProperty(required = true, value = "datetime_to")
    @JsonProperty(value = "datetime_to")
    private String datetimeTo;
    //    @JsonProperty(required = true)
    private BigInteger timestamp_from;
    //    @JsonProperty(required = true)
    private BigInteger timestamp_to;

    @JsonProperty("maintainer_id")
    private Integer maintainerId;

    @JsonProperty("maintainer_name")
    private String maintainerName;

    @JsonProperty("customer_contact_id")
    private Integer customerContactId;

    @JsonProperty("customer_company")
    private String customerCompany;

    @JsonProperty("customer_contact_name")
    private String customerContactName;

    @JsonProperty("blast_standard")
    private String blastStandard;
}
