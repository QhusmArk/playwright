package com.example.playwright.pageObjects;

public class DevicePO extends BasePO {

    public void clickDeviceDetails(String detail) {
        actions().click("(//div[@data-qa-id='panel-body'] //div[@class='col-xs-12 col-sm-6'])[5]");
    }

}
