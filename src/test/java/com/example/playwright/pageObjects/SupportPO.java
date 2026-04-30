package com.example.playwright.pageObjects;

public class SupportPO extends CommonPO {

    private static final String BUTTON_CLOSE = "//div[@class='q-card'] //button";
    private static final String BUTTON_SEND = "//div[@class='q-card'] //div[2] //button";
    private static final String MODUL = "//div[@class='q-card']";
    private static final String DROP_DOWN_LOG_REPORT = MODUL + "//div[@role='button']";
    private static final String TEXT_VERSION = "//div[contains(text(), 'Version')] /following-sibling::div";
    private static final String TEXT_USER_INFO = MODUL + "//div[contains(text(), 'id')]";

    public int getUserNumber() {
        actions().elementExistAndVisible("//div[@class='q-card'] //div[contains(text(), 'Support')]");

        actions().makeClick(DROP_DOWN_LOG_REPORT);

        String userInfo = actions().findOneElementsText(TEXT_USER_INFO);   // ie. OH_ 84 Admin (id: 1885)

        String[] afterColon = userInfo.substring(userInfo.indexOf("(")+1, userInfo.lastIndexOf(")")).split("id: ");
        return Integer.parseInt(afterColon[1]);
    }

    public String getVersionNumber() {
        return actions().findOneElementsText(TEXT_VERSION);
    }
}
