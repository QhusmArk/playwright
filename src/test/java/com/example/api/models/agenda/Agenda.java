package com.example.api.models.agenda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Agenda {

    private Integer id;

    private String name;

    @JsonProperty("project_id")
    private Integer projectId;

    @JsonProperty("expand_url")
    private String expandUrl;

    @JsonProperty("self_url")
    private String selfUrl;

    private List<Label> labels;

    private List<Definition> definitions;

    @JsonProperty("__all__")
    private String errorMessage;
}
