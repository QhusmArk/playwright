package com.example.api.models.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFilter("userFilter")
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class User {

    private Integer id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("mobile_phone")
    private String mobilePhone;

    private String email;
    private String language;
    private String customerCompany;
    @JsonProperty("customer_company_id")
    private Integer customerCompanyId;

    @JsonProperty("allow_support_login")
    private Boolean allowSupportLogin;

    private Boolean infraNetAccess;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("self_url")
    private String selfUrl;

    @JsonProperty("welcome_url")
    private String welcomeUrl;

    @JsonProperty("capabilities_url")
    private String capabilitiesUrl;

    @JsonProperty("role_id")
    private Integer roleId;

    @JsonProperty("user_role")
    private String userRole;

    @JsonProperty("project_id")
    private Integer[] projectIds;
}
