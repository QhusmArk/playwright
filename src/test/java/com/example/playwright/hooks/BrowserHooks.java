package com.example.playwright.hooks;

import com.example.api.RequestService;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.AsideSize;
import com.example.playwright.helpers.enums.DeviceType;
import com.example.playwright.helpers.enums.IconType;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.ParameterType;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BrowserHooks {

    private int step = 1;

    // Control headless or not with this boolean
    @Getter
    private static final boolean isHeadless = true;
//    private static final boolean isHeadless = false;


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
                        .setHeadless(isHeadless)
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

        // todo: hitta en bättre plats för den här?
        RequestService.setUp();
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
                .orElse(null);
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

    /**
     * TypeRegistry so that we can pass booleans from feature file to step definition.
     * @param value Should be "true" or "false"
     * @return Boolean true or false if value is "true" or "false"
     */
    @ParameterType(value = "true|false")
    public Boolean booleanValue(String value) {
        return Boolean.valueOf(value);
    }

    @ParameterType(value = "present|not present")
    public Boolean isPresent(String input) {
        return "present".equalsIgnoreCase(input);
    }

    /**
     * @param input like '| fw_version:2.1.11 |'
     */
    @ParameterType("\\|?\\s*([^:]+:[^|]+)\\s*(\\|\\s*[^:]+:[^|]+\\s*)*")
    public Map<String, String> keyValueTable(String input) {
        return Stream.of(input.split("\\|"))
                .map(String::trim)
                .map(entry -> entry.split(":"))
                .collect(Collectors.toMap(e -> e[0].trim(), e -> e[1].trim()));
    }

    /**
     * TypeRegistry so that we can cast expectations to boolean.
     * @param input success or failure
     * @return Boolean true or false if input is "success" or "failure"
     */
    @ParameterType("success|failure")
    public boolean result(String input) {
        return "success".equalsIgnoreCase(input);
    }

    @ParameterType("Device_.+")
    public DeviceType deviceType(String type) {
        type = type.replace("Device_", "");
        return DeviceType.fromType(type);
    }

    @ParameterType("Icon_.+")
    public IconType iconType(String type) {
        type = type.replace("Icon_", "");
        return IconType.valueOf(type);
    }

    @ParameterType("COMPACT|MEDIUM|FULL")
    public AsideSize asideSize(String asideSize) {
        return AsideSize.valueOf(asideSize);
    }
}