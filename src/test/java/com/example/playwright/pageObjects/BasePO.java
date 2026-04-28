package com.example.playwright.pageObjects;

import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.hooks.BrowserHooks;
import com.microsoft.playwright.Page;

public abstract class BasePO {

    // If I need to access page from Glue layer, use this
    protected Page page() {
        return BrowserHooks.getPage();
    }

    protected PlaywrightActions actions() {
        return BrowserHooks.getActions();
    }
}
