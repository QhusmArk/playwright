package com.example.api.models.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
//a.k.a. Notification
public class MessageRule {

    private Integer id;
    private String name;

    @JsonProperty("users_id")
    private List<String> usersId;

    @JsonProperty("groups_id")
    private Integer[] groupsId;

    private Boolean active;
    private String weekdays;
    private String hours;

    @JsonProperty("sms_per_hour")
    private Integer smsPerHour;
    @JsonProperty("waittime")
    private Integer waitTime;
    @JsonProperty("pretime")
    private Integer preTime;

    @JsonProperty("trig_types")
    private String trigTypes;   //

    @JsonProperty("blast_manager")
    private Boolean blastManager;

    @JsonProperty("stylesheet_id")
    private Integer stylesheetId;

    @JsonProperty("datetime_from")
    private String datetimeFrom;

    @JsonProperty("datetime_to")
    private String datetimeTo;

    @JsonProperty("context_selection")
    private ContextSelection contextSelection;

    // To be implemented
//    @JsonProperty("notification_preview")
//    private NotificationPreview notificationPreview;
}
