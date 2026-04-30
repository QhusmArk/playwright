package com.example.helpers.builders;

import com.example.api.models.project.Location;
import com.example.api.models.project.Project;
import com.example.api.models.project.Wgs84;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ProjectBuilder implements BuilderInterface<Project> {

    private Project project;
    private String name;
    private String description;
    private String timezone;
    private String projectId;
    private String datetimeFrom;
    private String datetimeTo;
    private String blastStandard;
    private Boolean active;
    private int maintainerId;
    private String maintainerName;
    private int customerContactId;
    private String customerCompany;
    private String customerContactName;
    private Location location;
    private Wgs84 wgs84;
    private Integer defaultPrice;

    public ProjectBuilder withDefaultPrice(final Integer defaultPrice) {
        this.defaultPrice = defaultPrice;
        return this;
    }

    public ProjectBuilder givenName(final String name) {
        this.name = name;
        return this;
    }

    public ProjectBuilder thenProjectId(final String projectId) {
        this.projectId = projectId;
        return this;
    }

    public ProjectBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public ProjectBuilder withTimezone(final String timezone) {
        this.timezone = timezone;
        return this;
    }

    public ProjectBuilder withTimeFrom(final String datetimeFrom) {
        this.datetimeFrom = datetimeFrom;
        return this;
    }

    public ProjectBuilder withTimeTo(final String datetimeTo) {
        this.datetimeTo = datetimeTo;
        return this;
    }

    public ProjectBuilder withBlastStandard(final String blastStandard) {
        this.blastStandard = blastStandard;
        return this;
    }

    public ProjectBuilder withIsActive(final boolean active) {
        this.active = active;
        return this;
    }

    public ProjectBuilder withMaintainerId(final int maintainerId) {
        this.maintainerId = maintainerId;
        return this;
    }

    public ProjectBuilder withMaintainerName(final String maintainerName) {
        this.maintainerName = maintainerName;
        return this;
    }

    public ProjectBuilder withCustomerCompany(final String customerCompany) {
        this.customerCompany = customerCompany;
        return this;
    }

    public ProjectBuilder withCustomerContactName(final String customerContactName) {
        this.customerContactName = customerContactName;
        return this;
    }

    public ProjectBuilder withCustomerContactId(final int customerContactId) {
        this.customerContactId = customerContactId;
        return this;
    }

    public ProjectBuilder givenLocation(final String description) {
        this.location = Location.builder().description(description).build();
        return this;
    }

    public ProjectBuilder givenLocation() {
        this.location = Location.builder().build();
        return this;
    }

    public ProjectBuilder thenWsg84Lat(final double lat) {
        if (location == null) {
            throw new NullPointerException("Method \"givenLocation\" must be set first!");
        }
        if (wgs84 == null) {
            wgs84 = Wgs84.builder().build();
        }
        wgs84.setLat(lat);
        location.setWgs84(wgs84);
        return this;
    }

    public ProjectBuilder thenWsg84Lng(final double lng) {
        if (location == null) {
            throw new NullPointerException("Method \"givenLocation\" must be set first!");
        }
        if (wgs84 == null) {
            wgs84 = Wgs84.builder().build();
        }
        wgs84.setLng(lng);
        location.setWgs84(wgs84);
        return this;
    }

    public ProjectBuilder thenWsg84Elevation(final double elevation) {
        if (location == null) {
            throw new NullPointerException("Method \"givenLocation\" must be set first!");
        }
        if (wgs84 == null) {
            wgs84 = Wgs84.builder().build();
        }
        wgs84.setElevation(elevation);
        location.setWgs84(wgs84);
        return this;
    }

    @Override
    public Project getProvider() {
        return project;
    }

    @Override
    public void setProvider(Project provider) {
        this.project = provider;
    }

    @Override
    public void build() {
        if (project == null) {
            project = new Project();
        }
        if (projectId != null) {
            project.setProjectId(projectId);
        }
        if (name != null) {
            project.setName(name);
        }
        if (description != null) {
            project.setDescription(description);
        }
//        project.setDescription(Objects.requireNonNullElse(description, ""));

//        if (sensorType != null) {
//            project.setSensorType(sensorType);
//        }
        if (timezone != null) {
            project.setTimezone(timezone);
        }
        if (datetimeFrom != null) {
            project.setDatetimeFrom(datetimeFrom);
        }
        if (datetimeTo != null) {
            project.setDatetimeTo(datetimeTo);
        }
        if (blastStandard != null) {
            project.setBlastStandard(blastStandard);
        }
        if (active != null) {
            project.setActive(active);
        }
        if (maintainerId > 0) {
            project.setMaintainerId(maintainerId);
        }
        if (maintainerName != null) {
            project.setMaintainerName(maintainerName);
        }
        if (customerCompany != null) {
            project.setCustomerCompany(customerCompany);
        }
        if (customerContactName != null) {
            project.setCustomerContactName(customerContactName);
        }
        if (customerContactId > 0) {
            project.setCustomerContactId(customerContactId);
        }
        if (location != null) {
            project.setLocation(location);
        }
        if (defaultPrice != null) {
            project.setDefaultPrice(defaultPrice);
        }
    }

    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(project);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
