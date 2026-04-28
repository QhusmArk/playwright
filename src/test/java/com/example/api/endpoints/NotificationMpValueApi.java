package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.message.NotificationMpValue;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;

public class NotificationMpValueApi extends ModelApi {

    public static List<NotificationMpValue> getNotificationMpValues(final int projectId, final int messageRuleId) {
        Response response = RequestService.request(Method.GET,
                "project/" + projectId + "/notification/" + messageRuleId + "/notification_mp_value/");
        return populateList(response, NotificationMpValue.class);
    }

    public static NotificationMpValue createNotificationMpValue(final int projectId, final int messageId, final String body) {
        Response response = RequestService.request(Method.POST,
                "project/" + projectId + "/notification/" + messageId + "/notification_mp_value/", body);
        return populateObject(response, NotificationMpValue.class);
    }

    public static NotificationMpValue createNotificationMpValue(final int projectId, final int messageId, final String body,
                                                                final String user, final String pw) {
        Response response = RequestService.request(Method.POST,
                "project/" + projectId + "/notification/" + messageId + "/notification_mp_value/", body, user, pw);
        return populateObject(response, NotificationMpValue.class);
    }

}
