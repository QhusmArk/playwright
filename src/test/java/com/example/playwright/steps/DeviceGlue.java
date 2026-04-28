package com.example.playwright.steps;

import com.example.playwright.helpers.PlaywrightActions;
import io.cucumber.java.en.When;

public class DeviceGlue extends BaseGlue {

    // device details
    @When("I click on {string}")
    public void iClickOn(String detail) {
        devicePO.clickDeviceDetails(detail);
        PlaywrightActions.sleep(2);
    }



}
