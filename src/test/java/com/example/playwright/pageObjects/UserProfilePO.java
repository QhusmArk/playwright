package com.example.playwright.pageObjects;


import com.example.playwright.helpers.PlaywrightActions;

public class UserProfilePO extends CommonPO {

    private static final String INPUT_FIRST_NAME = "//input[@data-qa-id='first_name']";
    private static final String INPUT_LAST_NAME = "//input[@data-qa-id='first_name']";
    private static final String INPUT_E_MAIL = "//input[@data-qa-id='email']";

    private static final String DROPDOWN_COUNTRY_CODE = ""; //Not implemented
    private static final String COUNTRY_CODE_SWEDEN = ""; //Not implemented

    private static final String INPUT_COUNTRY_CODE = ""; //Not implemented
    private static final String INPUT_MOBILE_NUMBER = ""; //Not implemented

    private static final String DROPDOWN_LANGUAGE = "//*[@data-qa-id='language']";
    private static final String LANGUAGE_ENGELSKA = "//*[@data-qa-id='Engelska']";
    private static final String LANGUAGE_ENGLISH = "//*[@data-qa-id='English']";
    private static final String LANGUAGE_SVENSKA = "//*[@data-qa-id='Svenska']";
    private static final String LANGUAGE_SWEDISH = "//*[@data-qa-id='Swedish']";

    private static final String INPUT_PASSWORD = "//*[@data-qa-id='old_pwd']"; //Not implemented
    private static final String INPUT_NEW_PASSWORD = "//*[@data-qa-id='new_pwd']"; //Not implemented
    private static final String INPUT_VERIFY_PASSWORD = "//*[@data-qa-id='verify_new_password']"; //Not implemented

    private static final String BUTTON_CLOSE = "//button //i[text()='close']";
    private static final String BUTTON_SAVE = "//*[@class='q-card'] //span[@class='block']";

    public void changePassword(String oldPwd, String newPwd) {
        actions().clearAndType(INPUT_PASSWORD, oldPwd);
        actions().clearAndType(INPUT_NEW_PASSWORD, newPwd);
        actions().clearAndType(INPUT_VERIFY_PASSWORD, newPwd);

        clickSaveButton();
    }

    public void clickSaveButton() {
        actions().makeClick(BUTTON_SAVE);
        // Give the updated user a second or two to be updated
        PlaywrightActions.sleep(2);
    }

    public String getFirstName() {
        return actions().findOneElementsAttribute(INPUT_FIRST_NAME, "value");
    }

    public void enterUserFirstName(String value) {
        PlaywrightActions.sleep(1);
        actions().clearAndType(INPUT_FIRST_NAME, value);
    }

    public String getCurrentLanguage() {
        return actions().findOneElementsText(DROPDOWN_LANGUAGE);
    }

    public void changeLanguage(String currentLanguage) {
        actions().makeClick(DROPDOWN_LANGUAGE);
        PlaywrightActions.sleep(2);

        //select another language
        switch (currentLanguage) {
            case "English" -> actions().makeClick(LANGUAGE_SWEDISH); // If currentLanguage=English, choose Swedish
            case "Svenska" -> actions().makeClick(LANGUAGE_ENGELSKA); // If currentLanguage=Svenska, choose Engelska
            case "Swedish" -> actions().makeClick(LANGUAGE_ENGLISH); // If currentLanguage=Swedish, choose English
            case "Engelska" -> actions().makeClick(LANGUAGE_SVENSKA); // If currentLanguage=Engelska, choose Svenska
        }
        clickSaveButton();
    }


}
