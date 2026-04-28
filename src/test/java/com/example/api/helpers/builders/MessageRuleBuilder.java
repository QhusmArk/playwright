package com.example.api.helpers.builders;

import com.example.api.models.message.ContextSelection;
import com.example.api.models.message.MessageRule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class MessageRuleBuilder implements BuilderInterface<MessageRule> {

    private MessageRule messageRule;
    private String name;
    private List<String> usersIds;
    private Boolean active;
    private String weekdays;
    private String hours;
//    private String smsPerHour;
    private Integer smsPerHour;
    private Integer waittime;
    private Integer pretime;
    private String trigTypes;
    private Boolean blastManager;
    private Integer stylesheetId;
    private String datetimeFrom;
    private String datetimeTo;
    private ContextSelection contextSelection;
//    private Boolean companyName;
//    private Boolean projectName;
//    private Boolean messageName;
//    private Boolean measurePointName;
//    private Boolean measurePointDescription;
//    private Boolean sensorName;
//    private Boolean sensorSerial;
//    private Boolean value;
//    private Boolean date;
//    private Boolean time;
//    private Boolean channel;
//    private Boolean standard;

//    To be implemented
//    private NotificationPreview notificationPreview;

    public MessageRuleBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public MessageRuleBuilder addUserId(final String usersId) {
        if (usersIds == null) {
            usersIds = new ArrayList<>();
        }
        usersIds.add(usersId);
        return this;
    }

    public MessageRuleBuilder withActive(final Boolean active) {
        this.active = active;
        return this;
    }

    public MessageRuleBuilder withWeekdays(final String weekdays) {
        if (weekdays.length() != 7) {
            throw new NullPointerException("Weekdays must consist of seven characters (1 or 0)!");
        }
        this.weekdays = weekdays;
        return this;
    }

    public MessageRuleBuilder withHours(final String hours) {
        if (hours.length() != 24) {
            throw new NullPointerException("Hours must consist of 24 characters (1 or 0)!");
        }
        this.hours = hours;
        return this;
    }

    public MessageRuleBuilder withSmsPerHour(final int smsPerHour) {
        this.smsPerHour = smsPerHour;
        return this;
    }

    public MessageRuleBuilder withWaitTime(final int waittime) {
        this.waittime = waittime;
        return this;
    }

    public MessageRuleBuilder withPreTime(final int pretime) {
        this.pretime = pretime;
        return this;
    }

    public MessageRuleBuilder withBlastManager(final Boolean blastManager) {
        this.blastManager = blastManager;
        return this;
    }

    public MessageRuleBuilder withTrigTypes(final String trigTypes) {
        this.trigTypes = trigTypes;
        return this;
    }

    public MessageRuleBuilder withStyleSheetId(final Integer stylesheetId) {
        this.stylesheetId = stylesheetId;
        return this;
    }

    public MessageRuleBuilder withDateTimeFrom(final String datetimeFrom) {
        this.datetimeFrom = datetimeFrom;
        return this;
    }

    public MessageRuleBuilder withDateTimeTo(final String datetimeTo) {
        this.datetimeTo = datetimeTo;
        return this;
    }

    public MessageRuleBuilder withContextSelection(final ContextSelection contextSelection) {
        this.contextSelection = contextSelection;
        return this;
    }

    @Override
    public void build() {
        if (messageRule == null) {
            messageRule = new MessageRule();
        }

        if (name != null) {
            messageRule.setName(name);
        }
        if (usersIds != null) {
            messageRule.setUsersId(usersIds);
        }
        if (active != null) {
            messageRule.setActive(active);
        }
        if (weekdays != null) {
            messageRule.setWeekdays(weekdays);
        }
        if (hours != null) {
            messageRule.setHours(hours);
        }
        if (smsPerHour != null) {
            messageRule.setSmsPerHour(smsPerHour);
        }
        if (waittime != null) {
            messageRule.setWaitTime(waittime);
        }
        if (pretime != null) {
            messageRule.setPreTime(pretime);
        }
        if (trigTypes != null) {
            messageRule.setTrigTypes(trigTypes);
        }
        if (blastManager != null) {
            messageRule.setActive(active);
        }
        if (stylesheetId != null) {
            messageRule.setStylesheetId(stylesheetId);
        }
        if (datetimeFrom != null) {
            messageRule.setDatetimeFrom(datetimeFrom);
        }
        if (datetimeTo != null) {
            messageRule.setDatetimeTo(datetimeTo);
        }
        if (contextSelection != null) {
            messageRule.setContextSelection(contextSelection);
        }
    }

    @Override
    public MessageRule getProvider() {
        return messageRule;
    }

    @Override
    public void setProvider(MessageRule provider) {
        this.messageRule = provider;
    }

    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageRule);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
