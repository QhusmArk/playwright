package com.example.playwright.pageObjects;

import java.util.ArrayList;
import java.util.List;

public class CommonElementsPO extends BasePO {

    public boolean isSessionExpired() {
        return actions().elementExistAndVisible("//*[contains(text(), 'Your session is expired')]", false);
    }

    public void clickButtonText(String buttonText) {
        actions().click("//button[.//span[text()='"+buttonText+"']]");
    }

    public void clickButton(String button) {
        switch (button.toLowerCase()) {
            case "apply", "create", "save" -> actions().click("//button[@type='submit']");
            case "meatball" -> actions().click("//div[@role='toolbar'] //i[text()='more_vert']/ancestor::button");
            case "vibration report" -> actions().click("//a[@data-qa-id='project_mp_vibration_report']");
            case "select columns" -> actions().click("//i[text()='view_column']");
            case "panel close" -> actions().click("//*[@data-qa-id='panel-btn-close']");
            case "list header plus" -> actions().click("//*[@data-qa-id='create-new-entity']");
            case "commit" -> actions().click("//div[text()='Commit']");
            case "discard", "add time slot"-> actions().click("//span[text()='"+button+"']");
            case "remove" -> actions().click("//div[@role='dialog'] //span[text()='Remove']");
//            case "+ create user" -> clickCreateNewUserButton();
//            case "copy agenda" -> clickOnButton(button);
            case "mon","tue","wed","thu","fri","sat","sun"  -> actions().click("//*[text()='" + button + "']");
            default -> throw new IllegalArgumentException("Unexpected button: " + button);
        }
    }

    /**
     * If project has pre-req for automatic transient reports, then this msg will likely come first if test is fast.
     * @return The error messages coming up from bottom of screen.
     */
    public List<String> getToasts() {
        //Give time for more than "Creating temporary report" to show.
//        PlaywrightActions.sleep(4);

        if (actions().elementExistAndVisible("//div[@class='q-notification__message col']", false)) {
            // Collect the messages into a list
            return actions().findManyElementsTexts("//div[@class='q-notification__message col']");

        } else {
            return new ArrayList<>();
        }
    }

}
