package com.example.playwright.hooks;

import com.example.playwright.helpers.PlaywrightActions;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;

import java.util.List;

public class BrowserHooks {

    private int step = 1;

    protected static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    protected static final ThreadLocal<Browser> browser = new ThreadLocal<>();
    protected static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    protected static final ThreadLocal<Page> page = new ThreadLocal<>();
    protected static final ThreadLocal<PlaywrightActions> actions = new ThreadLocal<>();

    @Before(order = 2)
    public void setUp() {
        Playwright pw = Playwright.create();
        playwright.set(pw);

        Browser br = pw.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
//                        .setHeadless(false)
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

        actions.set(new PlaywrightActions(pg));
    }

    public static Page getPage() {
        return page.get();
    }

    public static BrowserContext getContext() {
        return context.get();
    }

    public static PlaywrightActions getActions() {
        return actions.get();
    }

    public static Cookie getCookie(String name) {
        var cookies = context.get().cookies();

        return cookies.stream()
                .filter(c -> c.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cookie not found: " + name));
    }

    public static void addCookie(Cookie cookie) {
        System.out.println("Adding cookie.");
        context.get().addCookies(List.of(cookie));
    }

    /**
     * Method that runs before each step. Used in Jenkins job log to track steps.
     */
    @BeforeStep
    public void beforeStep() {
        System.out.println("*********** Starting step " + step + " ***********");
        step++;
    }

    @After(order = 1)
    public void tearDown() {
        if (page.get() != null) page.get().close();
        if (context.get() != null) context.get().close();
        if (browser.get() != null) browser.get().close();
        if (playwright.get() != null) playwright.get().close();

        page.remove();
        context.remove();
        browser.remove();
        playwright.remove();
        actions.remove();
    }
}