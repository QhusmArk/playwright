package com.example.playwright.pageObjects;

import com.example.playwright.helpers.PlaywrightActions;

public class MenuPO extends BasePO {

    public void openUserMenuAndSelectMenuItem(String menuItem) {
        actions().click("//header //*[text()='account_circle']");

        // Right after clicking on USER_ICON there is a new span element for half second. Wait it out.
        PlaywrightActions.sleep(1);

        switch (menuItem) {
            case "USER_PROFILE" -> actions().click("//div[@role='menu'] //a[@data-qa-id='user_settings']");
            case "INFRA_ACADEMY" -> actions().click("//div[@role='menu'] //a[@data-qa-id='infra_academy_link']");
            case "SUPPORT" -> actions().click("//div[@role='menu'] //a[@data-qa-id='support']");
            case "CLASSIC" -> actions().click("//div[@role='menu'] //a[@data-qa-id='old_client_link']");
            case "LOGOUT" -> actions().click("//div[@role='menu'] //a[@data-qa-id='logout']");
        }
    }


}
