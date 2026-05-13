package com.example.playwright.steps;

import com.example.playwright.components.panels.project.ProjectSettingsAgendasPanel;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class CommonGlue extends BaseGlue{

    @Then("I get message {string}")
    @Then("toast {string} is displayed")
    public void toastIsDisplayed(String expectedTextInEnglish) {
        String textBetweenSingleQuotes = checkForSingleQoutes(expectedTextInEnglish);

        // Remove qouted text
        if (!textBetweenSingleQuotes.isEmpty()) {
            expectedTextInEnglish = expectedTextInEnglish.replace(textBetweenSingleQuotes, "");
        }

        // Try go get all messages coming up from bottom of GUI
        List<String> toasts = menuPO.getToasts();

        // Translation logic for Swedish and French
        String expectedTextInSwedish = translateToSwedish(expectedTextInEnglish);
        String expectedTextInFrench = translateToFrench(expectedTextInEnglish);

        // Add qouted text
        if (!textBetweenSingleQuotes.isEmpty()) {
            expectedTextInEnglish += textBetweenSingleQuotes;
            expectedTextInSwedish += textBetweenSingleQuotes;
            expectedTextInFrench += textBetweenSingleQuotes;
        }

        // Loop through actual texts and check for a match
        boolean matchFound = false;
        for (String actualText : toasts) {
            if (actualText.contains(expectedTextInEnglish) ||
                    actualText.contains(expectedTextInSwedish) ||
                    actualText.contains(expectedTextInFrench)) {
                matchFound = true;
                break;
            }
        }

        // Assert that a match was found
        Assertions.assertTrue(matchFound, "None of the toasts contain the expected text: " + expectedTextInEnglish);
    }

    private String checkForSingleQoutes(String text) {
        // Check text for single qoutes
        String regex = "'[^']*'";  // Regular expression to capture the single quotes and everything inside

        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        // Find and print the quoted text
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }

    // Translation logic for Swedish
    private String translateToSwedish(String text) {
        return switch (text) {
            case "Sent invite e-mail to " -> "Skicka e-postinbjudan till ";
            case "User has been updated" -> "Användaren har uppdaterats";
            case "Your password has been updated" -> "Ditt lösenord har uppdaterats";
            case "Project with this Project id and Company already exists." -> "Project with this Project id and Company already exists.";
            case "Agenda with this Name and Project already exists." -> "Agenda with this Name and Project already exists.";
            default -> text;  // If no translation is found, return the original text
        };
    }

    // Translation logic for French
    private String translateToFrench(String text) {
        return switch (text) {
            case "Sent invite e-mail to " -> "Message de bienvenue envoyé à ";
            case "User has been updated" -> "L'utilisateur a été mis à jour";
            case "Your password has been updated" -> "Votre mot de passe a été mis à jour";
            case "Project with this Project id and Company already exists." -> "Project with this Project id and Company already exists.";
            case "Agenda with this Name and Project already exists." -> "Agenda with this Name and Project already exists.";
            default -> text;  // If no translation is found, return the original text
        };
    }

    // Validation message is below field, and is red
    @Then("{string} has validation message {string}")
    public void fieldHasValidationMessage(String field, String expectedValidationMessage) {
        String actualValidationMessage = menuPO.getValidationMessageText(field);
        System.out.println("expectedValidationMessage/actualValidationMessage" + expectedValidationMessage + "/" + actualValidationMessage);
        assertEquals(expectedValidationMessage, actualValidationMessage);
    }

    @Then("notify message {string} is displayed")
    public void iGetPanelNotify(String expectedNotifyMessage) {
        assertTrue(menuPO.checkForNotifyContaining(expectedNotifyMessage));
    }

    @Then("no toast is displayed")
    public void noToastIsDisplayed() {
        List<String> actualTexts = menuPO.getToasts();
        assertTrue(actualTexts.isEmpty());
    }

    @And("click on aside {string}")
    public void clickOnAsideProjectSettings(String textOnListItem) {
        menuPO.clickAsideListItem(textOnListItem);
    }

    @And("click on panel {string}")
    public void clickOnPanelAgendas(String panelSelectionText) {
        menuPO.clickPanelSelection(panelSelectionText);
    }

    @When("I click on {string} button")
    public void iClickOnButton(String buttonToClick) {
        menuPO.clickButton(buttonToClick);
    }

    @Then("these buttons are not present")
    public void theseButtonsAreNotPresent(DataTable table) {
        ProjectSettingsAgendasPanel panel = agendaPO.getAgendasPanel();

        assertNull(panel.getCopyAgendaButton());
        assertNull(panel.getCreateAgendaButton());
    }

}
