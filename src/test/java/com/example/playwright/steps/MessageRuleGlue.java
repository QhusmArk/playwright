package com.example.playwright.steps;

import com.example.api.endpoints.MeasuringPointApi;
import com.example.api.endpoints.MessageRuleApi;
import com.example.api.endpoints.UserApi;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.api.models.message.MessageRule;
import com.example.api.models.user.User;
import com.example.helpers.AssertionHelpers;
import com.example.helpers.Randomizer;
import com.example.helpers.SmsConnector;
import com.example.helpers.StatusAssesser;
import com.example.playwright.components.aside.asideItems.listItems.MessageRuleItem;
import com.example.playwright.components.panels.message_rule.MessageRuleCreatePanel;
import com.example.playwright.components.panels.message_rule.MessageRuleSendingThresholdPanel;
import com.example.playwright.components.parts.Table;
import com.example.playwright.helpers.Navigate;
import com.twilio.rest.api.v2010.account.Message;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.helpers.StatusAssesser.Status.ACTIVE;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;

public class MessageRuleGlue extends BaseGlue {

    @When("I create a message rule")
    public void iCreateAMessageRule() {
        Navigate.project(context().getProject().getId())
                .messageRules()
                .create()
                .get();

        mrPO.createMessageRule("auto_test_mr", "1100", Optional.empty(), "trigger", Optional.empty(), "5", Optional.empty());
    }

    @When("I select {string} for message rule")
    public void iSelectEMailTransientReportForMessageRule(String trigTypes) {
        mrPO.clickOnTab("General");
        mrPO.setGeneral(Randomizer.randomString(4), "0001");
    }

    @Then("I get a warning message")
    public void iGetAWarningMessage() {
        MessageRuleCreatePanel panel = mrPO.getMessageRuleCreatePanel();

        assertEquals("Please note that e-mail transient report will only be sent from vibration measuring points.",
                panel.getMessageRuleGeneralPanel().getTransientReportWarningMsg().getText());
    }

    @Then("I can change the message rule name")
    public void iCanChangeTheMessageRuleName() {
        // Create a new name and replace the old name
        String newName = Randomizer.randomString(8);
        mrPO.updateMessageRuleName(newName);

//        ag.thisCanBeFoundInAside("message rule", newName);
        List<MessageRuleItem> mps = asidePO.getAside().getMessageRuleItems();
        assertTrue(mps.stream().anyMatch(mr -> mr.getName().contains(newName)));
    }

    @And("I can delete the message rule")
    public void iCanDeleteTheMessageRule() {
        mrPO.deleteMessageRule();

        // Get actual message rules
        List<MessageRuleItem> mrAfterChange = asidePO.getAside().getMessageRuleItems();
        assertTrue(mrAfterChange.isEmpty());
    }

    @Then("I see the new message rule in aside")
    @Then("I see it in Message Rule list")
    public void mrIsInAside() {
        MessageRule mr = MessageRuleApi.getMessageRules(context().getProject().getId()).getFirst();
//        ag.thisCanBeFoundInAside("message rule", mr.getName());
        List<MessageRuleItem> mps = asidePO.getAside().getMessageRuleItems();
        assertTrue(mps.stream().anyMatch(messageRuleItem -> messageRuleItem.getName().contains(mr.getName())));
    }

    @Then("I can create a message rule with absolute value")
    public void iCanCreateAMessageRuleWithAbsoluteValue() {
        Navigate.project(context().getProject().getId())
                .messageRules()
                .create()
                .get();

        mrPO.createMessageRule("auto_test_mr", "0100",Optional.empty(),  "absolute", Optional.of("LAS"), "5", Optional.empty());
    }

    @Then("there is a toggle {string} in {string}")
    public void thereIsAToggleShowProjectSUsers(String toggleText, String state) {
        boolean expectedToggleState = state.equals("ON");

        boolean actualToggleState = mrPO.getToggleState(toggleText);
        assertEquals(expectedToggleState, actualToggleState);
    }

    @When("I set toggle {string} to {string}")
    public void iSetToggleTo(String toggleText, String state) {
        mrPO.setMRRecipientToggle(toggleText);
    }

    // .../message_rules/25614/settings/recipients
    // Create new Message Rule, Recipient tab
    @Then("all users in the project are visible")
    public void allUsersInTheProjectAreVisible() {
        List<User> users = context().getUsers();

        // First deselect Show selected-toggle
        mrPO.setMRRecipientToggle("Show selected");
        List<String> actualUserNames = mrPO.getMessageRuleRecipientUserList();

        users.forEach(user -> {
            String fullName = user.getFirstName() + " " + user.getLastName();

            boolean match = actualUserNames.stream().anyMatch(nameInList -> nameInList.equals(fullName));
            assertTrue(match, fullName + " was not in the recipient list.");
        });
    }

    @Then("text message contain labels")
    public void textMessageContainLabels(DataTable table) {
        List<String> expectedLabelsInMessage = table.row(0);

        MessageRule mr = context().getMessageRules().getFirst();
        String receiver = UserApi.getUser(Integer.parseInt(mr.getUsersId().getFirst())).getMobilePhone();

        SmsConnector smsConnector = new SmsConnector();

        Optional<Message> optMessage = smsConnector.searchForMessage(
                ZonedDateTime.now(UTC).minusMinutes(1L),
                receiver,
                body -> expectedLabelsInMessage.stream().allMatch(body::contains),
                receiver,
                90
        );

        assertTrue(optMessage.isPresent(),
                () -> "No message to mr.recipient containing expectedLabelsInMessage was found.");
    }

