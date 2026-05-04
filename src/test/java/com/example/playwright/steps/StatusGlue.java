package com.example.playwright.steps;

import com.example.api.models.device.Device;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.api.models.project.Project;
import com.example.helpers.StatusAssesser;
import com.example.helpers.StatusAssesser.*;
import com.example.playwright.components.aside.asideItems.listItems.DeviceItem;
import com.example.playwright.components.aside.asideItems.listItems.MeasuringPointItem;
import com.example.playwright.components.aside.asideItems.listItems.ProjectItem;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.enums.ColourSchema;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static com.example.helpers.StatusAssesser.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusGlue extends BaseGlue {

    @When("I look for the project")
    public void iLookForTheProject() {
        //Navigate to projects
        Navigate.company()
                .projects()
                .get();

        //filter to get all states
        filterPO.changeFilter("All projects");

        //search for the project
        asidePO.makeSearchInAside(context().getProject().getName());
    }

    /**
     * Accessible step for account and project level.
     */
    @And("I validate that project map status is correct")
    public void iValidateThatProjectMapStatusIsCorrect() {
        Project project = context().getProject();
        Status expected = assessProjectStatus(project);

        Status actual = mapPO.projectMapIconStatus(project.getName());

        assertEquals(expected, actual,
                () -> "expected/actual: " + expected + "/" + actual);
    }

    /**
     * Accessible step for account level.
     */
    @And("I validate that project list status is correct")
    public void iValidateThatProjectListStatusIsCorrect() {
        //no need to navigate because we're already at account level url
        Project project = context().getProject();
        Status expected = assessProjectStatus(project);

        List<ProjectItem> projects = asidePO.getAside().getProjectItems();

        ColourSchema actualProjectListColour = projects.getFirst().getLeftIcon().getColour();
        Status actual = (actualProjectListColour.equals(ColourSchema.DISABLED))
                ? INACTIVE
                : ACTIVE;

        assertEquals(expected, actual,
                () -> "expected/actual: " + expected + "/" + actual);
    }

    /**
     * Accessible step for project level.
     */
    @And("I validate that mp map status is correct")
    public void iValidateThatMeasuringPointMapStatusIsCorrect() {
        MeasuringPoint mp = context().getMeasuringPoints().getFirst();
        Status expected = assessMpStatus(mp);

        Status actual = mapPO.mpMapIconStatus(mp.getName());

        assertEquals(expected, actual,
                () -> "expected/actual: " + expected + "/" + actual);
    }

    /**
     * Accessible step for project level.
     */
    @Then("I validate that device map status is correct")
    public void iValidateThatDeviceMapStatusIsCorrect() {
        //fetch the testdata
        Project project = context().getProject();
        MeasuringPoint mp = context().getMeasuringPoints().getFirst();

        //device has info about connectedDevice mon_state
        Device connectedDevice = context().getDevices().stream()
                .filter(device -> (device.getSerial()==Integer.parseInt(mp.getSensors().getFirst().getSerial())))
                .findAny().orElse(null);
        assert connectedDevice != null;

        Status connectedDeviceMonStatus = getDeviceCurrentMonStatus(connectedDevice);  // MONON, MONOFF
        Status connectedDeviceBatteryStatus = getDeviceBatteryStatus(connectedDevice);     // NO_WARNING, WARNING
        // todo: Vad händer om en inaktiv device inte har kommunicerat på mer än 24 t?
        Status connectedDeviceConnectionStatus = StatusAssesser.deductDeviceCommunicationStatus(connectedDevice);   // NO_WARNING, WARNING

        Status projectStatus = assessProjectStatus(project);                        // ACTIVE, INACTIVE
        Status mpStatus = assessMpStatus(mp);                                       // ACTIVE, INACTIVE

        Status deviceExpectedStatus = assessDeviceMapStatus(projectStatus, mpStatus, connectedDeviceMonStatus, connectedDeviceBatteryStatus, connectedDeviceConnectionStatus);    // ACTIVE, INACTIVE, WARNING

        // Find the device on map
        Status deviceActualStatus = mapPO.deviceMapIconStatus(mp.getSensors().getFirst().getSerial());  // ACTIVE, INACTIVE, WARNING, NOT_PRESENT

        assertEquals(deviceExpectedStatus, deviceActualStatus,
                () -> "expected/actual: " + deviceExpectedStatus + "/" + deviceActualStatus);
    }

    /**
     * Accessible step for project level.
     */
    @Then("The mp should be inactive in List")
    public void theMpShouldBeInactiveInList() {
        //check the actual project status
        MeasuringPointItem mp = asidePO.getAside().getMeasuringPointItems().getFirst();

        ColourSchema expectedListItemColour = ColourSchema.DISABLED;
        ColourSchema actualColour = mp.getLeftIcon().getColour();

        assertEquals(expectedListItemColour, actualColour,
                () -> "expectedListItemColour/actualColour: " + expectedListItemColour + "/" + actualColour);
    }

    /**
     * Accessible step for project level.
     */
    @And("The mp should be inactive in Map")
    public void theMpShouldBeInactiveInMap() {
        Status expectedStatus = INACTIVE;

        MeasuringPoint measuringPoint = context().getMeasuringPoints().getFirst();
        Status actual = mapPO.mpMapIconStatus(measuringPoint.getName());

        assertEquals(expectedStatus, actual,
                () -> "expected/actual: " + expectedStatus + "/" + actual);
    }

    /**
     * Accessible step for project level.
     */
    @And("I validate that mp list status is correct")
    public void iValidateThatMpListStatusIsCorrect() {
        //get expected status for the mp
        MeasuringPoint measuringPoint = context().getMeasuringPoints().getFirst();
        Status expectedStatus = assessMpStatus(measuringPoint);
        System.out.println("expectedStatus: " + expectedStatus);
        //get to mp-list
        Navigate.project(context().getProject().getId())
                .measurePoints()
                .get();

        MeasuringPointItem mp = asidePO.getAside().getMeasuringPointItems().getFirst();

        ColourSchema actualListItemColour = mp.getLeftIcon().getColour();
        Status actualStatus = (actualListItemColour.equals(ColourSchema.DISABLED))
                ? INACTIVE
                : ACTIVE;

        assertEquals(expectedStatus, actualStatus,
                () -> "expectedStatus/actualStatus: " + expectedStatus + "/" + actualStatus);
    }

    @And("I validate that device list status is correct")
    public void iValidateThatDeviceListStatusIsCorrect() {
        //get to device-list
        Navigate.project(context().getProject().getId())
                .devices()
                .get();

        //fetch the testdata
        MeasuringPoint mp = context().getMeasuringPoints().getFirst();
        //device has info about connectedDevice mon_state
        Device connectedDevice = context().getDevices().stream()
                .filter(device -> (device.getSerial() == Integer.parseInt(mp.getSensors().getFirst().getSerial())))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Did not find a connected device."));

        //calculate the expected status for the sensor
        Status projectStatus = assessProjectStatus(context().getProject());                        // ACTIVE, INACTIVE
        Status mpStatus = assessMpStatus(mp);                                       // ACTIVE, INACTIVE
        Status connectedDeviceMonStatus = getDeviceCurrentMonStatus(connectedDevice);  // MONON, MONOFF

        Status deviceExpectedStatus = assessDeviceListStatus(projectStatus, mpStatus, connectedDeviceMonStatus);

        //get actual status for the sensor
        List<DeviceItem> actualDevices = asidePO.getAside().getDeviceItems();

        // If list is empty, we didn't find the device in list, ie NOT_PRESENT
        Status actualDeviceListStatus = (actualDevices.isEmpty())
                ? NOT_PRESENT
                : (actualDevices.getFirst().getLeftIcon().getColour().equals(ColourSchema.PRIMARY))
                    ? ACTIVE
                    : null;

        assertEquals(deviceExpectedStatus, actualDeviceListStatus,
                () -> "expected/actual: " + deviceExpectedStatus + "/" + actualDeviceListStatus);
    }

    @Then("The project should be inactive in Map")
    public void theProjectShouldBeInactiveInMap() {
        //make sure search field is not open
        Navigate.refreshBrowser();

        //filter to get all states
        filterPO.changeFilter("All projects");

        //search for the project
        asidePO.makeSearchInAside(context().getProject().getName());

        Status expected = INACTIVE;

        //look for the map icon
        Status actualProjectMapStatus = mapPO.projectMapIconStatus(context().getProject().getName());

        assertEquals(expected, actualProjectMapStatus,
                () -> "expected/actual: " + expected + "/" + actualProjectMapStatus);
    }

    @Then("The project should be inactive in List")
    public void theProjectShouldBeInactiveInList() {
        //make sure search field is not open
        Navigate.refreshBrowser();

        //filter to get all states
        filterPO.changeFilter("All projects");

        //search for the project
        asidePO.makeSearchInAside(context().getProject().getName());

        ColourSchema expected = ColourSchema.DISABLED;
        ProjectItem project = asidePO.getAside().getProjectItems().getFirst();

        //check the actual projects status
        ColourSchema actualProjectListStatus = project.getLeftIcon().getColour();

        assertEquals(expected, actualProjectListStatus,
                () -> "expected/actual: " + expected + "/" + actualProjectListStatus);
    }

    @And("the map is updated to show the new location")
    @And("the mp is visible on map")
    public void theMpIsVisibleOnMap() {
        // Assert that the mp map marker has correct status, and indirectly is visible on map
        Status actual = mapPO.mpMapIconStatus(context().getMeasuringPoints().getFirst().getName());
        assertEquals(ACTIVE, actual,
                () -> "expected/actual: " + ACTIVE + "/" + actual);
    }
}
