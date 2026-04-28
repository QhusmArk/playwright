package com.example.playwright.steps;

import com.example.api.models.user.User;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserGlue extends BaseGlue {

    private User userBeforeChange;

    @And("I open UserProfile")
    public void userProfile() {
        menuPO.openUserMenuAndSelectMenuItem("USER_PROFILE");
    }

    @When("I try to edit my first name")
    public void editUserFirstName() {
        this.userBeforeChange = new User();

        // store name before changing it
        userBeforeChange.setFirstName(upPO.getFirstName());

        // create a new name, enter it and save it
        String value = "AutoTest_" + (int) (Math.random() * 100);
        upPO.enterUserFirstName(value);
        cePO.clickButtonText("Save");
    }

    @Then("the name change request was a {string}")
    public void validateChangedFirstName(String expected) {
        //get the old name
        String previousFirstName = userBeforeChange.getFirstName();
        System.out.println("previousFirstName: " + previousFirstName);
        // Go to UserProfile to grab current data
        menuPO.openUserMenuAndSelectMenuItem("USER_PROFILE");

        //get the new name
        String currentFirstName = upPO.getFirstName();
        System.out.println("currentFirstName: " + currentFirstName);
        // successful namechange = true
        assertNotEquals(previousFirstName, currentFirstName);
    }
}
