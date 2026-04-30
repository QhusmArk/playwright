package com.example.playwright.steps.testdata;

import com.example.helpers.testData.Context;
import com.example.helpers.testData.TestDataBuilder;
import com.example.playwright.steps.BaseGlue;
import io.cucumber.java.en.Given;

import java.util.stream.IntStream;

public class TestDataCommon extends BaseGlue {
    Context context = context();

    public TestDataCommon() {}

    @Given("there is a project")
    public void createProject() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .build();
    }

    @Given("there is a project without blast standard")
    public void createProjectWithoutBlastStandard() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .build();
    }

    @Given("there is a project with an Agenda")
    public void thereIsAProjectWithAnAgenda() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("agenda-1")
                .build();
    }

    @Given("there is a project with an empty agenda")
    public void thereIsAProjectWithAnEmptyAgenda() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("agenda-2")
                .build();
    }

    @Given("there is a project with a comment")
    public void thereIsAProjectWithAComment() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("comment-1")
                .build();
    }

    @Given("there is a Project with an MP that has an Agenda")
    public void thereIsAProjectWithAnMPThatHasAnAgenda() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("agenda-1")
                .addTestData("measuringpoint-S50-1")
                .build();
    }

    @Given("There is a project that has one MP with Agenda and one MP with no Agenda")
    public void thereIsAProjectThatHasOneMPWithAgendaAndOneMPWithNoAgenda() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("agenda-1")
                .addTestData("measuringpoint-S50-1")
                .addTestData("measuringpoint-S50-2")
                .build();
    }

    @Given("There is a Project with an MP that has no Agenda")
    public void thereIsAProjectWithAnMPThatHasNoAgenda() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("agenda-1")
                .addTestData("measuringpoint-S50-3")
                .build();
    }

    @Given("a vibration report project")
    public void aVibrationReportProject() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("agenda-1")
                .addTestData("measuringpoint-vibReport-C22-1")
                .addTestData("measuringpoint-C22-2")
                .addTestData("search-vibration_report")
                .build();
    }

    @Given("a Project with an MP that has no Agenda")
    public void aProjectWithMpWithoutAgenda() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("agenda-1")
                .addTestData("measuringpoint-C22-1")
                .build();
    }

    @Given("there is a project with two measuring points and two blasts")
    public void twoMpsAndTwoBlasts() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("measuringpoint-C22-2")
                .addTestData("blast-two")
                .addTestData("blast-one")
                .build();
    }

    @Given("there is a project with a S50")
    public void there_is_a_project_with_a_S50() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-S50-2")
                .build();
    }

    @Given("there is a project with Blast Standard, blasts and saved interval report")
    @Given("there is a project with an interval report")
    public void there_is_a_previously_created_Datareport_project() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("measuringpoint-C22-2")
                .addTestData("blast-two")
                .addTestData("blast-one")
                .addTestData("search-1")
                .build();
    }

    @Given("There is previously created data reports in state temporary, saved and shared")
    public void createDataReportProject() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("search-1")    // saved + shared
                .addTestData("search-2")    // saved
                .addTestData("search-I")    // temp
                .build();
    }

    @Given("There is a project called aaa-{string}")
    public void thereIsAProjectCalledAaaTestCase(String testCase) {
        System.out.println("testcase: " + testCase);
        TestDataBuilder builder = new TestDataBuilder(context, "templates/status/" + testCase);
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .build();
    }

    @Given("there is a maxed out project")
    public void thereIsAMaxedOutProject() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("measuringpoint-C22-2")
                .addTestData("measuringpoint-S50-3")    //replacement for changed measuringpoint-S50-1
                .addTestData("measuringpoint-S50-2")
                .addTestData("blast-one")
                .addTestData("blast-two")
                .addTestData("blast-planned")
                .addTestData("search-D")
                .addTestData("search-E")
                .addTestData("search-F")
                .addTestData("messagerule-trigger")
                .addTestData("messagerule-absolute")
                .addTestData("user-blast")
                .addTestData("user-client-plus")
                .addTestData("user-client-with")
                .build();
    }

    @Given("there is a project with a message rule")
    public void thereIsProjectWithAMessageRule() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("messagerule-trigger")
                .build();
    }

    @Given("there is a project with an inactive message rule")
    public void thereIsAProjectWithAnInactiveMessageRule() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("messagerule-trigger")
                .addTestData("messagerule-inactive")
                .addTestData("user-client-with")
                .build();
    }

    @Given("There is a Project with Blast Standard")
    public void thereIsAProjectWithBlastStandard() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("search-1")
                .build();
    }

    @Given("There is a Project without Blast Standard")
    public void thereIsAProjectWithoutBlastStandard() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("search-1")
                .build();
    }

    @Given("There is a project with a DataReport")
    public void thereIsAProjectWithADataReport() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("blast-two")
                .addTestData("search-G")
                .build();
    }

    @Given("There is a project with a blast")
    public void thereIsAProjectWithABlast() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("blast-two")
                .build();
    }

    @Given("there is a project with mp that have measured with standard with letter appendix")
    public void thereIsAProjectWithMpThatHaveMeasuredWithStandardA() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-2")
                .addTestData("search-H")
                .build();
    }

    @Given("there is a project with a client")
    @Given("there is a project with a user connected to the project")
    public void thereIsAProjectWithAUser() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("user-client-with")
                .build();
    }

    @Given("there is a project with an mp and a client")
    public void thereIsAProjectWithAnMpAndAClient() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("user-client-with")
                .build();
    }

    @Given("there is a project with an Agenda and a client")
    public void thereIsAProjectWithAClient() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("agenda-1")
                .addTestData("user-client-with")
                .build();
    }

    @Given("there is a project with a blaster")
    public void thereIsAProjectWithABlaster() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("user-blast")
                .build();
    }

    @Given("there is a project with default_price and mp with price")
    public void thereIsAProjectWithDefault_priceAndMpWithPrice() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-price")
                .addTestData("measuringpoint-C22-price")
                .build();
    }

    @Given("there is a project with a temporary interval report")
    public void thereIsAProjectWithATemporaryIntervalReport() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-2")
                .addTestData("search-I")
                .build();
    }

    @Given("there is a project with a saved interval report")
    public void thereIsAProjectWithASavedIntervalReport() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("search-2")
                .build();
    }

    @Given("there is a project with a measuring point")
    public void thereIsAProjectWithAMeasuringPoint() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .build();
    }

    @Given("there is a project with blast standard and a measuring point")
    public void thereIsAProjectWithBlastStdAndAMeasuringPoint() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-2")
                .build();
    }

    @Given("there is a project with POINT connected to a mp")
    public void thereIsAProjectWithPOINTConnectedToAMp() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-VS12")
                .build();
    }

    @Given("there is a project with several measuring points")
    public void thereIsAProjectWithSeveralMeasuringPoints() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("measuringpoint-VS12")
                .addTestData("measuringpoint-S50-2")
                .build();
    }

    @Given("there is a project with a C50")
    public void thereIsAProjectWithAC50() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C50-1")
                .build();
    }

    @Given("there is a project with two measuring point to same C50")
    public void thereIsAProjectWithTwoMpToSameC50() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C50-1")
                .addTestData("measuringpoint-C50-2")
                .build();
    }

    @Given("there is a project with a C22 and interval report")
    public void thereIsAProjectWithAC22AndIntervalReport() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("search-K")
                .build();
    }

    @Given("there is a project with a {string} and transient report")
    public void thereIsAProjectWithATransientReport(String deviceType) {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        if (deviceType.equals("V12")) {
            builder
                    .addTestData("project-wo-blast std")
                    .addTestData("measuringpoint-V12-2")
                    .addTestData("search-J")
                    .build();
        } else if (deviceType.equals("C22")) {
            builder
                    .addTestData("project-wo-blast std")
                    .addTestData("measuringpoint-C22-1")
                    .addTestData("search-N")
                    .build();
        }
    }

    @Given("there is a project with an all data report for std 59")
    public void thereIsAProjectWithAIntervalReportForStd() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("search-L")
                .build();
    }


    @Given("there is a project for Account Projects Billing Report testing")
    @Given("there is a project for Project Billing testing")
    public void thereIsAProjectForProjectBillingTesting() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-pbr-1")
                .addTestData("measuringpoint-C22-pbr-2")
                .build();
    }

    @Given("there is a project with a measuring point that has two devices")
    public void thereIsAProjectWithAMeasuringPointThatHasTwoDevices() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-pbr-3")
                .build();
    }

    @Given("there is a guide value project")
    public void thereIsAGuideValueProject() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-guide-value_1")
                .addTestData("measuringpoint-C22-guide-value_2")
                .addTestData("measuringpoint-C22-guide-value_3")
                .addTestData("blast-three-a")
                .addTestData("blast-three-b")
                .addTestData("messagerule-guide-value_1")
                .addTestData("messagerule-guide-value_2")
                .addTestData("messagerule-guide-value_3")
                .addTestData("search-K")
                .build();
    }

    @Given("there is a message rule with all labels {string}")
    public void thereIsAMessageRuleWithAllLabels(String state) {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");

        switch (state) {
            case "ON" -> {
                builder
                        .addTestData("project-w-blast std")
                        .addTestData("measuringpoint-C22-1")
                        .addTestData("messagerule-absolute_labels ON")
                        .build();
            }
            case "OFF" -> {
                builder
                    .addTestData("project-w-blast std")
                    .addTestData("measuringpoint-C22-1")
                    .addTestData("messagerule-absolute_labels OFF")
                    .build();
            }
            default -> throw new IllegalStateException("Unexpected state: " + state);
        }
    }

    @Given("a data report for SBR-A")
    public void aDataReportForSBRA() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("search-O")
                .build();
    }

