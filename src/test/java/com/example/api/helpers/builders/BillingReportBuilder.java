package com.example.api.helpers.builders;

import com.example.api.models.report.BillingReportWrapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * BillingReportBuilder is used for POST /billing_report.
 * But GET /billing_report returns BillingReportWrapper,
 * so there is a mismatch between api endpoints and POJO's.
 */
public class BillingReportBuilder implements BuilderInterface<BillingReportWrapper.BillingReport> {

    private BillingReportWrapper.BillingReport billingReport;


    private String datetimeTo;

    private String datetimeFrom;

    private List<Integer> projectsIds;


    public BillingReportBuilder withDatetimeTo(final String datetimeTo) {
        this.datetimeTo = datetimeTo;
        return this;
    }

    public BillingReportBuilder withDatetimeFrom(final String datetimeFrom) {
        this.datetimeFrom = datetimeFrom;
        return this;
    }

    public BillingReportBuilder givenProjectsIds() {
        if (projectsIds == null) {
            this.projectsIds = new ArrayList<>();
        }
        return this;
    }

    public BillingReportBuilder thenProjectsId(final int projectId) {
        if (projectsIds == null) {
            throw new IllegalStateException("Method \"giveProjectsIds\" must be set first!");
        }
        projectsIds.add(projectId);
        return this;
    }

    @Override
    public BillingReportWrapper.BillingReport getProvider() {
        return billingReport;
    }

    @Override
    public void setProvider(BillingReportWrapper.BillingReport provider) {
        this.billingReport = provider;
    }

    @Override
    public void build() {
        if (billingReport == null) {
            billingReport = new BillingReportWrapper.BillingReport();
        }

        if (datetimeTo != null) {
            billingReport.setDatetimeTo(datetimeTo);
        }
        if (datetimeFrom != null) {
            billingReport.setDatetimeFrom(datetimeFrom);
        }
        if (projectsIds != null) {
            billingReport.setProjectsIds(projectsIds);
        }
    }

    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(billingReport);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
