package com.example.playwright.steps;

import com.example.playwright.helpers.Navigate;
import com.example.playwright.session.SessionCookieManager;
import com.example.playwright.testUsers.TestUser;
import com.example.playwright.testUsers.TestUserPool;
import com.microsoft.playwright.options.Cookie;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;

import java.time.Instant;
import java.util.Optional;

public class MultithreadingSteps extends BaseGlue {
    private int step = 1;

//    private static final Set<Long> THREAD_IDS = ConcurrentHashMap.newKeySet();

    /**
     * Method that runs before each step. Used in Jenkins job log to track steps.
     */
    @BeforeStep
    public void beforeStep() {
        System.out.println("*********** Starting step " + step + " ***********");
        step++;
    }

    @Before(order = 1)
    public void acquireUserWithRole(Scenario scenario) {

        String requiredRole = resolveRequiredRole(scenario);

        var user = (requiredRole != null)
                ? TestUserPool.acquireUserWithRole(requiredRole)
                : TestUserPool.acquireUser()
                .orElseThrow(() -> new RuntimeException("No available test users in pool"));
        System.out.println("user: " + user.email());
    }

    @Before(order = 3)
    public void prepareLoginSession(Scenario scenario) {
        TestUser currentUser = TestUserPool.getCurrentUser();

        Navigate.domain().get();

        if (isManualLoginScenario(scenario)) {

            loginCurrentUser();

        } else {

            Optional<Cookie> optCookie = SessionCookieManager.loadSessionCookie(currentUser);

            if (optCookie.isPresent()) {
                Cookie cookie = optCookie.get();

                boolean correctDomain = forThisDomain(optCookie.get());
                boolean hasNotExpired = hasExpired(optCookie.get());
                if (correctDomain && hasNotExpired) {
                    // If all checks, add a cookie to avoid manual login
                    Hooks.addCookie(cookie);
                    // Now when we have a cookie, and this navigation leads to .../account/overview
                    Navigate.domain().get();

                    boolean sessionHasExpired = cePO.isSessionExpired();

                    if (sessionHasExpired) {
                        loginAndStoreCookie(currentUser);
                    }
                } else {
                    loginAndStoreCookie(currentUser);
                }
            } else {
                loginAndStoreCookie(currentUser);
            }
        }
    }

    private void loginAndStoreCookie(TestUser currentUser) {
        loginCurrentUser();

        Cookie sessionid = Hooks.getCookie("sessionid");
        SessionCookieManager.saveSessionCookie(
                currentUser,
                sessionid
        );
    }

    private boolean hasExpired(Cookie cookie) {
        long millis = (long) (cookie.expires * 1000);
        Instant cookieExpiry = Instant.ofEpochMilli(millis);
        return Instant.now().isBefore(cookieExpiry);
    }

    private boolean forThisDomain(Cookie cookie) {
        String webUrl = Navigate.webUrl();
        return cookie.domain.equals(webUrl);
    }

    private boolean isManualLoginScenario(Scenario scenario) {
        var tags = scenario.getSourceTagNames();

        return tags.contains("@loginWithAdmin")
                || tags.contains("@loginWithUser")
                || tags.contains("@loginWithClient")
                || tags.contains("@loginWithBlaster");
    }

    private String resolveRequiredRole(Scenario scenario) {
        var tags = scenario.getSourceTagNames();

        int roleCount = 0;
        String role = null;

        if (tags.contains("@admin") || tags.contains("@loginWithAdmin")) {
            roleCount++;
            role = "ADMIN";
        }

        if (tags.contains("@user") || tags.contains("@loginWithUser")) {
            roleCount++;
            role = "USER";
        }

        if (tags.contains("@client") || tags.contains("@loginWithClient")) {
            roleCount++;
            role = "CLIENT";
        }

        if (tags.contains("@blaster") || tags.contains("@loginWithBlaster")) {
            roleCount++;
            role = "BLASTER";
        }

        if (roleCount > 1) {
            throw new RuntimeException("Scenario has multiple role tags: " + tags);
        }

        return role;
    }

    private void loginCurrentUser() {
        var user = TestUserPool.getCurrentUser();

        loginPO.login(
                user.email(),
                user.password()
        );

        Navigate.waitUntilUrlContains("overview");
    }

    @After
    public void releaseTestUser(Scenario scenario) {
        TestUserPool.releaseCurrentUser();
    }
}