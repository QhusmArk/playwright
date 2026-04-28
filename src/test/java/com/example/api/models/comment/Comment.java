package com.example.api.models.comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    private Integer timestamp;
    private String datetime;
    @JsonProperty("user_id")
    private Integer userId;     // the API can return a Comment wo user_id = bad form :-(
    @JsonProperty("self_url")
    private String selfUrl;
    @JsonProperty("infra_timestamp")
    private BigInteger infraTimestamp;
    private String id;
    private String comment;
}
