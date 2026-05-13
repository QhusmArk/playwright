package com.example.playwright.steps;

import com.example.helpers.Randomizer;
import com.example.playwright.components.panels.LoginPage;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.hooks.BrowserHooks;
import com.example.playwright.hooks.testUsers.TestUser;
import com.example.playwright.hooks.testUsers.TestUserPool;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticateGlue extends BaseGlue {

//    @When("I enter my email address as {string}")
//    public void enterEmailAddress(String username) {
//        loginPO.enterResetUsername(username);
//    }

    @When("I enter my email address")
    public void enterEmailAddress() {
        TestUser currentUser = TestUserPool.getCurrentUser();

        loginPO.enterResetUsername(currentUser.email());
    }

    @When("I use wrong password for login")
    public void iUseWrongPasswordForLogin() {
        TestUser currentUser = TestUserPool.getCurrentUser();

        loginPO.enterLoginCredentials(currentUser.email(), Randomizer.randomString(10));
        loginPO.clickButton("Login");
    }

    @Then("I get error messages")
    public void iGetErrorMessages() {
        LoginPage loginPage = loginPO.getLoginPage();

        assertEquals("Login Failed!",  loginPage.getErrorMessage());
        assertEquals("Your username and/or password were incorrect. Are you sure you're using the correct URL?",  loginPage.getErrorExplanation());
    }

    @Then("I can request a password reset")
    public void requestPasswordReset() {
        loginPO.clickRequestResetLink();
        assertUrlContain("passresetdone");
    }

    @Then("the session is closed")
    public void validateLoggedOut() {
        Navigate.refreshBrowser();
        boolean atLoginPage = assertUrlContain("/login");

        //locate session id
        boolean isSessionIdCookieDeletedFromDriver = BrowserHooks.getCookie("sessionid") == null;

        //if url contains /login and cookie is gone it's a success.
        assertTrue(atLoginPage && isSessionIdCookieDeletedFromDriver,
                () -> "atLoginPage/isSessionIdCookieDeletedFromDriver: " + atLoginPage + "/" + isSessionIdCookieDeletedFromDriver);
    }

    public boolean assertUrlContain(String partOfUrl) {

        if (waitForUrlToContain(partOfUrl)) {
            return true;
        }

        throw new IllegalStateException(
                "Url '" + Navigate.getCurrentUrl() + "' did not contain '" + partOfUrl + "'"
        );
    }

    public boolean waitForUrlToContain(final String partOfUrl) {
        return waitForUrlToContain(partOfUrl, 10);
    }

    public boolean waitForUrlToContain(final String partOfUrl, final int timeToWait) {
        System.out.println("Waiting for '" + partOfUrl + "' to be included in the url.");

        // Start polling
        for (int s = 0; s <= timeToWait; s++) {
            PlaywrightActions.sleep(1);
            if (Navigate.getCurrentUrl().contains(partOfUrl)) {
                return true;
            }
        }
        return false;
    }

    @Then("{string} link leads to {string}")
    public void linkLeadsToUrl(String link, String expectedUrl) {
        String urlInLink = loginPO.getRefUrlFromLink(link);

        System.out.println("expectedUrl: " + expectedUrl);
        System.out.println("urlInLink: " + urlInLink);

        assertTrue(urlInLink.contains(expectedUrl));
    }
}
