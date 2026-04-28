package com.example.playwright.hooks;

import com.example.playwright.helpers.Navigate;
import com.example.playwright.session.SessionCookieManager;
import com.example.playwright.steps.BaseGlue;
import com.example.playwright.testUsers.TestUser;
import com.example.playwright.testUsers.TestUserPool;
import com.microsoft.playwright.options.Cookie;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.time.Instant;
import java.util.Optional;

public class LoginHooks extends BaseGlue {

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
                    BrowserHooks.addCookie(cookie);
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

        Cookie sessionid = BrowserHooks.getCookie("sessionid");
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

    private void loginCurrentUser() {
        var user = TestUserPool.getCurrentUser();

        loginPO.login(
                user.email(),
                user.password()
        );

        Navigate.waitUntilUrlContains("overview");
    }
}