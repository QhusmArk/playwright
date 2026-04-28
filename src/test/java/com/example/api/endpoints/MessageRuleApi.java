package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.message.MessageRule;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;

public class MessageRuleApi extends ModelApi {

    public static MessageRule createMessageRule(final int projectId, final String body) {
        Response response = RequestService.request(Method.POST, "project/" + projectId + "/notification/", body);
        return populateObject(response, MessageRule.class);
    }

    public static MessageRule createMessageRule(final int projectId, final String body, final String user, final String pw) {
        Response response = RequestService.request(Method.POST, "project/" + projectId + "/notification/", body, user, pw);
        return populateObject(response, MessageRule.class);
    }

    public static MessageRule getMessageRule(final int projectId, final int messageRuleId) {
        Response response = RequestService.request(Method.GET, "project/" + projectId + "/notification/" + messageRuleId);
        return populateObject(response, MessageRule.class);
    }

    public static List<MessageRule> getMessageRules(final int projectId) {
        Response response = RequestService.request(Method.GET, "project/" + projectId + "/notification/");
        return populateList(response, MessageRule.class);
    }

    public static MessageRule updateMessageRule(final int projectId, final int messageRuleId, final String body) {
        Response response = RequestService.request(Method.PUT, "project/" + projectId + "/notification/" + messageRuleId, body);
        return populateObject(response, MessageRule.class);
    }

    public static MessageRule deleteMessageRule(final int projectId, final int messageRuleId) {
        Response response =
                RequestService.request(Method.DELETE, "project/" + projectId + "/notification/" + messageRuleId);
        return populateObject(response, MessageRule.class);
    }

}


