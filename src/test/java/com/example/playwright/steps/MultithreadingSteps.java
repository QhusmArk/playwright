package com.example.playwright.steps;

import com.example.playwright.navigation.Navigate;
import com.example.playwright.pageObjects.LoginPO;
import com.example.playwright.session.SessionCookieManager;
import com.example.playwright.testUsers.TestUser;
import com.example.playwright.testUsers.TestUserPool;
import com.microsoft.playwright.options.Cookie;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.messages.types.Hook;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultithreadingSteps {

    private static final Set<Long> THREAD_IDS = ConcurrentHashMap.newKeySet();

    @Before(order = 1)
    public void acquireUserWithRole(Scenario scenario) {

        String requiredRole = resolveRequiredRole(scenario);

        var user = (requiredRole != null)
                ? TestUserPool.acquireUserWithRole(requiredRole)
                : TestUserPool.acquireUser()
                .orElseThrow(() -> new RuntimeException("No available test users in pool"));

        System.out.println(
                "Thread " + Thread.currentThread().threadId() +
                        " acquired user: " + user.email() +
                        " with role: " + user.role()
        );
    }

    @Before(order = 3)
//    public void prepareLoginSession(Scenario scenario) {
//        TestUser currentUser = TestUserPool.getCurrentUser();
//
//        Navigate.domain().get();
//
//        if (isManualLoginScenario(scenario)) {
//
//            loginCurrentUser();
//
//        } else {
//
//            Optional<Cookie> optCookie = SessionCookieManager.loadSessionCookie(currentUser);
//
//            // Is for this user?
//            if (optCookie.isPresent()) {
//                Cookie cookie = optCookie.get();
//
//                // Is for this domain?
//                String webUrl = Navigate.webUrl();
//                if (cookie.domain.equals(webUrl))  {
//
//                    // Has not expired?
//                    long millis = (long) (cookie.expires * 1000);
//                    Instant cookieExpiry = Instant.ofEpochMilli(millis);
//
//                    if (Instant.now().isBefore(cookieExpiry)) {
//                        Hooks.addCookie(cookie);
//                        Navigate.refreshBrowser();
//                        // After this step some navigation to beyond /login has to be made
//
//                    } else {
//                        loginCurrentUser();
//                    }
//                } else {
//                    loginCurrentUser();
//                }
//            } else {
//                loginCurrentUser();
//            }
//        }
//
//        if (!isManualLoginScenario(scenario)) {
//            Cookie sessionid = Hooks.getCookie("sessionid");
//            SessionCookieManager.saveSessionCookie(
//                    currentUser,
//                    sessionid
//            );
//        }
//    }
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

                    Hooks.addCookie(cookie);
                    // Navigation has to be done for cookie to guide us past /login
                } else  {
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

    @Given("multithreading is required")
    public void multithreadingIsRequired() {
        assertTrue(
                Runtime.getRuntime().availableProcessors() > 1,
                "Multithreading cannot be used because only one processor is available."
        );
    }

    @When("the test execution starts")
    public void theTestExecutionStarts() {
        THREAD_IDS.add(Thread.currentThread().threadId());
    }

    @Then("this scenario records its thread")
    public void thisScenarioRecordsItsThread() {
        THREAD_IDS.add(Thread.currentThread().threadId());

        System.out.println(
                "Scenario running on thread: " + Thread.currentThread().threadId()
        );
    }

    @Then("the browser opens the web client")
    public void theBrowserOpensTheWebClient() {
        Navigate.domain().get();
    }

    @Then("the user logs in")
    public void theUserLogsIn() {
        loginCurrentUser();
    }

    @Then("the current user is printed")
    public void theCurrentUserIsPrinted() {
        var user = TestUserPool.getCurrentUser();

        System.out.println(
                "Thread " + Thread.currentThread().threadId() +
                        " using user: " + user.email()
        );
    }

//    @Then("the user is stored in the browser")
//    public void theUserIsStoredInTheBrowser() {
//        var user = TestUserPool.getCurrentUser();
//
//        Hooks.getPage().evaluate(
//                "user => localStorage.setItem('test-user', user)",
//                user.email()
//        );
//    }
//
//    @Then("the user in the browser should match the current user")
//    public void theUserInTheBrowserShouldMatchTheCurrentUser() {
//        var expectedUser = TestUserPool.getCurrentUser().email();
//
//        String actualUser = Hooks.getPage().evaluate(
//                "() => localStorage.getItem('test-user')"
//        ).toString();
//
//        assertTrue(
//                expectedUser.equals(actualUser),
//                "Mismatch between thread user and browser user. Expected: "
//                        + expectedUser + " but got: " + actualUser
//        );
//    }

    private void loginCurrentUser() {
        var user = TestUserPool.getCurrentUser();
        new LoginPO(Hooks.getPage()).login(
                user.email(),
                user.password()
        );
        Navigate.waitUntilUrlContains("overview");
    }

    @After
    public void releaseTestUser(Scenario scenario) {
        System.out.println(
                "Scenario '" + scenario.getName() + "' | Thread " +
                        Thread.currentThread().threadId() +
                        " releasing user: " +
                        TestUserPool.getCurrentUser().email()
        );

        TestUserPool.releaseCurrentUser();
    }
}