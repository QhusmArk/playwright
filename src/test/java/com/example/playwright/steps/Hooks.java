package com.example.playwright.steps;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;

import java.util.List;

public class Hooks {

    protected static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    protected static final ThreadLocal<Browser> browser = new ThreadLocal<>();
    protected static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    protected static final ThreadLocal<Page> page = new ThreadLocal<>();

    public static Page getPage() {
        return page.get();
    }

    @Before
    public void setUp() {
        Playwright pw = Playwright.create();
        playwright.set(pw);

        Browser br = pw.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(300) //     wait 300 milliseconds after each action
                        .setArgs(List.of("--window-size=900,700"))
        );
        browser.set(br);

        BrowserContext ctx = br.newContext(
                new Browser.NewContextOptions()
                        .setIgnoreHTTPSErrors(true)
                        .setPermissions(List.of()) // no permissions granted
        );
        context.set(ctx);

        Page pg = ctx.newPage();
        page.set(pg);
    }

    @After
    public void tearDown() {
        if (page.get() != null) page.get().close();
        if (context.get() != null) context.get().close();
        if (browser.get() != null) browser.get().close();
        if (playwright.get() != null) playwright.get().close();
    }
}