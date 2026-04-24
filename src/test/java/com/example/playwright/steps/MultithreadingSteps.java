package com.example.playwright.steps;

import com.example.playwright.config.TestEnvironment;
import com.example.playwright.navigation.Navigate;
import com.example.playwright.pageObjects.LoginPO;
import com.example.playwright.testUsers.TestUserPool;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultithreadingSteps {

    private static final Set<Long> THREAD_IDS = ConcurrentHashMap.newKeySet();

    @Before
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

    private String resolveRequiredRole(Scenario scenario) {
        var tags = scenario.getSourceTagNames();

        int roleCount = 0;
        String role = null;

        if (tags.contains("@admin")) {
            roleCount++;
            role = "ADMIN";
        }

        if (tags.contains("@user")) {
            roleCount++;
            role = "USER";
        }

        if (tags.contains("@client")) {
            roleCount++;
            role = "CLIENT";
        }

        if (tags.contains("@blaster")) {
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
        var user = TestUserPool.getCurrentUser();

        new LoginPO(Hooks.getPage()).login(
                user.email(),
                user.password()
        );
        Navigate.waitUntilUrlContains("overview");
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