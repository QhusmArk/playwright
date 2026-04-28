package com.example.playwright.pageObjects;

public class LoginPO extends BasePO {

    public void login(String email, String password) {
        // Enter email
        actions().clearAndType("//form //input[@name='username']", email);

        // Enter password
        actions().clearAndType("//form //input[@name='password']", password);

        // Click submit
        actions().click("//form //input[@name='submit']");
    }
}