    @Then("I get three sms notifications")
    public void iGetThreeSmsNotifications() {
        List<MessageRule> mrs = context().getMessageRules();

        // Set the test start time here, so that we can use same time for all messages
        SmsConnector smsConnector = new SmsConnector();
        List<Message> messages = smsConnector.fetchMessagesForTheseMessageRules(
                ZonedDateTime.now(UTC).minusMinutes(1L), context().getProject().getName(), mrs
        );

        if (messages.isEmpty()) {
            throw new IllegalStateException("No messages found");
        }

        mrs.forEach(messageRule -> {
            Message message = messages.stream()
                    .filter(m -> m.getBody().contains(messageRule.getName()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("No message found for: " + messageRule.getName()));

            // Use assertAll for better test output
            Assertions.assertAll("Message validation for rule: " + messageRule.getName(),
                    () -> {
                        switch (messageRule.getName()) {
                            case "MR_1" -> {
                                assertTrue(message.getBody().contains("Guide value:"), "Expected 'Guide value:' in MR_1");
                                assertFalse(message.getBody().contains("V10:"), "Unexpected 'V10:' in MR_1");
                            }
                            case "MR_2" -> {
                                assertFalse(message.getBody().contains("Guide value:"), "Unexpected 'Guide value:' in MR_2");
                                assertFalse(message.getBody().contains("V10:"), "Unexpected 'V10:' in MR_2");
                            }
                            case "MR_3" -> {
                                assertTrue(message.getBody().contains("V10:"), "Expected 'V10:' in MR_3");
                                assertFalse(message.getBody().contains("Guide value:"), "Unexpected 'Guide value:' in MR_3");
                            }
                        }
                    }
            );
        });
    }

    @Then("mp description is visible")
    public void mpDescriptionIsVisible() {
        // Set the values we need to set to be able to proceed to Sending Thresholds
        mrPO.setGeneral("mr_ssd-3092", "0100");
        mrPO.clickOnTab("Sending thresholds");

        // Get the expected mps
        List<MeasuringPoint> mps = MeasuringPointApi.getMeasuringPoints(context().getProject().getId());
        List<MeasuringPoint> activeMeasuringPoints = mps.stream()
                .filter(mp -> StatusAssesser.assessMpStatus(mp) == ACTIVE)
                .toList();

        // Get a list of all descriptions from expected mps
        List<String> expectedDescriptions = activeMeasuringPoints.stream()
                .filter(mp -> mp.getLocation().getDescription() != null)
                .filter(mp -> !mp.getLocation().getDescription().isEmpty())
                .map(mp -> mp.getLocation().getDescription())
                .toList();

        // Get the actual mps
        MessageRuleSendingThresholdPanel mrstp = mrPO.getMessageRuleSendingThresholdsPanel();

        List<Table.MpTableRow> mpList = mrstp.getMpTable().getMessageRuleMpRows();

        // Get a list of all descriptions from actual mpList
        List<String> actualDescriptions = mpList.stream()
                .filter(mpTableRow -> mpTableRow.getMpDescription() != null)
                .map(Table.MpTableRow::getMpDescription)
                .toList();

        // Make sure all descriptions are there
        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedDescriptions, actualDescriptions), "Lists are not equal");
    }

    @Then("MP in Sending Threshold is")
    public void mpInSendingThresholdIs(DataTable table) {
        List<String> expectedIcons = table.row(0);

        // Click on Sending Threshold tab
        mrPO.clickOnTab("Sending thresholds");

        MessageRuleSendingThresholdPanel panel = mrPO.getMessageRuleSendingThresholdsPanel();

        Set<String> actualIcons = panel.getMpTable().getMessageRuleMpRows().stream()
                .map(row -> row.getMpTypeIcon().getType().name())
                .collect(Collectors.toSet());

        boolean isAlike = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedIcons, actualIcons.stream().toList());
        assertTrue(isAlike);
    }


    @Then("Sending Thresholds show mp")
    public void sendingThresholdsShowMp(DataTable table) {
        List<String> expectedIcons = table.row(0);

        MessageRuleSendingThresholdPanel panel = mrPO.getMessageRuleSendingThresholdsPanel();

        Set<String> actualIcons = panel.getMpTable().getMessageRuleMpRows().stream()
                .map(row -> row.getMpTypeIcon().getType().name())
                .collect(Collectors.toSet());

        boolean isAlike = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedIcons, actualIcons.stream().toList());
        assertTrue(isAlike);
    }

    @Then("message rule recipients are {int}")
    public void messageRuleRecipientsAre(int expectedRecipients) {
        List<String> usersAfterUpdate = context().getMessageRules().getFirst().getUsersId();

        assertEquals(expectedRecipients, usersAfterUpdate.size(),
                ("expected/actual: " + expectedRecipients + "/" + usersAfterUpdate.size()));
    }
}
