package com.example.playwright.pageObjects;

public class UserProfilePO extends BasePO {

    public String getFirstName() {
        return actions().findOneElementsAttribute("//input[@data-qa-id='first_name']", "value");
    }

    public void enterUserFirstName(String value) {
//        PlaywrightActions.sleep(1);
        actions().clearAndType("//input[@data-qa-id='first_name']", value);
    }
}
