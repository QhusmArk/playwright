package com.example.playwright.steps;

import com.example.api.endpoints.BillingReportApi;
import com.example.api.endpoints.MeasuringPointApi;
import com.example.api.endpoints.SearchApi;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.api.models.measuringpoint.Sensor;
import com.example.api.models.report.BillingReportWrapper;
import com.example.api.models.report.Search;
import com.example.helpers.AssertionHelpers;
import com.example.helpers.TimeConverter;
import com.example.helpers.builders.BillingReportBuilder;
import com.example.helpers.builders.BuilderFactory;
import com.example.helpers.builders.SearchBuilder;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.TimeFrame;
import com.example.playwright.components.view.billingReports.AccountDevicesBillingReport;
import com.example.playwright.components.view.billingReports.AccountProjectsBillingReport;
import com.example.playwright.components.view.billingReports.ProjectMeasuringPointsBillingReport;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.DeviceType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BillingGlue extends BaseGlue {

    @When("I create a {string} Project Measuring Points Billing Report")
    public void createReportProject(String timeSpan) {
        billingPO.createProjectBillingReport(timeSpan);

        // Wait until report is done creating
        billingPO.waitForBillingReportCompletion();

//        System.out.println(Navigate.getCurrentUrl());
    }

    @And("I create an Account Projects Billing Report for {string}")
    public void iCreateAccountProjectBillingReportForProject(String timeSpan) {
        billingPO.selectReportPeriod("account", timeSpan);

        billingPO.selectReportType("Project");

        // Select only one project for 'Year to date', else test takes forever
        if (timeSpan.equals("Year to date")) {
            billingPO.selectProjectCheckboxes(1);
        } else {
            billingPO.selectProjectCheckboxes(3);
        }

        billingPO.clickCreateButtonAndWait();
    }

    @When("I create an Account Projects Billing Report with {string}")
    public void iCreateAnAccountBillingReportForTestAutoProject(String projectName) {
        // Give driver a few seconds to catch up with the redirect done if user have done changes before this
        PlaywrightActions.sleep(3);
        Navigate.company()
                .billingReports()
                .create()
                .get();
        billingPO.searchForProject(projectName);

        // Select top project
        billingPO.selectProjectCheckboxes(1);

        billingPO.clickCreateButtonAndWait();
    }

    @And("select {string} and open {string} calendar and select {string}")
    public void selectCustomAndOpenFromCalendarAndSelectProjectStart(String timeSpan, String dropdown, String calendarSelection) {
        billingPO.selectReportPeriod("project", timeSpan);

        billingPO.expandCalendar("From");

        billingPO.selectCalendarDate("Project start");

        // When I select project start
        // Then calendar From matches project start date
        TimeFrame timeFrame = billingPO.getTimeFrame();
        String fromValue = timeFrame.getFromDate().getValue();

        // Check if "2022-01-01" is included in "2022-01-01 00:00"
        assertTrue(context().getProject().getDatetimeFrom().contains(fromValue),
                () -> "From value did not match project start date.");
    }

    @And("I create an Account Devices Billing Report for {string}")
    public void iCreateAnPeriodAccountBillingReportForDevice(String timeSpan, DataTable table) {
        List<String> devices = table.row(0);

        billingPO.selectReportPeriod("account", timeSpan);

        billingPO.selectReportType("Device");

        devices.forEach(device -> {
            String serial = DeviceProperties.getConnectedSerial(device);
            billingPO.selectProjectCheckbox(serial);
        });

        billingPO.clickCreateButtonAndWait();

        // Wait until report is done creating
        billingPO.waitForBillingReportCompletion();
    }

    @Then("the {string} is created")
    public void theBillingReportIsCreated(String reportType) {

        switch (reportType) {
            case "Account Devices Billing Report" -> {
                AccountDevicesBillingReport report = billingPO.getAccountDeviceBillingReport();
                assertNotNull(report);
                assertTrue(report.getPanelHeader().getHeaderText().contains("Billing report"));
                assertTrue(Navigate.getCurrentUrl().contains("/company/billing_reports/devices/"),
                        "url: " + Navigate.getCurrentUrl());
            }
            case "Account Projects Billing Report" -> {
                AccountProjectsBillingReport report = billingPO.getAccountProjectsBillingReport();
                assertNotNull(report);
                assertTrue(report.getPanelHeader().getHeaderText().contains("Billing report"));
                assertTrue(Navigate.getCurrentUrl().contains("/company/billing_reports/measure_points/"),
                        "url: " + Navigate.getCurrentUrl());

            }
            case "Project Measuring Points Billing Report" -> {
                ProjectMeasuringPointsBillingReport report = billingPO.getProjectMeasuringPointsBillingReport();
                assertNotNull(report);
                assertTrue(report.getPanelHeader().getHeaderText().contains("Billing report"));
                assertTrue(Navigate.getCurrentUrl().contains("/project/"+context().getProject().getId()+"/billing_reports/"),
                        "url: " + Navigate.getCurrentUrl());

            }
            default -> throw new IllegalArgumentException("Unknown reportType: " + reportType);
        }
    }

    @When("I create an Account Projects Billing Report by api")
    public void iCreateAnAccountProjectsBillingReportByApi() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.now().minusDays(10).with(LocalTime.MIDNIGHT);
        // todo: this is not how client creates account project billing_reports. Those reports ends at 23:59
        LocalDateTime stop = LocalDateTime.now().with(LocalTime.MIDNIGHT);

        BillingReportBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.BILLING_REPORT,
                BillingReportBuilder.class);

        builder
                .withDatetimeFrom(start.format(formatter))
                .withDatetimeTo(stop.format(formatter))
                .givenProjectsIds()
                    .thenProjectsId(context().getProject().getId())
                .build();

        // Now get the new report and navigate to it.
        BillingReportWrapper billing = BillingReportApi.createBillingReport(builder.buildJson());

        Navigate.company()
                .billingReports()
                .measurePoints(billing.getId())
                .get();
    }

    @When("I use api to create an Account Projects Billing Report from {string} to {string}")
    public void iUseApiToCreateAProjectBillingReportFromTo(String from, String to) {
        BillingReportBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.BILLING_REPORT,
                BillingReportBuilder.class);

        builder
                .withDatetimeFrom(from)
                .withDatetimeTo(to)
                .givenProjectsIds()
                .thenProjectsId(context().getProject().getId())
                .build();

        // Now get the new report and navigate to it.
        BillingReportWrapper billing = BillingReportApi.createBillingReport(builder.buildJson());

        Navigate.company()
                .billingReports()
                .measurePoints(billing.getId())
                .get();
    }

    @Then("the measuring points has correctly calculated {string}")
    public void theMeasuringPointsHasCorrectlyCalculatedDaysActive(String header) {
        AccountProjectsBillingReport report = billingPO.getAccountProjectsBillingReport();

        Table.TableRow headerRow = report.getReportContent().getHeader();

        report.getReportContent().getContent().forEach(row -> {
            String actualDays = row.getStringByTableHeader(headerRow, "Days active");
            int actualDaysActive = Integer.parseInt(actualDays);

            // Use the data in the row to fetch mp data from api
            String connectedSensor = row.getStringByTableHeader(headerRow, "Serial number");

            MeasuringPoint mp = MeasuringPointApi.getMeasuringPointByConnectedSensorsSerial(context().getProject().getId(), connectedSensor);

            // Calculate the api mp real usage
            Sensor usedSensor = mp.getSensors().stream()
                    .filter(sensor -> sensor.getSerial().equals(connectedSensor))
                    .toList().getFirst();
            int expectedDaysActive = TimeConverter.calculateDaysBetween(usedSensor.getDatetimeFrom(), usedSensor.getDatetimeTo());

            assertEquals(expectedDaysActive, actualDaysActive,
                    () -> "expectedDaysActive/actualDaysActive: " + expectedDaysActive + "/" + actualDaysActive);
        });
    }

    @And("I create an Account Device Billing Report by api")
    public void iCreateAnAccountDeviceBillingReportByApi() {
        int serial = Integer.parseInt(DeviceProperties.getConnectedSerial("C22"));
        SearchBuilder builder = BuilderFactory.getBuilder(
            BuilderFactory.Providers.SEARCH,
            SearchBuilder.class);

        builder
                .withDateTimeFrom("2024-10-01 00:00")
                .withDateTimeTo("2024-11-01 00:00")
                .givenDataTypes()
                    .thenDataTypeMonon(true)
                .givenDevices()
                    .thenDevice(serial, "C22")
                .build();

        Search search = SearchApi.createSearchAndWaitForFinished(builder.buildJson(), 300);

        context().addSearch(search);
    }

    @When("I create a Project Measuring Points Billing Report by api")
    public void iCreateAProjectMeasuringPointBillingReportByApi() {
        SearchBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.SEARCH,
                SearchBuilder.class);

        builder
                .withDateTimeFrom("2024-10-01 00:00")
                .withDateTimeTo("2024-11-01 00:00")
                .givenDataTypes()
                    .thenDataTypeMonon(true)
                .givenMeasurePoint()
                    .thenMeasuringPointId(context().getMeasuringPoints().getFirst().getId())
                .build();

        Search search = SearchApi.createSearchAndWaitForFinished(context().getProject().getId(), builder.buildJson(), 300);
        context().addSearch(search);

        // Now get the finished search and navigate to it.
        Navigate.project(context().getProject().getId())
                .billingReports(search.getId())
                .get();
    }

    @Then("I can export by")
    public void availableFormats(DataTable table) {
        List<String> expected = table.row(0);
        List<String> actual = billingPO.getAvailableExportFormats();

        assertTrue(expected.containsAll(actual));
    }

    @Then("no device should have more Days Active than last month had days")
    public void noDeviceShouldHaveMoreDaysActiveThanLastMonthHadDays() {
        // get how many days there was last month
        int daysLastMonth = TimeConverter.getDaysInLastMonth();
        // Get all days from all devices
        AccountDevicesBillingReport report = billingPO.getAccountDeviceBillingReport();

        Table.TableRow headerRow = report.getReportContent().getHeader();

        // Assert that no device has been active more days than possible
        report.getReportContent().getContent().forEach(row -> {
            String actualDays = row.getStringByTableHeader(headerRow, "Days");

            int activeDays = Integer.parseInt(actualDays);
            assertTrue(activeDays <= daysLastMonth);
        });
    }

    @And("usage should be calculated correctly")
    public void usageShouldBeCalculatedCorrectly() {
        // Get how many days there was last month
        int daysLastMonth = TimeConverter.getDaysInLastMonth();

        AccountDevicesBillingReport report = billingPO.getAccountDeviceBillingReport();
        Table.TableRow headerRow = report.getReportContent().getHeader();

        report.getReportContent().getContent().forEach(row -> {
            // Get days used so that we can do our own expected percentage
            String actualDays = row.getStringByTableHeader(headerRow, "Days");
            int daysAsNumber = Integer.parseInt(actualDays);

            // Calculates expected usage percentage or returns "-" if daysAsNumber is 0
            String expectedUsage = (daysAsNumber == 0)
                    ? "-"
                    : (Math.round(((double) daysAsNumber / daysLastMonth * 100) * 100) / 100.0) + " %";

            // Get Usage on the table
            String actualUsage = row.getStringByTableHeader(headerRow, "Usage");

            assertEquals(expectedUsage, actualUsage,
                    () -> "actualDays/expectedUsage/actualUsage: " + daysAsNumber + "/" + expectedUsage + "/" + actualUsage);

        });
    }

    @Then("the mp price should be visible in the Account Projects Billing Report")
    public void mpPriceShouldBeVisibleInReport() {
        int expectedMpPrice = context().getMeasuringPoints().getFirst().getPrice();

        AccountProjectsBillingReport report = billingPO.getAccountProjectsBillingReport();
        Table.TableRow headerRow = report.getReportContent().getHeader();

        report.getReportContent().getContent().forEach(row -> {
            String actualDays = row.getStringByTableHeader(headerRow, "Price/Measuring point");
            int actualMpPrice = Integer.parseInt(actualDays);

            assertTrue(expectedMpPrice <= actualMpPrice);
        });
    }

    @When("I set price {string} on project")
    public void iSetPriceOnProject(String defaultPrice) {
        projectPO.setDefaultPrice(defaultPrice);
    }

    @When("I set price {string} on mp")
    public void iSetPriceOnMp(String defaultPrice) {
        boolean success = mpPO.setPrice(defaultPrice);
        if (!success) {
            throw new IllegalStateException("Did not manage with setting price on mp");
        }
    }

    @Then("price {string} should be visible in Account Projects Billing Report")
    public void priceShouldBeVisibleInAccountProjectsBillingReport(String expectedPrice) {
        AccountProjectsBillingReport report = billingPO.getAccountProjectsBillingReport();
        Table.TableRow headerRow = report.getReportContent().getHeader();

        report.getReportContent().getContent().forEach(row -> {
            String actualMpPrice = row.getStringByTableHeader(headerRow, "Price/Measuring point");

            assertEquals(expectedPrice, actualMpPrice,
                    () -> "expected/actual: " + expectedPrice + "/" + actualMpPrice);
        });
    }

    @Then("I can only select sensors for Account Devices Billing Report")
    public void iOnlySeeSensors() {
        billingPO.selectReportType("Device");

        List<String> devices = billingPO.getAllSelectableDevices();
        List<DeviceType> expectedMonitoringDevices = DeviceType.getMonitoringDevices();

        devices.forEach(device -> {
            DeviceType type = DeviceType.fromType(device);
            assertTrue(expectedMonitoringDevices.contains(type)
                    ,"The list of monitoring devices does not contain the expected type");
        });
    }

    @Then("Account Device Billing Report usage is {string}")
    public void deviceBillingReportUsageIs(String expectedUsage) {
        Navigate
            .company()
            .billingReports()
            .devices(context().getSearches().getFirst().getId())
            .get();

        AccountDevicesBillingReport report = billingPO.getAccountDeviceBillingReport();
        Table.TableRow headerRow = report.getReportContent().getHeader();

        report.getReportContent().getContent().forEach(row -> {
            String actualUsage = row.getStringByTableHeader(headerRow, "Usage");

            assertEquals(expectedUsage, actualUsage,
                    () -> "expected/actual: " + expectedUsage + "/" + actualUsage);
        });
    }

    @Then("Project Measuring Point Billing Report usage is {string}")
    public void projectMeasuringPointBillingReportUsageIs(String expectedUsage) {
        ProjectMeasuringPointsBillingReport report = billingPO.getProjectMeasuringPointsBillingReport();
        Table.TableRow headerRow = report.getReportContent().getHeader();

        report.getReportContent().getContent().forEach(row -> {
            String actualUsage = row.getStringByTableHeader(headerRow, "Usage");

            assertEquals(expectedUsage, actualUsage,
                    () -> "expected/actual: " + expectedUsage + "/" + actualUsage);
        });
    }

    @Then("these Project Measuring Points Billing Report headers are default")
    public void theseAccountProjectsBillingReportHeadersAreDefault(DataTable table) {
        List<String> expectedHeaders = table.row(0);

        ProjectMeasuringPointsBillingReport report = billingPO.getProjectMeasuringPointsBillingReport();

        List<String> actualHeaders = report.getReportContent().getHeader().getAllValuesAsString();

        AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedHeaders, actualHeaders);
    }
}
