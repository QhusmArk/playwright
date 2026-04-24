package com.example.playwright.steps;

import com.example.playwright.testUsers.TestUser;
import com.example.playwright.testUsers.TestUserPool;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultithreadingSteps {

    private static final Set<Long> THREAD_IDS = ConcurrentHashMap.newKeySet();

    @Given("multithreading is required")
    public void multithreadingIsRequired() {
        assertTrue(
                Runtime.getRuntime().availableProcessors() > 1,
                "Multithreading cannot be used because only one processor is available."
        );

        TestUserPool.acquireUser()
                .orElseThrow(() -> new RuntimeException("No available test users in pool"));

        System.out.println(
                "Thread " + Thread.currentThread().threadId() +
                        " acquired user: " + TestUserPool.getCurrentUser().username()
        );
    }

    @When("the test execution starts")
    public void theTestExecutionStarts() {
        THREAD_IDS.add(Thread.currentThread().threadId());
    }

    @Then("this scenario records its thread")
    public void thisScenarioRecordsItsThread() throws InterruptedException {
        THREAD_IDS.add(Thread.currentThread().threadId());

        // Give time for other threads to run
        Thread.sleep(500);

        assertTrue(
                THREAD_IDS.size() > 1,
                "Multithreading is NOT working. Only one thread detected."
        );
    }

    @Then("the browser opens {string}")
    public void theBrowserOpens(String url) {
        Hooks.getPage().navigate(url);
    }

    @Then("the Playwright homepage is visible")
    public void thePlaywrightHomepageIsVisible() {
        String title = Hooks.getPage().title();

        assertTrue(
                title.contains("Playwright"),
                "Expected Playwright page title, but got: " + title
        );
    }

    @Then("the current user is printed")
    public void theCurrentUserIsPrinted() {
        var user = TestUserPool.getCurrentUser();

        System.out.println(
                "Thread " + Thread.currentThread().threadId() +
                        " using user: " + user.username()
        );
    }

    @Then("the user is stored in the browser")
    public void theUserIsStoredInTheBrowser() {
        var user = TestUserPool.getCurrentUser();

        Hooks.getPage().evaluate(
                "user => localStorage.setItem('test-user', user)",
                user.username()
        );
    }

    @Then("the user in the browser should match the current user")
    public void theUserInTheBrowserShouldMatchTheCurrentUser() {
        var expectedUser = TestUserPool.getCurrentUser().username();

        String actualUser = Hooks.getPage().evaluate(
                "() => localStorage.getItem('test-user')"
        ).toString();
//        System.out.println("expectedUser: " + expectedUser + " actualUser: " + actualUser);
        assertTrue(
                expectedUser.equals(actualUser),
                "Mismatch between thread user and browser user. Expected: "
                        + expectedUser + " but got: " + actualUser
        );
    }

    @After
    public void releaseTestUser(Scenario scenario) {
        System.out.println(
                "Scenario '" + scenario.getName() + "' | Thread " +
                        Thread.currentThread().threadId() +
                        " releasing user: " +
                        TestUserPool.getCurrentUser().username()
        );

        TestUserPool.releaseCurrentUser();
    }
}