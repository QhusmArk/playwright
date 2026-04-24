package com.example.playwright.pageObjects;

import com.microsoft.playwright.Page;

public class LoginPO {

    private final Page page;

    public LoginPO(Page page) {
        this.page = page;
    }

    public void login(String email, String password) {
        // Enter email
        page.locator("//form //input[@name='username']").fill(email);

        // Enter password
        page.locator("//form //input[@name='password']").fill(password);

        // Click submit
        page.locator("//form //input[@name='submit']").click();
    }
}