//    @Given("{string} has {string} configuration")
//    public void deviceHasConfiguration(String device, String changeCase) {
//        TestDataBuilder builder = new TestDataBuilder(context, "templates/change");
//        builder
//                .addTestData("change-" + device + "_" + changeCase)
//                .build();
//    }

    @Given("there is a project with an outdated mp")
    public void thereIsAProjectWithAnOutdatedMp() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-outdated")
                .build();
    }

    @Given("there is a multiple mr project")
    public void thereIsAMultipleMrProject() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-guide-value_1")
                .addTestData("measuringpoint-C22-guide-value_2")
                .addTestData("measuringpoint-C22-guide-value_3")
                .addTestData("messagerule-trigger")
                .addTestData("messagerule-trigger-duplicate 2")
                .addTestData("messagerule-trigger-duplicate 3")
                .build();
    }

    @Given("there is a project with {int} measuring points")
    public void thereIsAProjectWithMeasuringPoints(int mpCount) {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std");

        IntStream.range(0, mpCount)
                .forEach(m -> builder.addTestData("measuringpoint-SDR-C22-1"));

        builder.build();
    }

    @Given("an SDR project")
    public void anSDRProject() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("agenda-1")
                .addTestData("measuringpoint-SDR-C22-1")
                .addTestData("measuringpoint-SDR-C22-2")
                .addTestData("measuringpoint-SDR-C50-1")
                .addTestData("measuringpoint-SDR-S50-1")
                .addTestData("measuringpoint-SDR-V12")
                .addTestData("measuringpoint-SDR-VS12")
                .addTestData("scheduledreport-1")
                .addTestData("scheduledreport-2")
                .addTestData("scheduledreport-3")
                .build();
    }

    @Given("a project with vib and noise")
    public void aProjectWithVibAndNoise() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("measuringpoint-C50-1")
                .build();
    }

    @Given("a transient project")
    public void aTransientProject() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("measuringpoint-C50-1")
                .addTestData("measuringpoint-S50-3")
                .addTestData("measuringpoint-V10")
                .addTestData("measuringpoint-V12")
                .addTestData("measuringpoint-A10")
                .addTestData("measuringpoint-VS12")
                .addTestData("measuringpoint-C12")
                .addTestData("search-transients")
                .build();
    }

    @Given("a project in Istanbul timezone")
    public void aProjectInIstanbulTimezone() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast_tz-istanbul")
                .build();
    }

    @Given("a C50 report with custom_Ln and dBCorr")
    public void aCReportWithCustom_LnAndDBCorr() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-wo-blast std")
                .addTestData("measuringpoint-C50-dBCorr")
                .addTestData("search-Ln")
                .build();
    }

    @Given("a project with a message rule with recipient")
    public void aProjectWithAMessageRuleWithRecipient_() {
        TestDataBuilder builder = new TestDataBuilder(context, "templates");
        builder
                .addTestData("project-w-blast std")
                .addTestData("measuringpoint-C22-1")
                .addTestData("user-client-plus")
                .addTestData("messagerule-trigger")
                .build();
    }
}
