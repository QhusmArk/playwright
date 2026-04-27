package com.example.playwright.steps;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import com.microsoft.playwright.BrowserContext;

import java.util.List;

public class Hooks {

    protected static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    protected static final ThreadLocal<Browser> browser = new ThreadLocal<>();
    protected static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    protected static final ThreadLocal<Page> page = new ThreadLocal<>();

    @Before(order = 2)
    public void setUp() {
        Playwright pw = Playwright.create();
        playwright.set(pw);

        Browser br = pw.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
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

    public static Page getPage() {
        return page.get();
    }

    public static BrowserContext getContext() {
        return context.get();
    }

    public static Cookie getCookie(String name) {
        var cookies = context.get().cookies();

        return cookies.stream()
                .filter(c -> c.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cookie not found: " + name));
    }

    public static void addCookie(Cookie cookie) {
        context.get().addCookies(List.of(cookie));
    }

    @After(order = 1)
    public void tearDown() {
        if (page.get() != null) page.get().close();
        if (context.get() != null) context.get().close();
        if (browser.get() != null) browser.get().close();
        if (playwright.get() != null) playwright.get().close();
    }
}