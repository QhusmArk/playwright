package com.example.helpers;

import com.example.api.endpoints.UserApi;
import com.example.api.models.message.MessageRule;
import com.example.playwright.config.TwilioProperties;
import com.example.playwright.helpers.PlaywrightActions;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

// todo: byt namn till typ TwilioApi o flytta till api-helpers
public class SmsConnector {

    public SmsConnector() {
        System.out.println("***** SMS connector *****");

        Twilio.init(
                TwilioProperties.getValue("TWILIO_API_KEY_SID"),
                TwilioProperties.getValue("TWILIO_API_KEY_SECRET"),
                TwilioProperties.getValue("TWILIO_ACCOUNT_SID")
        );

        try {
            // Retrieve messages
            ResourceSet<Message> messages = Message.reader().limit(1).read();

        } catch (com.twilio.exception.ApiException e) {
            // Handle invalid credentials or other API issues
            // NB. It's possible that creds to Twilio stop working for unknown reason.
            // The fix then might be to make manual login to Twilio and validate that the user is an active user (or something like that).
            throw new IllegalStateException("Failed to validate Twilio credentials: " + e.getMessage());
        }
    }

    public List<Message> fetchMessagesForTheseMessageRules(ZonedDateTime testStartTime, String projectName, List<MessageRule> mrs) {
        List<Message> messages = new ArrayList<>();

        for (MessageRule mr : mrs) {
            String mrName = mr.getName();
            String receiver = UserApi.getUser(Integer.parseInt(mr.getUsersId().getFirst())).getMobilePhone();

            Optional<Message> optMessage = searchForMessage(
                    testStartTime,
                    receiver,
                    body -> body.contains(projectName) && body.contains(mrName),
                    mrName
            );
            optMessage.ifPresent(messages::add);
        }

        return messages;
    }

    public Optional<Message> searchForMessage(
            ZonedDateTime testStartTime,
            String receiver,
            Predicate<String> messageFilter,
            String logInfo,
            int maxIterations) {

        System.out.println("Trying to find message for " + logInfo);

        for (int iteration = 1; iteration <= maxIterations; iteration++) {
            // Fetch messages for the given receiver
            ResourceSet<Message> messages = fetchMessages(testStartTime, receiver);

            for (Message message : messages) {
                String body = message.getBody();

                if (body != null && messageFilter.test(body)) {
                    return Optional.of(message);
                }
            }

            // Sleep before next polling attempt
            PlaywrightActions.sleep(1);
            System.out.println("Polling iteration " + iteration + " of " + maxIterations);
        }

        return Optional.empty();

    }


    public Optional<Message> searchForMessage(
            ZonedDateTime testStartTime,
            String receiver,
            Predicate<String> messageFilter,
            String logInfo) {

        int maxIterations = 60;
        return searchForMessage(testStartTime, receiver, messageFilter, logInfo, maxIterations);

    }

    /**
     * NB. If a message is split into two, then the second part is "lost".
     * @return the first message found that is after test start, and is to receiver.
     */
    private ResourceSet<Message> fetchMessages(ZonedDateTime testStartTime, String receiver) {
        return Message.reader()
                .setTo(receiver)
                .setDateSentAfter(testStartTime)
                .limit(20).read();
    }

    public void sendSmsAlert(String message, String recipient) {
        // Fetch the first available phone number from your Twilio account
        Iterable<IncomingPhoneNumber> phoneNumbers = IncomingPhoneNumber.reader().read();
        String twilioPhoneNumber = null;

        for (IncomingPhoneNumber phone : phoneNumbers) {
            twilioPhoneNumber = phone.getPhoneNumber().toString();
            break; // Get the first available recipient and exit loop
        }

        if (twilioPhoneNumber == null) {
            System.err.println("No Twilio phone numbers found in the account.");
            return;
        }

        // Send the message
        Message.creator(
                new PhoneNumber(recipient),  // Recipient's phone recipient
                new PhoneNumber(twilioPhoneNumber),  // Twilio phone recipient
                message  // SMS content
        ).create();
    }

}
