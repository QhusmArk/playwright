package com.example.playwright.pageObjects;

import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.steps.Hooks;
import com.microsoft.playwright.Page;

public abstract class BasePO {

    // If I need to access page from Glue layer, use this
    protected Page page() {
        return Hooks.getPage();
    }

    protected PlaywrightActions actions() {
        return Hooks.getActions();
    }
}
